package com.example.stocks.web;

import java.util.List;

public record PriceSeriesResponse(String symbol, List<String> labels, List<Double> closes) {}
