package com.example.twitterlike.batch;

import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class TweetItemWriter implements ItemWriter<TweetBatchItem> {

    private final JdbcTemplate jdbcTemplate;

    public TweetItemWriter(@Qualifier("sqliteDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void write(Chunk<? extends TweetBatchItem> items) {
        String sql = "INSERT INTO tweets (id, user_id, content, created_at) VALUES (?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, items.getItems(), items.size(), (ps, item) -> {
            ps.setLong(1, item.getId());
            ps.setLong(2, item.getUserId());
            ps.setString(3, item.getContent());
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
        });
    }
}
