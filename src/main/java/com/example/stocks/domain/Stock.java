package com.example.stocks.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Entity
@Table(
    name = "stock",
    indexes = {
      @Index(name = "idx_stock_symbol", columnList = "symbol"),
      @Index(name = "idx_stock_code", columnList = "code"),
      @Index(name = "idx_stock_name", columnList = "name"),
      @Index(name = "idx_stock_category", columnList = "category")
    })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  @Column(nullable = false, unique = true, length = 32)
  private String symbol; // 3543.JP, ^SPX

  @Column(length = 16)
  private String code; // 3543

  @Column(length = 128)
  private String name; // コメダHD

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  private StockCategory category = StockCategory.WATCH;
}
