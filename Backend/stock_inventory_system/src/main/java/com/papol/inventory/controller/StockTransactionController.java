package com.papol.inventory.controller;



import com.papol.inventory.model.StockTransaction;
import com.papol.inventory.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class StockTransactionController {

    @Autowired
    private StockService stockService;


    @GetMapping("/transactions")
    public List<StockTransaction> getAll() { return stockService.getAllTransactions(); }


    @PostMapping("/transactions")
    public ResponseEntity<StockTransaction> record(@RequestBody StockTransaction txn) {
        return ResponseEntity.status(201).body(stockService.recordTransaction(txn));
    }


    @PutMapping("/transactions/{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody StockTransaction txn) {
        try {
            return ResponseEntity.ok(stockService.updateTransaction(id, txn));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/transactions/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        stockService.deleteTransaction(id);
        return ResponseEntity.ok(Map.of("message","Deleted"));
    }
}