package cn.ljd.framework.service.impl;

import cn.ljd.framework.constant.StockConstant;
import cn.ljd.framework.dto.DrDto;
import cn.ljd.framework.dto.req.StockPullReqDto;
import cn.ljd.framework.dto.TencentHisResDto;
import cn.ljd.framework.mapper.FailLogMapper;
import cn.ljd.framework.mapper.StockBasicMapper;
import cn.ljd.framework.mapper.StockInfoRealtimeMapper;
import cn.ljd.framework.mapper.StockQuoteMapper;
import cn.ljd.framework.po.FailLogPo;
import cn.ljd.framework.po.StockBasicPo;
import cn.ljd.framework.po.StockInfoRealtimePo;
import cn.ljd.framework.po.StockQuotePo;
import cn.ljd.framework.service.StockQuoteService;
import cn.ljd.framework.sqlService.StockQuoteSqlService;
import cn.ljd.framework.utils.HttpClientPool;
import cn.ljd.framework.utils.ListUtil;
import cn.ljd.framework.utils.LocalDateTimeUtil;
import cn.ljd.framework.utils.SnowflakeIdWorker;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

@Service
@Slf4j
public class StockQuoteServiceImpl implements StockQuoteService {

    @Resource
    StockBasicMapper stockBasicMapper;
    @Resource
    StockQuoteMapper stockQuoteMapper;
    @Resource
    StockInfoRealtimeMapper stockInfoRealtimeMapper;
    @Resource
    FailLogMapper failLogMapper;

    @Autowired
    SnowflakeIdWorker snowflakeIdWorker;

    @Autowired
    StockQuoteSqlService stockQuoteSqlService;

    @Autowired
    StockQuoteService sockQuoteService;

    @Autowired
    DataSourceTransactionManager dataSourceTransactionManager;
    @Autowired
    TransactionDefinition transactionDefinition;

    static final String STOCK_QUOTE_URL = "https://web.ifzq.gtimg.cn/appstock/app/fqkline/get?";
    static final String STOCK_REALTIME_URL = "http://qt.gtimg.cn/q=";

    @Override
    public Integer execute(StockPullReqDto reqDto){
        int count = 0;
        List<String> stockCodeList = null;
        LocalDateTime quoteDate = null;
        if(StringUtils.isNotEmpty(reqDto.getQuoteDate())){
            quoteDate = LocalDateTimeUtil.convertString2LocalDateTime(reqDto.getQuoteDate() + " 00:00:00");
            if(quoteDate == null){
                throw new RuntimeException("wrong date format");
            }
        }

        if(StringUtils.isBlank(reqDto.getCodes())){
            stockCodeList =  stockBasicMapper.selectAllCodes();
        }else{
            stockCodeList = Arrays.asList(reqDto.getCodes().split(","));
        }

        for(String stockCode : stockCodeList){
            LocalDateTime startTime = LocalDateTime.now();
            stockCode = convertStockCode(stockCode);
            log.info("开始执行股票代码{},执行类型{}",stockCode,reqDto.getPullType());
            count = count + executeOneCode(stockCode, reqDto.getPullType(), quoteDate, reqDto.getForceRefresh());
            LocalDateTime endTime = LocalDateTime.now();
            long cost = (endTime.toInstant(ZoneOffset.of("+8")).toEpochMilli() - startTime.toInstant(ZoneOffset.of("+8")).toEpochMilli());
            log.info("执行股票代码{},执行类型{}, 耗时{}ms",stockCode,reqDto.getPullType(), cost);
        }
        return count;
    }

    private String convertStockCode(String tsCode){
        tsCode = tsCode.toLowerCase();
        if(tsCode.endsWith(".sz") || tsCode.endsWith(".sh")){
            String[] strings = tsCode.split("\\.");
            return strings[1].toLowerCase() + strings[0];
        }
        return tsCode.replace(".","");
    }


