package cn.ljd.framework.controller;

import cn.ljd.framework.constant.StockConstant;
import cn.ljd.framework.dto.ResultDTO;
import cn.ljd.framework.dto.req.StockPullReqDto;
import cn.ljd.framework.service.StockQuoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stock")
public class PullController {


	@Autowired
	StockQuoteService stockQuoteService;

	@PostMapping(value = "/pullStockQuote", produces = {"application/json"})
	public ResultDTO<Void> pull(@RequestBody StockPullReqDto reqDto){
		reqDto.setPullType(StockConstant.EXECUTE_TYPE_STOCK_QUOTE);
		return ResultDTO.ok(stockQuoteService.execute(reqDto));
	}

	@PostMapping(value = "/updatePreClosePrice", produces = {"application/json"})
	public ResultDTO<Void> updatePreClosePrice(){
		stockQuoteService.updatePreClosePrice();
		return ResultDTO.ok();
	}

	@PostMapping(value = "/qfq", produces = {"application/json"})
	public ResultDTO<Void> qfq(){
		stockQuoteService.qfq();
		return ResultDTO.ok();
	}

	@PostMapping(value = "/pullStockRealtime", produces = {"application/json"})
	public ResultDTO<Void> pullStockRealtime(@RequestBody StockPullReqDto reqDto){
		reqDto.setPullType(StockConstant.EXECUTE_TYPE_STOCK_REALTIME);
		stockQuoteService.execute(reqDto);
		return ResultDTO.ok();
	}

	@PostMapping(value = "/calcAverageLine", produces = {"application/json"})
	public ResultDTO<Void> calcAverageLine(@RequestBody StockPullReqDto reqDto){
		reqDto.setPullType(StockConstant.EXECUTE_CALCULATE_AVERAGE_LINE);
		stockQuoteService.execute(reqDto);
		return ResultDTO.ok();
	}

}
