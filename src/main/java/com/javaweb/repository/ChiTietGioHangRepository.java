package com.javaweb.repository;

import com.javaweb.entity.ChiTietGioHangEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ChiTietGioHangRepository extends JpaRepository<ChiTietGioHangEntity, ChiTietGioHangEntity.ChiTietGioHangId> {
    @Query("SELECT COUNT(c) FROM ChiTietGioHangEntity c JOIN c.gioHang g JOIN g.user u WHERE u.idUser = :userId")
    long countByUserId(@Param("userId") Long userId);
}


