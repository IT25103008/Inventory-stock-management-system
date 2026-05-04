package com.papol.inventory.model;

import jakarta.persistence.*;

// WHY: StaffMember is the second concrete subclass of Employee. Together with Admin,
//      it completes the INHERITANCE hierarchy. Both subclasses share the same fields
//      (inherited from Employee) but behave differently through getAccessLevel().
// WHAT: '@DiscriminatorValue("Staff")' tells JPA to map rows where role = 'Staff'
//       to this class. So the same 'users' table stores both Admin and StaffMember rows.
// NOTE: Having two subclasses with different discriminator values is how JPA implements
//       the SINGLE_TABLE inheritance strategy — one table, multiple Java types.

@Entity
@DiscriminatorValue("Staff")
public class StaffMember extends Employee {

    // WHY: Default constructor required by JPA. Calls the parent's constructor via super().
    public StaffMember() { super(); }

    // WHAT: Parameterised constructor that delegates to Employee's constructor.
    //       All field initialisation logic lives in the parent — no duplication here.
    public StaffMember(String userId, String name, String email, String password, String phone) {
        super(userId, name, email, password, phone);
    }

    // WHY: This override of getAccessLevel() is what makes Polymorphism work.
    //      If you have a List<Employee> and iterate over it, calling getAccessLevel()
    //      on each item will return "FULL_ACCESS" for Admins and "LIMITED_ACCESS" for Staff —
    //      without any if/else checks. Java resolves the correct method at runtime.
    // WHAT: Returns "LIMITED_ACCESS" — Staff members have restricted system privileges.
    @Override
    public String getAccessLevel() {
        return "LIMITED_ACCESS";  // Staff can view and do stock transactions
    }
}