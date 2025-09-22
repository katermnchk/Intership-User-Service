package com.innowise.innowiseuserservice.dto.cardInfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardInfoCreationDto {
  @JsonProperty("userId")
  @NotNull(message = "User ID can't be empty")
  private Long userId;

  @JsonProperty("cardNumber")
  @NotNull(message = "Card number can't be empty")
  @Pattern(regexp = "^\\d{16}$", message = "Card number must be exactly 16 digits")
  private String number;

  @JsonProperty("cardHolder")
  @NotNull(message = "Card holder can't be empty")
  @Pattern(
      regexp = "^[A-Za-z\\s-]+$",
      message = "Card holder must contain only letters, spaces or hyphens"
  )
  private String holder;

  @JsonProperty("expirationDate")
  @NotNull(message = "Expiration date can't be empty")
  @Pattern(
      regexp = "^(0[1-9]|1[0-2])/\\d{2}$",
      message = "Expiration date must be in MM/YY format"
  )
  private String expirationDate;

}
