package com.innowise.innowiseuserservice.controller;

import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.innowise.innowiseuserservice.dto.cardInfo.CardInfoCreationDto;
import com.innowise.innowiseuserservice.dto.cardInfo.CardInfoUpdateDto;
import com.innowise.innowiseuserservice.dto.user.UserCreationDto;
import com.innowise.innowiseuserservice.entity.User;
import com.innowise.innowiseuserservice.mapper.CardInfoMapper;
import com.innowise.innowiseuserservice.repository.CardInfoRepository;
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
import org.testcontainers.junit.jupiter.Testcontainers;
import org.springframework.http.MediaType;

@SpringBootTest
@Testcontainers
class CardInfoControllerTest extends AbstractIntegrationTest {

  @Autowired
  private CardInfoMapper cardInfoMapper;

  @Autowired
  private CardInfoRepository cardInfoRepository;

  private User user;

  private CardInfoCreationDto createCardInfoCreationDto(String number, String holder, String expirationDate) {
    CardInfoCreationDto dto = new CardInfoCreationDto();
    dto.setUserId(user.getId());
    dto.setNumber(number);
    dto.setHolder(holder);
    dto.setExpirationDate(expirationDate);
    return dto;
  }

  private CardInfoUpdateDto createCardInfoUpdateDTO(String number, String holder, String expirationDate) {
    CardInfoUpdateDto dto = new CardInfoUpdateDto();
    dto.setNumber(number);
    dto.setHolder(holder);
    dto.setExpirationDate(expirationDate);
    return dto;
  }

  @BeforeEach
  void setup() {
    userRepository.deleteAll();

    UserCreationDto createDTO = new UserCreationDto();
    createDTO.setName("Katya");
    createDTO.setSurname("Rem");
    createDTO.setEmail("test@gmail.com");
    createDTO.setBirthDate(LocalDate.of(2005, 10, 16));

    user = userRepository.save(userMapper.userDtoToUser(createDTO));
  }

  @Nested
  class CreateCardInfoTests {

    @Test
    void givenValidCardInfoCreationDto_whenCreateCard_thenCardSaved() throws Exception {
      CardInfoCreationDto dto = createCardInfoCreationDto(
          "1234567812345678", "Katya Rem", "12/25");

      mockMvc.perform(post("/api/v1/cards")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(dto)))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.data.cardNumber").value("1234567812345678"))
          .andExpect(jsonPath("$.data.cardHolder").value("Katya Rem"))
          .andExpect(jsonPath("$.data.expirationDate").value("12/25"))
          .andExpect(jsonPath("$.status").value(201))
          .andExpect(jsonPath("$.message").value("Card created successfully"));
    }

    private static Stream<Arguments> emptyFieldsProvider() {
      return Stream.of(
          Arguments.of("", "Katya Rem", "12/25", "number: Card number must be exactly 16 digits"),
          Arguments.of("1234567812345678", "", "12/25", "holder: Card holder must contain only letters, spaces or hyphens"),
          Arguments.of("1234567812345678", "Katya Rem", "", "expirationDate: Expiration date must be in MM/YY format")
      );
    }

    @ParameterizedTest
    @MethodSource("emptyFieldsProvider")
    void givenEmptyFields_whenCreateCard_thenValidationErrors(
        String number, String holder, String expirationDate, String expectedError) throws Exception {

      CardInfoCreationDto dto = createCardInfoCreationDto(number, holder, expirationDate);

      mockMvc.perform(post("/api/v1/cards")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(dto)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(400))
          .andExpect(jsonPath("$.message", hasItem(expectedError)))
          .andExpect(jsonPath("$.error").value("Bad request"));
    }

    private static Stream<Arguments> invalidNumberProvider() {
      return Stream.of(
          Arguments.of("1234", "number: Card number must be exactly 16 digits"),
          Arguments.of("abcd567812345678", "number: Card number must be exactly 16 digits")
      );
    }

