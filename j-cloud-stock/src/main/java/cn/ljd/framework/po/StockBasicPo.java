package cn.ljd.framework.po;


import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

@Data
@ToString(callSuper = true)
@TableName(value = "stock_basic", resultMap = "stockBasicMap")
public class StockBasicPo {

    String tsCode;
    String symbol;
    String name;
    String area;
    String industry;
    String market;
    String listDate;
    String sqCode;
}
