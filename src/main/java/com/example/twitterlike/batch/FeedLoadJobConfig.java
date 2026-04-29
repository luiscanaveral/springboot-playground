package com.example.twitterlike.batch;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class FeedLoadJobConfig {

    private final JobRepository jobRepository;
    private final PlatformTransactionManager transactionManager;

    public FeedLoadJobConfig(JobRepository jobRepository, PlatformTransactionManager transactionManager) {
        this.jobRepository = jobRepository;
        this.transactionManager = transactionManager;
    }

    @Bean
    public Job feedLoadJob(Step userLoadStep, Step tweetLoadStep, Step likeLoadStep) {
        return new JobBuilder("feedLoadJob", jobRepository)
                .start(userLoadStep)
                .next(tweetLoadStep)
                .next(likeLoadStep)
                .build();
    }

    @Bean
    public Step userLoadStep(ItemWriter<UserBatchItem> userWriter) {
        return new StepBuilder("userLoadStep", jobRepository)
                .<UserBatchItem, UserBatchItem>chunk(1000, transactionManager)
                .reader(userReader(null))
                .writer(userWriter)
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<UserBatchItem> userReader(
            @Value("#{jobParameters['userCount']}") Long userCount) {
        long count = userCount != null ? userCount : 1_000_000L;
        return new UserItemReader(count);
    }

    @Bean
    public Step tweetLoadStep(ItemWriter<TweetBatchItem> tweetWriter) {
        return new StepBuilder("tweetLoadStep", jobRepository)
                .<TweetBatchItem, TweetBatchItem>chunk(1000, transactionManager)
                .reader(tweetReader(null, null))
                .writer(tweetWriter)
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<TweetBatchItem> tweetReader(
            @Value("#{jobParameters['userCount']}") Long userCount,
            @Value("#{jobParameters['tweetCountPerUser']}") Long tweetCountPerUser) {
        long users = userCount != null ? userCount : 1_000_000L;
        long tweetsPerUser = tweetCountPerUser != null ? tweetCountPerUser : 20L;
        return new TweetItemReader(users * tweetsPerUser, tweetsPerUser);
    }

    @Bean
    public Step likeLoadStep(ItemWriter<LikeBatchItem> likeWriter) {
        return new StepBuilder("likeLoadStep", jobRepository)
                .<LikeBatchItem, LikeBatchItem>chunk(1000, transactionManager)
                .reader(likeReader(null, null, null))
                .writer(likeWriter)
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<LikeBatchItem> likeReader(
            @Value("#{jobParameters['userCount']}") Long userCount,
            @Value("#{jobParameters['tweetCountPerUser']}") Long tweetCountPerUser,
            @Value("#{jobParameters['likeCountPerTweet']}") Long likeCountPerTweet) {
        long users = userCount != null ? userCount : 1_000_000L;
        long tweetsPerUser = tweetCountPerUser != null ? tweetCountPerUser : 20L;
        long likesPerTweet = likeCountPerTweet != null ? likeCountPerTweet : 100L;
        return new LikeItemReader(users * tweetsPerUser, likesPerTweet, users);
    }
}
