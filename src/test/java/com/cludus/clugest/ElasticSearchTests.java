package com.cludus.clugest;

import com.cludus.clugest.dtos.EsPostReq;
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
import org.testcontainers.elasticsearch.ElasticsearchContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT )
@ActiveProfiles("elasticsearch")
class ElasticSearchTests {

	@Autowired
	private TestRestTemplate rest;

	@Container
	private static final ElasticsearchContainer CONTAINER
			= new ElasticsearchContainer(DockerImageName.parse("docker.elastic.co/elasticsearch/elasticsearch:7.17.10"))
					.withEnv("discovery.type", "single-node");

	@DynamicPropertySource
	static void configureProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.elasticsearch.uris", () -> "http://" + CONTAINER.getHttpHostAddress());
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
	void createPost() {
		var req = EsPostReq.builder()
				.id("1")
				.title("title")
				.content("my content")
				.build();
		String path = "/elasticsearch/post";
		var postResult = rest.postForEntity(path, req, EsPostReq.class);
		Assertions.assertThat(postResult.getStatusCode().is2xxSuccessful()).isTrue();

		var getResult = rest.getForEntity(path + req.getId(), EsPostReq.class);
		Assertions.assertThat(getResult.getStatusCode().is2xxSuccessful()).isTrue();
	}
}
