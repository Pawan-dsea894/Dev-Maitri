package com.iexceed.appzillonbanking.core.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:application.properties")
@ConfigurationProperties(prefix = "ab")
public class ExternalProperties {

	private final Map<String, String> common = new HashMap<>();

	public Map<String, String> getCommon() {
		return common;
	}
	
	

	/*
	 * @Value("${ab.common.interFaceDir}") private String interFaceDir;
	 * 
	 * @Value("${ab.common.soapProtocol}") private String soapProtocol;
	 */

	/*
	 * public Map<String, String> getCommon() { common.put("interFaceDir",
	 * interFaceDir); common.put("soapProtocol", soapProtocol); return common; }
	 */
}
