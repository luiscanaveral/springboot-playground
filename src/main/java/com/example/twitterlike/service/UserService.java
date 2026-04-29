package com.example.twitterlike.service;

import com.example.twitterlike.entity.User;
import com.example.twitterlike.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public User createUser(String username, String displayName) {
        User user = new User();
        user.setUsername(username);
        user.setDisplayName(displayName);
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long id, String displayName) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setDisplayName(displayName);
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
