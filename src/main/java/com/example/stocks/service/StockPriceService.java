package com.example.stocks.service;

import com.example.stocks.domain.StockPrice;
import com.example.stocks.domain.StockPriceId;
import com.example.stocks.repo.StockPriceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class StockPriceService {

    private final StooqClient stooqClient;
    private final StockPriceRepository priceRepo;

    public StockPriceService(StooqClient stooqClient, StockPriceRepository priceRepo) {
        this.stooqClient = stooqClient;
        this.priceRepo = priceRepo;
    }

    @Transactional
    public int refreshLast5Years(String symbol) {
        try {
            LocalDate to = LocalDate.now();
            LocalDate from = to.minusYears(5);
            String csv = stooqClient.downloadDailyCsv(symbol, from, to);
            List<StockPrice> parsed = parseStooqCsv(symbol, csv);
            priceRepo.saveAll(parsed); // merge by embedded id
            return parsed.size();
        } catch (Exception e) {
            throw new RuntimeException("Failed to refresh prices for " + symbol + ": " + e.getMessage(), e);
        }
    }

    public List<StockPrice> loadForRange(String symbol, String range) {
        LocalDate max = priceRepo.maxDate(symbol);
        if (max == null) return List.of();

        if ("max".equalsIgnoreCase(range)) {
            return priceRepo.findAllBySymbol(symbol);
        }

        LocalDate from = RangeUtil.fromDate(range, max);
        return priceRepo.findRange(symbol, from, max);
    }

    private static List<StockPrice> parseStooqCsv(String symbol, String csv) throws IOException {
        // Expected header: Date,Open,High,Low,Close,Volume
        List<StockPrice> out = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new StringReader(csv))) {
            String header = br.readLine();
            if (header == null) return out;

            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = line.split(",");
                if (parts.length < 6) continue;

                LocalDate date = LocalDate.parse(parts[0]);
                Double open = parseDouble(parts[1]);
                Double high = parseDouble(parts[2]);
                Double low = parseDouble(parts[3]);
                Double close = parseDouble(parts[4]);
                Long volume = parseLong(parts[5]);

                if (close == null) continue;

                StockPrice p = new StockPrice();
                p.setId(new StockPriceId(symbol, date));
                p.setOpen(open);
                p.setHigh(high);
                p.setLow(low);
                p.setClose(close);
                p.setVolume(volume);
                out.add(p);
            }
        }
        return out;
    }

    private static Double parseDouble(String s) {
        try {
            if (s == null || s.isBlank()) return null;
            return Double.parseDouble(s);
        } catch (Exception e) {
            return null;
        }
    }

    private static Long parseLong(String s) {
        try {
            if (s == null || s.isBlank()) return null;
            return Long.parseLong(s);
        } catch (Exception e) {
            return null;
        }
    }
}
