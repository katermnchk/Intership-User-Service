package com.innowise.innowiseuserservice.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.innowise.innowiseuserservice.dto.cardInfo.CardInfoCreationDto;
import com.innowise.innowiseuserservice.dto.cardInfo.CardInfoResponseDto;
import com.innowise.innowiseuserservice.dto.cardInfo.CardInfoUpdateDto;
import com.innowise.innowiseuserservice.entity.CardInfo;
import com.innowise.innowiseuserservice.entity.User;
import com.innowise.innowiseuserservice.exception.CardNotFoundException;
import com.innowise.innowiseuserservice.exception.DuplicateUserCardException;
import com.innowise.innowiseuserservice.exception.UserNotFoundException;
import com.innowise.innowiseuserservice.mapper.CardInfoMapper;
import com.innowise.innowiseuserservice.repository.CardInfoRepository;
import com.innowise.innowiseuserservice.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

@ExtendWith(MockitoExtension.class)
class CardInfoServiceTest {
  @Mock
  private CardInfoRepository cardInfoRepository;

  @Mock
  private CardInfoMapper cardInfoMapper;

  @Mock
  private UserRepository userRepository;

  @Mock
  private CacheManager cacheManager;

  @Mock
  private Cache cache;

  @InjectMocks
  private CardInfoService cardInfoService;

  private User user;
  private CardInfo card;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(1L);
    user.setName("Katsiaryna");

    card = new CardInfo();
    card.setId(1L);
    card.setCardNumber("1111-2222-3333-4444");
    card.setUser(user);

