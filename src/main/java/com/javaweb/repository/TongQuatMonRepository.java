package com.javaweb.repository;

import com.javaweb.entity.TongQuatMonEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TongQuatMonRepository extends JpaRepository<TongQuatMonEntity, Long> {
    boolean existsByTenLoaiMon(String tenLoaiMon);
    TongQuatMonEntity findByTenLoaiMon(String tenLoaiMon);
}
