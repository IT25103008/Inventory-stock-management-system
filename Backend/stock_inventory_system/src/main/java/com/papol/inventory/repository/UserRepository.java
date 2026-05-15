package com.papol.inventory.repository;

import com.papol.inventory.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Employee, String> {

    Optional<Employee> findByEmailAndPassword(String email, String password);

    boolean existsByEmail(String email);
}