    private Integer executeOneCode(String stockCode, String pullType, LocalDateTime quoteDate, boolean forceRefresh){
        String symbol = stockCode.substring(2);
        StockBasicPo stockBasicPo = stockBasicMapper.selectOne(new LambdaQueryWrapper<StockBasicPo>().eq(StockBasicPo::getSymbol, symbol));
        if(stockBasicPo == null){
            log.error("错误的股票代码:{}", symbol);
            return 0;
        }

        if(StockConstant.EXECUTE_TYPE_STOCK_REALTIME.equals(pullType)){
            return pullStockRealtime(stockCode);
        }

        if (StockConstant.EXECUTE_CALCULATE_AVERAGE_LINE.equals(pullType)) {
            calcAverageLine(stockCode, forceRefresh);
            return 0;
        }

        int count = 0;
        int thisYear = 2022;
        int queryYears = 30;
        String queryDays = "300";

        if(quoteDate == null) {
            sockQuoteService.deleteByStockCode(stockCode);
        }else{
            Integer exists = stockQuoteMapper.selectCount(new LambdaQueryWrapper<StockQuotePo>().eq(StockQuotePo::getCode,stockCode).eq(StockQuotePo::getQuoteDate, quoteDate));
            if(exists != null && exists > 0){
                log.info("记录已同步:{}[{}]", symbol, quoteDate);
                return 0;
            }
            queryDays = "1";
        }


        for(int i =0; i<queryYears; i++){
            int year = thisYear - i;
            String dates = year + "-01-01," + year + "-12-31";
            String queryDate = "";
            if(quoteDate != null){
                queryDate = LocalDateTimeUtil.convertLocalDateTime2DateString(quoteDate);
                dates = queryDate + "," + queryDate;
            }

            try {
                String url = STOCK_QUOTE_URL + "param="+stockCode+",day," + dates + "," + queryDays + ",qfq";
                String res = HttpClientPool.getHttpClient().get(url);
                if(StringUtils.isNotBlank(res)) {
                    String jsonString = res.replace(stockCode, "stockInfo").replace("{\"day\"","{\"qfqday\"");
                    if(StringUtils.isNotEmpty(queryDate) && !jsonString.contains("\"qfqday\":[[\"" +queryDate +"\"")){
                        break;
                    }

                    TencentHisResDto tencentHisResDto = JSON.parseObject(jsonString, TencentHisResDto.class);
                    //log.info("parse object" + tencentHisResDto.toString());
                    int thisYearCount = sockQuoteService.save(stockCode, stockBasicPo.getName(), tencentHisResDto);
                    count = count + thisYearCount;
                    if(thisYearCount == 0){
                        break;
                    }
                }else {
                    break;
                }

                if(quoteDate != null){
                    break;
                }
            } catch (Exception e) {
                saveErrorRecord(stockCode, stockBasicPo.getName(), year, e);
                log.info("pull fail", e);
            }
        }
       return count;
    }

    @Transactional
    @Override
    public int save(String stockCode, String stockName, TencentHisResDto tencentHisResDto){

        if(tencentHisResDto == null || tencentHisResDto.getData() == null || tencentHisResDto.getData().getStockInfo() == null){
            return 0;
        }
        List<List<String>> qfqday = tencentHisResDto.getData().getStockInfo().getQfqday();
        if(CollectionUtils.isEmpty(qfqday)){
            return 0;
        }

        List<StockQuotePo> quotePoList = Lists.newArrayList();

        LocalDateTime createTime = LocalDateTime.now();

        BigDecimal preClosePrice = null;
        for(List<String> fieldList : qfqday){
            if(CollectionUtils.isEmpty(fieldList)){
                continue;
            }

            StockQuotePo po = new StockQuotePo();
            po.setId(snowflakeIdWorker.nextId());
            po.setCreateTime(createTime);
            po.setCode(stockCode);
            po.setName(stockName);
            po.setSymbol(stockCode.substring(2));
            po.setQuoteDate(LocalDateTimeUtil.convertString2LocalDateTime(fieldList.get(0) + " 00:00:00"));
            po.setOpenPrice(new BigDecimal(fieldList.get(1)));
            po.setClosePrice(new BigDecimal(fieldList.get(2)));
            po.setHighPrice(new BigDecimal(fieldList.get(3)));
            po.setLowPrice(new BigDecimal(fieldList.get(4)));
            //po.setDrRate(BigDecimal.valueOf(1));
            po.setDrClosePrice(po.getClosePrice());

            if(fieldList.size()>5) {
                po.setVolume(new BigDecimal(fieldList.get(5)));
            }
            if(fieldList.size()>6) {
                po.setDr(fieldList.get(6));
            }
            if(preClosePrice != null){
                po.setPreClosePrice(preClosePrice);
                po.setRise(po.getClosePrice().subtract(preClosePrice).multiply(BigDecimal.valueOf(100)).divide(preClosePrice,2, BigDecimal.ROUND_HALF_UP));
            }
            BigDecimal diff = po.getHighPrice().subtract(po.getLowPrice()).multiply(BigDecimal.valueOf(100)).divide(po.getOpenPrice(),2, BigDecimal.ROUND_HALF_UP);
            po.setDiff(diff);
            quotePoList.add(po);
            preClosePrice = po.getClosePrice();
        }

        if(CollectionUtils.isNotEmpty(quotePoList)) {
            stockQuoteSqlService.saveBatch(quotePoList);
        }
        return quotePoList.size();
    }


