package com.innowise.innowiseuserservice.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserCreationDto {

  @JsonProperty("userName")
  @NotBlank(message = "Name can't be empty")
  @Pattern(
      regexp = "^[\\p{L}\\s-]+$",
      message = "Name must contain only letters, spaces or hyphens"
  )
  private String name;

  @JsonProperty("userSurname")
  @NotBlank(message = "Surname can't be empty")
  @Pattern(
      regexp = "^[\\p{L}\\s-]+$",
      message = "Surname must contain only letters, spaces or hyphens"
  )
  private String surname;

  @JsonProperty("userBirthDate")
  @Past(message = "Birth date must be in the past")
  private LocalDate birthDate;

  @JsonProperty("userEmail")
  @NotBlank(message = "Email can't be empty")
  @Email(message = "Email should be valid")
  private String email;
}
