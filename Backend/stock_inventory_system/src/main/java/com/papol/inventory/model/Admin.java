package com.papol.inventory.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("Admin")
public class Admin extends Employee {

    public Admin() { super(); }

    public Admin(String userId, String name, String email, String password, String phone) {
        super(userId, name, email, password, phone);
    }

    @Override
    public String getAccessLevel() {
        return "FULL_ACCESS";  // Admin can manage all modules
    }
}
