package com.papol.inventory.model;

import jakarta.persistence.*;


@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "role", discriminatorType = DiscriminatorType.STRING)
public abstract class Employee {


    @Id
    @Column(name = "user_id")
    private String userId;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "role", insertable = false, updatable = false)
    private String role;

    @Column(name = "phone")
    private String phone;

    @Column(name = "status")
    private String status;

    public Employee() {}

    public Employee(String userId, String name, String email, String password, String phone) {
        this.userId   = userId;
        this.name     = name;
        this.email    = email;
        this.password = password;
        this.phone    = phone;
        this.status   = "Active";
    }

    public abstract String getAccessLevel();

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