    lenient().when(cacheManager.getCache("users")).thenReturn(cache);
  }

  @Test
  void givenValidCardRequest_whenCreateCard_thenReturnCardResponseDto() {
    CardInfoCreationDto creationDto = new CardInfoCreationDto();
    creationDto.setUserId(user.getId());
    creationDto.setNumber("1111-2222-3333-4444");

    CardInfoResponseDto responseDto = new CardInfoResponseDto();
    responseDto.setId(card.getId());
    responseDto.setNumber(card.getCardNumber());

    when(cardInfoRepository.existsByUserIdAndCardNumber(user.getId(), creationDto.getNumber()))
        .thenReturn(false);
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    when(cardInfoMapper.cardInfoDtoToCardInfo(creationDto)).thenReturn(card);
    when(cardInfoRepository.save(card)).thenReturn(card);
    when(cardInfoMapper.cardInfoToCardInfoDto(card)).thenReturn(responseDto);

    CardInfoResponseDto result = cardInfoService.createCard(creationDto);

    assertAll(
        () -> assertEquals(responseDto, result),
        () -> verify(cardInfoRepository).existsByUserIdAndCardNumber(user.getId(), creationDto.getNumber()),
        () -> verify(userRepository).findById(user.getId()),
        () -> verify(cardInfoRepository).save(card),
        () -> verify(cardInfoMapper).cardInfoToCardInfoDto(card)
    );
  }

  @Test
  void givenDuplicateCard_whenCreateCard_thenThrowException() {
    CardInfoCreationDto creationDto = new CardInfoCreationDto();
    creationDto.setUserId(user.getId());
    creationDto.setNumber("1111-2222-3333-4444");

    when(cardInfoRepository.existsByUserIdAndCardNumber(user.getId(), creationDto.getNumber()))
        .thenReturn(true);

    assertAll(
        () -> assertThrows(DuplicateUserCardException.class,
            () -> cardInfoService.createCard(creationDto)),
        () -> verify(cardInfoRepository).existsByUserIdAndCardNumber(user.getId(), creationDto.getNumber()),
        () -> verifyNoMoreInteractions(cardInfoRepository),
        () -> verifyNoInteractions(cardInfoMapper)
    );
  }

  @Test
  void givenInvalidUser_whenCreateCard_thenThrowUserNotFound() {
    CardInfoCreationDto creationDto = new CardInfoCreationDto();
    creationDto.setUserId(user.getId());
    creationDto.setNumber("1111-2222-3333-4444");

    when(cardInfoRepository.existsByUserIdAndCardNumber(user.getId(), creationDto.getNumber()))
        .thenReturn(false);
    when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class,
        () -> cardInfoService.createCard(creationDto));
  }

  @Test
  void givenValidId_whenGetCardById_thenReturnDto() {
    Long cardId = 1L;

    CardInfoResponseDto responseDto = new CardInfoResponseDto();
    responseDto.setId(card.getId());

    when(cardInfoRepository.findById(cardId)).thenReturn(Optional.of(card));
    when(cardInfoMapper.cardInfoToCardInfoDto(card)).thenReturn(responseDto);

    CardInfoResponseDto result = cardInfoService.getCardById(cardId);
    assertAll(
        () -> assertEquals(responseDto, result),
        () -> verify(cardInfoRepository).findById(cardId),
        () -> verify(cardInfoMapper).cardInfoToCardInfoDto(card)
    );
  }

  @Test
  void givenInvalidId_whenGetCardById_thenThrowException() {
    Long invalidId = 100L;

    when(cardInfoRepository.findById(invalidId)).thenReturn(Optional.empty());
    assertThrows(CardNotFoundException.class,
        () -> cardInfoService.getCardById(invalidId));
  }

  @Test
  void givenValidIds_whenGetCardsByIds_thenReturnListOfDtos() {
    Long cardId1 = 1L;
    Long cardId2 = 2L;

    CardInfo card2 = new CardInfo();
    card2.setId(cardId2);
    card2.setCardNumber("5555-6666-7777-8888");

    CardInfoResponseDto responseDto1 = new CardInfoResponseDto();
    responseDto1.setId(card.getId());

    CardInfoResponseDto responseDto2 = new CardInfoResponseDto();
    responseDto2.setId(card2.getId());

    when(cardInfoRepository.findAllByIdIn(List.of(cardId1, cardId2)))
        .thenReturn(List.of(card, card2));
    when(cardInfoMapper.cardInfoToCardInfoDto(card)).thenReturn(responseDto1);
    when(cardInfoMapper.cardInfoToCardInfoDto(card2)).thenReturn(responseDto2);

    var result = cardInfoService.getCardsByIds(List.of(cardId1, cardId2));

    assertAll(
        () -> assertEquals(2, result.size()),
        () -> assertTrue(result.contains(responseDto1)),
        () -> assertTrue(result.contains(responseDto2)),
        () -> verify(cardInfoRepository).findAllByIdIn(anyList()),
        () -> verify(cardInfoMapper).cardInfoToCardInfoDto(card),
        () -> verify(cardInfoMapper).cardInfoToCardInfoDto(card2)
    );
  }

  @Test
  void givenInvalidIds_whenGetCardsByIds_thenReturnEmptyList() {
    Long invalidId1 = 100L;
    Long invalidId2 = 200L;

    when(cardInfoRepository.findAllByIdIn(List.of(invalidId1, invalidId2)))
        .thenReturn(Collections.emptyList());

    List<CardInfoResponseDto> result = cardInfoService.getCardsByIds(List.of(invalidId1, invalidId2));

    assertAll(
        () -> assertTrue(result.isEmpty()),
        () -> verify(cardInfoRepository).findAllByIdIn(List.of(invalidId1, invalidId2)),
        () -> verifyNoInteractions(cardInfoMapper)
    );
  }

  @Test
  void givenValidUpdate_whenUpdateCardById_thenReturnUpdatedDto() {
    Long cardId = 1L;
    CardInfoUpdateDto updateDto = new CardInfoUpdateDto();
    updateDto.setNumber("9999-8888-7777-6666");

    CardInfo updatedCard = new CardInfo();
    updatedCard.setId(cardId);
    updatedCard.setCardNumber(updateDto.getNumber());
    updatedCard.setUser(user);

    CardInfoResponseDto responseDto = new CardInfoResponseDto();
    responseDto.setId(cardId);
    responseDto.setNumber(updateDto.getNumber());

    when(cardInfoRepository.findById(cardId)).thenReturn(Optional.of(card));
    when(cardInfoRepository.existsByUserIdAndCardNumber(user.getId(), updateDto.getNumber()))
        .thenReturn(false);
    when(cardInfoRepository.save(card)).thenReturn(updatedCard);
    when(cardInfoMapper.cardInfoToCardInfoDto(updatedCard)).thenReturn(responseDto);

    CardInfoResponseDto result = cardInfoService.updateCardById(cardId, updateDto);

    assertAll(
        () -> assertEquals(responseDto, result),
        () -> verify(cardInfoRepository).findById(cardId),
        () -> verify(cardInfoRepository).existsByUserIdAndCardNumber(user.getId(), updateDto.getNumber()),
        () -> verify(cardInfoRepository).save(card),
        () -> verify(cardInfoMapper).cardInfoToCardInfoDto(updatedCard)
    );
  }

  @Test
  void givenInvalidId_whenUpdateCardById_thenThrowException() {
    Long invalidId = 100L;
    CardInfoUpdateDto updateDto = new CardInfoUpdateDto();
    updateDto.setNumber("9999-8888-7777-6666");

    when(cardInfoRepository.findById(invalidId)).thenReturn(Optional.empty());

    assertThrows(CardNotFoundException.class,
        () -> cardInfoService.updateCardById(invalidId, updateDto));
  }

  @Test
  void givenValidId_whenDeleteCardById_thenDelete() {
    Long cardId = 1L;

    when(cardInfoRepository.findById(cardId)).thenReturn(Optional.of(card));

    cardInfoService.deleteCardById(cardId);

    assertAll(
        () -> verify(cardInfoRepository).findById(cardId),
        () -> verify(cardInfoRepository).deleteById(cardId)
    );
  }

  @Test
  void givenInvalidId_whenDeleteCardById_thenThrowException() {
    Long invalidId = 100L;

    when(cardInfoRepository.findById(invalidId)).thenReturn(Optional.empty());

    assertThrows(CardNotFoundException.class,
        () -> cardInfoService.deleteCardById(invalidId));
  }
}
