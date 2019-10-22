package com.genfare.cloud.batch.resolver;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;

import com.genfare.cloud.batch.utils.PropertiesRetrieve;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class DetectedTenant {

	private static final DetectedTenant detectedTenant;
	private List<String> supportedTenantNames = new ArrayList<String>();
//	private final String DB_NAME = "config";
//	private final String COLLECTION_NAME = "tenants";

	private String tenant;

	@Autowired
	private MongoClient mongoClient;
	
	

	static {
		detectedTenant = new DetectedTenant();
	}

	private DetectedTenant() {
		this.tenant = "cdta";
	}

	public String getTenant() {
		return this.tenant;
	}

	public void setTenant(String tenant) {
		this.tenant = tenant;
	}

	public static DetectedTenant getDetectedTenant() {
		return detectedTenant;
	}

	public List<String> getSupportedTenantNames() {
		//Properties properties=propertiesRetrieve.getProperties();
		
		if (supportedTenantNames.isEmpty()) {
			@SuppressWarnings("deprecation")
			DB db = mongoClient.getDB(System.getenv("db_name"));
			DBCollection collection = db.getCollection(System.getenv("collection_name"));
			DBCursor dbCursor = collection.find();
			while (dbCursor.hasNext()) {
				supportedTenantNames.add(String.valueOf(dbCursor.next().get(System.getenv("schema_name"))));
			}
		}
		return this.supportedTenantNames;
	}

}
