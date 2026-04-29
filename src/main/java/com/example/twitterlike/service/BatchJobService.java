package com.example.twitterlike.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.stereotype.Service;

@Service
public class BatchJobService {

    private final JobLauncher jobLauncher;
    private final Job feedLoadJob;

    public BatchJobService(JobLauncher jobLauncher, Job feedLoadJob) {
        this.jobLauncher = jobLauncher;
        this.feedLoadJob = feedLoadJob;
    }

    public void runFeedLoadJob(long userCount, long tweetsPerUser, long likesPerTweet) {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("userCount", userCount)
                .addLong("tweetCountPerUser", tweetsPerUser)
                .addLong("likeCountPerTweet", likesPerTweet)
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        try {
            jobLauncher.run(feedLoadJob, jobParameters);
        } catch (Exception e) {
            throw new RuntimeException("Failed to run feed load job", e);
        }
    }
}
