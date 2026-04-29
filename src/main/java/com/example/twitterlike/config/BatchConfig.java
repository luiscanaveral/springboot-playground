package com.example.twitterlike.config;

import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
public class BatchConfig {

    @Bean
    @Primary
    public DataSource batchDataSource() {
        return new org.springframework.jdbc.datasource.DriverManagerDataSource(
                "jdbc:h2:mem:batchdb;DB_CLOSE_DELAY=-1", "sa", "");
    }

    @Bean
    public DataSource sqliteDataSource() {
        org.springframework.jdbc.datasource.DriverManagerDataSource ds =
                new org.springframework.jdbc.datasource.DriverManagerDataSource();
        ds.setUrl("jdbc:sqlite:./data/twitterlike.db");
        ds.setDriverClassName("org.sqlite.JDBC");
        return ds;
    }
}
