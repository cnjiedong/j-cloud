package cn.ljd.framework.service;

import cn.ljd.framework.dto.req.StockPullReqDto;
import cn.ljd.framework.dto.TencentHisResDto;
import cn.ljd.framework.po.FailLogPo;

public interface StockQuoteService {

    Integer execute(StockPullReqDto reqDto);

    int save(String stockCode, String stockName, TencentHisResDto tencentHisResDto);

    void saveError(FailLogPo po);

    void deleteByStockCode(String stockCode);

    void updatePreClosePrice();

    void qfq();

    Integer pullStockRealtime(String stockCode);


    void calcAverageLine(String stockCode, boolean all);

}
