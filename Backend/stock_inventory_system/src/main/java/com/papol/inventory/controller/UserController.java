package com.papol.inventory.controller;



import com.papol.inventory.model.*;
import com.papol.inventory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

  
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        Optional<Employee> emp = userService.login(body.get("email"), body.get("password"));
        return emp.isPresent()
                ? ResponseEntity.ok(emp.get())
                : ResponseEntity.status(401).body(Map.of("message","Invalid credentials"));
    }

    @GetMapping("/users")
    public List<Employee> getAllUsers() { return userService.getAllUsers(); }

    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(@PathVariable String id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PostMapping("/users")
    public ResponseEntity<?> addUser(@RequestBody Map<String, String> body) {
        try {
       
            Employee user;
            if ("Staff".equalsIgnoreCase(body.get("role"))) {
                user = new StaffMember();
            } else {
                user = new Admin();
            }
            user.setUserId(body.get("userId"));
            user.setName(body.get("name"));
            user.setEmail(body.get("email"));
            user.setPassword(body.getOrDefault("password", "1234"));
            user.setPhone(body.get("phone"));
            user.setStatus(body.getOrDefault("status", "Active"));
            return ResponseEntity.status(201).body(userService.addUser(user));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

 
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody Map<String, String> body) {
        try {

            Employee updates = new Admin();         
            updates.setName(body.get("name"));
            updates.setEmail(body.get("email"));
            updates.setPhone(body.get("phone"));
            updates.setStatus(body.getOrDefault("status", "Active"));
            updates.setPassword(body.getOrDefault("password", ""));  
            String newRole = body.getOrDefault("role", "Admin");
            return ResponseEntity.ok(userService.updateUser(id, updates, newRole));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

   
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message","Deleted"));
    }
}
