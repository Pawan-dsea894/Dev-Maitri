package com.iexceed.appzillonbanking.core.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
public class CommonProperties {

	private final Map<String, String> common = new HashMap<>();


	public Map<String, String> getCommon() {
		return common;
	}

}
