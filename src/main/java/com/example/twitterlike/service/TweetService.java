package com.example.twitterlike.service;

import com.example.twitterlike.entity.Tweet;
import com.example.twitterlike.entity.User;
import com.example.twitterlike.neo4j.UserNodeRepository;
import com.example.twitterlike.repository.TweetRepository;
import com.example.twitterlike.repository.UserRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class TweetService {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    private final UserNodeRepository userNodeRepository;

    public TweetService(TweetRepository tweetRepository, UserRepository userRepository, UserNodeRepository userNodeRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
        this.userNodeRepository = userNodeRepository;
    }

    @Transactional
    public Tweet createTweet(Long userId, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Tweet tweet = new Tweet();
        tweet.setUser(user);
        tweet.setContent(content);
        Tweet saved = tweetRepository.save(tweet);
        fanOutTweet(saved);
        return saved;
    }

    private void fanOutTweet(Tweet tweet) {
        var followerIds = userNodeRepository.findFollowerIdsByUserId(tweet.getUser().getId());
        for (Long followerId : followerIds) {
            System.out.println("Fan out tweet " + tweet.getId() + " to user " + followerId);
        }
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

    public List<Tweet> getFeedTweets(Long userId) {
        var followingIds = neo4jFriendshipService.getFollowingIds(userId);
        followingIds.add(userId);
        return tweetRepository.findByUserIdIn(
                followingIds.stream().toList(),
                PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
    }
}
