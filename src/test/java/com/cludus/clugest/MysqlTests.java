package com.cludus.clugest;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
@ActiveProfiles("jpa")
class MysqlTests {

	private static final MySQLContainer<?> CONTAINER = new MySQLContainer<>(DockerImageName.parse("mysql:latest"))
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

	@Test
	void contextLoads() {

	}

	void testPostgres() {

	}
}
