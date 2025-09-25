package com.innowise.innowiseuserservice.service;

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
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardInfoService implements ICardInfo {

  private final CardInfoRepository cardInfoRepository;
  private final CardInfoMapper cardInfoMapper;
  private final UserRepository userRepository;

  @Override
  public CardInfoResponseDto createCard(CardInfoCreationDto cardInfoCreationDto) {
    if (cardInfoRepository.existsByUserIdAndCardNumber(
        cardInfoCreationDto.getUserId(), cardInfoCreationDto.getNumber())
    ) {
      throw new DuplicateUserCardException(cardInfoCreationDto.getNumber());
    }

    User user = userRepository.findById(cardInfoCreationDto.getUserId())
        .orElseThrow(() -> new UserNotFoundException(cardInfoCreationDto.getUserId()));

    CardInfo cardInfo = cardInfoMapper.cardInfoDtoToCardInfo(cardInfoCreationDto);
    cardInfo.setUser(user);
    cardInfo = cardInfoRepository.save(cardInfo);

    return cardInfoMapper.cardInfoToCardInfoDto(cardInfo);
  }

  @Override
  public CardInfoResponseDto getCardById(Long id) {
    CardInfo cardInfo = cardInfoRepository.findById(id)
        .orElseThrow(() -> new CardNotFoundException(id));

    return cardInfoMapper.cardInfoToCardInfoDto(cardInfo);
  }

  @Override
  public List<CardInfoResponseDto> getCardsByIds(List<Long> ids) {
    return cardInfoRepository.findAllByIdIn(ids).stream()
        .map(cardInfoMapper::cardInfoToCardInfoDto)
        .toList();
  }

  @Override
  @Transactional
  public CardInfoResponseDto updateCardById(Long id, CardInfoUpdateDto cardInfoUpdateDto) {

    CardInfo cardInfo = cardInfoRepository.findById(id)
        .orElseThrow(() -> new CardNotFoundException(id));

    if (cardInfoRepository.existsByUserIdAndCardNumber(
        cardInfo.getUser().getId(), cardInfoUpdateDto.getNumber()) &&
        !cardInfoUpdateDto.getNumber().equals(cardInfo.getCardNumber())
    ) {
      throw new DuplicateUserCardException(cardInfoUpdateDto.getNumber());
    }

    cardInfoMapper.updateCardInfoFromCardInfoDto(cardInfoUpdateDto, cardInfo);

    CardInfo updatedCard = cardInfoRepository.save(cardInfo);

    return cardInfoMapper.cardInfoToCardInfoDto(updatedCard);
  }

  @Override
  @Transactional
  public void deleteCardById(Long id) {
    CardInfo cardInfo = cardInfoRepository.findById(id)
        .orElseThrow(() -> new CardNotFoundException(id));

    cardInfoRepository.deleteById(id);
  }

}
