package com.innowise.innowiseuserservice.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
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
public class UserUpdateDto {

  @JsonProperty("userName")
  @Pattern(
      regexp = "^[\\p{L}\\s-]+$",
      message = "Name must contain only letters, spaces or hyphens"
  )
  private String name;

  @JsonProperty("userSurname")
  @Pattern(
      regexp = "^[\\p{L}\\s-]+$",
      message = "Surname must contain only letters, spaces or hyphens"
  )
  private String surname;

  @JsonProperty("userBirthDate")
  private LocalDate birthDate;

  @JsonProperty("userEmail")
  @Email(message = "Email should be valid")
  private String email;
}