    private void saveErrorRecord(String stockCode, String stockName, int year, Exception e){
        String errorMsg = null;
        if(e != null) {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            errorMsg = sw.toString();
            if(errorMsg != null && errorMsg.length()>10000){
                errorMsg = errorMsg.substring(0, 10000);
            }
        }
        FailLogPo errorPo = new FailLogPo();
        errorPo.setId(snowflakeIdWorker.nextId());
        errorPo.setCreateTime(LocalDateTime.now());
        errorPo.setCode(stockCode);
        errorPo.setName(stockName);
        errorPo.setOperateType("pull");
        errorPo.setPullYear(year+"");
        errorPo.setStatus("0");
        errorPo.setErrMsg(errorMsg);
        sockQuoteService.saveError(errorPo);
    }

    @Transactional
    @Override
    public void saveError(FailLogPo po){
        failLogMapper.insert(po);
    }

    @Transactional
    @Override
    public void deleteByStockCode(String stockCode){
        stockQuoteMapper.deleteByCode(stockCode);
    }

    @Override
    public void updatePreClosePrice(){
        List<String> stockCodeList =  stockBasicMapper.selectAllCodes();

        int i = 0;
        for(String stockCode : stockCodeList){
            i++;
            LocalDateTime startTime = LocalDateTime.now();
            stockCode = convertStockCode(stockCode);
            TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
            try {
                stockQuoteMapper.updatePreClosePrice(stockCode);
                stockQuoteMapper.updateRise(stockCode);
                dataSourceTransactionManager.commit(transactionStatus);
            }catch (Exception e) {
                LocalDateTime endTime = LocalDateTime.now();
                long cost = (endTime.toInstant(ZoneOffset.of("+8")).toEpochMilli() - startTime.toInstant(ZoneOffset.of("+8")).toEpochMilli());
                dataSourceTransactionManager.rollback(transactionStatus);
                log.info("更新{}的preClosePrice失败,耗时{}ms",stockCode,cost);
            }
            LocalDateTime endTime = LocalDateTime.now();
            long cost = (endTime.toInstant(ZoneOffset.of("+8")).toEpochMilli() - startTime.toInstant(ZoneOffset.of("+8")).toEpochMilli());
            log.info("更新第{}条记录,{}的preClosePrice,耗时{}ms",i,stockCode,cost);
        }
    }


    @Override
    public void qfq(){
        List<String> sqCodeList =  stockBasicMapper.selectAllSqCodes();
        int i = 0;
        for(String sqCode : sqCodeList){
            i++;
            LocalDateTime startTime = LocalDateTime.now();
            try {
                List<StockQuotePo> updateQuotePoList = Lists.newArrayList();
                BigDecimal drRate = BigDecimal.valueOf(1);
                List<StockQuotePo> drStockQuotePoList = stockQuoteMapper.selectListByCode(sqCode);
                for (StockQuotePo drPo : drStockQuotePoList) {
                    if(drPo.getDrClosePrice() != null){
                        drStockQuotePoList.clear();
                        break;
                    }

                    DrDto drDto = JSON.parseObject(drPo.getDr(), DrDto.class);
                    if (drDto == null || StringUtils.isEmpty(drDto.getFHcontent())) {
                        StockQuotePo updatePo = new StockQuotePo();
                        updatePo.setId(drPo.getId());
                        updatePo.setDrRate(drRate);
                        updatePo.setDrClosePrice(drPo.getClosePrice().multiply(drRate));
                        updateQuotePoList.add(updatePo);
                        continue;
                    }

                    String fhContent = drDto.getFHcontent();
                    fhContent = fhContent.replaceFirst("10", "");
                    fhContent = fhContent.replace("派", "\"pai\":").replace("送", "\"song\":").replace("转", "\"zhuan\":");
                    fhContent = fhContent.replace("元", ",").replace("股", ",");
                    fhContent = "{" + fhContent + "}";
                    fhContent = fhContent.replace(",}", "}");

                    DrDto pszDto = JSON.parseObject(fhContent, DrDto.class);
                    BigDecimal returnRate = updateDrRate(drPo, pszDto, drRate, updateQuotePoList);
                    if(returnRate != null){
                        drRate = drRate.multiply(returnRate);
                    }
                }

                batchUpdate(updateQuotePoList);
                drStockQuotePoList.clear();
            }catch (Exception e){
                LocalDateTime endTime = LocalDateTime.now();
                long cost = (endTime.toInstant(ZoneOffset.of("+8")).toEpochMilli() - startTime.toInstant(ZoneOffset.of("+8")).toEpochMilli());
                log.info("更新第{}条记录{}的drRate失败,耗时{}ms",i,sqCode,cost);

            }
            LocalDateTime endTime = LocalDateTime.now();
            long cost = (endTime.toInstant(ZoneOffset.of("+8")).toEpochMilli() - startTime.toInstant(ZoneOffset.of("+8")).toEpochMilli());
            log.info("更新第{}条记录,{}的drRate,耗时{}ms",i,sqCode,cost);
        }
    }

