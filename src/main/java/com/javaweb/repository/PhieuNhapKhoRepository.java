package com.javaweb.repository;

import com.javaweb.entity.PhieuNhapKhoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface PhieuNhapKhoRepository extends JpaRepository<PhieuNhapKhoEntity, Long>, PagingAndSortingRepository<PhieuNhapKhoEntity, Long> {
}
