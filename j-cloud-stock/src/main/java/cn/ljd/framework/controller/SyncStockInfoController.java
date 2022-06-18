package cn.ljd.framework.controller;

import cn.ljd.framework.dto.ResultDTO;
import cn.ljd.framework.service.SyncStockInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sync")
public class SyncStockInfoController {

    @Autowired
    SyncStockInfoService syncStockInfoService;

    @PostMapping(value = "/phoenixBasic", produces = {"application/json"})
    public ResultDTO<Void> phoenixBasic(){
        return ResultDTO.ok(syncStockInfoService.syncFromPhoenix());
    }
}
