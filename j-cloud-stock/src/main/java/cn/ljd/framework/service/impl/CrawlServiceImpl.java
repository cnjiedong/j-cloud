package cn.ljd.framework.service.impl;

import cn.ljd.framework.po.FhStockBasicPo;
import cn.ljd.framework.service.CrawlService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CrawlServiceImpl implements CrawlService {

    public List<FhStockBasicPo> crawlBasicFromPhoenix(){
        return PhoenixCrawler.craw();
    }
}
