package com.genfare.cloud.batch;

import java.sql.SQLException;

import javax.naming.NamingException;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.s3.event.S3EventNotification;
import com.genfare.cloud.batch.service.BadListAndGoodListService;
import com.genfare.cloud.batch.utils.PropertiesRetrieve;

public class BadListGoodListApplication {
//	public static void main(String[] args) throws Exception {
	public String handleRequest(S3EventNotification input, Context con) throws Exception {
		long first_time =System.currentTimeMillis();
	      System.out.println("first milli seconds::"+first_time);
		// PropertiesRetrieve.getProperties();
		@SuppressWarnings("resource")
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.scan("com.genfare.cloud.batch");
		context.refresh();
		BadListAndGoodListService service = context.getBean(BadListAndGoodListService.class);
		service.createGoodListAndBadList();
		long second_time = System.currentTimeMillis();
		System.out.println("second milli seconds::"+second_time);
		long difference=second_time-first_time;
		System.out.println("Final time taken to execute the application in milliseconds::"+difference);
		return "Inserted";
	}
	
}
