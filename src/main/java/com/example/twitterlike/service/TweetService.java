package com.example.twitterlike.service;

import com.example.twitterlike.entity.Tweet;
import com.example.twitterlike.entity.User;
import com.example.twitterlike.repository.TweetRepository;
import com.example.twitterlike.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TweetService {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public TweetService(TweetRepository tweetRepository, UserRepository userRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Tweet createTweet(Long userId, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Tweet tweet = new Tweet();
        tweet.setUser(user);
        tweet.setContent(content);
        return tweetRepository.save(tweet);
    }

    @Transactional
    public Tweet updateTweet(Long tweetId, String content) {
        return tweetRepository.findById(tweetId)
                .map(tweet -> {
                    tweet.setContent(content);
                    return tweetRepository.save(tweet);
                })
                .orElseThrow(() -> new RuntimeException("Tweet not found"));
    }

    @Transactional
    public void deleteTweet(Long tweetId) {
        tweetRepository.deleteById(tweetId);
    }

    public Optional<Tweet> getTweetById(Long id) {
        return tweetRepository.findById(id);
    }

    public List<Tweet> getTweetsByUserId(Long userId) {
        return tweetRepository.findByUserId(userId);
    }

    public List<Tweet> getAllTweets() {
        return tweetRepository.findAll();
    }
}
