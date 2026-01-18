package com.example.stocks.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class StooqClient {

  private final HttpClient httpClient = HttpClient.newHttpClient();
  private final String baseUrl;

  public StooqClient(@Value("${app.stooq.base-url:https://stooq.com}") String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String downloadDailyCsv(String symbol, LocalDate from, LocalDate to)
      throws IOException, InterruptedException {
    // Endpoint: /q/d/l/?s=...&d1=YYYYMMDD&d2=YYYYMMDD&i=d
    DateTimeFormatter fmt = DateTimeFormatter.BASIC_ISO_DATE;
    String s = symbol.toLowerCase();

    String url =
        baseUrl
            + "/q/d/l/?s="
            + encodeSymbol(s)
            + "&d1="
            + fmt.format(from)
            + "&d2="
            + fmt.format(to)
            + "&i=d";

    HttpRequest req =
        HttpRequest.newBuilder()
            .uri(URI.create(url))
            .GET()
            .header("User-Agent", "local-stock-viewer/1.0")
            .build();

    HttpResponse<String> resp = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
    if (resp.statusCode() != 200) {
      throw new IOException(
          "Stooq CSV download failed: status=" + resp.statusCode() + " url=" + url);
    }
    return resp.body();
  }

  private static String encodeSymbol(String s) {
    // minimal encoding for symbols like ^spx
    return s.replace("^", "%5E");
  }
}
