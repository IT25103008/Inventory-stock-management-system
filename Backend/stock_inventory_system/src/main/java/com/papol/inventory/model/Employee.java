package com.papol.inventory.model;

import jakarta.persistence.*;

// WHY: Employee is marked 'abstract' to enforce the OOP principle of ABSTRACTION.
//      You cannot create a plain "Employee" object — you must create either an Admin
//      or a StaffMember. This forces every user in the system to have a specific role.
// WHAT: This is the base class (superclass) for the user hierarchy.
//       It holds all the common fields that every employee shares.
// NOTE: JPA maps this class and its subclasses into a SINGLE database table called 'users'.
//       The '@Inheritance(strategy = SINGLE_TABLE)' annotation tells Hibernate to store
//       Admin and StaffMember rows in ONE table, using the 'role' column to distinguish them.

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
public abstract class Employee {

    // WHY: Each field is declared 'private' to enforce ENCAPSULATION.
    //      Outside classes cannot directly read or change these values —
    //      they must go through the public getter/setter methods below.
    // WHAT: '@Id' marks userId as the primary key. '@Column' maps it to the DB column.

    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "name", nullable = false)
    private String name;

    // NOTE: 'unique = true' means no two users can share the same email address.
    //       The database enforces this constraint automatically.
    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    // WHY: 'insertable = false, updatable = false' because the 'role' column is
    //      automatically managed by JPA's discriminator mechanism.
    //      We can read it, but we never write to it directly — Hibernate fills it
    //      with 'Admin' or 'Staff' depending on which subclass is being saved.
    @Column(name = "role", insertable = false, updatable = false)
    private String role;

    @Column(name = "phone")
    private String phone;

    @Column(name = "status")
    private String status;

    // WHY: JPA requires a no-argument constructor to create objects when reading
    //      rows from the database. Without this, Hibernate throws an error.
    public Employee() {}

    // WHAT: Parameterised constructor — used when creating a new employee from code.
    //       Sets status to "Active" by default (defensive programming: avoids null status).
    public Employee(String userId, String name, String email, String password, String phone) {
        this.userId   = userId;
        this.name     = name;
        this.email    = email;
        this.password = password;
        this.phone    = phone;
        this.status   = "Active";
    }

    // WHY: This abstract method demonstrates POLYMORPHISM.
    //      Each subclass (Admin, StaffMember) MUST provide its own implementation.
    //      When you call getAccessLevel() on an Employee reference, Java automatically
    //      calls the correct version depending on whether the object is an Admin or Staff.
    //      This is known as "runtime polymorphism" or "dynamic method dispatch".
    public abstract String getAccessLevel();

    // WHY: Getters and setters are the mechanism that enforces ENCAPSULATION.
    //      They provide controlled access to private fields. For example, we could
    //      add validation inside a setter to reject invalid values — all without
    //      changing any code that calls the setter.
    public String getUserId()              { return userId; }
    public void   setUserId(String id)     { this.userId = id; }

    public String getName()                { return name; }
    public void   setName(String n)        { this.name = n; }

    public String getEmail()               { return email; }
    public void   setEmail(String e)       { this.email = e; }

    public String getPassword()            { return password; }
    public void   setPassword(String p)    { this.password = p; }

    public String getRole()                { return role; }

    public String getPhone()               { return phone; }
    public void   setPhone(String ph)      { this.phone = ph; }

    public String getStatus()              { return status; }
    public void   setStatus(String s)      { this.status = s; }
}