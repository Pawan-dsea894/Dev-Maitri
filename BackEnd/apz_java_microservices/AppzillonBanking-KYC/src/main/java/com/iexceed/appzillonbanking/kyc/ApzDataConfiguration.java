
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
@EnableJpaRepositories(entityManagerFactoryRef = "apzEntityManagerFactory", transactionManagerRef = "apzTransactionManager", basePackages = {
		"com.iexceed.appzillonbanking.*.repository.apz" })
public class ApzDataConfiguration {
	
	@Bean(name = "apzDataSourceProps")
	@ConfigurationProperties(prefix = "apz.datasource")
	public DataSourceProperties getDatasourceProperties() {
		return new DataSourceProperties();
	}

	@Bean(name = "apzDataSource")
	//@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource dataSource() {
		JasyptConfig jasyptConfig = new JasyptConfig();
		String decryptedPassword = jasyptConfig.getPasswordEncryptor().decrypt(getDatasourceProperties().getPassword());
		return DataSourceBuilder.create().url(getDatasourceProperties().getUrl())
				.username(getDatasourceProperties().getUsername()).password(decryptedPassword)
				.driverClassName(getDatasourceProperties().getDriverClassName()).build();
	}

	@Bean(name = "apzEntityManagerFactory")
	public LocalContainerEntityManagerFactoryBean apzEntityManagerFactory(EntityManagerFactoryBuilder builder,

			@Qualifier("apzDataSource") DataSource apzDataSource) {
		return builder.dataSource(apzDataSource).packages("com.iexceed.appzillonbanking.*.domain.apz")
				.persistenceUnit("apzdata").build();
	}

	@Bean(name = "apzTransactionManager")
	public PlatformTransactionManager apzTransactionManager(

		@Qualifier("apzEntityManagerFactory") EntityManagerFactory apzEntityManagerFactory) {
		return new JpaTransactionManager(apzEntityManagerFactory);
	}
}
