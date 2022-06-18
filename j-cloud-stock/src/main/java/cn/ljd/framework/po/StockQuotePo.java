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
@TableName(value = "stock_quote", resultMap = "stockQuoteMap")
public class StockQuotePo {

    @TableId(value = "id", type = IdType.INPUT)
    Long id;
    LocalDateTime createTime;
    LocalDateTime modifyTime;
    String code;
    String name;
    String symbol;
    LocalDateTime quoteDate;
    BigDecimal openPrice;
    BigDecimal closePrice;
    BigDecimal highPrice;
    BigDecimal lowPrice;
    BigDecimal preClosePrice;
    BigDecimal volume;
    BigDecimal rise;
    BigDecimal diff;
    String dr;
    BigDecimal drRate;
    BigDecimal drClosePrice;

    BigDecimal average5;
    BigDecimal average10;
    BigDecimal average20;
    BigDecimal average30;
    BigDecimal average60;
    BigDecimal average120;
    BigDecimal average250;
}
