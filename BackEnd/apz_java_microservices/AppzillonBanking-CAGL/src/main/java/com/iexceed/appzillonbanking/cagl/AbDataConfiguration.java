
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
import org.springframework.context.annotation.Primary;
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
//@PropertySource("file:${dbProperties.path}")
@PropertySource("classpath:application.properties")
@EnableJpaRepositories(entityManagerFactoryRef = "abEntityManagerFactory", transactionManagerRef = "abTransactionManager", basePackages = {
		"com.iexceed.appzillonbanking.*.repository.ab", "com.iexceed.appzillonbanking.cagl.repository.ab",
		"com.iexceed.appzillonbanking.cagl.*.repository.ab" })
public class AbDataConfiguration {
	
	
	@Value("${spring.jpa.properties.hibernateDialect}")
	private String springJPADialect;

	@Value("${spring.jpa.properties.ddlAuto}")
	private String springJPADDLAuto;

	@Value("${spring.jpa.properties.showSQL}")
	private String springShowSQL;

	@Value("${spring.jpa.properties.formatSQL}")
	private boolean springFormatSQL;
	
	@Value("${spring.datasource.maximumPoolSize}")
	private int springMaximumPoolSize;
	
	@Value("${spring.datasource.minimumIdle}")
	private int springMinimumIdle;
	
	@Value("${spring.datasource.poolName}")
	private String springPoolName;
	
	@Value("${spring.datasource.idleTimeOut}")
	private long springIdleTimeOut;

	@Primary
	@Bean(name = "abDataSourceProps")
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSourceProperties getDatasourceProperties() {
		return new DataSourceProperties();
	}

	@Primary
	@Bean(name = "abDataSource")
	// @ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		JasyptConfig jasyptConfig = new JasyptConfig();
		String decryptedPassword = jasyptConfig.getPasswordEncryptor().decrypt(getDatasourceProperties().getPassword());
		/*
		 * return DataSourceBuilder.create().url(getDatasourceProperties().getUrl())
		 * .username(getDatasourceProperties().getUsername()).password(
		 * decryptedPassword)
		 * .driverClassName(getDatasourceProperties().getDriverClassName()).build();
		 */
		
		HikariDataSource hikariDataSource = DataSourceBuilder.create()
	            .type(HikariDataSource.class)
	            .url(getDatasourceProperties().getUrl())
	            .username(getDatasourceProperties().getUsername())
	            .password(decryptedPassword)
	            .driverClassName(getDatasourceProperties().getDriverClassName())
	            .build();
	    hikariDataSource.setMaximumPoolSize(springMaximumPoolSize);
	    hikariDataSource.setMinimumIdle(springMinimumIdle);
	    hikariDataSource.setPoolName(springPoolName);
	    hikariDataSource.setIdleTimeout(springIdleTimeOut);
	    return hikariDataSource;
	}

	@Primary
	@Bean(name = "abEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean barEntityManagerFactory(EntityManagerFactoryBuilder builder,

			@Qualifier("abDataSource") DataSource abDataSource, JpaProperties springJpaProperties) {
		return builder
				.dataSource(abDataSource).packages("com.iexceed.appzillonbanking.*.domain.ab",
						"com.iexceed.appzillonbanking.cagl.domain.ab", "com.iexceed.appzillonbanking.cagl.*.domain.ab")
				.persistenceUnit("abdata")
				.properties(getJpaProperties(springJpaProperties))
				.build();
	}
	
	// Common method to configure JPA properties for both datasources
	private Map<String, Object> getJpaProperties(JpaProperties jpaProperties) {
		Map<String, Object> properties = new HashMap<>(jpaProperties.getProperties());
		properties.put("hibernate.hbm2ddl.auto", springJPADDLAuto);
		properties.put("hibernate.dialect", springJPADialect);
		properties.put("hibernate.show_sql", springShowSQL);
		properties.put("hibernate.format_sql", springFormatSQL);
		return properties;
	}

	@Primary
	@Bean(name = "abTransactionManager")
	public PlatformTransactionManager abTransactionManager(

			@Qualifier("abEntityManagerFactory") EntityManagerFactory abEntityManagerFactory) {
		return new JpaTransactionManager(abEntityManagerFactory);
	}
}
