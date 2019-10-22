package com.genfare.cloud.batch.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.genfare.cloud.batch.resolver.DetectedTenant;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

@Configuration
public class MongoDBConfig {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MongoDBConfig.class);
			
	private String connectionString = System.getenv("mongodb_connectionString") == null ? System.getenv("mongodbURL"): System.getenv("mongodb_connectionString");

    private MongoClient client;
	
	@Bean
	public MongoClient buildConnecnametion() {
        if (client != null) {
            LOGGER.warn("Client Connection Already Configured - All Connections will be closed");
            client.close();
        }
        client = new MongoClient(new MongoClientURI(connectionString));
        LOGGER.info("MongoDB Connection Made");
        return client;
    }
    
    @Bean
    public DetectedTenant detectedTenant() {
    	return DetectedTenant.getDetectedTenant();
    }
}