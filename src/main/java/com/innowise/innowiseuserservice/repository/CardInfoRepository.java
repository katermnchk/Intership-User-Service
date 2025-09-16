package com.innowise.innowiseuserservice.repository;

import com.innowise.innowiseuserservice.entity.CardInfo;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CardInfoRepository extends JpaRepository<CardInfo, Long> {

  @Query(
      value = "SELECT * FROM card_info WHERE id = :id",
      nativeQuery = true
  )
  Optional<CardInfo> findCardById(Long id);

  List<CardInfo> findAllByIdIn(List<Long> ids);

  @Modifying
  @Transactional
  @Query("UPDATE CardInfo c SET c.cardNumber = :cardNumber,c.cardHolderName = :holder, "
      + "c.expirationDate = :expirationDate WHERE c.id = :id")
  void updateCardById(@Param("id") Long id,
                      @Param("cardNumber") String cardNumber,
                      @Param("holder") String holder,
                      @Param("expirationDate") String expirationDate);

  @Modifying
  @Transactional
  @Query(
      value = "DELETE FROM card_info WHERE id = :id",
      nativeQuery = true
  )
  void deleteCardById(@Param("id") Long id);
}
