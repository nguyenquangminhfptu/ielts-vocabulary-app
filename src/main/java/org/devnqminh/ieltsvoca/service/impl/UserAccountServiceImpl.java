package org.devnqminh.ieltsvoca.service.impl;

import org.devnqminh.ieltsvoca.dto.UserAccountDTO;
import org.devnqminh.ieltsvoca.entity.UserAccount;
import org.devnqminh.ieltsvoca.repository.UserAccountRepository;
import org.devnqminh.ieltsvoca.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserAccountServiceImpl implements UserAccountService {

    @Autowired
    private UserAccountRepository userAccountRepository;

    @Override
    public List<UserAccountDTO> getAllUsers() {
        return userAccountRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserAccountDTO getUserById(Long id) {
        return userAccountRepository.findById(id)
                .map(this::convertToDTO)
                .orElse(null);
    }

    @Override
    public UserAccountDTO createUser(UserAccount user) {
        return convertToDTO(userAccountRepository.save(user));
    }

    @Override
    public UserAccountDTO updateUser(Long id, UserAccount userDetails) {
        return userAccountRepository.findById(id).map(user -> {
            user.setEmail(userDetails.getEmail());
            user.setName(userDetails.getName());
            user.setPasswordHash(userDetails.getPasswordHash());
            return convertToDTO(userAccountRepository.save(user));
        }).orElse(null);
    }

    @Override
    public void deleteUser(Long id) {
        userAccountRepository.deleteById(id);
    }

    private UserAccountDTO convertToDTO(UserAccount user) {
        return UserAccountDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
