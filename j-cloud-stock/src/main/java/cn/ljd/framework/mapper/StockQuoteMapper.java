package cn.ljd.framework.mapper;

import cn.ljd.framework.po.StockQuotePo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StockQuoteMapper extends BaseMapper<StockQuotePo> {

    @Select({"<script>" +
            "select * from stock_quote sq" +
            " WHERE " +
            " code = #{code} order by quote_date desc " +
            "</script>"})
    List<StockQuotePo> selectListByCode(@Param("code") String code);

    @Select({"<script>" +
            "select * from stock_quote sq" +
            " WHERE " +
            " code = #{code} order by quote_date desc limit #{limit}" +
            "</script>"})
    List<StockQuotePo> selectListByCodeLimit(@Param("code") String code, @Param("limit") String limit);


    @Delete({"<script>" +
            " delete from stock_quote sq" +
            " WHERE " +
            " code = #{code}" +
            "</script>"})
    void deleteByCode(@Param("code") String code);

    @Update({"<script>" +
            " update stock_quote t1 set pre_close_price = (select close_price from stock_quote t2 where t2.code = t1.code and t2.quote_date <![CDATA[ < ]]> t1.quote_date " +
            " order by quote_date desc limit 1) " +
            " where  pre_close_price is null " +
            " and " +
            " code = #{code}" +
            "</script>"})
    void updatePreClosePrice(@Param("code") String code);

    @Delete({"<script>" +
            " update stock_quote t1 set rise = (close_price-pre_close_price)*100/pre_close_price " +
            " where  pre_close_price is not null and rise is null" +
            " and " +
            " code = #{code}" +
            "</script>"})
    void updateRise(@Param("code") String code);

    @Select({"<script>" +
            "select * from stock_quote sq" +
            " WHERE " +
            " code = #{code} and dr is not null order by quote_date desc " +
            "</script>"})
    List<StockQuotePo> selectDrListByCode(@Param("code") String code);
}
