package com.javaweb.repository;

import com.javaweb.entity.NguyenLieuEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface NguyenLieuRepository extends JpaRepository<NguyenLieuEntity, Long>, PagingAndSortingRepository<NguyenLieuEntity, Long> {
    NguyenLieuEntity findByTenNguyenLieu(String tenNguyenLieu);
    Boolean existsByTenNguyenLieu(String tenNguyenLieu);
    List<NguyenLieuEntity> findBySoLuongLessThanEqual(Long soLuong);
    Page<NguyenLieuEntity> findBySoLuongLessThanEqual(Long soLuong, Pageable pageable);
}
