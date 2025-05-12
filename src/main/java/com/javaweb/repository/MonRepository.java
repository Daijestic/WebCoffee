package com.javaweb.repository;

import com.javaweb.entity.MonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;
import java.util.Optional;

public interface MonRepository extends JpaRepository<MonEntity, Long>, PagingAndSortingRepository<MonEntity, Long> {
    List<MonEntity> findByTenMonContaining(String name);
    Optional<MonEntity> findByIdMon(Long id);
    @Query("SELECT m FROM MonEntity m WHERE m.loaiMon = ?1")
    List<MonEntity> findMonByLoaiMonId(String loaiMonId);
    List<MonEntity> findAllByLoaiMon(String loaiMon);
    List<MonEntity> findByTenMon(String tenMon);

}
