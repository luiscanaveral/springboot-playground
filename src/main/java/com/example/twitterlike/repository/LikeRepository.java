package com.example.twitterlike.repository;

import com.example.twitterlike.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByUserIdAndTweetId(Long userId, Long tweetId);
    void deleteByUserIdAndTweetId(Long userId, Long tweetId);
    long countByTweetId(Long tweetId);
}
