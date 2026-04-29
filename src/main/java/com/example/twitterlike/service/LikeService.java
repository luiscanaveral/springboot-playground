package com.example.twitterlike.service;

import com.example.twitterlike.entity.Like;
import com.example.twitterlike.entity.Tweet;
import com.example.twitterlike.entity.User;
import com.example.twitterlike.repository.LikeRepository;
import com.example.twitterlike.repository.TweetRepository;
import com.example.twitterlike.repository.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final TweetRepository tweetRepository;

    public LikeService(LikeRepository likeRepository, UserRepository userRepository, TweetRepository tweetRepository) {
        this.likeRepository = likeRepository;
        this.userRepository = userRepository;
        this.tweetRepository = tweetRepository;
    }

    @Transactional
    @CacheEvict(value = {"likes", "likeCounts"}, key = "#tweetId", allEntries = true)
    public Like likeTweet(Long userId, Long tweetId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new RuntimeException("Tweet not found"));

        Optional<Like> existingLike = likeRepository.findByUserIdAndTweetId(userId, tweetId);
        if (existingLike.isPresent()) {
            throw new RuntimeException("User already liked this tweet");
        }

        Like like = new Like();
        like.setUser(user);
        like.setTweet(tweet);
        return likeRepository.save(like);
    }

    @Transactional
    @CacheEvict(value = {"likes", "likeCounts"}, key = "#tweetId", allEntries = true)
    public void unlikeTweet(Long userId, Long tweetId) {
        likeRepository.deleteByUserIdAndTweetId(userId, tweetId);
    }

    @Cacheable(value = "likes", key = "{#userId, #tweetId}")
    public Optional<Like> getLike(Long userId, Long tweetId) {
        return likeRepository.findByUserIdAndTweetId(userId, tweetId);
    }

    @Cacheable(value = "likeCounts", key = "#tweetId")
    public long getLikeCount(Long tweetId) {
        return likeRepository.countByTweetId(tweetId);
    }
}
