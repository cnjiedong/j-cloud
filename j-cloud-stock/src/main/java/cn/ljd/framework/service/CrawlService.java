package cn.ljd.framework.service;

import cn.ljd.framework.po.FhStockBasicPo;

import java.util.List;

public interface CrawlService {

    List<FhStockBasicPo> crawlBasicFromPhoenix();
}
