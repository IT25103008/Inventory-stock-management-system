package com.papol.inventory.controller;


import com.papol.inventory.model.*;
import com.papol.inventory.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class ReportController {

    @Autowired
    private ReportService reportService;

    @GetMapping("/reports/low-stock")
    public List<Product> getLowStock() { return reportService.getLowStockProducts(); }


    @GetMapping("/reports/inventory-value")
    public Map<String, Object> getInventoryValue() { return reportService.getInventoryValue(); }

    @GetMapping("/reports/stock-movement")
    public List<StockTransaction> getMovement() { return reportService.getStockMovement(); }

    @PostMapping("/reports")
    public ResponseEntity<Report> save(@RequestBody Report r) {
        return ResponseEntity.status(201).body(reportService.saveReport(r));
    }

    @DeleteMapping("/reports/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        reportService.deleteReport(id);
        return ResponseEntity.ok(Map.of("message","Deleted"));
    }
}