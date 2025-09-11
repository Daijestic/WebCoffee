package com.javaweb.converter.entity_to_dto;

import com.javaweb.dto.reponse.ChiTietHoaDonResponse;
import com.javaweb.entity.ChiTietHoaDonEntity;
import com.javaweb.entity.GiaMonSizeEntity;
import com.javaweb.repository.GiaMonSizeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChiTietHoaDonEntityToDTO {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    ProductEntiryToDto productEntiryToDto;

    @Autowired
    SizeEntityToDto sizeEntityToDto;

    @Autowired
    GiaMonSizeRepository giaMonSizeRepository;

    public ChiTietHoaDonResponse converter(ChiTietHoaDonEntity entity) {
        ChiTietHoaDonResponse response = new ChiTietHoaDonResponse();
        response.setSoLuong(entity.getSoLuong());
        response.setGhiChu(entity.getGhiChu());
        GiaMonSizeEntity giaMonSizeEntity = giaMonSizeRepository
                .getReferenceById(new GiaMonSizeEntity.GiaMonSizeId(entity.getMon().getIdMon(), entity.getSize().getIdSize()));
        if (entity.getSize() != null) {
            response.setSize(entity.getSize().getTenSize());
        }
        if (entity.getMon() != null) {
            response.setIdMon(entity.getMon().getIdMon());
            response.setTenMon(entity.getMon().getTenMon());
        }
        response.setGiaBan(giaMonSizeEntity.getGiaBan());
        return response;
    }
}
