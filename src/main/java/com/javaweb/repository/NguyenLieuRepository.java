package com.javaweb.repository;

import com.javaweb.entity.NguyenLieuEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface NguyenLieuRepository extends JpaRepository<NguyenLieuEntity, Long>, PagingAndSortingRepository<NguyenLieuEntity, Long> {
    NguyenLieuEntity findByTenNguyenLieu(String tenNguyenLieu);
}
