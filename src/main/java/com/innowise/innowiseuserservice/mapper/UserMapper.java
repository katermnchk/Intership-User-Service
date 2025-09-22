package com.innowise.innowiseuserservice.mapper;

import com.innowise.innowiseuserservice.dto.user.UserCreationDto;
import com.innowise.innowiseuserservice.dto.user.UserResponseDto;
import com.innowise.innowiseuserservice.dto.user.UserUpdateDto;
import com.innowise.innowiseuserservice.entity.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring", uses = {CardInfoMapper.class})
public interface UserMapper {
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "cards", ignore = true)
  User userDtoToUser(UserCreationDto userCreationDto);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "cards", ignore = true)
  @BeanMapping(nullValuePropertyMappingStrategy = org.mapstruct.NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromUserDto(UserUpdateDto userUpdateDto, @MappingTarget User user);

  UserResponseDto userToUserDto(User user);

}