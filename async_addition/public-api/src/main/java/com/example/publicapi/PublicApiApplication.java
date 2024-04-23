package com.example.publicapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.web.reactive.function.client.WebClient;

@SpringBootApplication
@EnableKafka
public class PublicApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(PublicApiApplication.class, args);
    }

    @Bean
    public NewTopic topic() {
        return TopicBuilder.name("addition-service.results")
                .partitions(10)
                .replicas(1)
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Bean
    public WebClient webClientAddition() {
        return WebClient.create("http://localhost:3000");
    }

}
