package com.papol.inventory.model;

import jakarta.persistence.*;


@Entity
@Table(name = "suppliers")
public class Supplier {

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

   
    
    public Supplier() {}

   
    public Supplier(String supplierId, String name, String contact, String email, String address) {
        this.supplierId = supplierId;
        this.name       = name;
        this.contact    = contact;
        this.email      = email;
        this.address    = address;
    }

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
