package com.innowise.innowiseuserservice.controller;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasItem;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.innowise.innowiseuserservice.dto.user.UserCreationDto;
import com.innowise.innowiseuserservice.dto.user.UserUpdateDto;
import com.innowise.innowiseuserservice.entity.User;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.testcontainers.junit.jupiter.Testcontainers;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
@Testcontainers
class UserControllerTest extends AbstractIntegrationTest {

  @Autowired
  private StringRedisTemplate redisTemplate;

  @BeforeEach
  void setup() {
    userRepository.deleteAll();
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

    private User user1;
    private User user2;

    @BeforeEach
    void setUp() {
      user1 = new User();
      user1.setName("Katsiaryna");
      user1.setSurname("Ramenchyk");
      user1.setEmail("test@gmail.com");
      user1.setBirthDate(LocalDate.of(2005, 10, 16));
      user1 = userRepository.save(user1);

      user2 = new User();
      user2.setName("Arseny");
      user2.setSurname("Herasimovich");
      user2.setEmail("arseniy@gmail.com");
      user2.setBirthDate(LocalDate.of(2007, 5, 6));
      user2 = userRepository.save(user2);
    }

    @Test
    void givenExistingUserId_whenGetUserById_thenUserReturnedAndCached() throws Exception {
      mockMvc.perform(get("/api/v1/users/{id}", user1.getId())
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
          .andExpect(jsonPath("$.error").value("Not found"))
          .andExpect(jsonPath("$.message").value("User with ID 100 not found"));
    }

    @Test
    void givenExistingEmail_whenGetUserByEmail_thenUserReturned() throws Exception {
      mockMvc.perform(get("/api/v1/users/email")
              .param("email", user1.getEmail())
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.userName").value("Katsiaryna"))
          .andExpect(jsonPath("$.data.userSurname").value("Ramenchyk"))
          .andExpect(jsonPath("$.data.userEmail").value("test@gmail.com"))
          .andExpect(jsonPath("$.status").value(200))
          .andExpect(jsonPath("$.message").value("User fetched successfully"));
    }

    @Test
    void givenNonExistingEmail_whenGetUserByEmail_thenNotFound() throws Exception {
      mockMvc.perform(get("/api/v1/users/email")
              .param("email", "notfound@gmail.com")
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.status").value(404))
          .andExpect(jsonPath("$.error").value("Not found"))
          .andExpect(jsonPath("$.message").value("User with email: notfound@gmail.com not found"));
    }

    @Test
    void givenValidIds_whenGetUsersByIds_thenReturnUsersList() throws Exception {
      mockMvc.perform(get("/api/v1/users/ids")
              .param("ids", user1.getId() + "," + user2.getId())
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data[0].userName").value("Katsiaryna"))
          .andExpect(jsonPath("$.data[0].userSurname").value("Ramenchyk"))
          .andExpect(jsonPath("$.data[1].userName").value("Arseny"))
          .andExpect(jsonPath("$.data[1].userSurname").value("Herasimovich"))
          .andExpect(jsonPath("$.status").value(200))
          .andExpect(jsonPath("$.message").value("Users fetched successfully"));
    }

    @Test
    void givenMixedExistingAndNonExistingIds_whenGetUsersByIds_thenReturnOnlyExisting()
        throws Exception {
      mockMvc.perform(get("/api/v1/users/ids")
              .param("ids", user1.getId() + ",9999")
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.length()").value(1))
          .andExpect(jsonPath("$.data[0].userName").value("Katsiaryna"))
          .andExpect(jsonPath("$.status").value(200))
          .andExpect(jsonPath("$.message").value("Users fetched successfully"));
    }
  }

  @Nested
  class UpdateUserById {

    private User existingUser;

    @BeforeEach
    void setUp() {
      User user = new User();
      user.setName("Katsiaryna");
      user.setSurname("Ramenchyk");
      user.setEmail("test@gmail.com");
      user.setBirthDate(LocalDate.of(2005, 10, 16));
      existingUser = userRepository.save(user);
    }

    @Test
    void givenValidUserUpdateDto_whenUpdateUser_thenUserUpdated() throws Exception {
      UserUpdateDto updateDto = new UserUpdateDto();
      updateDto.setName("UpdatedName");
      updateDto.setSurname("UpdatedSurname");
      updateDto.setEmail("updated@gmail.com");
      updateDto.setBirthDate(LocalDate.of(2000, 10, 16));

      mockMvc.perform(patch("/api/v1/users/{id}", existingUser.getId())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updateDto)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.userName").value("UpdatedName"))
          .andExpect(jsonPath("$.data.userSurname").value("UpdatedSurname"))
          .andExpect(jsonPath("$.data.userEmail").value("updated@gmail.com"))
          .andExpect(jsonPath("$.data.userBirthDate").value("2000-10-16"))
          .andExpect(jsonPath("$.status").value(200))
          .andExpect(jsonPath("$.message").value("User updated successfully"));
    }

    private static Stream<UserUpdateDto> provideInvalidUpdateDtos() {
      return Stream.of(
          createUpdateDto("", "Surname", "valid@gmail.com", LocalDate.of(2000, 10, 16)),

          createUpdateDto("Name", "", "valid@gmail.com", LocalDate.of(2000, 10, 16)),

          createUpdateDto("Name", "Surname", "invalid-email", LocalDate.of(2000, 10, 16))
      );
    }

    private static UserUpdateDto createUpdateDto(String name, String surname, String email, LocalDate birthDate) {
      UserUpdateDto dto = new UserUpdateDto();
      dto.setName(name);
      dto.setSurname(surname);
      dto.setEmail(email);
      dto.setBirthDate(birthDate);
      return dto;
    }

    @ParameterizedTest
    @MethodSource("provideInvalidUpdateDtos")
    void givenInvalidUpdateDto_whenUpdateUser_thenValidationErrors(UserUpdateDto invalidDto) throws Exception {
      mockMvc.perform(patch("/api/v1/users/{id}", existingUser.getId())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(invalidDto)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(400))
          .andExpect(jsonPath("$.message").isArray())
          .andExpect(jsonPath("$.message[0]").exists())
          .andExpect(jsonPath("$.error").value("Bad request"));
    }

    @Test
    void givenNonExistingUserId_whenUpdateUser_thenNotFound() throws Exception {
      UserUpdateDto updateDto = new UserUpdateDto();
      updateDto.setName("UpdatedName");
      updateDto.setSurname("UpdatedSurname");
      updateDto.setEmail("updated@gmail.com");
      updateDto.setBirthDate(LocalDate.of(2000, 10, 16));

      mockMvc.perform(patch("/api/v1/users/{id}", 100L)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updateDto)))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.status").value(404))
          .andExpect(jsonPath("$.error").value("Not found"))
          .andExpect(jsonPath("$.message").value("User with ID 100 not found"));
    }
  }

  @Nested
  class DeleteUserById {

    private User existingUser;

    @BeforeEach
    void setUp() {
      User user = new User();
      user.setName("ToDelete");
      user.setSurname("User");
      user.setEmail("delete@gmail.com");
      user.setBirthDate(LocalDate.of(2005, 10, 16));
      existingUser = userRepository.save(user);
    }

    @Test
    void givenExistingUserId_whenDeleteUser_thenUserDeleted() throws Exception {
      mockMvc.perform(delete("/api/v1/users/{id}", existingUser.getId())
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(200))
          .andExpect(jsonPath("$.message").value("User deleted successfully"));


      mockMvc.perform(get("/api/v1/users/{id}", existingUser.getId())
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.status").value(404))
          .andExpect(jsonPath("$.error").value("Not found"));
    }

    @Test
    void givenNonExistingUserId_whenDeleteUser_thenNotFound() throws Exception {
      mockMvc.perform(delete("/api/v1/users/{id}", 100L)
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.status").value(404))
          .andExpect(jsonPath("$.error").value("Not found"))
          .andExpect(jsonPath("$.message").value("User with ID 100 not found"));
    }
  }

  @Nested
  class CacheTests {

    private User savedUser;

    @BeforeEach
    void setUp() {
      userRepository.deleteAll();
      redisTemplate.getConnectionFactory().getConnection().flushAll();

      User user = new User();
      user.setName("CacheTest");
      user.setSurname("User");
      user.setEmail("cache@test.com");
      user.setBirthDate(LocalDate.of(1999, 5, 20));
      savedUser = userRepository.save(user);
    }

    @Test
    void givenUserFetched_whenGetUserById_thenUserCachedInRedis() throws Exception {
      mockMvc.perform(get("/api/v1/users/{id}", savedUser.getId())
              .contentType(MediaType.APPLICATION_JSON))
          .andExpect(status().isOk());

      String redisKey = "users::" + savedUser.getId();
      boolean isCached = Boolean.TRUE.equals(redisTemplate.hasKey(redisKey));
      Assertions.assertTrue(isCached, "User should be cached in Redis");
    }

    @Test
    void givenCachedUser_whenUpdateUser_thenCacheUpdated() throws Exception {
      mockMvc.perform(get("/api/v1/users/{id}", savedUser.getId()))
          .andExpect(status().isOk());

      UserUpdateDto updateDto = new UserUpdateDto();
      updateDto.setName("UpdatedName");
      updateDto.setSurname("UpdatedSurname");
      updateDto.setEmail("updated@test.com");
      updateDto.setBirthDate(LocalDate.of(2005, 10, 16));

      mockMvc.perform(patch("/api/v1/users/{id}", savedUser.getId())
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updateDto)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.userName").value("UpdatedName"));

      String redisKey = "users::" + savedUser.getId();
      String cachedValue = redisTemplate.opsForValue().get(redisKey);

      assertThat(cachedValue).contains("UpdatedName");
    }

    @Test
    void givenCachedUser_whenDeleteUser_thenCacheEvicted() throws Exception {
      mockMvc.perform(get("/api/v1/users/{id}", savedUser.getId()))
          .andExpect(status().isOk());

      mockMvc.perform(delete("/api/v1/users/{id}", savedUser.getId()))
          .andExpect(status().isOk());

      String redisKey = "users::" + savedUser.getId();
      String cachedValue = redisTemplate.opsForValue().get(redisKey);

      assertThat(cachedValue).isNull();
    }
  }

}

