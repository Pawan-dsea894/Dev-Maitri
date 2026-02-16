package com.iexceed.appzillonbanking.cagl.incomeassesment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.iexceed.appzillonbanking.*" })
public class AppzillonBankingIncomeAssesmentApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppzillonBankingIncomeAssesmentApplication.class, args);
	}
}
