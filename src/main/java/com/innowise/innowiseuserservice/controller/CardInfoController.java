package com.innowise.innowiseuserservice.controller;

import com.innowise.innowiseuserservice.dto.responsies.ApiResponse;
import com.innowise.innowiseuserservice.dto.cardInfo.CardInfoCreationDto;
import com.innowise.innowiseuserservice.dto.cardInfo.CardInfoResponseDto;
import com.innowise.innowiseuserservice.dto.cardInfo.CardInfoUpdateDto;
import com.innowise.innowiseuserservice.service.CardInfoService;
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
@RequestMapping("${app.api.base-path}/cards")
@RequiredArgsConstructor
public class CardInfoController {

  private final CardInfoService cardInfoService;

  @PostMapping
  public ResponseEntity<ApiResponse<CardInfoResponseDto>> createCardInfo(
      @RequestBody @Valid CardInfoCreationDto cardInfoCreationDto
  ) {
    CardInfoResponseDto cardDto = cardInfoService.createCard(cardInfoCreationDto);
    return ResponseEntity.
        status(HttpStatus.CREATED).
        body(new ApiResponse<>(201, "Card created successfully", cardDto));
  }

  @GetMapping("/{id}")
  public ResponseEntity<ApiResponse<CardInfoResponseDto>> getCardById(@PathVariable Long id) {
    CardInfoResponseDto cardDto = cardInfoService.getCardById(id);
    return ResponseEntity.
        ok(new ApiResponse<>(200, "Card fetched successfully", cardDto));
  }

  @GetMapping
  public ResponseEntity<ApiResponse<List<CardInfoResponseDto>>> getCardsByIds(@RequestParam List<Long> ids) {
    List<CardInfoResponseDto> cards = cardInfoService.getCardsByIds(ids);
    return ResponseEntity.
        ok(new ApiResponse<>(200, "Cards fetched successfully", cards));
  }

  @PatchMapping("/{id}")
  public ResponseEntity<ApiResponse<CardInfoResponseDto>> updateCard(
      @PathVariable Long id,
      @RequestBody @Valid CardInfoUpdateDto cardInfoUpdateDto
  ) {
    CardInfoResponseDto cardDto = cardInfoService.updateCardById(id, cardInfoUpdateDto);
    return ResponseEntity.
        ok(new ApiResponse<>(200, "Card updated successfully", cardDto));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<ApiResponse<Void>> deleteCardInfo(@PathVariable Long id) {
    cardInfoService.deleteCardById(id);
    return ResponseEntity.
        ok(new ApiResponse<>(200, "Card deleted successfully", null));
  }

}
