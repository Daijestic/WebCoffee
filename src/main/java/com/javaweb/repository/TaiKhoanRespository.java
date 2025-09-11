package com.javaweb.repository;

import com.javaweb.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaiKhoanRespository extends JpaRepository<UserEntity, Long> {
    boolean existsByDangNhap(String username);
    Optional<UserEntity> findByDangNhap(String username);
}
