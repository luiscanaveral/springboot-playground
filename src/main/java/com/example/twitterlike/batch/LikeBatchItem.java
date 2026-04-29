package com.example.twitterlike.batch;

public class LikeBatchItem {
    private Long userId;
    private Long tweetId;

    public LikeBatchItem(Long userId, Long tweetId) {
        this.userId = userId;
        this.tweetId = tweetId;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Long getTweetId() { return tweetId; }
    public void setTweetId(Long tweetId) { this.tweetId = tweetId; }
}
