package com.papol.inventory.model;

import jakarta.persistence.*;
import java.time.LocalDate;



@Entity
@Table(name = "stock_transactions")
public class StockTransaction {


    @Id
    @Column(name = "transaction_id")
    private String transactionId;


    @Column(name = "product_id")
    private String productId;


    @Column(name = "type", nullable = false)
    private String type;    // 'IN' or 'OUT'

    @Column(name = "quantity", nullable = false)
    private int quantity;


    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "notes")
    private String notes;


    public StockTransaction() {}


    public StockTransaction(String transactionId, String productId, String type, int quantity, LocalDate date, String notes) {
        this.transactionId = transactionId;
        this.productId     = productId;
        this.type          = type;
        this.quantity      = quantity;
        this.date          = date;
        this.notes         = notes;
    }


    public String       getTransactionId()               { return transactionId; }
    public void         setTransactionId(String id)      { this.transactionId = id; }

    public String       getProductId()                   { return productId; }
    public void         setProductId(String p)           { this.productId = p; }

    public String       getType()                        { return type; }
    public void         setType(String t)                { this.type = t; }

    public int          getQuantity()                    { return quantity; }
    public void         setQuantity(int q)               { this.quantity = q; }

    public LocalDate    getDate()                        { return date; }
    public void         setDate(LocalDate d)             { this.date = d; }

    public String       getNotes()                       { return notes; }
    public void         setNotes(String n)               { this.notes = n; }
}