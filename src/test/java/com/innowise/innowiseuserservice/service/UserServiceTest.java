package com.innowise.innowiseuserservice.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.testcontainers.shaded.com.google.common.primitives.Longs.asList;

import com.innowise.innowiseuserservice.dto.user.UserCreationDto;
import com.innowise.innowiseuserservice.dto.user.UserResponseDto;
import com.innowise.innowiseuserservice.dto.user.UserUpdateDto;
import com.innowise.innowiseuserservice.entity.User;
import com.innowise.innowiseuserservice.exception.EmailAlreadyExistsException;
import com.innowise.innowiseuserservice.exception.UserNotFoundException;
import com.innowise.innowiseuserservice.mapper.UserMapper;
import com.innowise.innowiseuserservice.repository.UserRepository;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
  @Mock
  protected UserRepository userRepository;

  @Mock
  protected UserMapper userMapper;

  @InjectMocks
  protected UserService userService;

  private User user;

  @BeforeEach
  void setUp() {
    user = new User();
    user.setId(1L);
    user.setName("Katsiaryna");
    user.setSurname("Ramenchyk");
    user.setBirthDate(LocalDate.of(2005, 10, 16));
    user.setEmail("test@gmail.com");
  }

  @Test
  void givenValidUserRequest_whenCreateUser_thenReturnUserResponseDto() {
    UserCreationDto creationDto =
        new UserCreationDto("Katsiaryna", "Ramenchyk",
            LocalDate.of(2007, 5, 6), "test@gmail.com");

    UserResponseDto responseDto =
        new UserResponseDto(1L, "Katsiaryna", "Ramenchyk",
            creationDto.getBirthDate(), creationDto.getEmail(), null);

    when(userMapper.userDtoToUser(creationDto)).thenReturn(user);
    when(userRepository.save(user)).thenReturn(user);
    when(userMapper.userToUserDto(user)).thenReturn(responseDto);

    UserResponseDto result = userService.createUser(creationDto);

    assertAll(
        () -> assertEquals(responseDto, result),
        () -> verify(userMapper).userDtoToUser(creationDto),
        () -> verify(userRepository).save(user),
        () -> verify(userMapper).userToUserDto(user)
    );
  }

  @Test
  void givenExistingEmail_whenCreateUser_thenThrowEmailAlreadyExistsException() {
    UserCreationDto creationDto = new UserCreationDto();
    creationDto.setEmail("test@gmail.com");

    when(userRepository.existsByEmail(creationDto.getEmail())).thenReturn(true);

    assertAll(
        () -> assertThrows(EmailAlreadyExistsException.class,
            () -> userService.createUser(creationDto)),
        () -> verify(userRepository).existsByEmail(creationDto.getEmail()),
        () -> verifyNoMoreInteractions(userRepository),
        () -> verifyNoInteractions(userMapper)
    );
  }

  @Test
  void givenValidUserId_whenGetUserById_thenReturnUserResponseDto() {
    Long userId = 1L;

    UserResponseDto responseDto =
        new UserResponseDto(1L, "Katsiaryna", "Ramenchyk",
            user.getBirthDate(), user.getEmail(), null);


    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(userMapper.userToUserDto(user)).thenReturn(responseDto);

    UserResponseDto result = userService.getUserById(userId);

    assertAll(
        () -> assertEquals(responseDto, result),
        () -> verify(userRepository).findById(userId),
        () -> verify(userMapper).userToUserDto(user)
    );
  }

  @Test
  void givenInvalidUserId_whenGetUserById_thenThrowUserNotFoundException() {
    Long userId = 1L;

    when(userRepository.findById(userId)).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class,
        () -> userService.getUserById(userId));
  }

  @Test
  void givenValidUsersId_whenGetUsersByIdIn_thenReturnListOfUserResponseDto() {
    Long userId1 = 1L;
    Long userId2 = 2L;

    User user2 = new User();
    user2.setId(userId2);
    user2.setName("Arseniy");
    user2.setSurname("Herasimovich");
    user2.setBirthDate(LocalDate.of(2007, 5, 6));
    user2.setEmail("arstest@gmail.com");

    UserResponseDto dto1 =
        new UserResponseDto(user.getId(), user.getName(), user.getSurname(),
            user.getBirthDate(), user.getEmail(), null);

    UserResponseDto dto2 =
        new UserResponseDto(user2.getId(), user2.getName(), user2.getSurname(),
            user2.getBirthDate(), user2.getEmail(), null);

    when(userRepository.findAllByIdIn(anyList()))
        .thenReturn(java.util.Arrays.asList(user, user2));
    when(userMapper.userToUserDto(user)).thenReturn(dto1);
    when(userMapper.userToUserDto(user2)).thenReturn(dto2);

    var result = userService.getUsersByIdIn(java.util.Arrays.asList(userId1, userId2));

    assertAll(
        () -> assertEquals(2, result.size()),
        () -> assertTrue(result.contains(dto1)),
        () -> assertTrue(result.contains(dto2)),
        () -> verify(userRepository, times(2)).findAllByIdIn(anyList()),
        () -> verify(userMapper).userToUserDto(user),
        () -> verify(userMapper).userToUserDto(user2)
    );
  }

  @Test
  void givenInvalidUsersId_whenGetUsersByIdIn_thenThrowUserNotFoundException() {
    Long invalidId1 = 100L;
    Long invalidId2 = 200L;

    when(userRepository.findAllByIdIn(List.of(invalidId1, invalidId2)))
        .thenReturn(Collections.emptyList());

    assertAll(
        () -> assertThrows(UserNotFoundException.class,
            () -> userService.getUsersByIdIn(List.of(invalidId1, invalidId2))),
        () -> verify(userRepository).findAllByIdIn(List.of(invalidId1, invalidId2)),
        () -> verifyNoInteractions(userMapper)
    );
  }


  @Test
  void givenValidEmail_whenGetUserByEmail_thenReturnUserResponseDto() {
    String email = "test@gmail.com";

    UserResponseDto responseDto =
        new UserResponseDto(1L, "Katsiaryna", "Ramenchyk",
            user.getBirthDate(), user.getEmail(), null);

    when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
    when(userMapper.userToUserDto(user)).thenReturn(responseDto);

    UserResponseDto result = userService.getUserByEmail(email);

    assertAll(
        () -> assertEquals(responseDto, result),
        () -> verify(userRepository).findByEmail(email),
        () -> verify(userMapper).userToUserDto(user)
    );
  }

  @Test
  void givenInvalidEmail_whenGetUserByEmail_thenThrowUserNotFoundException() {
    when(userRepository.findByEmail("")).thenReturn(Optional.empty());

    assertThrows(UserNotFoundException.class,
        () -> userService.getUserByEmail(""));
  }

  @Test
  void givenValidUserUpdate_whenUpdateUserById_thenReturnUpdatedUserResponseDto() {
    Long userId = 1L;
    var updateDto = new UserUpdateDto();
    updateDto.setName("KatsiarynaUpdated");
    updateDto.setSurname("RamenchykUpdated");

    User updatedUser = new User();
    updatedUser.setId(userId);
    updatedUser.setName(updateDto.getName());
    updatedUser.setSurname(updateDto.getSurname());
    updatedUser.setBirthDate(user.getBirthDate());
    updatedUser.setEmail(user.getEmail());

    UserResponseDto responseDto = new UserResponseDto(
        updatedUser.getId(),
        updatedUser.getName(),
        updatedUser.getSurname(),
        updatedUser.getBirthDate(),
        updatedUser.getEmail(),
        null
    );

    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    doAnswer(invocation -> {
      user.setName(updateDto.getName());
      user.setSurname(updateDto.getSurname());
      return null;
    }).when(userMapper).updateEntityFromUserDto(updateDto, user);
    when(userMapper.userToUserDto(user)).thenReturn(responseDto);
    when(userRepository.save(user)).thenReturn(user);

    UserResponseDto result = userService.updateUserById(userId, updateDto);

    assertAll(
        () -> assertEquals(responseDto, result),
        () -> verify(userRepository).findById(userId),
        () -> verify(userMapper).updateEntityFromUserDto(updateDto, user),
        () -> verify(userRepository).save(user),
        () -> verify(userMapper).userToUserDto(user)
    );
  }

  @Test
  void givenInvalidUserId_whenUpdateUserById_thenThrowUserNotFoundException() {
    Long invalidId = 100L;
    UserUpdateDto updateDto = new UserUpdateDto();

    when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

    assertAll(
        () -> assertThrows(UserNotFoundException.class,
            () -> userService.updateUserById(invalidId, updateDto)),
        () -> verify(userRepository).findById(invalidId),
        () -> verifyNoInteractions(userMapper)
    );
  }

  @Test
  void givenValidUserId_whenDeleteUserById_thenVerifyDeletion() {
    Long userId = 1L;

    when(userRepository.existsById(userId)).thenReturn(true);

    userService.deleteUserById(userId);

    assertAll(
        () -> verify(userRepository).existsById(userId),
        () -> verify(userRepository).deleteById(userId)
    );
  }

  @Test
  void givenInvalidUserId_whenDeleteUserById_thenThrowUserNotFoundException() {
    Long invalidId = 100L;

    when(userRepository.existsById(invalidId)).thenReturn(false);

    assertAll(
        () -> assertThrows(UserNotFoundException.class,
            () -> userService.deleteUserById(invalidId)),
        () -> verify(userRepository).existsById(invalidId),
        () -> verifyNoMoreInteractions(userRepository)
    );
  }

}
