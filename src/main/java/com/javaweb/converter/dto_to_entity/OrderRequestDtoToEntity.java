package com.javaweb.converter.dto_to_entity;

import com.javaweb.dto.request.ItemsRequest;
import com.javaweb.dto.request.OrderRequest;
import com.javaweb.entity.ChiTietHoaDonEntity;
import com.javaweb.entity.HoaDonEntity;
import com.javaweb.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class OrderRequestDtoToEntity {

    @Autowired
    private ItemsRequestDtoToEntity itemsRequestDtoToEntity;

    public HoaDonEntity convertToEntity(OrderRequest orderRequest, HoaDonEntity hoaDonEntity) {
        List<ChiTietHoaDonEntity> chiTietHoaDonEntities = new ArrayList<>();
        for (ItemsRequest itemsRequest : orderRequest.getItems()) {
            ChiTietHoaDonEntity chiTietHoaDonEntity = itemsRequestDtoToEntity.convertToEntity(itemsRequest, hoaDonEntity);
            chiTietHoaDonEntities.add(chiTietHoaDonEntity);
        }
        hoaDonEntity.setChiTietHoaDons(chiTietHoaDonEntities);
        hoaDonEntity.setPhiShip(Long.valueOf(orderRequest.getShippingFee()));
        return hoaDonEntity;
    }
}
