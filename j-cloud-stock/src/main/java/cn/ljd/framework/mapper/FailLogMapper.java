package cn.ljd.framework.mapper;

import cn.ljd.framework.po.FailLogPo;
import cn.ljd.framework.po.StockQuotePo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FailLogMapper extends BaseMapper<FailLogPo> {

}
