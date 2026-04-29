package com.example.twitterlike.batch;

import com.example.twitterlike.config.BatchConfig;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@Component
public class UserItemWriter implements ItemWriter<UserBatchItem> {

    private final JdbcTemplate jdbcTemplate;

    public UserItemWriter(@Qualifier("sqliteDataSource") DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void write(Chunk<? extends UserBatchItem> items) {
        String sql = "INSERT INTO users (id, username, display_name, created_at) VALUES (?, ?, ?, ?)";
        jdbcTemplate.batchUpdate(sql, items.getItems(), items.size(), (ps, item) -> {
            ps.setLong(1, item.getId());
            ps.setString(2, item.getUsername());
            ps.setString(3, item.getDisplayName());
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
        });
    }
}
