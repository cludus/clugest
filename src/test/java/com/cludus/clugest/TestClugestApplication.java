package com.cludus.clugest;

import org.springframework.boot.SpringApplication;

public class TestClugestApplication {

	public static void main(String[] args) {
		SpringApplication.from(ClugestApplication::main).with(TestcontainersConfiguration.class).run(args);
	}

}
