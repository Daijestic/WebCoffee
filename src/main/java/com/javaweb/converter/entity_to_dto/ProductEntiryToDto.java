package com.javaweb.converter.entity_to_dto;

import com.javaweb.dto.reponse.ProductResponse;
import com.javaweb.entity.GiaMonSizeEntity;
import com.javaweb.entity.MonEntity;
import com.javaweb.entity.SizeEntity;
import com.javaweb.repository.GiaMonSizeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ProductEntiryToDto {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    GiaMonSizeRepository giaMonSizeRepository;

    public ProductResponse toProductReponse(MonEntity monEntity, SizeEntity sizeEntity) {
        ProductResponse productResponse = modelMapper.map(monEntity, ProductResponse.class);
        GiaMonSizeEntity.GiaMonSizeId giaMonSizeId = new GiaMonSizeEntity.GiaMonSizeId(monEntity.getIdMon(), sizeEntity.getIdSize());
        if (sizeEntity != null) {
            GiaMonSizeEntity giaMonSizeEntity = giaMonSizeRepository.findById(giaMonSizeId).orElse(null);
            if (giaMonSizeEntity != null) {
                productResponse.setGiaBan(giaMonSizeEntity.getGiaBan());
            }
        }
        productResponse.setLoaiMon(monEntity.getLoaiMon());
        productResponse.setPath("/images/" + monEntity.getPath());
        return productResponse;
    }
}
