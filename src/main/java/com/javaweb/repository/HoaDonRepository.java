package com.javaweb.repository;

import com.javaweb.entity.HoaDonEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface HoaDonRepository extends JpaRepository<HoaDonEntity, Long>, PagingAndSortingRepository<HoaDonEntity, Long> {
    HoaDonEntity findByIdHoaDon(long id);
}
