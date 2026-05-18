package com.papol.inventory.service;


import com.papol.inventory.model.*;
import com.papol.inventory.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;

@Service
@Transactional
public class UserService {


    @Autowired
    private UserRepository userRepository;


    public List<Employee> getAllUsers()                  { return userRepository.findAll(); }


    public Optional<Employee> getUserById(String id)    { return userRepository.findById(id); }


    public Employee addUser(Employee user) {
        if (userRepository.existsByEmail(user.getEmail()))
            throw new RuntimeException("Email already exists");
        return userRepository.save(user);
    }


    public Employee updateUser(String id, Employee updated, String newRole) {
        Employee existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String currentRole = existing.getRole();   // "Admin" or "Staff" from DB

        if (currentRole != null && currentRole.equalsIgnoreCase(newRole)) {
            existing.setName(updated.getName());
            existing.setEmail(updated.getEmail());
            existing.setPhone(updated.getPhone());
            existing.setStatus(updated.getStatus());
            return userRepository.save(existing);
        }


        String savedPassword = existing.getPassword();
        userRepository.deleteById(id);
        userRepository.flush();

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

        String incomingPwd = updated.getPassword();
        replacement.setPassword((incomingPwd != null && !incomingPwd.isEmpty()) ? incomingPwd : savedPassword);

        return userRepository.save(replacement);
    }


    public void deleteUser(String id)                   { userRepository.deleteById(id); }


    public Optional<Employee> login(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }
}