package com.innowise.innowiseuserservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "card_info")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CardInfo {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Column(name = "number", nullable = false, length = 16)
  private String cardNumber;

  @Column(name = "holder", nullable = false, length = 100)
  private String cardHolderName;

  @Column(name = "expiration_date", nullable = false, length = 5)
  private String expirationDate;
}
