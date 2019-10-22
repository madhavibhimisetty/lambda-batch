package com.genfare.cloud.batch.datasource;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.genfare.cloud.batch.utils.PropertiesRetrieve;

public class DataSourceFactory {

	private static DataSource dataSource;
	
	

	private DataSourceFactory() {

	}

	public static DataSource getDataSource() {
		if (dataSource == null) {
			synchronized(DataSourceFactory.class) {
				if(dataSource == null)
					dataSource = new DataSourceFactory().dataSource();
			}
		}
		return dataSource;
	}

	/*
	 * private DataSource dataSource() { DriverManagerDataSource dataSource = new
	 * DriverManagerDataSource();
	 * dataSource.setUsername(env.getProperty("default.db.username"));
	 * dataSource.setPassword(env.getProperty("default.db.password"));
	 * dataSource.setUrl(env.getProperty("default.db.ulr")); return dataSource; }
	 */
	
	private DataSource dataSource() {
		DriverManagerDataSource dataSource = new DriverManagerDataSource();
		dataSource.setDriverClassName(System.getenv("jdbc_driver"));
		dataSource.setUsername(System.getenv("jdbc_username"));
		dataSource.setPassword(System.getenv("jdbc_password"));
		dataSource.setUrl(System.getenv("jdbc_url"));
		return dataSource;
	}
}
