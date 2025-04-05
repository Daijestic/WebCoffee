package com.javaweb.repository;

import com.javaweb.entity.TaiKhoanEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaiKhoanRespository extends JpaRepository<TaiKhoanEntity, String> {
    boolean existsByUsername(String username);
    Optional<TaiKhoanEntity> findByUsername(String username);
}
