package com.javaweb.repository;

import com.javaweb.entity.KhachHangEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KhachHangRepository extends JpaRepository<KhachHangEntity, Long> {
    @Override
    Optional<KhachHangEntity> findById(Long aLong);
    void deleteById(Long id);
    boolean existsById(Long id);
}
