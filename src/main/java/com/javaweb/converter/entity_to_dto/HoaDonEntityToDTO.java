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
        Long sum = 0L;
        if (hoaDonEntity != null && hoaDonEntity.getChiTietHoaDons() != null) {
            List<ChiTietHoaDonEntity> chiTietHoaDonEntities = hoaDonEntity.getChiTietHoaDons();
            for (ChiTietHoaDonEntity chiTietHoaDonEntity : chiTietHoaDonEntities) {
                ChiTietHoaDonResponse chiTietHoaDonResponse = chiTietHoaDonEntityToDTO.converter(chiTietHoaDonEntity);
                sum += chiTietHoaDonResponse.getGiaBan();
                chiTietHoaDonResponses.add(chiTietHoaDonResponse);
            }
        }
        hoaDonResponse.setTenUser(hoaDonEntity.getUser().getHoTen());
        hoaDonResponse.setIdUser(hoaDonEntity.getUser().getIdUser());
        hoaDonResponse.setTongTien(sum);
        hoaDonResponse.setIdHoaDon(hoaDonEntity.getIdHoaDon());
        hoaDonResponse.setProducts(chiTietHoaDonResponses);
        return hoaDonResponse;
    }
}
