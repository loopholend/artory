package com.artory.backend.controller;

import com.artory.backend.dto.JwtResponse;
import com.artory.backend.dto.LoginRequest;
import com.artory.backend.dto.SignupRequest;
import com.artory.backend.model.Role;
import com.artory.backend.model.User;
import com.artory.backend.repository.UserRepository;
import com.artory.backend.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginRequest loginRequest) {
        // support login by email or username
        String loginIdentifier = loginRequest.getUsername();
        // try find by email first
        Optional<User> byEmail = userRepository.findByEmail(loginIdentifier);
        String usernameForAuth = byEmail.map(User::getUsername).orElse(loginIdentifier);

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(usernameForAuth, loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtUtil.generateToken(authentication.getName());

        User user = userRepository.findByUsername(usernameForAuth).orElseThrow();
        user.setPassword(null); // don't return password

        return ResponseEntity.ok(Map.of(
            "token", jwt,
            "user", user
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body("Error: Email is already in use!");
        }

        Role role = Role.ROLE_USER;
        if (signUpRequest.getRole() != null) {
            try { role = Role.valueOf(signUpRequest.getRole()); } catch (Exception ignored) {}
        }

        User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(),
                             encoder.encode(signUpRequest.getPassword()), role);
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully!");
    }

    // GET /api/auth/me — returns logged-in user's profile
    @GetMapping("/me")
    public ResponseEntity<?> getMe(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body("Unauthorized");
        Optional<User> opt = userRepository.findByEmail(auth.getName());
        if (opt.isEmpty()) opt = userRepository.findByUsername(auth.getName());
        if (opt.isEmpty()) return ResponseEntity.status(404).body("User not found");
        User u = opt.get();
        u.setPassword(null);
        return ResponseEntity.ok(u);
    }

    // GET /api/auth/user/{id} — returns any user's public profile
    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserById(@PathVariable String id) {
        Optional<User> opt = userRepository.findById(id);
        if (opt.isEmpty()) return ResponseEntity.status(404).body("User not found");
        User u = opt.get();
        u.setPassword(null);
        return ResponseEntity.ok(u);
    }

    // PUT /api/auth/profile — update profile fields
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> body, Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).body("Unauthorized");
        Optional<User> opt = userRepository.findByEmail(auth.getName());
        if (opt.isEmpty()) opt = userRepository.findByUsername(auth.getName());
        if (opt.isEmpty()) return ResponseEntity.status(404).body("User not found");

        User u = opt.get();
        if (body.containsKey("username")) u.setUsername((String) body.get("username"));
        if (body.containsKey("bio")) u.setBio((String) body.get("bio"));
        if (body.containsKey("skills")) {
            Object skills = body.get("skills");
            if (skills instanceof List<?>) u.setSkills((List<String>) skills);
        }
        userRepository.save(u);
        u.setPassword(null);
        return ResponseEntity.ok(u);
    }
}
