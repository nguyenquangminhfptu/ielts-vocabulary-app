package org.devnqminh.ieltsvoca.controller;

import org.devnqminh.ieltsvoca.dto.UserAccountDTO;
import org.devnqminh.ieltsvoca.entity.UserAccount;
import org.devnqminh.ieltsvoca.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserAccountController {

    @Autowired
    private UserAccountService userAccountService;

    @GetMapping
    public List<UserAccountDTO> getAllUsers() {
        return userAccountService.getAllUsers();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserAccountDTO> getUserById(@PathVariable Long id) {
        UserAccountDTO user = userAccountService.getUserById(id);
        if (user != null) {
            return ResponseEntity.ok(user);
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public UserAccountDTO createUser(@RequestBody UserAccount user) {
        return userAccountService.createUser(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserAccountDTO> updateUser(@PathVariable Long id, @RequestBody UserAccount userDetails) {
        UserAccountDTO updatedUser = userAccountService.updateUser(id, userDetails);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userAccountService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
