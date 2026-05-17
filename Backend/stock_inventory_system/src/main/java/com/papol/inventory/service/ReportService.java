package com.papol.inventory.service;


import com.papol.inventory.model.*;
import com.papol.inventory.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ReportService {


    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StockTransactionRepository transactionRepository;

    @Autowired
    private ReportRepository reportRepository;

    public List<Product> getLowStockProducts() {
        return productRepository.findByQuantityLessThan(5);
    }


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

    public List<StockTransaction> getStockMovement() {
        return transactionRepository.findAll();
    }

    public Report saveReport(Report r)   { return reportRepository.save(r); }

    public void deleteReport(String id)  { reportRepository.deleteById(id); }
}