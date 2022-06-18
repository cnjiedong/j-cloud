package cn.ljd.framework.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ToString(callSuper = true)
@TableName(value = "stock_info_realtime", resultMap = "stockInfoRealtimeMap")
public class StockInfoRealtimePo {

    @TableId(value = "id", type = IdType.INPUT)
    Long id;
    LocalDateTime createTime;
    LocalDateTime modifyTime;
    String code;
    String name;
    String symbol;
    LocalDateTime updateDate;
    BigDecimal openPrice;
    BigDecimal closePrice;
    BigDecimal highPrice;
    BigDecimal lowPrice;
    BigDecimal volume;
    BigDecimal tradeAmount;
    BigDecimal totalValue;
    BigDecimal exchangeValue;
    BigDecimal pe;
    BigDecimal pb;

    BigDecimal dynamicPe;
    BigDecimal ttm;
    //股息率
    BigDecimal interestRate;

    BigDecimal beta;
}
