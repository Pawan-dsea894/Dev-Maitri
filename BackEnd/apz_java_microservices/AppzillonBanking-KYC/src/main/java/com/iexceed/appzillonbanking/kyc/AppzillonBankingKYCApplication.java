package com.iexceed.appzillonbanking.kyc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.iexceed.appzillonbanking.*"})
public class AppzillonBankingKYCApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppzillonBankingKYCApplication.class, args);
	}

}
