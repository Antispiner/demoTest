package com.example.demo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class DemoApplicationTests {

	private static final Logger LOG = LoggerFactory.getLogger(DemoApplicationTests.class);

	@Value("${test.message}")
	private String message;

	@Test
	void contextLoads() {
		assertThat(message).isEqualTo("Test complete");
		LOG.debug(message);
	}

}
