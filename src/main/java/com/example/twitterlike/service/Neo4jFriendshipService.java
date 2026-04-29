package com.example.twitterlike.service;

import com.example.twitterlike.neo4j.UserNode;
import com.example.twitterlike.neo4j.UserNodeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class Neo4jFriendshipService {

    private final UserNodeRepository userNodeRepository;

    public Neo4jFriendshipService(UserNodeRepository userNodeRepository) {
        this.userNodeRepository = userNodeRepository;
    }

    @Transactional
    public UserNode createUserNode(Long userId, String username) {
        UserNode userNode = new UserNode(userId, username);
        return userNodeRepository.save(userNode);
    }

    @Transactional
    public void follow(Long followerId, Long followeeId) {
        UserNode follower = userNodeRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found in Neo4j"));
        UserNode followee = userNodeRepository.findById(followeeId)
                .orElseThrow(() -> new RuntimeException("Followee not found in Neo4j"));

        follower.getFollowing().add(followee);
        userNodeRepository.save(follower);
    }

    @Transactional
    public void unfollow(Long followerId, Long followeeId) {
        UserNode follower = userNodeRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower not found in Neo4j"));

        follower.getFollowing().removeIf(user -> user.getId().equals(followeeId));
        userNodeRepository.save(follower);
    }

    public Set<Long> getFollowerIds(Long userId) {
        return userNodeRepository.findFollowerIdsByUserId(userId);
    }

    public Set<Long> getFollowingIds(Long userId) {
        UserNode user = userNodeRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found in Neo4j"));
        Set<Long> followingIds = new HashSet<>();
        for (UserNode following : user.getFollowing()) {
            followingIds.add(following.getId());
        }
        return followingIds;
    }
}
