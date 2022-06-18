package cn.ljd.framework.sqlService;

import cn.ljd.framework.mapper.StockQuoteMapper;
import cn.ljd.framework.po.StockQuotePo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class StockQuoteSqlService  extends ServiceImpl<StockQuoteMapper, StockQuotePo> {
}
