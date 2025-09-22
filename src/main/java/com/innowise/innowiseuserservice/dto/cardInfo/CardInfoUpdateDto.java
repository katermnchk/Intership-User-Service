package com.innowise.innowiseuserservice.dto.cardInfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Setter;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardInfoUpdateDto {

  @JsonProperty("cardNumber")
  @Pattern(regexp = "^\\d{16}$", message = "Card number must be exactly 16 digits")
  private String number;

  @JsonProperty("cardHolder")
  @Pattern(
      regexp = "^[A-Za-z\\s-]+$",
      message = "Card holder must contain only letters, spaces or hyphens"
  )
  private String holder;

  @JsonProperty("expirationDate")
  @Pattern(
      regexp = "^(0[1-9]|1[0-2])/\\d{2}$",
      message = "Expiration date must be in MM/YY format"
  )
  private String expirationDate;
}
