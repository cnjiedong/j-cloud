package cn.ljd.framework.po;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@ToString(callSuper = true)
@TableName(value = "fail_log", resultMap = "failLogMap")
public class FailLogPo {

    Long id;
    LocalDateTime createTime;
    LocalDateTime modifyTime;
    String code;
    String name;
    String operateType;
    String pullYear;
    String status;
    String errMsg;
}
