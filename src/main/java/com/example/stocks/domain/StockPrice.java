package com.example.stocks.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stock_price",
       indexes = {
           @Index(name = "idx_stock_price_symbol_date", columnList = "symbol, date")
       })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockPrice {

    @EmbeddedId
    private StockPriceId id;

    // NOTE: symbol/date columns are defined by EmbeddedId; no duplicate mappings here.

    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private Long volume;
}
