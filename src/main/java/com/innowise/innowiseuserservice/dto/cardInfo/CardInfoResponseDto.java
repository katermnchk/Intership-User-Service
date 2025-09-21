package com.innowise.innowiseuserservice.dto.cardInfo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CardInfoResponseDto {
  @JsonProperty("cardId")
  private Long id;

  @JsonProperty("userId")
  private Long userId;

  @JsonProperty("cardNumber")
  private String number;

  @JsonProperty("cardHolder")
  private String holder;

  @JsonProperty("expirationDate")
  private String expirationDate;
}
