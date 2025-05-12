package com.javaweb.repository;

import com.javaweb.entity.ChiTietGioHangEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChiTietGioHangRepository extends JpaRepository<ChiTietGioHangEntity, ChiTietGioHangEntity.ChiTietGioHangId> {

    List<ChiTietGioHangEntity> findByIdUserId(Long userId);

    Optional<ChiTietGioHangEntity> findByIdMonIdAndIdSizeIdAndIdUserId(Long monId, Long sizeId, Long userId);
}



