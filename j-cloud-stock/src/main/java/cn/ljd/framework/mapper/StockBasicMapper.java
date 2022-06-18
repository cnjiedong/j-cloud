package cn.ljd.framework.mapper;


import cn.ljd.framework.po.StockBasicPo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface StockBasicMapper  extends BaseMapper<StockBasicPo> {

    @Select({"<script>" +
            "select * from stock_basic " +
            "</script>"})
    List<StockBasicPo> selectAll();

    @Select({"<script>" +
            "select ts_code from stock_basic " +
            "</script>"})
    List<String> selectAllCodes();

    @Select({"<script>" +
            "select ts_code from stock_basic t1 where not exists(select 1 from stock_quote t2 where t2.name = t1.name) " +
            "</script>"})
    List<String> selectNotPullCodes();

    @Select({"<script>" +
            "select sq_code from stock_basic " +
            "</script>"})
    List<String> selectAllSqCodes();
}
