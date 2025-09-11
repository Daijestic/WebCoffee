package com.javaweb.converter.entity_to_dto;

import com.javaweb.dto.reponse.ChiTietNhapKhoResponse;
import com.javaweb.dto.reponse.PhieuNhapKhoResponse;
import com.javaweb.entity.ChiTietNhapKhoEntity;
import com.javaweb.entity.NhaCungCapEntity;
import com.javaweb.entity.PhieuNhapKhoEntity;
import com.javaweb.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PhieuNhapKhoEntiryToDto {

    @Autowired
    private ChiTietNhapKhoEntityToDto chiTietNhapKhoEntityToDto;

    public PhieuNhapKhoResponse convertToDto(PhieuNhapKhoEntity phieuNhapKhoEntity) {
        PhieuNhapKhoResponse phieuNhapKhoResponse = new PhieuNhapKhoResponse();
        phieuNhapKhoResponse.setIdPhieuNhapKho(phieuNhapKhoEntity.getIdPhieuNhap());
        phieuNhapKhoResponse.setNgayNhap(phieuNhapKhoEntity.getNgayNhap());
        UserEntity userEntity = phieuNhapKhoEntity.getUser();
        NhaCungCapEntity nhaCungCapEntity = phieuNhapKhoEntity.getNhaCungCap();
        phieuNhapKhoResponse.setIdNhanVien(userEntity.getIdUser());
        phieuNhapKhoResponse.setTenNhanVien(userEntity.getHoTen());
        phieuNhapKhoResponse.setIdNhaCungCap(nhaCungCapEntity.getIdNhaCungCap());
        phieuNhapKhoResponse.setTenNhaCungCap(nhaCungCapEntity.getTenNCC());
        List<ChiTietNhapKhoEntity> chiTietNhapKhoEntities = phieuNhapKhoEntity.getChiTietNhapKhoList();
        List<ChiTietNhapKhoResponse> chiTietNhapKhoResponses = new ArrayList<>();
        for (ChiTietNhapKhoEntity chiTietNhapKhoEntity : chiTietNhapKhoEntities) {
            chiTietNhapKhoResponses.add(chiTietNhapKhoEntityToDto.convertToDto(chiTietNhapKhoEntity));
        }
        phieuNhapKhoResponse.setChiTietNhapKhoList(chiTietNhapKhoResponses);
        return phieuNhapKhoResponse;
    }
}
