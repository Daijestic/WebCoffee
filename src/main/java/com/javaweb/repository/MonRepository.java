package com.javaweb.repository;

import com.javaweb.entity.MonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MonRepository extends JpaRepository<MonEntity, Long> {
    List<MonEntity> findByTenMonContaining(String name);
    Optional<MonEntity> findById(long id);
    @Query("SELECT m FROM MonEntity m WHERE m.loaiMon.id = ?1")
    List<MonEntity> findMonByLoaiMonId(Long loaiMonId);
}
