package com.javaweb.repository;

import com.javaweb.entity.GioHangEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GioHangRepository extends JpaRepository<GioHangEntity, Long> {
    Optional<GioHangEntity> findByUserId(Long userId);
}

