package com.innowise.innowiseuserservice.repository;

import com.innowise.innowiseuserservice.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  List<User> findAllByIdIn(List<Long> ids);

  @Query("SELECT u FROM User u WHERE u.email = :email")
  Optional<User> findByEmail(@Param("email") String email);

  @Modifying
  @Query("UPDATE User u SET u.name = :name, u.surname = :surname, "
      + "u.birthDate = :birthDate, u.email = :email WHERE u.id = :id")
  void updateUserById(@Param("id") Long id,
                      @Param("name") String name,
                      @Param("surname") String surname,
                      @Param("birthDate") java.time.LocalDate birthDate,
                      @Param("email") String email);

}