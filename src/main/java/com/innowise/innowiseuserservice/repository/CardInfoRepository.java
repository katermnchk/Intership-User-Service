package com.innowise.innowiseuserservice.repository;

import com.innowise.innowiseuserservice.entity.CardInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CardInfoRepository extends JpaRepository<CardInfo, Long> {

  List<CardInfo> findAllByIdIn(List<Long> ids);

  @Modifying
  @Query("UPDATE CardInfo c SET c.cardNumber = :cardNumber,c.cardHolderName = :holder, "
      + "c.expirationDate = :expirationDate WHERE c.id = :id")
  void updateCardById(@Param("id") Long id,
                      @Param("cardNumber") String cardNumber,
                      @Param("holder") String holder,
                      @Param("expirationDate") String expirationDate);

}
