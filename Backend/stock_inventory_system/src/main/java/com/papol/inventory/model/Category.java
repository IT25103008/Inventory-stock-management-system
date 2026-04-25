package com.papol.inventory.model;

import jakarta.persistence.*;

// WHY: Category groups related products together (e.g., "Electronics", "Furniture").
//      It is a simple entity with private fields and controlled access — a textbook
//      example of ENCAPSULATION in action.
// WHAT: Maps to the 'categories' table. Products reference categories by category_id.
// NOTE: If a category is deleted, products that reference it will still have the old
//       category_id value. There is no cascading delete — this is a project-scope choice.

@Entity
@Table(name = "categories")
public class Category {

    // WHY: Private fields — ENCAPSULATION principle. The categoryId, name, and
    //      description can only be accessed through getter/setter methods.
    @Id
    @Column(name = "category_id")
    private String categoryId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    // WHY: No-arg constructor needed by JPA/Hibernate to instantiate objects from DB rows.
    public Category() {}

    // WHAT: Parameterised constructor for creating a category programmatically.
    public Category(String categoryId, String name, String description) {
        this.categoryId  = categoryId;
        this.name        = name;
        this.description = description;
    }

    // WHY: Getters and setters — the Encapsulation access mechanism.
    public String getCategoryId()              { return categoryId; }
    public void   setCategoryId(String id)     { this.categoryId = id; }

    public String getName()                    { return name; }
    public void   setName(String n)            { this.name = n; }

    public String getDescription()             { return description; }
    public void   setDescription(String d)     { this.description = d; }
}