    private BigDecimal updateDrRate(StockQuotePo drPo, DrDto pszDto,BigDecimal preDrRate, List<StockQuotePo> updateQuotePoList){
        BigDecimal preClosePrice = drPo.getPreClosePrice();
        if(preClosePrice == null){
            StockQuotePo newPo = new StockQuotePo();
            newPo.setId(drPo.getId());
            newPo.setDrRate(preDrRate);
            newPo.setDrClosePrice(drPo.getClosePrice().multiply(preDrRate));
            updateQuotePoList.add(newPo);
            return null;
        }

        BigDecimal afterPrice = preClosePrice.multiply(BigDecimal.valueOf(10));
        if(pszDto.getPai() != null){
            afterPrice = afterPrice.subtract(pszDto.getPai());
        }

        BigDecimal afterNumber = BigDecimal.valueOf(10);
        if(pszDto.getSong() != null){
            afterNumber = afterNumber.add(pszDto.getSong());
        }

        if(pszDto.getZhuan() != null){
            afterNumber = afterNumber.add(pszDto.getZhuan());
        }

        afterPrice = afterPrice.divide(afterNumber,4, BigDecimal.ROUND_HALF_UP);
        BigDecimal drRate = afterPrice.divide(preClosePrice,6, BigDecimal.ROUND_HALF_UP);

        StockQuotePo newPo = new StockQuotePo();
        newPo.setId(drPo.getId());
        newPo.setDrRate(preDrRate);
        newPo.setDrClosePrice(drPo.getClosePrice().multiply(preDrRate));
        updateQuotePoList.add(newPo);
        return drRate;
    }

