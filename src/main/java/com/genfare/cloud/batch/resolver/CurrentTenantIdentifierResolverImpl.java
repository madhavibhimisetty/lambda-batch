package com.genfare.cloud.batch.resolver;

import org.hibernate.context.spi.CurrentTenantIdentifierResolver;

public class CurrentTenantIdentifierResolverImpl implements CurrentTenantIdentifierResolver {
	
	private DetectedTenant detectedTenant;

	public CurrentTenantIdentifierResolverImpl(){
		detectedTenant = DetectedTenant.getDetectedTenant();
	}
	
	@Override
	public String resolveCurrentTenantIdentifier() {
		return detectedTenant.getTenant();
	}

	@Override
	public boolean validateExistingCurrentSessions() {
		return true;
	}
}