package com.papol.inventory.service;



import com.papol.inventory.model.StockTransaction;
import com.papol.inventory.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class StockService {

    @Autowired
    private StockTransactionRepository transactionRepository;

    @Autowired
    private ProductRepository productRepository;


    public List<StockTransaction> getAllTransactions() { return transactionRepository.findAll(); }

    public StockTransaction recordTransaction(StockTransaction txn) {

        productRepository.findById(txn.getProductId()).ifPresent(product -> {
            if ("IN".equalsIgnoreCase(txn.getType())) {
                product.setQuantity(product.getQuantity() + txn.getQuantity());
            } else if ("OUT".equalsIgnoreCase(txn.getType())) {
                product.setQuantity(product.getQuantity() - txn.getQuantity());
            }
            productRepository.save(product);
        });
        return transactionRepository.save(txn);
    }

    public StockTransaction updateTransaction(String id, StockTransaction updated) {
        StockTransaction existing = transactionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        existing.setNotes(updated.getNotes());
        existing.setDate(updated.getDate());
        return transactionRepository.save(existing);
    }


    public void deleteTransaction(String id) { transactionRepository.deleteById(id); }
}