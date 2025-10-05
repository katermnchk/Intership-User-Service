package com.innowise.innowiseuserservice.dto.responsies;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ValidationResponse {
  @JsonProperty("isValid")
  private boolean isValid;
  private String message;
  private String userId;
}
