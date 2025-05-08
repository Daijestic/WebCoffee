package com.javaweb.converter.entity_to_dto;

import com.javaweb.dto.reponse.ChiTietHoaDonResponse;
import com.javaweb.dto.reponse.HoaDonResponse;
import com.javaweb.entity.ChiTietHoaDonEntity;
import com.javaweb.entity.HoaDonEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class HoaDonEntityToDTO {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private ChiTietHoaDonEntityToDTO chiTietHoaDonEntityToDTO;

    public HoaDonResponse convert(HoaDonEntity hoaDonEntity) {
        HoaDonResponse hoaDonResponse = new HoaDonResponse();
        hoaDonResponse.setNgayGioLapHoaDon(hoaDonEntity.getNgayGioLapHoaDon());
        hoaDonResponse.setDiemDaDung(hoaDonEntity.getDiemDaDung());
        hoaDonResponse.setGiamGia(hoaDonEntity.getGiamGia());
        hoaDonResponse.setPhiShip(hoaDonEntity.getPhiShip());
        hoaDonResponse.setPhuongThucThanhToan(hoaDonEntity.getPhuongThucThanhToan());
        hoaDonResponse.setTrangThai(hoaDonEntity.getTrangThai());
        hoaDonResponse.setHinhThuc(hoaDonEntity.getHinhThuc());

        List<ChiTietHoaDonResponse> chiTietHoaDonResponses = new ArrayList<>();
        if (hoaDonEntity != null && hoaDonEntity.getChiTietHoaDons() != null) {
            List<ChiTietHoaDonEntity> chiTietHoaDonEntities = hoaDonEntity.getChiTietHoaDons();
            // Xử lý tiếp...
            for (ChiTietHoaDonEntity chiTietHoaDonEntity : chiTietHoaDonEntities) {
                chiTietHoaDonResponses.add(chiTietHoaDonEntityToDTO.converter(chiTietHoaDonEntity));
            }
        }
        hoaDonResponse.setProducts(chiTietHoaDonResponses);
        return hoaDonResponse;
    }
}
