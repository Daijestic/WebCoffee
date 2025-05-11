package com.javaweb.converter.entity_to_dto;

import com.javaweb.dto.reponse.ChiTietXuatKhoResponse;
import com.javaweb.dto.reponse.PhieuXuatKhoResponse;
import com.javaweb.entity.ChiTietXuatKhoEntity;
import com.javaweb.entity.PhieuXuatKhoEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PhieuXuatKhoEntityToDto {

    @Autowired
    private ChiTietXuatKhoEntityToDto chiTietXuatKhoEntityToDto;

    public PhieuXuatKhoResponse convertToDto(PhieuXuatKhoEntity phieuXuatKhoEntity){
        PhieuXuatKhoResponse phieuXuatKhoResponse = new PhieuXuatKhoResponse();
        phieuXuatKhoResponse.setIdPhieuXuatKho(phieuXuatKhoEntity.getIdPhieuXuatKho());
        phieuXuatKhoResponse.setNgayXuat(phieuXuatKhoEntity.getNgayXuat());
        if (phieuXuatKhoEntity.getUser() != null) {
            phieuXuatKhoResponse.setIdNhanVien(phieuXuatKhoEntity.getUser().getIdUser());
            phieuXuatKhoResponse.setTenNhanVien(phieuXuatKhoEntity.getUser().getHoTen());
        }
        List<ChiTietXuatKhoEntity> chiTietXuatKhoEntities = phieuXuatKhoEntity.getChiTietXuatKhoList();
        if (chiTietXuatKhoEntities != null && !chiTietXuatKhoEntities.isEmpty()) {
            List<ChiTietXuatKhoResponse> chiTietXuatKhoResponses = chiTietXuatKhoEntities.stream()
                    .map(chiTietXuatKhoEntityToDto::convertToDto)
                    .toList();
            phieuXuatKhoResponse.setChiTietXuatKhoList(chiTietXuatKhoResponses);
        }
        return phieuXuatKhoResponse;
    }
}
