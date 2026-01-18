package com.example.stocks.web;

import com.example.stocks.service.StockPriceService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class StockApiController {

    private final StockPriceService priceService;

    public StockApiController(StockPriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping("/stocks/{symbol}/prices")
    public PriceSeriesResponse prices(@PathVariable String symbol,
                                      @RequestParam(value = "range", defaultValue = "1y") String range) {
        var list = priceService.loadForRange(symbol, range);
        var labels = list.stream().map(p -> p.getId().getDate().toString()).toList();
        var closes = list.stream().map(p -> p.getClose()).toList();
        return new PriceSeriesResponse(symbol, labels, closes);
    }
}
