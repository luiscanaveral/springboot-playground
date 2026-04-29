package com.example.twitterlike.controller;

import com.example.twitterlike.entity.Comment;
import com.example.twitterlike.entity.Like;
import com.example.twitterlike.entity.Tweet;
import com.example.twitterlike.entity.User;
import com.example.twitterlike.service.BatchJobService;
import com.example.twitterlike.service.CommentService;
import com.example.twitterlike.service.LikeService;
import com.example.twitterlike.service.Neo4jFriendshipService;
import com.example.twitterlike.service.TweetService;
import com.example.twitterlike.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class TwitterController {

    private final UserService userService;
    private final TweetService tweetService;
    private final LikeService likeService;
    private final CommentService commentService;
    private final BatchJobService batchJobService;
    private final Neo4jFriendshipService neo4jFriendshipService;

    public TwitterController(UserService userService, TweetService tweetService, LikeService likeService,
                             CommentService commentService, BatchJobService batchJobService,
                             Neo4jFriendshipService neo4jFriendshipService) {
        this.userService = userService;
        this.tweetService = tweetService;
        this.likeService = likeService;
        this.commentService = commentService;
        this.batchJobService = batchJobService;
        this.neo4jFriendshipService = neo4jFriendshipService;
    }

    @PostMapping("/users")
    public User createUser(@RequestParam String username, @RequestParam String displayName) {
        return userService.createUser(username, displayName);
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @PostMapping("/tweets")
    public Tweet createTweet(@RequestParam Long userId, @RequestParam String content) {
        return tweetService.createTweet(userId, content);
    }

    @GetMapping("/tweets")
    public List<Tweet> getAllTweets() {
        return tweetService.getAllTweets();
    }

    @PostMapping("/likes")
    public Like likeTweet(@RequestParam Long userId, @RequestParam Long tweetId) {
        return likeService.likeTweet(userId, tweetId);
    }

    @DeleteMapping("/likes")
    public void unlikeTweet(@RequestParam Long userId, @RequestParam Long tweetId) {
        likeService.unlikeTweet(userId, tweetId);
    }

    @GetMapping("/tweets/{tweetId}/likes/count")
    public long getLikeCount(@PathVariable Long tweetId) {
        return likeService.getLikeCount(tweetId);
    }

    @PostMapping("/comments")
    public Comment createComment(@RequestParam Long userId, @RequestParam Long tweetId, @RequestParam String content) {
        return commentService.createComment(userId, tweetId, content);
    }

    @GetMapping("/tweets/{tweetId}/comments")
    public List<Comment> getComments(@PathVariable Long tweetId) {
        return commentService.getCommentsByTweetId(tweetId);
    }

    @DeleteMapping("/comments/{commentId}")
    public void deleteComment(@PathVariable Long commentId) {
        commentService.deleteComment(commentId);
    }

    @PostMapping("/batch/feed")
    public String runFeedLoadJob(
            @RequestParam(defaultValue = "1000000") long userCount,
            @RequestParam(defaultValue = "20") long tweetsPerUser,
            @RequestParam(defaultValue = "100") long likesPerTweet) {
        batchJobService.runFeedLoadJob(userCount, tweetsPerUser, likesPerTweet);
        return "Feed load job started";
    }

    @PostMapping("/users/{userId}/follow")
    public void followUser(@PathVariable Long userId, @RequestParam Long followeeId) {
        neo4jFriendshipService.follow(userId, followeeId);
    }

    @DeleteMapping("/users/{userId}/follow")
    public void unfollowUser(@PathVariable Long userId, @RequestParam Long followeeId) {
        neo4jFriendshipService.unfollow(userId, followeeId);
    }

    @GetMapping("/users/{userId}/following")
    public Set<Long> getFollowing(@PathVariable Long userId) {
        return neo4jFriendshipService.getFollowingIds(userId);
    }

    @GetMapping("/users/{userId}/followers")
    public Set<Long> getFollowers(@PathVariable Long userId) {
        return neo4jFriendshipService.getFollowerIds(userId);
    }

    @GetMapping("/users/{userId}/feed")
    public List<Tweet> getFeed(@PathVariable Long userId) {
        return tweetService.getFeedTweets(userId);
    }
}
