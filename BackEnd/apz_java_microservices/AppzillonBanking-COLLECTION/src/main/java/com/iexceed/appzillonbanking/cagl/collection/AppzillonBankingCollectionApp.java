package com.iexceed.appzillonbanking.cagl.collection;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "com.iexceed.appzillonbanking.*" })
public class AppzillonBankingCollectionApp {

	public static void main(String[] args) {
		SpringApplication.run(AppzillonBankingCollectionApp.class, args);
	}
}
