package com.genfare.cloud.batch.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import com.genfare.cloud.batch.datasource.DataSourceFactory;

@Configuration
@EnableJpaRepositories(basePackages = "com.genfare.cloud.batch.repository")
public class DBConfig {
	
	@Autowired
	DataSource dataSource;

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
		em.setPersistenceUnitName("genfare-batch-jpa");
		em.setDataSource(dataSource);
		em.setJpaProperties(additionalProperties());
		return em;
	}

	@Bean
	public PlatformTransactionManager transactionManager() {
		JpaTransactionManager transactionManager = new JpaTransactionManager();
		transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
		return transactionManager;
	}
	
	@Bean
	public DataSource dataSource() {
		return DataSourceFactory.getDataSource();
	}
	
    private Properties additionalProperties() {
        Properties properties = new Properties();
       // properties.setProperty("hibernate.hbm2ddl.auto", "validate");
        properties.setProperty("hibernate.show_sql", "true");
        properties.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQL5Dialect");
        properties.setProperty("hibernate.multiTenancy", "SCHEMA");
        properties.setProperty("hibernate.multi_tenant_connection_provider", "com.genfare.cloud.batch.provider.SchemaBasedMultiTenantConnectionProvider");
        properties.setProperty("hibernate.tenant_identifier_resolver", "com.genfare.cloud.batch.resolver.CurrentTenantIdentifierResolverImpl");
        properties.setProperty("current_session_context_class","thread");
        properties.setProperty("hibernate.enable_lazy_load_no_trans" , "true");
        return properties;
    }
}