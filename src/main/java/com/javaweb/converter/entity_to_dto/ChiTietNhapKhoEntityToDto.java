package com.javaweb.converter.entity_to_dto;

import com.javaweb.dto.reponse.ChiTietNhapKhoResponse;
import com.javaweb.entity.ChiTietNhapKhoEntity;
import org.springframework.stereotype.Component;

@Component
public class ChiTietNhapKhoEntityToDto {

    public ChiTietNhapKhoResponse convertToDto(ChiTietNhapKhoEntity chiTietNhapKhoEntity) {
        ChiTietNhapKhoResponse chiTietNhapKhoResponse = new ChiTietNhapKhoResponse();
        chiTietNhapKhoResponse.setIdNguyenLieu(chiTietNhapKhoEntity.getIdNguyenLieu().getIdNguyenLieu());
        chiTietNhapKhoResponse.setTenNguyenLieu(chiTietNhapKhoEntity.getIdNguyenLieu().getTenNguyenLieu());
        chiTietNhapKhoResponse.setSoLuong(chiTietNhapKhoEntity.getSoLuong());
        chiTietNhapKhoResponse.setGiaTien(chiTietNhapKhoEntity.getGiaTien());
        chiTietNhapKhoResponse.setDonViTinh(chiTietNhapKhoEntity.getIdNguyenLieu().getDonViTinh());
        return chiTietNhapKhoResponse;
    }
}
