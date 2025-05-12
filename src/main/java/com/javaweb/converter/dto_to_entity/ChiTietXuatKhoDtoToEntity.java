package com.javaweb.converter.dto_to_entity;

import com.javaweb.dto.request.ChiTietXuatKhoRequest;
import com.javaweb.entity.ChiTietXuatKhoEntity;
import com.javaweb.entity.NguyenLieuEntity;
import com.javaweb.entity.PhieuXuatKhoEntity;
import com.javaweb.repository.ChiTietXuatKhoRepository;
import com.javaweb.repository.NguyenLieuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChiTietXuatKhoDtoToEntity {

    @Autowired
    private NguyenLieuRepository nguyenLieuRepository;

    @Autowired
    private ChiTietXuatKhoRepository chiTietXuatKhoRepository;

    public ChiTietXuatKhoEntity convertToEntity(ChiTietXuatKhoRequest request, PhieuXuatKhoEntity phieuXuatKhoEntity) {
        ChiTietXuatKhoEntity.ChiTietXuatKhoId id = new ChiTietXuatKhoEntity.ChiTietXuatKhoId(
                request.getIdNguyenLieu(),
                phieuXuatKhoEntity.getIdPhieuXuatKho()
        );

        // Tìm nếu có sẵn trong DB hoặc tạo mới
        ChiTietXuatKhoEntity entity = chiTietXuatKhoRepository.findById(id).orElse(new ChiTietXuatKhoEntity());
        NguyenLieuEntity nguyenLieuEntity = nguyenLieuRepository.findById(request.getIdNguyenLieu()).orElse(null);
        if (nguyenLieuEntity != null) {
            if (nguyenLieuEntity.getSoLuong() < request.getSoLuong()) {
                throw new RuntimeException("Số lượng nguyên liệu không đủ");
            }
            nguyenLieuEntity.setSoLuong(nguyenLieuEntity.getSoLuong() - request.getSoLuong());
        }
        entity.setId(id);
        entity.setPhieuXuatKho(phieuXuatKhoEntity);
        entity.setNguyenLieu(nguyenLieuRepository.findById(request.getIdNguyenLieu()).orElse(null));
        entity.setSoLuong(request.getSoLuong());

        return entity; // ✅ KHÔNG save ở đây
    }
}

