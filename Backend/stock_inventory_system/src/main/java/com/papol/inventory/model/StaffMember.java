package com.papol.inventory.model;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("Staff")
public class StaffMember extends Employee {

    public StaffMember() { super(); }

    public StaffMember(String userId, String name, String email, String password, String phone) {
        super(userId, name, email, password, phone);
    }
    @Override
    public String getAccessLevel() {
        return "LIMITED_ACCESS";  
    }
}
