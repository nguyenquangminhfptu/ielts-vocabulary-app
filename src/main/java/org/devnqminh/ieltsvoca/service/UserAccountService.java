package org.devnqminh.ieltsvoca.service;

import org.devnqminh.ieltsvoca.dto.UserAccountDTO;
import org.devnqminh.ieltsvoca.entity.UserAccount;

import java.util.List;

public interface UserAccountService {
    List<UserAccountDTO> getAllUsers();
    UserAccountDTO getUserById(Long id);
    UserAccountDTO createUser(UserAccount user);
    UserAccountDTO updateUser(Long id, UserAccount userDetails);
    void deleteUser(Long id);
    UserAccountDTO login(String email, String password);
}
