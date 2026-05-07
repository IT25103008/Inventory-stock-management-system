package com.papol.inventory.model;

import jakarta.persistence.*;


@Entity
@Table(name = "categories")
public class Category {

    
    @Id
    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    
    public Category() {}

    
    public Category(String categoryId, String name, String description) {
        this.categoryId  = categoryId;
        this.name        = name;
        this.description = description;
    }

    
    public String getCategoryId()              { return categoryId; }
    public void   setCategoryId(String id)     { this.categoryId = id; }

    public String getName()                    { return name; }
    public void   setName(String n)            { this.name = n; }

    public String getDescription()             { return description; }
    public void   setDescription(String d)     { this.description = d; }
}
