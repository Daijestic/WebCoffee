package com.javaweb.converter.entity_to_dto;

import com.javaweb.dto.reponse.ProductResponse;
import com.javaweb.entity.MonEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductEntiryToDto {

    @Autowired
    private ModelMapper modelMapper;

    public ProductResponse toProductReponse(MonEntity monEntity) {
        ProductResponse productResponse = modelMapper.map(monEntity, ProductResponse.class);
        productResponse.setLoaiMon(monEntity.getLoaiMon().getTenLoaiMon());
        productResponse.setPath("/images/" + monEntity.getPath());
        return productResponse;
    }
}
