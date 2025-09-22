package com.innowise.innowiseuserservice.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.innowise.innowiseuserservice.dto.user.UserCreationDto;
import com.innowise.innowiseuserservice.dto.user.UserUpdateDto;
import com.innowise.innowiseuserservice.entity.User;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.testcontainers.junit.jupiter.Testcontainers;




@SpringBootTest
@Testcontainers
class UserControllerTest extends AbstractIntegrationTest {

  @Autowired
  private StringRedisTemplate redisTemplate;

  @BeforeEach
  void setup() {
    userRepository.deleteAll();
    redisTemplate.getConnectionFactory().getConnection().flushAll();
  }

  private UserCreationDto createUserCreationDto(String name, String surname, String email,
      LocalDate birthDate) {
    UserCreationDto userCreationDto = new UserCreationDto();
    userCreationDto.setName(name);
    userCreationDto.setSurname(surname);
    userCreationDto.setEmail(email);
    userCreationDto.setBirthDate(birthDate);
    return userCreationDto;
  }

  private UserUpdateDto createUserUpdateDto(String name, String surname, String email,
      LocalDate birthDate) {
    UserUpdateDto userUpdateDto = new UserUpdateDto();
    userUpdateDto.setName(name);
    userUpdateDto.setSurname(surname);
    userUpdateDto.setEmail(email);
    userUpdateDto.setBirthDate(birthDate);
    return userUpdateDto;
  }

  @Nested
  class GivenUserCreation {

    @Test
    void givenValidUserCreationDto_whenCreateUser_thenUserSavedAndCached() throws Exception {
      UserCreationDto userCreationDto = createUserCreationDto(
          "Katsiaryna",
          "Ramenchyk",
          "test@gmail.com",
          LocalDate.of(2005, 10, 16));

      mockMvc.perform(post("/api/v1/users")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(userCreationDto)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.data.userName").value("Katsiaryna"))
          .andExpect(jsonPath("$.data.userSurname").value("Ramenchyk"))
          .andExpect(jsonPath("$.data.userEmail").value("test@gmail.com"))
          .andExpect(jsonPath("$.data.userBirthDate").value("2005-10-16"))
          .andExpect(jsonPath("$.status").value(201))
          .andExpect(jsonPath("$.message").value("User created successfully"));
    }

    private static Stream<Arguments> invalidBirthDateProvider() {
      return Stream.of(
          Arguments.of(LocalDate.now().plusDays(1), "birthDate: Birth date must be in the past"),

          Arguments.of(LocalDate.now(), "birthDate: Birth date must be in the past")
      );
    }

    @ParameterizedTest
    @MethodSource("invalidBirthDateProvider")
    void givenInvalidBirthDate_whenCreateUser_thenValidationErrors(LocalDate birthDate,
        String expectedError) throws Exception {

      UserCreationDto dto = createUserCreationDto("Katsiaryna", "Ramenchyk", "test@gmail.com",
          birthDate);

      mockMvc.perform(post("/api/v1/users")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(dto)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(400))
          .andExpect(jsonPath("$.message", hasItem(expectedError)))
          .andExpect(jsonPath("$.error").value("Bad request"));
    }

    private static Stream<Arguments> emptyFieldsProvider() {
      return Stream.of(
          Arguments.of("", "Ramenchyk", "test@gmail.com",
              LocalDate.of(2005, 10, 16), "name: Name can't be empty"),

          Arguments.of("Katsiaryna", "", "test@gmail.com",
              LocalDate.of(2005, 10, 16), "surname: Surname can't be empty"),

          Arguments.of("Katsiaryna", "Ramenchyk", "",
              LocalDate.of(2005, 10, 16), "email: Email can't be empty")
      );
    }

    @ParameterizedTest
    @MethodSource("emptyFieldsProvider")
    void givenEmptyFields_whenCreateUser_thenValidationErrors(
        String name, String surname, String email, LocalDate birthDate, String expectedError)
        throws Exception {

      UserCreationDto dto = createUserCreationDto(name, surname, email, birthDate);

      mockMvc.perform(post("/api/v1/users")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(dto)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(400))
          .andExpect(jsonPath("$.message", hasItem(expectedError)))
          .andExpect(jsonPath("$.error").value("Bad request"));
    }

    private static Stream<Arguments> invalidEmailProvider() {
      return Stream.of(
          Arguments.of("plainaddress", "email: Email should be valid"),
          Arguments.of("missing-at.com", "email: Email should be valid")
      );
    }

    @ParameterizedTest
    @MethodSource("invalidEmailProvider")
    void givenInvalidEmail_whenCreateUser_thenValidationErrors(
        String email, String expectedError) throws Exception {

      UserCreationDto dto = createUserCreationDto("Katsiaryna", "Ramenchyk", email,
          LocalDate.of(2005, 10, 16));

      mockMvc.perform(post("/api/v1/users")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(dto)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(400))
          .andExpect(jsonPath("$.message", hasItem(expectedError)))
          .andExpect(jsonPath("$.error").value("Bad request"));
    }

    @Test
    void givenDuplicateEmail_whenCreateUser_thenConflictError() throws Exception {
      UserCreationDto userCreationDto = createUserCreationDto(
          "Katsiaryna",
          "Ramenchyk",
          "test@gmail.com",
          LocalDate.of(2005, 10, 16));

      mockMvc.perform(post("/api/v1/users")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(userCreationDto)))
          .andExpect(status().isCreated());

      mockMvc.perform(post("/api/v1/users")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(userCreationDto)))
          .andExpect(status().isConflict());
    }

  }

  @Nested
  class GetUserById {
    private User savedUser;

    @BeforeEach
    void setUp() {
      User user = new User();
      user.setName("Katsiaryna");
      user.setSurname("Ramenchyk");
      user.setEmail("test@gmail.com");
      user.setBirthDate(LocalDate.of(2005, 10, 16));
      savedUser = userRepository.save(user);
    }

    @Test
    void givenExistingUserId_whenGetUserById_thenUserReturnedAndCached() throws Exception {
      mockMvc.perform(get("/api/v1/users/{id}", savedUser.getId())
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.userName").value("Katsiaryna"))
          .andExpect(jsonPath("$.data.userSurname").value("Ramenchyk"))
          .andExpect(jsonPath("$.data.userEmail").value("test@gmail.com"))
          .andExpect(jsonPath("$.data.userBirthDate").value("2005-10-16"))
          .andExpect(jsonPath("$.status").value(200))
          .andExpect(jsonPath("$.message").value("User fetched successfully"));
    }

    @Test
    void givenNonExistingUserId_whenGetUserById_thenNotFound() throws Exception {
      mockMvc.perform(get("/api/v1/users/{id}", 100L)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.status").value(404))
          .andExpect(jsonPath("$.error").value("Not Found"))
          .andExpect(jsonPath("$.message").value("User not found with id: 100"));
    }
  }



}
