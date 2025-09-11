package com.javaweb.converter.entity_to_dto;

import com.javaweb.dto.reponse.ChiTietXuatKhoResponse;
import com.javaweb.entity.ChiTietXuatKhoEntity;
import org.springframework.stereotype.Component;

@Component
public class ChiTietXuatKhoEntityToDto {

    public ChiTietXuatKhoResponse convertToDto(ChiTietXuatKhoEntity chiTietXuatKhoEntity) {
        ChiTietXuatKhoResponse chiTietXuatKhoResponse = new ChiTietXuatKhoResponse();
        chiTietXuatKhoResponse.setIdNguyenLieu(chiTietXuatKhoEntity.getNguyenLieu().getIdNguyenLieu());
        chiTietXuatKhoResponse.setTenNguyenLieu(chiTietXuatKhoEntity.getNguyenLieu().getTenNguyenLieu());
        chiTietXuatKhoResponse.setSoLuong(chiTietXuatKhoEntity.getSoLuong());
        return chiTietXuatKhoResponse;
    }
}
