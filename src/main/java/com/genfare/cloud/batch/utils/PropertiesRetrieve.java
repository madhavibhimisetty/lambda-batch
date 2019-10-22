package com.genfare.cloud.batch.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Logger;


import org.slf4j.LoggerFactory;

import com.genfare.cloud.batch.config.MongoDBConfig;

public class PropertiesRetrieve {
	
	 public static void getProperties() {
		Properties properties=null;
		try {
			 properties = new Properties(System.getProperties());
            properties.load(PropertiesRetrieve.class.getClassLoader().getResourceAsStream("application.properties"));
            for (String key : System.getenv().keySet()) {
                properties.put(key, System.getenv(key));
            }
            System.setProperties(properties);
        } catch (Exception e) {
        	e.printStackTrace();
        	
        }
		
	
	}	
	}

