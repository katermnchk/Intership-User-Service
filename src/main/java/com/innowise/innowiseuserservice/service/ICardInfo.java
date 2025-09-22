package com.innowise.innowiseuserservice.service;

import com.innowise.innowiseuserservice.dto.cardInfo.CardInfoCreationDto;
import com.innowise.innowiseuserservice.dto.cardInfo.CardInfoResponseDto;
import com.innowise.innowiseuserservice.dto.cardInfo.CardInfoUpdateDto;
import java.util.List;

public interface ICardInfo {

  CardInfoResponseDto createCard(CardInfoCreationDto cardInfoCreationDto);

  CardInfoResponseDto getCardById(Long id);

  List<CardInfoResponseDto> getCardsByIds(List<Long> ids);

  CardInfoResponseDto updateCardById(Long id, CardInfoUpdateDto cardInfoUpdateDto);

  void deleteCardById(Long id);
}
