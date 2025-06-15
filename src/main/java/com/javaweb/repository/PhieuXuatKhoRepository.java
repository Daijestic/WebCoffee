package com.javaweb.repository;

import com.javaweb.entity.PhieuXuatKhoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface PhieuXuatKhoRepository extends JpaRepository<PhieuXuatKhoEntity, Long>, PagingAndSortingRepository<PhieuXuatKhoEntity, Long> {
    List<PhieuXuatKhoEntity> findByNgayXuatBetween(Date startDate, Date endDate);
    @Query("SELECT SUM(c.soLuong) " +
            "FROM ChiTietXuatKhoEntity c " +
            "WHERE c.id.nguyenLieuId = :idNguyenLieu " +
            "AND c.phieuXuatKho.ngayXuat < :startDate")
    Long sumXuatTruocNgay(@Param("idNguyenLieu") Long idNguyenLieu,
                          @Param("startDate") Date startDate);
}
