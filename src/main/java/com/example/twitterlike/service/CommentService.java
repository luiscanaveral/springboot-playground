package com.example.twitterlike.service;

import com.example.twitterlike.entity.Comment;
import com.example.twitterlike.entity.Tweet;
import com.example.twitterlike.entity.User;
import com.example.twitterlike.repository.CommentRepository;
import com.example.twitterlike.repository.TweetRepository;
import com.example.twitterlike.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TweetRepository tweetRepository;

    public CommentService(CommentRepository commentRepository, UserRepository userRepository, TweetRepository tweetRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.tweetRepository = tweetRepository;
    }

    @Transactional
    public Comment createComment(Long userId, Long tweetId, String content) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Tweet tweet = tweetRepository.findById(tweetId)
                .orElseThrow(() -> new RuntimeException("Tweet not found"));

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setTweet(tweet);
        comment.setContent(content);
        return commentRepository.save(comment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        commentRepository.deleteById(commentId);
    }

    public List<Comment> getCommentsByTweetId(Long tweetId) {
        return commentRepository.findByTweetId(tweetId);
    }

    public Optional<Comment> getCommentById(Long id) {
        return commentRepository.findById(id);
    }
}
