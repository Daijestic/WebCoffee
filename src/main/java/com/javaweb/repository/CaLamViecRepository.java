package com.javaweb.repository;

import com.javaweb.entity.CaLamViecEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface CaLamViecRepository extends JpaRepository<CaLamViecEntity, Long>, PagingAndSortingRepository<CaLamViecEntity, Long> {
}
