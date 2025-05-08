package com.javaweb.repository;

import com.javaweb.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByIdUser(Long aLong);
    void deleteByIdUser(Long id);
    boolean existsByIdUser(Long id);
    Optional<UserEntity> findByEmail(String email);
}
