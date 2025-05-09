package com.javaweb.repository;

import com.javaweb.entity.ChiTietNhapKhoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChiTietNhapKhoRepository extends JpaRepository<ChiTietNhapKhoEntity, ChiTietNhapKhoEntity.ChiTietNhapKhoId> {
}
