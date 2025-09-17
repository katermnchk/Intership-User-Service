package com.innowise.innowiseuserservice.dto.user;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.innowise.innowiseuserservice.dto.cardInfo.CardInfoResponseDto;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseDto {
    @JsonProperty("userId")
    private Long id;

    @JsonProperty("userName")
    private String name;

    @JsonProperty("userSurname")
    private String surname;

    @JsonProperty("userBirthDate")
    private LocalDate birthDate;

    @JsonProperty("userEmail")
    private String email;

    @JsonProperty("userCards")
    private List<CardInfoResponseDto> cards;

}
