package com.example.twitterlike.repository;

import com.example.twitterlike.entity.Tweet;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TweetRepository extends JpaRepository<Tweet, Long> {
    List<Tweet> findByUserId(Long userId);
    List<Tweet> findByUserIdIn(List<Long> userIds, Pageable pageable);
}
