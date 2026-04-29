package com.example.twitterlike.service;

import com.example.twitterlike.entity.Tweet;
import com.example.twitterlike.entity.User;
import com.example.twitterlike.neo4j.UserNodeRepository;
import com.example.twitterlike.repository.TweetRepository;
import com.example.twitterlike.repository.UserRepository;
import com.example.twitterlike.service.Neo4jFriendshipService;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
    private final Neo4jFriendshipService neo4jFriendshipService;

    public TweetService(TweetRepository tweetRepository, UserRepository userRepository, UserNodeRepository userNodeRepository, Neo4jFriendshipService neo4jFriendshipService) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
        this.userNodeRepository = userNodeRepository;
        this.neo4jFriendshipService = neo4jFriendshipService;
    }

    @Transactional
    @CacheEvict(value = {"feedTweets"}, allEntries = true)
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
    @CacheEvict(value = {"tweets", "userTweets"}, key = "#tweetId")
    public Tweet updateTweet(Long tweetId, String content) {
        return tweetRepository.findById(tweetId)
                .map(tweet -> {
                    tweet.setContent(content);
                    return tweetRepository.save(tweet);
                })
                .orElseThrow(() -> new RuntimeException("Tweet not found"));
    }

    @Transactional
    @CacheEvict(value = {"tweets", "userTweets", "feedTweets"}, allEntries = true)
    public void deleteTweet(Long tweetId) {
        tweetRepository.deleteById(tweetId);
    }

    @Cacheable(value = "tweets", key = "#id")
    public Optional<Tweet> getTweetById(Long id) {
        return tweetRepository.findById(id);
    }

    @Cacheable(value = "userTweets", key = "#userId")
    public List<Tweet> getTweetsByUserId(Long userId) {
        return tweetRepository.findByUserId(userId);
    }

    @Cacheable(value = "allTweets")
    public List<Tweet> getAllTweets() {
        return tweetRepository.findAll();
    }

    @Cacheable(value = "feedTweets", key = "#userId")
    public List<Tweet> getFeedTweets(Long userId) {
        var followingIds = neo4jFriendshipService.getFollowingIds(userId);
        followingIds.add(userId);
        return tweetRepository.findByUserIdIn(
                followingIds.stream().toList(),
                PageRequest.of(0, 50, Sort.by(Sort.Direction.DESC, "createdAt"))
        );
    }
}