    @ParameterizedTest
    @MethodSource("invalidNumberProvider")
    void givenInvalidNumber_whenCreateCard_thenValidationErrors(String number, String expectedError) throws Exception {
      CardInfoCreationDto dto = createCardInfoCreationDto(number, "Katya Rem", "12/25");

      mockMvc.perform(post("/api/v1/cards")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(dto)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(400))
          .andExpect(jsonPath("$.message", hasItem(expectedError)))
          .andExpect(jsonPath("$.error").value("Bad request"));
    }

    private static Stream<Arguments> invalidExpirationDateProvider() {
      return Stream.of(
          Arguments.of("01.20", "expirationDate: Expiration date must be in MM/YY format"),
          Arguments.of("2025-12", "expirationDate: Expiration date must be in MM/YY format")
      );
    }

    @ParameterizedTest
    @MethodSource("invalidExpirationDateProvider")
    void givenInvalidExpirationDate_whenCreateCard_thenValidationErrors(String expirationDate, String expectedError) throws Exception {
      CardInfoCreationDto dto = createCardInfoCreationDto("1234567812345678", "Katya Rem", expirationDate);

      mockMvc.perform(post("/api/v1/cards")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(dto)))
          .andExpect(status().isBadRequest())
          .andExpect(jsonPath("$.status").value(400))
          .andExpect(jsonPath("$.message", hasItem(expectedError)))
          .andExpect(jsonPath("$.error").value("Bad request"));
    }

    @Test
    void givenDuplicateCardNumber_whenCreateCard_thenConflictError() throws Exception {
      CardInfoCreationDto dto = createCardInfoCreationDto(
          "1234567812345678", "Katya Rem", "12/25");

      mockMvc.perform(post("/api/v1/cards")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(dto)))
          .andExpect(status().isCreated());

      mockMvc.perform(post("/api/v1/cards")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(dto)))
          .andExpect(status().isConflict());
    }

    @Test
    void givenNonExistingUser_whenCreateCard_thenNotFound() throws Exception {
      CardInfoCreationDto dto = new CardInfoCreationDto();
      dto.setUserId(100L);
      dto.setNumber("1111222233334444");
      dto.setHolder("Katya Rem");
      dto.setExpirationDate("12/25");

      mockMvc.perform(post("/api/v1/cards")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(dto)))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.status").value(404))
          .andExpect(jsonPath("$.message").value("User with ID 100 not found"))
          .andExpect(jsonPath("$.error").value("Not found"));
    }

  }

  @Nested
  class GetCardInfoTests {

    private Long cardId1;
    private Long cardId2;

    @BeforeEach
    void initCards() throws Exception {
      CardInfoCreationDto card1 = createCardInfoCreationDto("1111222233334444", "Katya Rem", "12/25");
      CardInfoCreationDto card2 = createCardInfoCreationDto("5555666677778888", "Katya Rem", "01/26");

      mockMvc.perform(post("/api/v1/cards")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(card1)))
          .andExpect(status().isCreated());

      mockMvc.perform(post("/api/v1/cards")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(card2)))
          .andExpect(status().isCreated());

      cardId1 = cardInfoRepository.findAll().get(0).getId();
      cardId2 = cardInfoRepository.findAll().get(1).getId();
    }

    @Test
    void givenExistingCardId_whenGetCardById_thenReturnCard() throws Exception {

      mockMvc.perform(get("/api/v1/cards/{id}", cardId1))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.cardId").value(cardId1))
          .andExpect(jsonPath("$.data.cardNumber").value("1111222233334444"))
          .andExpect(jsonPath("$.data.cardHolder").value("Katya Rem"))
          .andExpect(jsonPath("$.data.expirationDate").value("12/25"))
          .andExpect(jsonPath("$.status").value(200))
          .andExpect(jsonPath("$.message").value("Card fetched successfully"));
    }

    @Test
    void givenNonExistingCardId_whenGetCardById_thenReturnNotFound() throws Exception {
      mockMvc.perform(get("/api/v1/cards/{id}", 100L))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.status").value(404))
          .andExpect(jsonPath("$.error").value("Not found"))
          .andExpect(jsonPath("$.message").value("Card with ID 100 not found"));
    }

    @Test
    void givenExistingIds_whenGetAllCards_thenReturnListOfCards() throws Exception {
      mockMvc.perform(get("/api/v1/cards")
              .param("ids", cardId1.toString(), cardId2.toString()))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data").isArray())
          .andExpect(jsonPath("$.data.length()").value(2))
          .andExpect(jsonPath("$.data[0].cardNumber").value("1111222233334444"))
          .andExpect(jsonPath("$.data[1].cardNumber").value("5555666677778888"))
          .andExpect(jsonPath("$.status").value(200))
          .andExpect(jsonPath("$.message").value("Cards fetched successfully"));
    }

  }

  @Nested
  class UpdateCardInfoTests {

    private Long cardId;

    @BeforeEach
    void initCard() throws Exception {
      CardInfoCreationDto card = createCardInfoCreationDto("9999888877776666", "Katya Rem", "12/25");
      mockMvc.perform(post("/api/v1/cards")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(card)))
          .andExpect(status().isCreated());

      cardId = cardInfoRepository.findAll().get(0).getId();
    }

    @Test
    void givenValidUpdateDto_whenUpdateCard_thenCardUpdated() throws Exception {
      CardInfoUpdateDto updateDto = createCardInfoUpdateDTO(
          "9999888877776666", "Katya Updated", "01/27");

      mockMvc.perform(patch("/api/v1/cards/{id}", cardId)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updateDto)))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.data.cardId").value(cardId))
          .andExpect(jsonPath("$.data.cardHolder").value("Katya Updated"))
          .andExpect(jsonPath("$.data.expirationDate").value("01/27"))
          .andExpect(jsonPath("$.status").value(200))
          .andExpect(jsonPath("$.message").value("Card updated successfully"));
    }

    @Test
    void givenNonExistingCardId_whenUpdateCard_thenNotFound() throws Exception {
      CardInfoUpdateDto updateDto = createCardInfoUpdateDTO(
          "1111222233334444", "Katya Rem", "12/25");

      mockMvc.perform(patch("/api/v1/cards/{id}", 100L)
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(updateDto)))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.status").value(404))
          .andExpect(jsonPath("$.message").value("Card with ID 100 not found"))
          .andExpect(jsonPath("$.error").value("Not found"));
    }
  }

  @Nested
  class DeleteCardInfoTests {

    private Long cardId;

    @BeforeEach
    void initCard() throws Exception {
      CardInfoCreationDto card = createCardInfoCreationDto(
          "2222333344445555", "Katya Rem", "12/25");
      mockMvc.perform(post("/api/v1/cards")
              .contentType(MediaType.APPLICATION_JSON)
              .content(objectMapper.writeValueAsString(card)))
          .andExpect(status().isCreated());

      cardId = cardInfoRepository.findAll().get(0).getId();
    }

    @Test
    void givenExistingCardId_whenDeleteCard_thenDeleted() throws Exception {
      mockMvc.perform(delete("/api/v1/cards/{id}", cardId))
          .andExpect(status().isOk())
          .andExpect(jsonPath("$.status").value(200))
          .andExpect(jsonPath("$.message").value("Card deleted successfully"));

      mockMvc.perform(get("/api/v1/cards/{id}", cardId))
          .andExpect(status().isNotFound());
    }

    @Test
    void givenNonExistingCardId_whenDeleteCard_thenNotFound() throws Exception {
      mockMvc.perform(delete("/api/v1/cards/{id}", 100L))
          .andExpect(status().isNotFound())
          .andExpect(jsonPath("$.status").value(404))
          .andExpect(jsonPath("$.message").value("Card with ID 100 not found"))
          .andExpect(jsonPath("$.error").value("Not found"));
    }
  }


}
