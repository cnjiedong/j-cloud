package cn.ljd.framework.service.impl;

import cn.ljd.framework.dto.res.SyncResDto;
import cn.ljd.framework.mapper.FhStockBasicMapper;
import cn.ljd.framework.po.FhStockBasicPo;
import cn.ljd.framework.po.StockBasicPo;
import cn.ljd.framework.po.StockQuotePo;
import cn.ljd.framework.service.CrawlService;
import cn.ljd.framework.service.SyncStockInfoService;
import cn.ljd.framework.sqlService.FhStockBasicSqlService;
import cn.ljd.framework.sqlService.StockBasicSqlService;
import cn.ljd.framework.sqlService.StockQuoteSqlService;
import cn.ljd.framework.utils.SnowflakeIdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 股票信息同步服务
 *
 * @author: Liang Jiedong
 * @email: jiedong008@gmail.com
 */

@Service
public class SyncStockInfoServiceImpl implements SyncStockInfoService {

    @Autowired
    CrawlService crawlService;
    @Autowired
    FhStockBasicSqlService fhStockBasicSqlService;
    @Autowired
    StockBasicSqlService stockBasicSqlService;
    @Autowired
    StockQuoteSqlService stockQuoteSqlService;

    @Override
    public SyncResDto syncFromPhoenix(){
        SyncResDto resDto = new SyncResDto();
        List<FhStockBasicPo> fhStockBasicPoList = crawlService.crawlBasicFromPhoenix();
        if(CollectionUtils.isEmpty(fhStockBasicPoList)){
            return resDto;
        }
        List<FhStockBasicPo> existsPoList = fhStockBasicSqlService.list();
        Map<String, FhStockBasicPo> poMap = existsPoList.stream().collect(Collectors.toMap(FhStockBasicPo::getCode, Function.identity()));
        List<FhStockBasicPo> insertPoList = Lists.newArrayList();
        List<FhStockBasicPo> updatePoList = Lists.newArrayList();
        LocalDateTime syncTime = LocalDateTime.now();
        for(FhStockBasicPo po : fhStockBasicPoList){
             FhStockBasicPo existPo = poMap.get(po.getCode());
             if(existPo == null){
                 po.setId(SnowflakeIdUtil.nextId());
                 po.setCreateTime(syncTime);
                 insertPoList.add(po);
             }else if(!po.equals(existPo)){
                 existPo.setIdx(po.getIdx());
                 existPo.setName(po.getName());
                 existPo.setModifyTime(syncTime);
                 updatePoList.add(existPo);
             }
        }

        if(CollectionUtils.isNotEmpty(insertPoList)){
            fhStockBasicSqlService.saveBatch(insertPoList);
            convertAndSaveFhToBasic(insertPoList);
        }
        if(CollectionUtils.isNotEmpty(updatePoList)){
            fhStockBasicSqlService.updateBatchById(updatePoList);
        }

        resDto.setInsert(insertPoList.size());
        resDto.setUpdate(updatePoList.size());
        resDto.setDelete(0);
        return resDto;
    }

    private void convertAndSaveFhToBasic(List<FhStockBasicPo> insertPoList){
        List<StockBasicPo> basicPos = Lists.newArrayList();
        for(FhStockBasicPo fhPo : insertPoList){
            StockBasicPo basicPo = new StockBasicPo();
            basicPo.setTsCode(fhPo.getSqCode());
            basicPo.setSymbol(fhPo.getCode());
            basicPo.setName(fhPo.getName());
            basicPo.setSqCode(fhPo.getSqCode());
            basicPos.add(basicPo);
        }

        stockBasicSqlService.saveBatch(basicPos);
    }
}
