package com.example.readingwritingexcel.config;

import java.text.MessageFormat;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoClientDatabaseFactory;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;

/**
 * @author aek
 */
@Configuration
public class MongodbConfig {
    private final MongodbProperties mongodbProperties;

    public MongodbConfig(MongodbProperties mongodbProperties) {
        this.mongodbProperties = mongodbProperties;
    }

    @Bean
    public MongoClient mongoClient() {

        var connectionString =MessageFormat.format("mongodb://{0}:{1}@{2}:{3}/",
                mongodbProperties.getUsername(), mongodbProperties.getPassword(), mongodbProperties.getHost(), mongodbProperties.getPort());

        return MongoClients.create(connectionString);
    }

    @Bean
    public MongoTemplate mongoTemplate(MongoClient mongoClient) {
        return new MongoTemplate(mongoClient, mongodbProperties.getDatabase());
    }

    @Bean
    public MongoDatabaseFactory mongoDatabaseFactory(MongoClient mongoClient) {
        return new SimpleMongoClientDatabaseFactory(mongoClient, mongodbProperties.getDatabase());
    }

    @Bean
    public MongoTransactionManager transactionManager(MongoDatabaseFactory mongoDatabaseFactory) {
        return new MongoTransactionManager(mongoDatabaseFactory);
    }

}