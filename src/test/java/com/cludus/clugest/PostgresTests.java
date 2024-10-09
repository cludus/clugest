package com.cludus.clugest;

import com.cludus.clugest.dtos.JpaPersonReq;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
@ActiveProfiles("jpa")
class PostgresTests {

	@Autowired
	private TestRestTemplate rest;

	@Container
	private static final PostgreSQLContainer<?> CONTAINER = new PostgreSQLContainer<>("pgvector/pgvector:pg16")
			.withDatabaseName("clugest_real_state")
			.withUsername("testuser")
			.withPassword("testpassword");

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.driver-class-name", CONTAINER::getDriverClassName);
		registry.add("spring.datasource.url", CONTAINER::getJdbcUrl);
		registry.add("spring.datasource.username", CONTAINER::getUsername);
		registry.add("spring.datasource.password", CONTAINER::getPassword);

		registry.add("spring.liquibase.enabled", () -> "true");
		registry.add("spring.liquibase.clear-checksums", () -> "true");
		registry.add("spring.liquibase.driver-class-name", CONTAINER::getDriverClassName);
		registry.add("spring.liquibase.url", CONTAINER::getJdbcUrl);
		registry.add("spring.liquibase.user", CONTAINER::getUsername);
		registry.add("spring.liquibase.password", CONTAINER::getPassword);
	}

	@BeforeAll
	static void beforeAll() {
		CONTAINER.start();
	}

	@AfterAll
	static void afterAll() {
		CONTAINER.stop();
	}

	@Test
	void createPerson() {
		var req = JpaPersonReq.builder()
				.name("name")
				.build();
		var result = rest.postForEntity("/jpa/person", req, JpaPersonReq.class);
		Assertions.assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
	}
}
