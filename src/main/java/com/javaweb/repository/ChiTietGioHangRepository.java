package com.javaweb.repository;

import com.javaweb.entity.ChiTietGioHangEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietGioHangRepository extends JpaRepository<ChiTietGioHangEntity, ChiTietGioHangEntity.ChiTietGioHangId> {
}



