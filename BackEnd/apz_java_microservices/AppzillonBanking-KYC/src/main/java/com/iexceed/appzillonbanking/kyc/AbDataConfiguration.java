
package com.iexceed.appzillonbanking.kyc;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
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

import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableTransactionManagement
//@PropertySource("file:${dbProperties.path}")
@PropertySource("classpath:application.properties")
@EnableJpaRepositories(entityManagerFactoryRef = "abEntityManagerFactory", transactionManagerRef = "abTransactionManager", basePackages = {
		"com.iexceed.appzillonbanking.kyc.repository.ab", "com.iexceed.appzillonbanking.kyc.*.repository.ab",
		"com.iexceed.appzillonbanking.*.repository.ab", "com.iexceed.appzillonbanking.kyc.*.repository.ab",
		"com.iexceed.appzillonbanking.kyc.repository.cus" })
public class AbDataConfiguration {

	@Primary
	@Bean(name = "abDataSourceProps")
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSourceProperties getDatasourceProperties() {
		return new DataSourceProperties();
	}

	@Primary
	@Bean(name = "abDataSource")
	//@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		JasyptConfig jasyptConfig = new JasyptConfig();
		String decryptedPassword = jasyptConfig.getPasswordEncryptor().decrypt(getDatasourceProperties().getPassword());
		return DataSourceBuilder.create().url(getDatasourceProperties().getUrl())
				.username(getDatasourceProperties().getUsername()).password(decryptedPassword)
				.driverClassName(getDatasourceProperties().getDriverClassName()).build();
	}

	@Primary
	@Bean(name = "abEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean barEntityManagerFactory(EntityManagerFactoryBuilder builder,

			@Qualifier("abDataSource") DataSource abDataSource) {
		return builder.dataSource(abDataSource)
				.packages("com.iexceed.appzillonbanking.*.domain.ab", "com.iexceed.appzillonbanking.kyc.domain.ab",
						"com.iexceed.appzillonbanking.kyc.*.domain.ab",
						"com.iexceed.appzillonbanking.kyc.*.domain.ab", "com.iexceed.appzillonbanking.kyc.domain.cus",
						"com.iexceed.appzillonbanking.kyc.entity")
				.persistenceUnit("abdata").build();
	}

	@Primary
	@Bean(name = "abTransactionManager")
	public PlatformTransactionManager abTransactionManager(

			@Qualifier("abEntityManagerFactory") EntityManagerFactory abEntityManagerFactory) {
		return new JpaTransactionManager(abEntityManagerFactory);
	}
}
