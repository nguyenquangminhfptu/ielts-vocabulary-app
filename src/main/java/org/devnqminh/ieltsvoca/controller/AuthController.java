package org.devnqminh.ieltsvoca.controller;

import org.devnqminh.ieltsvoca.dto.UserAccountDTO;
import org.devnqminh.ieltsvoca.entity.UserAccount;
import org.devnqminh.ieltsvoca.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private org.devnqminh.ieltsvoca.util.JwtUtil jwtUtil;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserAccount loginRequest) {
        UserAccountDTO user = userAccountService.login(loginRequest.getEmail(), loginRequest.getPasswordHash());
        if (user != null) {
            String token = jwtUtil.generateToken(user.getEmail());
            java.util.Map<String, Object> response = new java.util.HashMap<>();
            response.put("user", user);
            response.put("token", token);
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(401).build();
    }

    @PostMapping("/register")
    public ResponseEntity<UserAccountDTO> register(@RequestBody UserAccount registerRequest) {
        try {
            UserAccountDTO user = userAccountService.createUser(registerRequest);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
