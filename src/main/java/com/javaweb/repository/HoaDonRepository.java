package com.javaweb.repository;

import com.javaweb.entity.HoaDonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HoaDonRepository extends JpaRepository<HoaDonEntity, Long> {
    HoaDonEntity findById(long id);
}
