package com.example.twitterlike.service;

import com.example.twitterlike.entity.User;
import com.example.twitterlike.neo4j.UserNode;
import com.example.twitterlike.neo4j.UserNodeRepository;
import com.example.twitterlike.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserNodeRepository userNodeRepository;

    public UserService(UserRepository userRepository, UserNodeRepository userNodeRepository) {
        this.userRepository = userRepository;
        this.userNodeRepository = userNodeRepository;
    }

    @Transactional
    @CacheEvict(value = {"users", "usersByUsername", "allUsers"}, key = "#id", allEntries = true)
    public User createUser(String username, String displayName) {
        User user = new User();
        user.setUsername(username);
        user.setDisplayName(displayName);
        User saved = userRepository.save(user);
        userNodeRepository.save(new UserNode(saved.getId(), saved.getUsername()));
        return saved;
    }

    @Transactional
    @CacheEvict(value = {"users", "allUsers"}, key = "#id")
    public User updateUser(Long id, String displayName) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setDisplayName(displayName);
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Transactional
    @CacheEvict(value = {"users", "usersByUsername", "allUsers"}, allEntries = true)
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @Cacheable(value = "users", key = "#id")
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    @Cacheable(value = "usersByUsername", key = "#username")
    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Cacheable(value = "allUsers")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
