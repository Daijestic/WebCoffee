package com.javaweb.converter.entity_to_dto;

import com.javaweb.dto.reponse.LichSuNhapXuatNguyenLieuResponse;
import com.javaweb.entity.ChiTietNhapKhoEntity;
import com.javaweb.entity.ChiTietXuatKhoEntity;
import com.javaweb.entity.NguyenLieuEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class NhapXuatEntityToDto {

    public List<LichSuNhapXuatNguyenLieuResponse> convertToDto(NguyenLieuEntity nguyenLieuEntity){
        List<LichSuNhapXuatNguyenLieuResponse> lichSuNhapXuatNguyenLieuResponse = new ArrayList<>();
        if (nguyenLieuEntity.getChiTietNhapKhoEntities() != null) {
            for (ChiTietNhapKhoEntity chiTietNhapKho : nguyenLieuEntity.getChiTietNhapKhoEntities()) {
                lichSuNhapXuatNguyenLieuResponse.add(lichSuNhapNguyenLieuResponse(chiTietNhapKho));
            }
        }
        if (nguyenLieuEntity.getChiTietXuatKhoEntities() != null) {
            for (ChiTietXuatKhoEntity chiTietXuatKho : nguyenLieuEntity.getChiTietXuatKhoEntities()) {
                lichSuNhapXuatNguyenLieuResponse.add(lichSuXuatNguyenLieuResponse(chiTietXuatKho));
            }
        }
        return lichSuNhapXuatNguyenLieuResponse;
    }

    public LichSuNhapXuatNguyenLieuResponse lichSuNhapNguyenLieuResponse(ChiTietNhapKhoEntity chiTietNhapKho) {
        return new LichSuNhapXuatNguyenLieuResponse()
                .builder()
                .idMaPhieu(chiTietNhapKho.getIdPhieuNhapKho().getIdPhieuNhap())
                .ngayNhapXuat(chiTietNhapKho.getIdPhieuNhapKho().getNgayNhap())
                .soLuong(chiTietNhapKho.getSoLuong())
                .donViTinh(chiTietNhapKho.getIdNguyenLieu().getDonViTinh())
                .hinhThuc("Nhập kho")
                .giaTien(chiTietNhapKho.getGiaTien())
                .idUser(chiTietNhapKho.getIdPhieuNhapKho().getUser().getIdUser())
                .tenUser(chiTietNhapKho.getIdPhieuNhapKho().getUser().getHoTen())
                .build();
    }

    public LichSuNhapXuatNguyenLieuResponse lichSuXuatNguyenLieuResponse(ChiTietXuatKhoEntity chiTietNhapKho) {
        return new LichSuNhapXuatNguyenLieuResponse()
                .builder()
                .idMaPhieu(chiTietNhapKho.getPhieuXuatKho().getIdPhieuXuatKho())
                .ngayNhapXuat(chiTietNhapKho.getPhieuXuatKho().getNgayXuat())
                .soLuong(chiTietNhapKho.getSoLuong())
                .donViTinh(chiTietNhapKho.getNguyenLieu().getDonViTinh())
                .hinhThuc("Xuất kho")
                .idUser(chiTietNhapKho.getPhieuXuatKho().getUser().getIdUser())
                .tenUser(chiTietNhapKho.getPhieuXuatKho().getUser().getHoTen())
                .build();
    }
}
