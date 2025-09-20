package com.innowise.innowiseuserservice.repository;

import com.innowise.innowiseuserservice.entity.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

  List<User> findAllByIdIn(List<Long> ids);

  Optional<User> findByEmail(String email);

}