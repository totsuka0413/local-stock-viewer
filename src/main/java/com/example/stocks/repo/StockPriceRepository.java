package com.example.stocks.repo;

import com.example.stocks.domain.StockPrice;
import com.example.stocks.domain.StockPriceId;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockPriceRepository extends JpaRepository<StockPrice, StockPriceId> {

  @Query(
      """
        select p from StockPrice p
        where p.id.symbol = :symbol
          and p.id.date between :from and :to
        order by p.id.date asc
    """)
  List<StockPrice> findRange(
      @Param("symbol") String symbol, @Param("from") LocalDate from, @Param("to") LocalDate to);

  @Query(
      """
        select p from StockPrice p
        where p.id.symbol = :symbol
        order by p.id.date asc
    """)
  List<StockPrice> findAllBySymbol(@Param("symbol") String symbol);

  @Query("""
        select min(p.id.date) from StockPrice p where p.id.symbol = :symbol
    """)
  LocalDate minDate(@Param("symbol") String symbol);

  @Query("""
        select max(p.id.date) from StockPrice p where p.id.symbol = :symbol
    """)
  LocalDate maxDate(@Param("symbol") String symbol);
}
