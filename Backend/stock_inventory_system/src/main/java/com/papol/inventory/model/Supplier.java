package com.papol.inventory.model;

import jakarta.persistence.*;

// WHY: Supplier represents a vendor that provides products to the inventory.
//      Like Product and Category, it uses ENCAPSULATION — all fields are private
//      and accessed only through getters and setters.
// WHAT: Maps to the 'suppliers' table. Products link to suppliers via supplier_id.
// NOTE: Supplier contact info (phone, email, address) is optional — columns allow NULL.
//       This is a practical design choice: not all suppliers may have email addresses.

@Entity
@Table(name = "suppliers")
public class Supplier {

    // WHY: Private fields enforcing ENCAPSULATION — controlled access only.
    @Id
    @Column(name = "supplier_id")
    private String supplierId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "contact")
    private String contact;

    @Column(name = "email")
    private String email;

    @Column(name = "address")
    private String address;

    // WHY: JPA no-arg constructor requirement.
    public Supplier() {}

    // WHAT: Full constructor for programmatic creation.
    public Supplier(String supplierId, String name, String contact, String email, String address) {
        this.supplierId = supplierId;
        this.name       = name;
        this.contact    = contact;
        this.email      = email;
        this.address    = address;
    }

    // WHY: Getters and setters — Encapsulation access mechanism.
    public String getSupplierId()              { return supplierId; }
    public void   setSupplierId(String id)     { this.supplierId = id; }

    public String getName()                    { return name; }
    public void   setName(String n)            { this.name = n; }

    public String getContact()                 { return contact; }
    public void   setContact(String c)         { this.contact = c; }

    public String getEmail()                   { return email; }
    public void   setEmail(String e)           { this.email = e; }

    public String getAddress()                 { return address; }
    public void   setAddress(String a)         { this.address = a; }
}