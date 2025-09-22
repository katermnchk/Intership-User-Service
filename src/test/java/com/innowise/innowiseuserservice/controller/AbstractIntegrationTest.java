package com.innowise.innowiseuserservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.innowiseuserservice.mapper.UserMapper;
import com.innowise.innowiseuserservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
public abstract class AbstractIntegrationTest {

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  @Autowired
  protected UserRepository userRepository;

  @Autowired
  protected UserMapper userMapper;


  @Container
  public static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>("postgres:15")
      .withDatabaseName("testdb")
      .withUsername(System.getenv().getOrDefault("INNOWISE_USER_SERVICE_USERNAME", "test"))
      .withPassword(System.getenv().getOrDefault("INNOWISE_USER_SERVICE_PASSWORD", "test"));

  @Container
  public static final GenericContainer<?> REDIS = new GenericContainer<>(DockerImageName.parse("redis:7.2.4"))
      .withExposedPorts(6379);

  @DynamicPropertySource
  static void overrideProperties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", POSTGRE_SQL_CONTAINER::getJdbcUrl);
    registry.add("spring.datasource.username", POSTGRE_SQL_CONTAINER::getUsername);
    registry.add("spring.datasource.password", POSTGRE_SQL_CONTAINER::getPassword);

    registry.add("spring.liquibase.url", POSTGRE_SQL_CONTAINER::getJdbcUrl);
    registry.add("spring.liquibase.user", POSTGRE_SQL_CONTAINER::getUsername);
    registry.add("spring.liquibase.password", POSTGRE_SQL_CONTAINER::getPassword);

    registry.add("spring.data.redis.host", REDIS::getHost);
    registry.add("spring.data.redis.port", REDIS::getFirstMappedPort);

  }

  @BeforeAll
  static void beforeAll() {
    System.out.println("Postgres container started at: " + POSTGRE_SQL_CONTAINER.getJdbcUrl());
  }
}