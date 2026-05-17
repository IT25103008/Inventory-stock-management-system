package com.papol.inventory.service;

// WHY: ReportService demonstrates ABSTRACTION — it hides complex computation logic
//      (low-stock filtering, inventory value calculation) behind simple method calls.
//      The controller just calls getLowStockProducts() without knowing the threshold
//      or the SQL query behind it.
// WHAT: Provides read-only analytics: low-stock alerts, inventory valuation,
//       and stock movement history. Also manages report metadata CRUD.

import com.papol.inventory.model.*;
import com.papol.inventory.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ReportService {

    // WHY: Three repositories injected — reports draw data from products,
    //      transactions, AND the reports metadata table. This shows that a
    //      service can coordinate across multiple data sources.
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockTransactionRepository transactionRepository;

    @Autowired
    private ReportRepository reportRepository;

    // WHY: Uses the custom repository method findByQuantityLessThan(5).
    //      The threshold of 5 is a business rule — products with fewer than
    //      5 units in stock are considered "low stock" and need attention.
    // WHAT: Returns a list of products that are below the low-stock threshold.
    public List<Product> getLowStockProducts() {
        return productRepository.findByQuantityLessThan(5);
    }

    // WHY: Calculates the total monetary value of all inventory.
    //      Uses Java Streams — a functional programming technique — to multiply
    //      price × quantity for each product and sum the results.
    // WHAT: Returns a Map with two keys: "products" (full list) and "totalValue" (the sum).
    // NOTE: The Map<String, Object> return type is flexible but not type-safe.
    //       In a larger project, you'd create a dedicated DTO (Data Transfer Object) class.
    public Map<String, Object> getInventoryValue() {
        List<Product> products = productRepository.findAll();
        double totalValue = products.stream()
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();
        Map<String, Object> result = new HashMap<>();
        result.put("products", products);
        result.put("totalValue", totalValue);
        return result;
    }

    // WHAT: Returns ALL stock transactions — used to show full movement history.
    public List<StockTransaction> getStockMovement() {
        return transactionRepository.findAll();
    }

    // WHAT: Saves report metadata (who generated it, when, what type).
    public Report saveReport(Report r)   { return reportRepository.save(r); }

    // WHAT: Deletes a report metadata record by ID.
    public void deleteReport(String id)  { reportRepository.deleteById(id); }
}