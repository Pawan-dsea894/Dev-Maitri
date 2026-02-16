package com.iexceed.appzillonbanking.core.utils;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jasypt.iv.RandomIvGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JasyptConfig {

	@Bean(name = "jasyptStringEncryptor")
	public StringEncryptor getPasswordEncryptor() {

		PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
		SimpleStringPBEConfig config = new SimpleStringPBEConfig();
		//String encryptedKeyStr = AppzillonAESUtils.encryptString("key", "3ecR31@1234$#$");
		config.setPassword("3ecR31@1234$#$");

		config.setAlgorithm("PBEWithHMACSHA512AndAES_256");
		config.setIvGenerator(new RandomIvGenerator());
		config.setKeyObtentionIterations("1000");
		config.setPoolSize("1");
		config.setSaltGeneratorClassName("org.jasypt.salt.RandomSaltGenerator");
		config.setStringOutputType("base64");

		encryptor.setConfig(config);

		return encryptor;
	}

	

	 public static void main(String[] args)
	 { 
		try { JasyptConfig jasyptConfig = new JasyptConfig(); 
		StringEncryptor encryptor = jasyptConfig.getPasswordEncryptor();
		//String encryptedPw =  encryptor.encrypt("c@glunidb@12345"); 
		//System.out.println(encryptedPw);
	 
	 String decryptedPassword = encryptor.decrypt("9e+O4GoC8Mt8SuFq5OJNuaRyn5hOIIxNjrRdX9NKI0Mwgc7bGJAQvDAPRanGjlpI");
	 System.out.println(decryptedPassword);
	 
	 
	  } catch (Exception e) { // TODOAuto-generated catch block
	  e.printStackTrace(); } }
	
	 
}
