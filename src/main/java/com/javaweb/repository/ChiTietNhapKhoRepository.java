package com.javaweb.repository;

import com.javaweb.entity.ChiTietNhapKhoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChiTietNhapKhoRepository extends JpaRepository<ChiTietNhapKhoEntity, ChiTietNhapKhoEntity.ChiTietNhapKhoId> {
    boolean existsById(ChiTietNhapKhoEntity.ChiTietNhapKhoId id);
    Optional<ChiTietNhapKhoEntity> findById(ChiTietNhapKhoEntity.ChiTietNhapKhoId id);
    void deleteById(ChiTietNhapKhoEntity.ChiTietNhapKhoId id);
}
