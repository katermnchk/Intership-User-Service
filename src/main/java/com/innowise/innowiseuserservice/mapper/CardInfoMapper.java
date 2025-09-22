package com.innowise.innowiseuserservice.mapper;

import com.innowise.innowiseuserservice.dto.cardInfo.CardInfoCreationDto;
import com.innowise.innowiseuserservice.dto.cardInfo.CardInfoResponseDto;
import com.innowise.innowiseuserservice.dto.cardInfo.CardInfoUpdateDto;
import com.innowise.innowiseuserservice.entity.CardInfo;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface CardInfoMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  @Mapping(source = "number", target = "cardNumber")
  @Mapping(source = "holder", target = "cardHolderName")
  CardInfo cardInfoDtoToCardInfo(CardInfoCreationDto cardInfoCreationDto);

  @Mapping(target = "userId", expression = "java(cardInfo.getUser().getId())")
  @Mapping(source = "cardNumber", target = "number")
  @Mapping(source = "cardHolderName", target = "holder")
  CardInfoResponseDto cardInfoToCardInfoDto(CardInfo cardInfo);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "user", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
  void updateCardInfoFromCardInfoDto(
      CardInfoUpdateDto cardInfoUpdateDto,
      @MappingTarget CardInfo cardInfo
  );

}