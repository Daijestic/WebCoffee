package com.javaweb.converter.dto_to_entity;

import com.javaweb.dto.request.NguyenLieuRequest;
import com.javaweb.entity.NguyenLieuEntity;
import com.javaweb.repository.NguyenLieuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NguyenLieuDtoToEntity {

    @Autowired
    private NguyenLieuRepository nguyenLieuRepository;

    public NguyenLieuEntity convertToEntity(NguyenLieuRequest nguyenLieuRequest) {
        NguyenLieuEntity nguyenLieuEntity;
        if (nguyenLieuRepository.existsByTenNguyenLieu(nguyenLieuRequest.getTenNguyenLieu())) {
            nguyenLieuEntity = nguyenLieuRepository.findByTenNguyenLieu(nguyenLieuRequest.getTenNguyenLieu());
        } else {
            nguyenLieuEntity = new NguyenLieuEntity();
        }
        nguyenLieuEntity.setTenNguyenLieu(nguyenLieuRequest.getTenNguyenLieu());
        nguyenLieuEntity.setSoLuong(nguyenLieuRequest.getSoLuong());
        nguyenLieuEntity.setDonViTinh(nguyenLieuRequest.getDonVi());
        return nguyenLieuEntity;
    }
}
