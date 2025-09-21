package com.innowise.innowiseuserservice.service;

import com.innowise.innowiseuserservice.dto.user.UserCreationDto;
import com.innowise.innowiseuserservice.dto.user.UserResponseDto;
import com.innowise.innowiseuserservice.dto.user.UserUpdateDto;
import com.innowise.innowiseuserservice.entity.User;
import com.innowise.innowiseuserservice.exception.EmailAlreadyExistsException;
import com.innowise.innowiseuserservice.exception.UserNotFoundException;
import com.innowise.innowiseuserservice.mapper.UserMapper;
import com.innowise.innowiseuserservice.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {

  private final UserRepository userRepository;
  private final UserMapper userMapper;

  @Override
  public UserResponseDto createUser(UserCreationDto userCreationDto) {
    if (userRepository.existsByEmail(userCreationDto.getEmail())) {
      throw new EmailAlreadyExistsException(userCreationDto.getEmail());
    }

    User user = userMapper.userDtoToUser(userCreationDto);
    user = userRepository.save(user);

    return userMapper.userToUserDto(user);
  }

  @Override
  public UserResponseDto getUserById(Long id) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

    return userMapper.userToUserDto(user);
  }

  @Override
  public List<UserResponseDto> getUsersByIdIn(List<Long> ids) {

    List<User> users = userRepository.findAllByIdIn(ids);
    if (users.isEmpty()) {
      throw new UserNotFoundException(ids);
    }

    return userRepository.findAllByIdIn(ids).stream()
        .map(userMapper::userToUserDto)
        .toList();
  }

  @Override
  public UserResponseDto getUserByEmail(String email) {
    User user = userRepository.findByEmail(email)
        .orElseThrow(() -> new UserNotFoundException(email));

    return userMapper.userToUserDto(user);
  }

  @Override
  @Transactional
  public UserResponseDto updateUserById(Long id, UserUpdateDto userUpdateDto) {
    User user = userRepository.findById(id)
        .orElseThrow(() -> new UserNotFoundException(id));

    userMapper.updateEntityFromUserDto(userUpdateDto, user);

    userRepository.save(user);

    return userMapper.userToUserDto(user);
  }

  @Override
  @Transactional
  public void deleteUserById(Long id) {
    if (!userRepository.existsById(id)) {
      throw new UserNotFoundException(id);
    }
    userRepository.deleteById(id);
  }

}