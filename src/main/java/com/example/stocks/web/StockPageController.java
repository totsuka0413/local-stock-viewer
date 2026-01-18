package com.example.stocks.web;

import com.example.stocks.domain.Stock;
import com.example.stocks.domain.StockCategory;
import com.example.stocks.service.StockPriceService;
import com.example.stocks.service.StockService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class StockPageController {

    private final StockService stockService;
    private final StockPriceService priceService;

    public StockPageController(StockService stockService, StockPriceService priceService) {
        this.stockService = stockService;
        this.priceService = priceService;
    }

    @GetMapping("/")
    public String root() {
        return "redirect:/stocks";
    }

    @GetMapping("/stocks")
    public String stocks(@RequestParam(value = "q", required = false) String q, Model model,
                         @ModelAttribute("msg") String msg) {
        model.addAttribute("holdings", stockService.holdings());
        model.addAttribute("watch", stockService.watch());
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("results", stockService.search(q));
        model.addAttribute("categories", StockCategory.values());
        return "stocks/index";
    }

    @PostMapping("/stocks/add")
    public String add(@RequestParam("input") String input,
                      @RequestParam(value = "name", required = false) String name,
                      @RequestParam("category") StockCategory category,
                      RedirectAttributes ra) {
        Stock s = stockService.addOrUpdate(input, name, category);
        ra.addFlashAttribute("msg", "追加/更新: " + s.getSymbol() + " (" + s.getCategory() + ")");
        return "redirect:/stocks";
    }

    @PostMapping("/stocks/{id}/category")
    public String changeCategory(@PathVariable Long id,
                                 @RequestParam("category") StockCategory category,
                                 RedirectAttributes ra) {
        stockService.changeCategory(id, category);
        ra.addFlashAttribute("msg", "区分変更: " + id + " -> " + category);
        return "redirect:/stocks";
    }

    @PostMapping("/stocks/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes ra) {
        stockService.delete(id);
        ra.addFlashAttribute("msg", "削除: " + id);
        return "redirect:/stocks";
    }

    @GetMapping("/stocks/{id}")
    public String detail(@PathVariable Long id,
                         @RequestParam(value = "range", required = false, defaultValue = "1y") String range,
                         Model model) {
        Stock s = stockService.get(id);
        model.addAttribute("stock", s);
        model.addAttribute("range", range);
        model.addAttribute("ranges", List.of("1d","5d","1m","3m","6m","1y","5y","max"));
        return "stocks/detail";
    }

    @PostMapping("/stocks/{id}/refresh")
    public String refresh(@PathVariable Long id,
                          @RequestParam(value = "range", required = false, defaultValue = "1y") String range,
                          RedirectAttributes ra) {
        Stock s = stockService.get(id);
        int count = priceService.refreshLast5Years(s.getSymbol());
        ra.addFlashAttribute("msg", "価格更新: " + s.getSymbol() + " (" + count + "件)");
        return "redirect:/stocks/" + id + "?range=" + range;
    }
}