    private void batchUpdate(List<StockQuotePo> updateQuotePoList){
        if(CollectionUtils.isEmpty(updateQuotePoList)) {
            return;

        }
        List<List<StockQuotePo>> listList = ListUtil.groupList(updateQuotePoList,1000);
        for(List<StockQuotePo>  list : listList){
            TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);

            try {
                stockQuoteSqlService.updateBatchById(list);
                dataSourceTransactionManager.commit(transactionStatus);
            }catch (Exception e){
                log.error("batchUpdate fail", e);
                dataSourceTransactionManager.rollback(transactionStatus);
            }
        }
    }


    @Override
    public Integer pullStockRealtime(String stockCode){
        TransactionStatus transactionStatus = dataSourceTransactionManager.getTransaction(transactionDefinition);
        try {
            String url = STOCK_REALTIME_URL + stockCode;
            String res = HttpClientPool.getHttpClient().get(url);
            if(StringUtils.isNotBlank(res)){
                List<String> fieldList = Arrays.asList(res.split("~"));

                StockInfoRealtimePo stockInfoRealtimePo = new StockInfoRealtimePo();
                stockInfoRealtimePo.setId(snowflakeIdWorker.nextId());
                stockInfoRealtimePo.setCreateTime(LocalDateTime.now());
                stockInfoRealtimePo.setCode(stockCode);
                stockInfoRealtimePo.setName(fieldList.get(1));
                stockInfoRealtimePo.setSymbol(fieldList.get(2));

                stockInfoRealtimePo.setVolume(new BigDecimal(fieldList.get(6)));
                stockInfoRealtimePo.setOpenPrice(new BigDecimal(fieldList.get(5)));
                stockInfoRealtimePo.setClosePrice(new BigDecimal(fieldList.get(3)));
                stockInfoRealtimePo.setHighPrice(new BigDecimal(fieldList.get(33)));
                stockInfoRealtimePo.setLowPrice(new BigDecimal(fieldList.get(34)));
                String updateDate = fieldList.get(30).substring(0,8);
                updateDate = updateDate.substring(0,4) + "-" + updateDate.substring(4,6) + "-" + updateDate.substring(6,8) + " 00:00:00";
                stockInfoRealtimePo.setUpdateDate(LocalDateTimeUtil.convertString2LocalDateTime(updateDate));
                stockInfoRealtimePo.setExchangeValue(new BigDecimal(fieldList.get(44)));
                stockInfoRealtimePo.setTotalValue(new BigDecimal(fieldList.get(45)));
                stockInfoRealtimePo.setPb(new BigDecimal(fieldList.get(46)));
                stockInfoRealtimePo.setPe(new BigDecimal(fieldList.get(53)));
                if(StringUtils.isNotBlank(fieldList.get(56))) {
                    stockInfoRealtimePo.setBeta(new BigDecimal(fieldList.get(56)));
                }
                if(StringUtils.isNotBlank(fieldList.get(57))) {
                    stockInfoRealtimePo.setTradeAmount(new BigDecimal(fieldList.get(57)));
                }

                stockInfoRealtimePo.setDynamicPe(new BigDecimal(fieldList.get(52)));
                stockInfoRealtimePo.setTtm(new BigDecimal(fieldList.get(39)));
                if(StringUtils.isNotBlank(fieldList.get(64))) {
                    stockInfoRealtimePo.setInterestRate(new BigDecimal(fieldList.get(64)));
                }
                stockInfoRealtimeMapper.delete(new LambdaUpdateWrapper<StockInfoRealtimePo>().eq(StockInfoRealtimePo::getCode, stockCode));
                stockInfoRealtimeMapper.insert(stockInfoRealtimePo);
                dataSourceTransactionManager.commit(transactionStatus);

            }

        }catch (Exception e){
            dataSourceTransactionManager.rollback(transactionStatus);
            log.error("pullStockRealtime fail",e);
            return 0;
        }

        return 1;
    }

    @Override
    public void calcAverageLine(String stockCode, boolean forceRefresh) {
        LocalDateTime startTime = LocalDateTime.now();
        try {
            List<StockQuotePo> updateQuotePoList = Lists.newArrayList();
            List<StockQuotePo> stockQuotePoList = stockQuoteMapper.selectListByCode(stockCode);
            LocalDateTime modifyTime = LocalDateTime.now();
            for (int j = 0; j < stockQuotePoList.size(); j++) {
                StockQuotePo po = stockQuotePoList.get(j);
                if (po.getAverage5() != null && !forceRefresh) {
                    continue;
                }
                BigDecimal average5 = getAverage(5, j, stockQuotePoList);
                BigDecimal average10 = getAverage(10, j, stockQuotePoList);
                BigDecimal average20 = getAverage(20, j, stockQuotePoList);
                BigDecimal average30 = getAverage(30, j, stockQuotePoList);
                BigDecimal average60 = getAverage(60, j, stockQuotePoList);
                BigDecimal average120 = getAverage(120, j, stockQuotePoList);
                BigDecimal average250 = getAverage(250, j, stockQuotePoList);

                StockQuotePo updatePo = new StockQuotePo();
                updatePo.setModifyTime(modifyTime);
                updatePo.setId(po.getId());
                updatePo.setAverage5(average5);
                updatePo.setAverage10(average10);
                updatePo.setAverage20(average20);
                updatePo.setAverage30(average30);
                updatePo.setAverage60(average60);
                updatePo.setAverage120(average120);
                updatePo.setAverage250(average250);
                updateQuotePoList.add(updatePo);
            }
            batchUpdate(updateQuotePoList);

        } catch (Exception e) {
            LocalDateTime endTime = LocalDateTime.now();
            long cost = (endTime.toInstant(ZoneOffset.of("+8")).toEpochMilli() - startTime.toInstant(ZoneOffset.of("+8")).toEpochMilli());
            log.info("计算{}均线失败,耗时{}ms", stockCode, cost);
        }
        LocalDateTime endTime = LocalDateTime.now();
        long cost = (endTime.toInstant(ZoneOffset.of("+8")).toEpochMilli() - startTime.toInstant(ZoneOffset.of("+8")).toEpochMilli());
        log.info("计算{}均线,耗时{}ms", stockCode, cost);

    }

    private BigDecimal getAverage(int days, int index, List<StockQuotePo> stockQuotePoList){
            int count = 0;
            BigDecimal sum = BigDecimal.ZERO;
            for(int i=index; i< stockQuotePoList.size() && i < index+days; i++){
                count++;
                StockQuotePo po = stockQuotePoList.get(i);
                sum = sum.add(po.getDrClosePrice());
            }
            return sum.divide(BigDecimal.valueOf(count), 2, BigDecimal.ROUND_HALF_UP);
    }
}
