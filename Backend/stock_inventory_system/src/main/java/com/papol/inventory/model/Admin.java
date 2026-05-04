package com.papol.inventory.model;

import jakarta.persistence.*;

// WHY: Admin is a CONCRETE subclass of Employee. This demonstrates INHERITANCE —
//      Admin inherits all fields (userId, name, email, etc.) from Employee without
//      rewriting them. This eliminates code duplication and follows the DRY principle.
// WHAT: '@DiscriminatorValue("Admin")' tells JPA that whenever the 'role' column
//       in the 'users' table contains "Admin", this row should be loaded as an Admin object.
// NOTE: Admin has no additional fields of its own — the only difference from StaffMember
//       is the return value of getAccessLevel(). This is enough to demonstrate Polymorphism.

@Entity
@DiscriminatorValue("Admin")
public class Admin extends Employee {

    // WHY: JPA requires a no-argument constructor. We call super() to invoke
    //      the parent's (Employee's) default constructor first.
    public Admin() { super(); }

    // WHAT: Parameterised constructor — delegates all field-setting to the parent class.
    //       This is the INHERITANCE chain in action: Admin doesn't duplicate the logic.
    public Admin(String userId, String name, String email, String password, String phone) {
        super(userId, name, email, password, phone);
    }

    // WHY: This is the POLYMORPHISM showcase. Employee declares getAccessLevel() as abstract,
    //      so Admin MUST provide its own version. When the system calls getAccessLevel()
    //      on an Employee reference that happens to be an Admin, this version runs.
    // WHAT: Returns "FULL_ACCESS" — meaning Admins can manage all modules in the system.
    @Override
    public String getAccessLevel() {
        return "FULL_ACCESS";  // Admin can manage all modules
    }
}