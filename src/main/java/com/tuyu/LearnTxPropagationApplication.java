package com.tuyu;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan(basePackages = "com.tuyu.dao")
@SpringBootApplication
public class LearnTxPropagationApplication {

	public static void main(String[] args) {
		SpringApplication.run(LearnTxPropagationApplication.class, args);
	}

}
