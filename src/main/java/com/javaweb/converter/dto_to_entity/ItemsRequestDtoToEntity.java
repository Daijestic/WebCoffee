package com.javaweb.converter.dto_to_entity;

import com.javaweb.dto.request.ItemsRequest;
import com.javaweb.entity.*;
import com.javaweb.repository.MonRepository;
import com.javaweb.repository.SizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ItemsRequestDtoToEntity {

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private MonRepository monRepository;

    public ChiTietHoaDonEntity convertToEntity(ItemsRequest itemsRequest, HoaDonEntity hoaDonEntity) {
        ChiTietHoaDonEntity chiTietHoaDonEntity = new ChiTietHoaDonEntity();
        SizeEntity sizeEntity = sizeRepository.findByTenSize(itemsRequest.getSize()).get();
        MonEntity monEntity = monRepository.findById(Long.valueOf(itemsRequest.getId())).get();
        ChiTietHoaDonEntity.ChiTietHoaDonId chiTietHoaDonId = new ChiTietHoaDonEntity.ChiTietHoaDonId(
                monEntity.getIdMon(),
                sizeEntity.getIdSize(),
                hoaDonEntity.getIdHoaDon()
        );
        chiTietHoaDonEntity.setHoaDon(hoaDonEntity);
        chiTietHoaDonEntity.setSize(sizeEntity);
        chiTietHoaDonEntity.setMon(monEntity);
        chiTietHoaDonEntity.setId(chiTietHoaDonId);
        chiTietHoaDonEntity.setSoLuong(Long.valueOf(itemsRequest.getQuantity()));
        return chiTietHoaDonEntity;
    }
}
