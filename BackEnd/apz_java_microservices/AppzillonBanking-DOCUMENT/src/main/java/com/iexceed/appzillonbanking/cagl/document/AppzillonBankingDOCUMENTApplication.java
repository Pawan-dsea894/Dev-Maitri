package com.iexceed.appzillonbanking.cagl.document;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.iexceed.appzillonbanking.*" })
public class AppzillonBankingDOCUMENTApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppzillonBankingDOCUMENTApplication.class, args);
	}
}
