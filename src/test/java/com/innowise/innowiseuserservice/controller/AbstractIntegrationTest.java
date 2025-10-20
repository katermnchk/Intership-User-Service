package com.innowise.innowiseuserservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.innowise.innowiseuserservice.mapper.UserMapper;
import com.innowise.innowiseuserservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
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


  @ServiceConnection
  public static final PostgreSQLContainer<?> POSTGRE_SQL_CONTAINER = new PostgreSQLContainer<>("postgres:15")
      .withDatabaseName("testdb")
      .withUsername(System.getenv().getOrDefault("INNOWISE_USER_SERVICE_USERNAME", "test"))
      .withPassword(System.getenv().getOrDefault("INNOWISE_USER_SERVICE_PASSWORD", "test"));

  @ServiceConnection
  public static final GenericContainer<?> REDIS = new GenericContainer<>(DockerImageName.parse("redis:7.2.4"))
      .withExposedPorts(6379);

  @DynamicPropertySource
  static void configureProperties(DynamicPropertyRegistry registry) {
    registry.add("internal.api.secret", () -> "test-secret-key");
    registry.add("internal.api.header-name", () -> "Internal-Service-Secret");
    registry.add("app.auth-service.validate-url", () -> "http://localhost:9999/validate");
  }

}