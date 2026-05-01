package com.ralph.marketdatafeed.DataEngine;

import com.ralph.marketdatafeed.enums.Ticker;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class PriceEngine {

    private final ConcurrentHashMap<Ticker, BigDecimal> priceCache = new ConcurrentHashMap<>();
    private ScheduledExecutorService scheduledExecutorService;

    @PostConstruct
    public void startMarketDatafeed(){

        priceCache.put(Ticker.BTCUSDT,new BigDecimal("65000.00"));

        //create
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();


        scheduledExecutorService.scheduleAtFixedRate(() -> {
            BigDecimal currentPrice = priceCache.get(Ticker.BTCUSDT);

            // simulate price change +5 -5
            double jitter = (Math.random() * 10) - 5;
            BigDecimal newPrice = currentPrice.add(BigDecimal.valueOf(jitter))
                    .setScale(2, RoundingMode.HALF_UP);

            priceCache.put(Ticker.BTCUSDT, newPrice);

        }, 0, 50, TimeUnit.MILLISECONDS);
    }


    @PreDestroy
    public void stopMarketDatafeed() {
        if (scheduledExecutorService != null && !scheduledExecutorService.isShutdown()) {
            scheduledExecutorService.shutdown();
        }
    }

    public BigDecimal getLatestPrice(Ticker ticker) {
        return priceCache.getOrDefault(ticker, BigDecimal.ZERO);
    }
}
