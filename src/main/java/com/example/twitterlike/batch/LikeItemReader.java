package com.example.twitterlike.batch;

import org.springframework.batch.item.ItemReader;

public class LikeItemReader implements ItemReader<LikeBatchItem> {

    private long currentTweetId = 1;
    private long currentLikeInTweet = 0;
    private final long totalTweets;
    private final long likesPerTweet;
    private final long userCount;

    public LikeItemReader(long totalTweets, long likesPerTweet, long userCount) {
        this.totalTweets = totalTweets;
        this.likesPerTweet = likesPerTweet;
        this.userCount = userCount;
    }

    @Override
    public LikeBatchItem read() {
        if (currentTweetId > totalTweets) {
            return null;
        }
        long likingUserId = (currentTweetId + currentLikeInTweet) % userCount + 1;
        LikeBatchItem item = new LikeBatchItem(likingUserId, currentTweetId);
        currentLikeInTweet++;
        if (currentLikeInTweet >= likesPerTweet) {
            currentLikeInTweet = 0;
            currentTweetId++;
        }
        return item;
    }
}
