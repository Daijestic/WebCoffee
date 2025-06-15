package com.javaweb.repository;

import com.javaweb.entity.PhieuNhapKhoEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;

public interface PhieuNhapKhoRepository extends JpaRepository<PhieuNhapKhoEntity, Long>, PagingAndSortingRepository<PhieuNhapKhoEntity, Long> {
    List<PhieuNhapKhoEntity> findByNgayNhapBetween(Date startDate, Date endDate);
    @Query("SELECT SUM(c.soLuong) FROM ChiTietNhapKhoEntity c " +
            "WHERE c.id.idNguyenLieu = :idNguyenLieu " +
            "AND c.idPhieuNhapKho.ngayNhap < :startDate")
    Long sumNhapTruocNgay(@Param("idNguyenLieu") Long idNguyenLieu,
                          @Param("startDate") Date startDate);

}
