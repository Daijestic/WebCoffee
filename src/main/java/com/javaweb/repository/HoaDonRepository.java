package com.javaweb.repository;

import com.javaweb.entity.HoaDonEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.Date;
import java.util.List;

public interface HoaDonRepository extends JpaRepository<HoaDonEntity, Long>, PagingAndSortingRepository<HoaDonEntity, Long> {
    HoaDonEntity findByIdHoaDon(long id);
    Page<HoaDonEntity> findByTrangThai(String trangThai, Pageable pageable);
    List<HoaDonEntity> findByNgayGioLapHoaDonBetweenAndTrangThai(
            Date startDate, Date endDate, String trangThai);

    List<HoaDonEntity> findByNgayGioLapHoaDonBetween(Date start, Date end);

    Long countByNgayGioLapHoaDonBetween(Date start, Date end);
}
