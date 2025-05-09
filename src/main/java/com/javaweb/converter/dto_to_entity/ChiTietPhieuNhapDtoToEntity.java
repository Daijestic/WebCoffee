package com.javaweb.converter.dto_to_entity;

import com.javaweb.dto.reponse.ChiTietHoaDonResponse;
import com.javaweb.dto.reponse.ChiTietNhapKhoResponse;
import com.javaweb.dto.request.ChiTietNhapKhoRequest;
import com.javaweb.entity.ChiTietHoaDonEntity;
import com.javaweb.entity.ChiTietNhapKhoEntity;
import com.javaweb.entity.PhieuNhapKhoEntity;
import com.javaweb.repository.ChiTietNhapKhoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChiTietPhieuNhapDtoToEntity {

    @Autowired
    private ChiTietNhapKhoRepository chiTietNhapKhoRepository;

    public ChiTietNhapKhoEntity toChiTietPhieuNhapEntity(ChiTietNhapKhoRequest chiTietNhapKhoResponse,
                                                         PhieuNhapKhoEntity phieuNhapKhoEntity) {
        ChiTietNhapKhoEntity.ChiTietNhapKhoId chiTietNhapKhoId =
                new ChiTietNhapKhoEntity.ChiTietNhapKhoId(phieuNhapKhoEntity.getIdPhieuNhap(),
                        chiTietNhapKhoResponse.getIdNguyenLieu());
        ChiTietNhapKhoEntity chiTietNhapKhoEntity = chiTietNhapKhoRepository.findById(chiTietNhapKhoId).get();
        chiTietNhapKhoEntity.setSoLuong(chiTietNhapKhoResponse.getSoLuong());
        chiTietNhapKhoEntity.setGiaTien(chiTietNhapKhoResponse.getGiaTien());
        chiTietNhapKhoRepository.save(chiTietNhapKhoEntity);
        return chiTietNhapKhoEntity;
    }
}
