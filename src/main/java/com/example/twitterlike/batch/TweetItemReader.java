package com.example.twitterlike.batch;

import org.springframework.batch.item.ItemReader;

public class TweetItemReader implements ItemReader<TweetBatchItem> {

    private long currentTweetId = 1;
    private final long totalTweets;
    private final long tweetsPerUser;

    public TweetItemReader(long totalTweets, long tweetsPerUser) {
        this.totalTweets = totalTweets;
        this.tweetsPerUser = tweetsPerUser;
    }

    @Override
    public TweetBatchItem read() {
        if (currentTweetId > totalTweets) {
            return null;
        }
        long userId = (currentTweetId - 1) / tweetsPerUser + 1;
        TweetBatchItem item = new TweetBatchItem(currentTweetId, userId,
                "Tweet " + currentTweetId + " from user " + userId);
        currentTweetId++;
        return item;
    }
}
