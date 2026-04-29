package com.example.twitterlike.batch;

import org.springframework.batch.item.ItemReader;

public class UserItemReader implements ItemReader<UserBatchItem> {

    private long currentId = 1;
    private final long userCount;

    public UserItemReader(long userCount) {
        this.userCount = userCount;
    }

    @Override
    public UserBatchItem read() {
        if (currentId > userCount) {
            return null;
        }
        UserBatchItem item = new UserBatchItem(currentId, "user" + currentId, "User " + currentId);
        currentId++;
        return item;
    }
}
