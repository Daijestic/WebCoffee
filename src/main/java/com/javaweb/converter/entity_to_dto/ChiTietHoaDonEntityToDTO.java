package com.javaweb.converter.entity_to_dto;

import com.javaweb.dto.reponse.ChiTietHoaDonResponse;
import com.javaweb.entity.ChiTietHoaDonEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChiTietHoaDonEntityToDTO {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    ProductEntiryToDto productEntiryToDto;

    public ChiTietHoaDonResponse converter(ChiTietHoaDonEntity entity) {
        ChiTietHoaDonResponse response = modelMapper.map(entity, ChiTietHoaDonResponse.class);
        response.setProduct(productEntiryToDto.toProductReponse(entity.getMon()));
        return response;
    }
}
