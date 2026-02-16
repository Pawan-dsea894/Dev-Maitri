package com.iexceed.appzillonbanking.cagl.loan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableAutoConfiguration
@ComponentScan(basePackages = { "com.iexceed.appzillonbanking.*" })
public class AppzillonBankingLOANApplication {

	public static void main(String[] args) {
		SpringApplication.run(AppzillonBankingLOANApplication.class, args);
	}
}
