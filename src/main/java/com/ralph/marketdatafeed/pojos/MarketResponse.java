package com.ralph.marketdatafeed.pojos;

import java.math.BigDecimal;


public record MarketResponse(BigDecimal price) {
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private BigDecimal price;


        public Builder price(BigDecimal price) {
            this.price = price;
            return this;
        }

        public MarketResponse build() {
            return new MarketResponse(price);
        }
    }
}
