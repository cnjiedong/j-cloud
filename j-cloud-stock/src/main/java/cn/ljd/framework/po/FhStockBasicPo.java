package cn.ljd.framework.po;

import cn.ljd.framework.utils.NumberUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;
import org.apache.commons.lang.StringUtils;

import java.time.LocalDateTime;

@Data
@ToString(callSuper = true)
@TableName(value = "fh_stock_basic", resultMap = "fhStockBasicMap")
public class FhStockBasicPo{

    @TableId(value = "id", type = IdType.INPUT)
    Long id;
    LocalDateTime createTime;
    LocalDateTime modifyTime;
    Integer idx;
    String code;
    String name;
    String sqCode;

    public FhStockBasicPo(){

    }

    public FhStockBasicPo(Integer idx, String code, String name){
         this.idx = idx;
         this.code = code;
         this.name = name;
         if(code.startsWith("6")){
             this.sqCode = "sh" + this.code;
         }else if (code.startsWith("0") || code.startsWith("3")){
            this.sqCode = "sz" + this.code;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FhStockBasicPo) {
            FhStockBasicPo po = (FhStockBasicPo) obj;
            return StringUtils.equals(code, po.code) && StringUtils.equals(name, po.name) && NumberUtil.equals(idx, po.idx);
        }
        return false;
    }
}
