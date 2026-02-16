package com.iexceed.appzillonbanking.cagl;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.iexceed.appzillonbanking.core.utils.JasyptConfig;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
@PropertySource("classpath:application.properties")
@EnableJpaRepositories(entityManagerFactoryRef = "cdhEntityManagerFactory", transactionManagerRef = "cdhTransactionManager", basePackages = {
		"com.iexceed.appzillonbanking.cagl.repository.cus" })
public class CDHDataConfiguration {

	@Value("${cdh.jpa.properties.hibernateDialect}")
	private String cdhJPADialect;

	@Value("${cdh.jpa.properties.ddlAuto}")
	private String cdhJPADDLAuto;

	@Value("${cdh.jpa.properties.showSQL}")
	private String cdhShowSQL;

	@Value("${cdh.jpa.properties.formatSQL}")
	private boolean cdhFormatSQL;
	
	@Value("${cdh.datasource.maximumPoolSize}")
	private int maximumPoolSize;
	
	@Value("${cdh.datasource.minimumIdle}")
	private int minimumIdle;
	
	@Value("${cdh.datasource.poolName}")
	private String poolName;
	
	@Value("${cdh.datasource.idleTimeOut}")
	private long cdhIdleTimeout;
	
	@Value("${cdh.datasource.maxLifeTime}")
	private long cdhMaxLifeTime;

	@Bean(name = "cdhDataSourceProps")
	@ConfigurationProperties(prefix = "cdh.datasource")
	public DataSourceProperties getDatasourceProperties() {
		return new DataSourceProperties();
	}

	@Bean(name = "cdhDataSource")
	public DataSource dataSource() {
		JasyptConfig jasyptConfig = new JasyptConfig();
		String decryptedPassword = jasyptConfig.getPasswordEncryptor().decrypt(getDatasourceProperties().getPassword());
		HikariDataSource hikariDataSource = DataSourceBuilder.create()
	            .type(HikariDataSource.class)
	            .url(getDatasourceProperties().getUrl())
	            .username(getDatasourceProperties().getUsername())
	            .password(decryptedPassword)
	            .driverClassName(getDatasourceProperties().getDriverClassName())
	            .build();
	    hikariDataSource.setMaximumPoolSize(maximumPoolSize);
	    hikariDataSource.setMinimumIdle(minimumIdle);
	    hikariDataSource.setPoolName(poolName);
	    hikariDataSource.setIdleTimeout(cdhIdleTimeout);
	    hikariDataSource.setMaxLifetime(cdhMaxLifeTime);
	    return hikariDataSource;
	}

	@Bean(name = "cdhEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean cdhEntityManagerFactory(EntityManagerFactoryBuilder builder,
			@Qualifier("cdhDataSource") DataSource cdhDataSource, JpaProperties cdhJpaProperties) {
		return builder.dataSource(cdhDataSource)
				.packages("com.iexceed.appzillonbanking.cagl.domain.cus", "com.iexceed.appzillonbanking.cagl.entity")
				.persistenceUnit("cdhdata").properties(getJpaProperties(cdhJpaProperties)).build();
	}

	// Common method to configure JPA properties for both datasources
	private Map<String, Object> getJpaProperties(JpaProperties jpaProperties) {
		Map<String, Object> properties = new HashMap<>(jpaProperties.getProperties());
		properties.put("hibernate.hbm2ddl.auto", cdhJPADDLAuto);
		properties.put("hibernate.dialect", cdhJPADialect);
		properties.put("hibernate.show_sql", cdhShowSQL);
		properties.put("hibernate.format_sql", cdhFormatSQL);
		return properties;
	}

	@Bean(name = "cdhTransactionManager")
	public PlatformTransactionManager cdhTransactionManager(
			@Qualifier("cdhEntityManagerFactory") EntityManagerFactory cdhEntityManagerFactory) {
		return new JpaTransactionManager(cdhEntityManagerFactory);
	}
}
