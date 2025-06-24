package com.zidio.jobportal.controller;

import com.zidio.jobportal.model.User;
import com.zidio.jobportal.repository.UserRepository;
import com.zidio.jobportal.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // ✅ 1. Any logged-in user (Student, HR, Admin) view their profile
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('STUDENT','EMPLOYEE', 'RECRUITER', 'HR', 'ADMIN')")

    public ResponseEntity<?> getUserProfile(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Missing or invalid Authorization header");
        }

        String token = authHeader.substring(7);
        String email = jwtUtil.extractUsername(token);

        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }
        System.out.println("🔍 Email from token: " + email);


        User user = userOpt.get();
        System.out.println("✅ Found user: " + user.getFullName());
        return ResponseEntity.ok(new UserDTO(user.getId(), user.getFullName(), user.getEmail(), user.getRole()));
    }

    // ✅ 2. Admin views all users
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userRepository.findAll();

        List<UserDTO> safeUsers = new ArrayList<>();
        for (User user : users) {
            safeUsers.add(new UserDTO(
                    user.getId(),
                    user.getFullName(),
                    user.getEmail(),
                    user.getRole()
            ));
        }

        return ResponseEntity.ok(safeUsers);
    }
    // ✅ 3. Admin deletes a user by ID
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body("User not found");
        }

        userRepository.deleteById(id);
        return ResponseEntity.ok("User deleted successfully.");
    }
    // ✅ 4. Admin promotes a user to a new role
    @PutMapping("/promote/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> promoteUser(@PathVariable UUID id, @RequestParam String newRole) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "User not found"));

        }

        User user = optionalUser.get();
        user.setRole(newRole.toUpperCase()); // e.g., "HR" or "ADMIN"
        userRepository.save(user);

        return ResponseEntity.ok("User role updated to: " + newRole.toUpperCase());
    }



    // ✅ DTO class to hide password
    static class UserDTO {
        private UUID id;
        private String fullName;
        private String email;
        private String role;

        public UserDTO(UUID id, String fullName, String email, String role) {
            this.id = id;
            this.fullName = fullName;
            this.email = email;
            this.role = role;
        }

        public UUID getId() { return id; }
        public String getFullName() { return fullName; }
        public String getEmail() { return email; }
        public String getRole() { return role; }
    }
}
