package com.example.twitterlike.batch;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class LikeItemWriter implements ItemWriter<LikeBatchItem> {

    private final JdbcTemplate jdbcTemplate;

    public LikeItemWriter(@Qualifier("sqliteDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void write(Chunk<? extends LikeBatchItem> items) {
        String sql = "INSERT INTO likes (user_id, tweet_id, created_at) VALUES (?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, items.getItems(), items.size(), (ps, item) -> {
            ps.setLong(1, item.getUserId());
            ps.setLong(2, item.getTweetId());
            ps.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
        });
    }
}
