package com.innowise.innowiseuserservice.service;

import com.innowise.innowiseuserservice.dto.user.UserCreationDto;
import com.innowise.innowiseuserservice.dto.user.UserResponseDto;
import com.innowise.innowiseuserservice.dto.user.UserUpdateDto;
import java.util.List;

public interface IUserService {

  UserResponseDto createUser(UserCreationDto userCreationDto);

  UserResponseDto getUserById(Long id);

  List<UserResponseDto> getUsersByIdIn(List<Long> ids);

  UserResponseDto getUserByEmail(String email);

  UserResponseDto updateUserById(Long id, UserUpdateDto userUpdateDto);

  void deleteUserById(Long id);

}
