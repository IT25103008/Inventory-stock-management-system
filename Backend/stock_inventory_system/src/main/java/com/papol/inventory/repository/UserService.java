package com.papol.inventory.service;

// WHY: The Service layer is where ABSTRACTION is most clearly demonstrated.
//      The controller doesn't know anything about the database — it only calls
//      UserService methods like getAllUsers() or addUser(). All the DB logic,
//      validation, and business rules are hidden (abstracted) inside this class.
// WHAT: UserService handles all business operations for user management:
//       listing, creating, updating, deleting, and authenticating users.
// NOTE: '@Service' tells Spring to create one instance of this class and manage
//       its lifecycle. This is the Dependency Injection pattern — Spring "injects"
//       the UserRepository into this service automatically via @Autowired.

import com.papol.inventory.model.*;
import com.papol.inventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@Transactional
public class UserService {

    // WHY: '@Autowired' is Dependency Injection — Spring automatically provides
    //      the UserRepository instance. We don't create it with 'new'. This makes
    //      the code loosely coupled: UserService doesn't depend on a specific
    //      repository implementation, only on the interface.
    @Autowired
    private UserRepository userRepository;

    // WHAT: Returns all users from the database (both Admin and StaffMember).
    // NOTE: Returns an empty list — not null — if no users exist. This is
    //       defensive programming that prevents NullPointerException downstream.
    public List<Employee> getAllUsers()                  { return userRepository.findAll(); }

    // WHAT: Finds a single user by their user_id. Returns Optional to signal
    //       that the user may or may not exist.
    public Optional<Employee> getUserById(String id)    { return userRepository.findById(id); }

    // WHY: Before saving a new user, we check if the email already exists.
    //      This is a business rule: no two users should share the same email.
    //      If violated, we throw a RuntimeException which the controller catches
    //      and converts to an HTTP 400 Bad Request response.
    public Employee addUser(Employee user) {
        if (userRepository.existsByEmail(user.getEmail()))
            throw new RuntimeException("Email already exists");
        return userRepository.save(user);
    }

    // WHAT: Updates an existing user's details. We first find the existing record,
    //       then selectively update only the allowed fields (name, email, phone, status, role).
    // NOTE: We use orElseThrow() — if the user ID doesn't exist, we throw an exception
    //       immediately rather than proceeding with a null reference. This is the
    //       defensive programming pattern.
    // WHY (role change): The 'role' column is a JPA discriminator column that is
    //       insertable=false, updatable=false — JPA controls it based on the subclass type.
    //       A plain UPDATE cannot change it. The ONLY way to change a user's role is to
    //       DELETE the old row and INSERT a new row of the correct subclass (Admin or StaffMember).
    //       This method detects a role change and performs delete + re-insert automatically.
    public Employee updateUser(String id, Employee updated, String newRole) {
        Employee existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String currentRole = existing.getRole();   // "Admin" or "Staff" from DB

        // If the role has NOT changed, do a simple field update
        if (currentRole != null && currentRole.equalsIgnoreCase(newRole)) {
            existing.setName(updated.getName());
            existing.setEmail(updated.getEmail());
            existing.setPhone(updated.getPhone());
            existing.setStatus(updated.getStatus());
            return userRepository.save(existing);
        }

        // Role HAS changed — must delete old record and insert new subclass instance
        // WHY: JPA discriminator column cannot be changed with UPDATE; delete + insert is required
        String savedPassword = existing.getPassword();  // preserve existing password
        userRepository.deleteById(id);
        userRepository.flush();   // ensure DELETE is committed before INSERT

        Employee replacement;
        if ("Staff".equalsIgnoreCase(newRole)) {
            replacement = new StaffMember();
        } else {
            replacement = new Admin();
        }
        replacement.setUserId(id);
        replacement.setName(updated.getName());
        replacement.setEmail(updated.getEmail());
        replacement.setPhone(updated.getPhone());
        replacement.setStatus(updated.getStatus());
        // Keep the existing password unless a new one was provided
        String incomingPwd = updated.getPassword();
        replacement.setPassword((incomingPwd != null && !incomingPwd.isEmpty()) ? incomingPwd : savedPassword);

        return userRepository.save(replacement);
    }

    // WHAT: Deletes a user by their ID. JPA handles the DELETE SQL automatically.
    public void deleteUser(String id)                   { userRepository.deleteById(id); }

    // WHY: Login is a read-only operation that searches by email + password.
    //      Returns Optional<Employee> so the controller can check if credentials matched.
    // NOTE: Passwords are compared as plain text (no hashing). In a production system,
    //       you would use BCrypt or similar — but for this Year 1 project, plain text is acceptable.
    public Optional<Employee> login(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }
}