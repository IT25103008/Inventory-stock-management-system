package com.papol.inventory.model;

import jakarta.persistence.*;

// WHY: Product is a standalone entity that represents a single item in the inventory.
//      Unlike Employee, it does not participate in any inheritance hierarchy — it is
//      a simple domain object. This shows that not every class needs inheritance;
//      ENCAPSULATION alone is sufficient when there are no subtypes.
// WHAT: Maps to the 'products' table in MySQL. Each row = one product.
// NOTE: Products reference categories and suppliers by their String IDs (category_id,
//       supplier_id) rather than using JPA @ManyToOne relationships. This keeps the
//       model simple for a Year 1 project while still maintaining logical links.

@Entity
@Table(name = "products")
public class Product {

    // WHY: All fields are private — ENCAPSULATION. External code cannot directly
    //      set productId or price to invalid values; it must go through setters.
    @Id
    @Column(name = "product_id")
    private String productId;

    @Column(name = "name", nullable = false)
    private String name;

    // NOTE: categoryId and supplierId are stored as plain Strings, not as JPA
    //       relationships. This is a deliberate simplification — in a production
    //       system you would use @ManyToOne with a Category/Supplier reference.
    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "supplier_id")
    private String supplierId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "price", nullable = false)
    private double price;

    // WHY: No-arg constructor required by JPA to create instances from DB rows.
    public Product() {}

    // WHAT: Full constructor for creating a product programmatically.
    public Product(String productId, String name, String categoryId, String supplierId, int quantity, double price) {
        this.productId  = productId;
        this.name       = name;
        this.categoryId = categoryId;
        this.supplierId = supplierId;
        this.quantity   = quantity;
        this.price      = price;
    }

    // WHY: Getters and setters enforce ENCAPSULATION — controlled access to private fields.
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
}