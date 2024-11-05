package com.example.bookreadingonline.repository;

import com.example.bookreadingonline.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface UserRepository
    extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

  boolean existsByUsername(String username);

  Optional<User> findFirstByUsername(String username);

}