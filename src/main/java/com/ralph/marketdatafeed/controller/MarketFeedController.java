package com.ralph.marketdatafeed.controller;

import com.ralph.marketdatafeed.DataEngine.PriceEngine;
import com.ralph.marketdatafeed.pojos.MarketResponse;
import com.ralph.marketdatafeed.enums.Ticker;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/marketfeed")
public class MarketFeedController {

    private final PriceEngine priceEngine;

    public  MarketFeedController(PriceEngine priceEngine) {
        this.priceEngine = priceEngine;
    }

    @GetMapping("/{ticker}")
    public ResponseEntity<MarketResponse> getMarketData(@PathVariable Ticker ticker){

        return ResponseEntity
                .ok(MarketResponse
                        .builder()
                        .price(priceEngine.getLatestPrice(ticker))
                        .build());
    }

}
