package com.papol.inventory.controller;

// WHY: The Controller is the ENTRY POINT of the application — it receives HTTP
//      requests from the frontend (JavaScript fetch() calls) and delegates work
//      to the Service layer. The controller is deliberately kept THIN: it contains
//      no business logic, only request handling and response formatting.
//      This follows the Separation of Concerns principle.
// WHAT: UserController handles all user-related HTTP endpoints: login, CRUD.
// NOTE: '@RestController' combines @Controller + @ResponseBody, meaning every
//       method's return value is automatically converted to JSON.
//       '@CrossOrigin(origins = "*")' allows requests from any frontend origin
//       (needed because frontend and backend run on different ports).

import com.papol.inventory.model.*;
import com.papol.inventory.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class UserController {

    // WHY: Dependency Injection — Spring provides the UserService instance.
    //      The controller never creates services with 'new'.
    @Autowired
    private UserService userService;

    // WHAT: POST /login — authenticates a user by email and password.
    //       Returns the Employee JSON (200 OK) if valid, or 401 Unauthorized if not.
    // NOTE: '@RequestBody Map<String, String>' means Spring parses the JSON body
    //       into a key-value map. We extract "email" and "password" from it.
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {
        Optional<Employee> emp = userService.login(body.get("email"), body.get("password"));
        return emp.isPresent()
                ? ResponseEntity.ok(emp.get())
                : ResponseEntity.status(401).body(Map.of("message","Invalid credentials"));
    }

    // WHAT: GET /users — returns all users as a JSON array. HTTP 200 automatically.
    @GetMapping("/users")
    public List<Employee> getAllUsers() { return userService.getAllUsers(); }

    // WHAT: GET /users/{id} — returns one user by ID, or HTTP 404 if not found.
    // NOTE: '@PathVariable' extracts the {id} from the URL path.
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUser(@PathVariable String id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // WHAT: POST /users — creates a new user (Admin or StaffMember).
    // WHY: We accept a Map<String, String> instead of a specific class because
    //      we need to read the 'role' field first, then INSTANTIATE the correct
    //      subclass. This is the Polymorphism principle — the right object type
    //      is determined at runtime based on the incoming data.
    // NOTE: Returns HTTP 201 (Created) on success, or HTTP 400 (Bad Request) on error.
    @PostMapping("/users")
    public ResponseEntity<?> addUser(@RequestBody Map<String, String> body) {
        try {
            // WHY: We read the 'role' field to decide which subclass to create.
            //       This is the Polymorphism principle — the correct object type
            //       (Admin or StaffMember) is chosen at runtime based on data.
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

    // WHAT: PUT /users/{id} — updates an existing user's details, including role.
    // WHY: Role is included in the update payload. If the role changes (Admin→Staff or
    //       Staff→Admin), the service performs a delete + re-insert because JPA's
    //       SINGLE_TABLE discriminator column cannot be changed with a simple UPDATE.
    // NOTE: Returns HTTP 200 on success, or HTTP 404 if user ID not found.
    @PutMapping("/users/{id}")
    public ResponseEntity<?> updateUser(@PathVariable String id, @RequestBody Map<String, String> body) {
        try {
            // WHY: We pass a temporary Employee object carrying the new field values.
            //      The 'role' is read separately and passed to the service method,
            //      which decides whether to update in-place or delete+reinsert.
            Employee updates = new Admin();          // carrier object — subclass type doesn't matter
            updates.setName(body.get("name"));
            updates.setEmail(body.get("email"));
            updates.setPhone(body.get("phone"));
            updates.setStatus(body.getOrDefault("status", "Active"));
            updates.setPassword(body.getOrDefault("password", ""));  // "" means "keep existing"
            String newRole = body.getOrDefault("role", "Admin");
            return ResponseEntity.ok(userService.updateUser(id, updates, newRole));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // WHAT: DELETE /users/{id} — removes a user from the database.
    // NOTE: Returns a JSON message { "message": "Deleted" } with HTTP 200.
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(Map.of("message","Deleted"));
    }
}