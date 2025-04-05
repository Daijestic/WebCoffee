package com.javaweb.repository;

import com.javaweb.entity.NhanVienEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NhanVienRepository extends JpaRepository<NhanVienEntity, Long> {
}
