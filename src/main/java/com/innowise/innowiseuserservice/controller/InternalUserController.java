package com.innowise.innowiseuserservice.controller;

import com.innowise.innowiseuserservice.dto.responsies.ApiResponse;
import com.innowise.innowiseuserservice.dto.user.UserCreationDto;
import com.innowise.innowiseuserservice.dto.user.UserResponseDto;
import com.innowise.innowiseuserservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${app.api.base-path}/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

  private final UserService userService;

  @PostMapping
  public ResponseEntity<ApiResponse<UserResponseDto>> createUser(
      @RequestBody @Valid UserCreationDto userCreationDto
  ) {
    UserResponseDto userDto = userService.createUser(userCreationDto);
    return ResponseEntity.
        status(HttpStatus.CREATED).
        body(new ApiResponse<>(HttpStatus.CREATED.value(), "User created successfully", userDto));
  }

}