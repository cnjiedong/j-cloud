package cn.ljd.framework.schedule;

import cn.ljd.framework.constant.StockConstant;
import cn.ljd.framework.dto.req.StockPullReqDto;
import cn.ljd.framework.service.StockQuoteService;
import cn.ljd.framework.utils.LocalDateTimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Slf4j
@Component
public class SyncTask {

    @Autowired
    StockQuoteService stockQuoteService;

    @Scheduled(cron = "0 0 17 * * ? ")
    public void pullStockQuoteTask(){
        String quoteDate = LocalDateTimeUtil.convertLocalDateTime2DateString(LocalDateTime.now());
        log.info("start schedule task : sync stockQuote[{}]",quoteDate);
        if(LocalDateTimeUtil.isHoliday()){
            log.info("end schedule task : sync stockQuote[{}], no deal for holiday",quoteDate);
            return ;
        }

        StockPullReqDto reqDto = new StockPullReqDto();
        reqDto.setQuoteDate(quoteDate);
        reqDto.setPullType(StockConstant.EXECUTE_TYPE_STOCK_QUOTE);
        try {
            stockQuoteService.execute(reqDto);
        }catch (Exception e){
            log.error("fail schedule task : sync stockQuote[{}]",quoteDate);
        }
        log.info("end schedule task : sync stockQuote[{}]",quoteDate);
    }

    @Scheduled(cron = "0 20 17 * * ? ")
    public void pullStockRealtimeTask(){
        String quoteDate = LocalDateTimeUtil.convertLocalDateTime2DateString(LocalDateTime.now());
        log.info("start schedule task : sync stockRealtime[{}]",quoteDate);
        if(LocalDateTimeUtil.isHoliday()){
            log.info("end schedule task : sync stockRealtime[{}], no deal for holiday",quoteDate);
            return ;
        }
        StockPullReqDto reqDto = new StockPullReqDto();
        reqDto.setQuoteDate(quoteDate);
        reqDto.setPullType(StockConstant.EXECUTE_TYPE_STOCK_REALTIME);
        try {
            stockQuoteService.execute(reqDto);
        }catch (Exception e){
            log.error("fail schedule task : sync stockRealtime[{}]",quoteDate);
        }
        log.info("end schedule task : sync stockRealtime[{}]",quoteDate);
    }

    @Scheduled(cron = "0 40 17 * * ? ")
    public void calAverageLineTask(){
        String quoteDate = LocalDateTimeUtil.convertLocalDateTime2DateString(LocalDateTime.now());
        log.info("start schedule task : sync averageLine[{}]",quoteDate);
        if(LocalDateTimeUtil.isHoliday()){
            log.info("end schedule task : sync averageLine[{}], no deal for holiday",quoteDate);
            return ;
        }
        StockPullReqDto reqDto = new StockPullReqDto();
        reqDto.setQuoteDate(quoteDate);
        reqDto.setPullType(StockConstant.EXECUTE_CALCULATE_AVERAGE_LINE);
        try {
            stockQuoteService.execute(reqDto);
        }catch (Exception e){
            log.error("fail schedule task : sync averageLine[{}]",quoteDate);
        }
        log.info("end schedule task : sync averageLine[{}]",quoteDate);
    }
}
