package com.javaweb.repository;

import com.javaweb.entity.PhieuXuatKhoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PhieuXuatKhoRepository extends JpaRepository<PhieuXuatKhoEntity, Long>, PagingAndSortingRepository<PhieuXuatKhoEntity, Long> {
}
