package com.papol.inventory.model;

import jakarta.persistence.*;



@Entity
@Table(name = "products")
public class Product {

    
    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(name = "name", nullable = false)
    private String name;

    
    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "supplier_id")
    private String supplierId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price", nullable = false)
    private double price;

    
    public Product() {}

    
    public Product(String productId, String name, String categoryId, String supplierId, int quantity, double price) {
        this.productId  = productId;
        this.name       = name;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
        this.quantity   = quantity;
        this.price      = price;
    }

    
    public String getProductId()               { return productId; }
    public void   setProductId(String id)      { this.productId = id; }

    public String getName()                    { return name; }
    public void   setName(String n)            { this.name = n; }

    public String getCategoryId()              { return categoryId; }
    public void   setCategoryId(String c)      { this.categoryId = c; }

    public String getSupplierId()              { return supplierId; }
    public void   setSupplierId(String s)      { this.supplierId = s; }

    public int    getQuantity()                { return quantity; }
    public void   setQuantity(int q)           { this.quantity = q; }

    public double getPrice()                   { return price; }
    public void   setPrice(double p)           { this.price = p; }

    //PRODUCT.JAVA
}
