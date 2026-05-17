package com.papol.inventory.controller;

// WHY: ReportController provides READ-ONLY analytics endpoints plus report metadata CRUD.
//      It delegates all computation to ReportService (ABSTRACTION).
// WHAT: Endpoints for low-stock alerts, inventory valuation, and stock movement history.

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

    // WHAT: GET /reports/low-stock — returns products with quantity below 5.
    @GetMapping("/reports/low-stock")
    public List<Product> getLowStock() { return reportService.getLowStockProducts(); }

    // WHAT: GET /reports/inventory-value — returns all products + their total value.
    //       Response shape: { "products": [...], "totalValue": 12345.00 }
    @GetMapping("/reports/inventory-value")
    public Map<String, Object> getInventoryValue() { return reportService.getInventoryValue(); }

    // WHAT: GET /reports/stock-movement — returns full transaction history.
    @GetMapping("/reports/stock-movement")
    public List<StockTransaction> getMovement() { return reportService.getStockMovement(); }

    // WHAT: POST /reports — saves report metadata. Returns HTTP 201.
    @PostMapping("/reports")
    public ResponseEntity<Report> save(@RequestBody Report r) {
        return ResponseEntity.status(201).body(reportService.saveReport(r));
    }

    // WHAT: DELETE /reports/{id} — removes a report metadata record.
    @DeleteMapping("/reports/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        reportService.deleteReport(id);
        return ResponseEntity.ok(Map.of("message","Deleted"));
    }
}