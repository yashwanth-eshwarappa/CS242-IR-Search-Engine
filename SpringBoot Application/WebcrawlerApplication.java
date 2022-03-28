package com.group1.webcrawler;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages="com.group1.webcrawler")
public class WebcrawlerApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebcrawlerApplication.class, args);
	}
}
