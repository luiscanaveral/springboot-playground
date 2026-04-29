package com.example.twitterlike;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TwitterlikeApplication {

    public static void main(String[] args) {
        SpringApplication.run(TwitterlikeApplication.class, args);
    }

}
