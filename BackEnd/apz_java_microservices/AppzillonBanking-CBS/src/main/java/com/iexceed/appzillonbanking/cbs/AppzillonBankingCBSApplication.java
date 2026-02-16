package com.iexceed.appzillonbanking.cbs;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = {"com.iexceed.appzillonbanking.*"})
public class AppzillonBankingCBSApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppzillonBankingCBSApplication.class, args);
	}

}
