package com.javaweb.converter.dto_to_entity;

import com.javaweb.dto.reponse.ChiTietHoaDonResponse;
import com.javaweb.dto.reponse.ChiTietNhapKhoResponse;
import com.javaweb.dto.request.ChiTietNhapKhoRequest;
import com.javaweb.entity.ChiTietHoaDonEntity;
import com.javaweb.entity.ChiTietNhapKhoEntity;
import com.javaweb.entity.NguyenLieuEntity;
import com.javaweb.entity.PhieuNhapKhoEntity;
import com.javaweb.repository.ChiTietNhapKhoRepository;
import com.javaweb.repository.NguyenLieuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChiTietPhieuNhapDtoToEntity {

    @Autowired
    private ChiTietNhapKhoRepository chiTietNhapKhoRepository;

    @Autowired
    private NguyenLieuRepository nguyenLieuRepository;

    public ChiTietNhapKhoEntity toChiTietPhieuNhapEntity(ChiTietNhapKhoRequest chiTietNhapKhoRequest,
                                                         PhieuNhapKhoEntity phieuNhapKhoEntity) {
        ChiTietNhapKhoEntity.ChiTietNhapKhoId chiTietNhapKhoId =
                new ChiTietNhapKhoEntity.ChiTietNhapKhoId(
                        phieuNhapKhoEntity.getIdPhieuNhap(),
                        chiTietNhapKhoRequest.getIdNguyenLieu()
                );

        ChiTietNhapKhoEntity chiTietNhapKhoEntity;

        if (chiTietNhapKhoRepository.existsById(chiTietNhapKhoId)) {
            chiTietNhapKhoEntity = chiTietNhapKhoRepository.findById(chiTietNhapKhoId).get();
        } else {
            chiTietNhapKhoEntity = new ChiTietNhapKhoEntity();
            chiTietNhapKhoEntity.setId(chiTietNhapKhoId);
        }

        NguyenLieuEntity nguyenLieuEntity = nguyenLieuRepository.findById(chiTietNhapKhoRequest.getIdNguyenLieu()).orElse(null);
        if (nguyenLieuEntity != null) {
            nguyenLieuEntity.setSoLuong(nguyenLieuEntity.getSoLuong() + chiTietNhapKhoRequest.getSoLuong());
        }
        // Set both sides of the relationship
        chiTietNhapKhoEntity.setIdPhieuNhapKho(phieuNhapKhoEntity);
        chiTietNhapKhoEntity.setIdNguyenLieu(
                nguyenLieuRepository.findById(chiTietNhapKhoRequest.getIdNguyenLieu())
                        .orElseThrow(() -> new RuntimeException("Nguyên liệu không tồn tại"))
        );

        chiTietNhapKhoEntity.setSoLuong(chiTietNhapKhoRequest.getSoLuong());
        chiTietNhapKhoEntity.setGiaTien(chiTietNhapKhoRequest.getGiaTien());

        return chiTietNhapKhoEntity;
    }
}
