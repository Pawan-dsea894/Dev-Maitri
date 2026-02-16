package com.iexceed.appzillonbanking.kendra.assignment.config;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;

import com.iexceed.appzillonbanking.core.utils.JasyptConfig;

@Configuration
@PropertySource("classpath:application.properties")
public class AppConfig {

	@Primary
	@Bean(name = "kmdatasource")
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSourceProperties getDatasourceProperties() {
		return new DataSourceProperties();
	}

	@Primary
	@Bean(name = "kendraManagementDataSource")
	public DataSource dataSource() {
		JasyptConfig jasyptConfig = new JasyptConfig();
		String decryptedPassword = jasyptConfig.getPasswordEncryptor().decrypt(getDatasourceProperties().getPassword());
		return DataSourceBuilder.create().url(getDatasourceProperties().getUrl())
				.username(getDatasourceProperties().getUsername()).password(decryptedPassword)
				.driverClassName(getDatasourceProperties().getDriverClassName()).build();
	}

}
