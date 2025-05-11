package com.javaweb.repository;

import com.javaweb.entity.LichLamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface LichLamRepository extends JpaRepository<LichLamEntity, Long>, PagingAndSortingRepository<LichLamEntity, Long> {
}
