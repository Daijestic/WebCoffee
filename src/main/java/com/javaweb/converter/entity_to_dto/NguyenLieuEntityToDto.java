package com.javaweb.converter.entity_to_dto;

import com.javaweb.dto.reponse.NguyenLieuResponse;
import com.javaweb.entity.NguyenLieuEntity;
import org.springframework.stereotype.Component;

@Component
public class NguyenLieuEntityToDto {

    public NguyenLieuResponse convertToDto(NguyenLieuEntity nguyenLieuEntity){
        NguyenLieuResponse nguyenLieuResponse = new NguyenLieuResponse();
        nguyenLieuResponse.setIdNguyenLieu(nguyenLieuEntity.getIdNguyenLieu());
        nguyenLieuResponse.setTenNguyenLieu(nguyenLieuEntity.getTenNguyenLieu());
        nguyenLieuResponse.setSoLuong(nguyenLieuEntity.getSoLuong());
        nguyenLieuResponse.setDonViTinh(nguyenLieuEntity.getDonViTinh());
        return nguyenLieuResponse;
    }
}
