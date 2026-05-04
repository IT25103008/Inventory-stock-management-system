package com.papol.inventory.repository;

// WHY: We use an INTERFACE here — not a class — to achieve ABSTRACTION.
//      By extending JpaRepository, Spring Data JPA automatically provides
//      full CRUD operations (findAll, findById, save, deleteById) at runtime
//      without us writing a single line of SQL or implementation code.
// WHAT: This interface gives the service layer access to the 'users' table
//       through the Employee entity. JpaRepository<Employee, String> means:
//       - Employee = the entity type being managed
//       - String   = the type of the primary key (user_id)
// NOTE: Even though Employee is abstract, JPA can still find and return the
//       correct subclass (Admin or StaffMember) based on the discriminator column.

import com.papol.inventory.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Employee, String> {

    // WHY: Spring Data JPA generates SQL from the method name automatically.
    //      'findByEmailAndPassword' becomes: SELECT * FROM users WHERE email = ? AND password = ?
    //      This is used by the login endpoint to authenticate users.
    // NOTE: Returns Optional<Employee> — not null — to avoid NullPointerException.
    //       The caller must check isEmpty()/isPresent() before using the result.
    Optional<Employee> findByEmailAndPassword(String email, String password);

    // WHAT: Returns true if any user with the given email already exists in the DB.
    //       Used by UserService.addUser() to prevent duplicate email registrations.
    boolean existsByEmail(String email);
}