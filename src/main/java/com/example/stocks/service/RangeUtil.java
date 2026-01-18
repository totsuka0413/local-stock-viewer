package com.example.stocks.service;

import java.time.LocalDate;

public class RangeUtil {

    public static LocalDate fromDate(String range, LocalDate maxDate) {
        if (maxDate == null) maxDate = LocalDate.now();
        String r = (range == null) ? "1y" : range.toLowerCase();

        return switch (r) {
            case "1d" -> maxDate.minusDays(7);  // 営業日ズレ吸収
            case "5d" -> maxDate.minusDays(14);
            case "1m" -> maxDate.minusMonths(1);
            case "3m" -> maxDate.minusMonths(3);
            case "6m" -> maxDate.minusMonths(6);
            case "1y" -> maxDate.minusYears(1);
            case "5y" -> maxDate.minusYears(5);
            case "max" -> LocalDate.of(1900, 1, 1);
            default -> maxDate.minusYears(1);
        };
    }

    private RangeUtil() {}
}
