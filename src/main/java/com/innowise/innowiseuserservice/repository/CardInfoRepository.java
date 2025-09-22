package com.innowise.innowiseuserservice.repository;

import com.innowise.innowiseuserservice.entity.CardInfo;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardInfoRepository extends JpaRepository<CardInfo, Long> {

  List<CardInfo> findAllByIdIn(List<Long> ids);

  boolean existsByUserIdAndCardNumber(Long id, String cardNumber);
}