package com.javaweb.repository;

import com.javaweb.entity.ChiTietXuatKhoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ChiTietXuatKhoRepository extends JpaRepository<ChiTietXuatKhoEntity, ChiTietXuatKhoEntity.ChiTietXuatKhoId> {
    Optional<ChiTietXuatKhoEntity> findById(ChiTietXuatKhoEntity.ChiTietXuatKhoId id);
}
