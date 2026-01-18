package com.example.stocks.service;

import com.example.stocks.domain.Stock;
import com.example.stocks.domain.StockCategory;
import com.example.stocks.repo.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StockService {

    private final StockRepository stockRepo;

    public StockService(StockRepository stockRepo) {
        this.stockRepo = stockRepo;
    }

    public List<Stock> holdings() {
        return stockRepo.findByCategoryOrderByIdDesc(StockCategory.HOLDING);
    }

    public List<Stock> watch() {
        return stockRepo.findByCategoryOrderByIdDesc(StockCategory.WATCH);
    }

    public List<Stock> search(String q) {
        if (q == null || q.isBlank()) return List.of();
        return stockRepo.search(q.trim());
    }

    public Stock get(Long id) {
        return stockRepo.findById(id).orElseThrow();
    }

    @Transactional
    public Stock addOrUpdate(String input, String name, StockCategory category) {
        String trimmed = (input == null) ? "" : input.trim();
        if (trimmed.isEmpty()) throw new IllegalArgumentException("symbol/code is required");

        Stock normalized = normalize(trimmed);
        if (name != null && !name.isBlank()) {
            normalized.setName(name.trim());
        }
        normalized.setCategory(category == null ? StockCategory.WATCH : category);

        return stockRepo.findBySymbolIgnoreCase(normalized.getSymbol())
                .map(existing -> {
                    existing.setCategory(normalized.getCategory());
                    if (normalized.getCode() != null) existing.setCode(normalized.getCode());
                    if (normalized.getName() != null) existing.setName(normalized.getName());
                    return stockRepo.save(existing);
                })
                .orElseGet(() -> stockRepo.save(normalized));
    }

    @Transactional
    public void changeCategory(Long id, StockCategory category) {
        Stock s = stockRepo.findById(id).orElseThrow();
        s.setCategory(category);
        stockRepo.save(s);
    }

    @Transactional
    public void delete(Long id) {
        stockRepo.deleteById(id);
    }

    private static Stock normalize(String input) {
        Stock s = new Stock();

        // digits => JP stock code
        if (input.matches("^\\d{4,5}$")) {
            s.setCode(input);
            s.setSymbol(input + ".JP");
            return s;
        }

        s.setSymbol(input.toUpperCase());

        var m = java.util.regex.Pattern
                .compile("^(\\d{4,5})\\.(JP|T)$", java.util.regex.Pattern.CASE_INSENSITIVE)
                .matcher(input);
        if (m.find()) s.setCode(m.group(1));

        return s;
    }
}
