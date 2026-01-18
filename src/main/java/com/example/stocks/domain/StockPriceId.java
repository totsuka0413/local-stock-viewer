package com.example.stocks.domain;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import lombok.*;

@Embeddable
@Getter
@Setter
@Builder
public class StockPriceId implements Serializable {

  private String symbol;
  private LocalDate date;

  public StockPriceId() {}

  public StockPriceId(String symbol, LocalDate date) {
    this.symbol = symbol;
    this.date = date;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    StockPriceId that = (StockPriceId) o;
    return Objects.equals(symbol, that.symbol) && Objects.equals(date, that.date);
  }

  @Override
  public int hashCode() {
    return Objects.hash(symbol, date);
  }
}
