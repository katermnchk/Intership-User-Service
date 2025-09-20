package com.innowise.innowiseuserservice.controller;

import com.innowise.innowiseuserservice.dto.responsies.ApiResponse;
import com.innowise.innowiseuserservice.dto.user.UserCreationDto;
import com.innowise.innowiseuserservice.dto.user.UserResponseDto;
import com.innowise.innowiseuserservice.dto.user.UserUpdateDto;
import com.innowise.innowiseuserservice.service.UserService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("${app.api.base-path}/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping
  public ResponseEntity<ApiResponse<UserResponseDto>> createUser(
      @RequestBody @Valid UserCreationDto userCreationDto
  ) {
    UserResponseDto userDto = userService.createUser(userCreationDto);
    return ResponseEntity.
        status(HttpStatus.CREATED).
        body(new ApiResponse<>(201, "User created successfully", userDto));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<UserResponseDto>> getUserById(@PathVariable Long id) {
    UserResponseDto userDto = userService.getUserById(id);
    return ResponseEntity.
        ok(new ApiResponse<>(200, "User fetched successfully", userDto));
  }

  @GetMapping("/email")
  public ResponseEntity<ApiResponse<UserResponseDto>> getUserByEmail(@RequestParam String email) {
    UserResponseDto userDto = userService.getUserByEmail(email);
    return ResponseEntity.
        ok(new ApiResponse<>(200, "User fetched successfully", userDto));
  }

  @GetMapping("/ids")
  public ResponseEntity<ApiResponse<List<UserResponseDto>>> getUsersByIds(@RequestParam List<Long> ids) {
    List<UserResponseDto> users = userService.getUsersByIdIn(ids);
    return ResponseEntity.
        ok(new ApiResponse<>(200, "Users fetched successfully", users));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<ApiResponse<UserResponseDto>> updateUser(
      @PathVariable Long id,
      @RequestBody @Valid UserUpdateDto userUpdateDto
  ) {
    UserResponseDto userDto = userService.updateUserById(id, userUpdateDto);
    return ResponseEntity.
        ok(new ApiResponse<>(200, "User updated successfully", userDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
    userService.deleteUserById(id);
    return ResponseEntity.
        ok(new ApiResponse<>(200, "User deleted successfully", null));
  }

}
