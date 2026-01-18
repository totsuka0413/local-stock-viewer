package com.example.stocks.repo;

import com.example.stocks.domain.Stock;
import com.example.stocks.domain.StockCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StockRepository extends JpaRepository<Stock, Long> {

  Optional<Stock> findBySymbolIgnoreCase(String symbol);

  List<Stock> findByCategoryOrderByIdDesc(StockCategory category);

  @Query(
      """
        select s from Stock s
        where lower(s.symbol) like lower(concat('%', :q, '%'))
           or lower(coalesce(s.code,'')) like lower(concat('%', :q, '%'))
           or lower(coalesce(s.name,'')) like lower(concat('%', :q, '%'))
        order by s.id desc
    """)
  List<Stock> search(@Param("q") String q);
}
