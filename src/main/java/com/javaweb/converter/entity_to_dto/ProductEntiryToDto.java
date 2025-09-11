package com.javaweb.converter.entity_to_dto;

import com.javaweb.dto.reponse.GiaMonSizeResponse;
import com.javaweb.dto.reponse.ProductResponse;
import com.javaweb.entity.GiaMonSizeEntity;
import com.javaweb.entity.MonEntity;
import com.javaweb.entity.SizeEntity;
import com.javaweb.repository.GiaMonSizeRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ProductEntiryToDto {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private GiaMonSizeEntityToDto giaMonSizeEntityToDto;

    public ProductResponse toProductReponse(MonEntity monEntity) {
        ProductResponse productResponse = modelMapper.map(monEntity, ProductResponse.class);
        List<GiaMonSizeEntity> monSizeEntityList = monEntity.getGiaMonSizeEntities();
        List<GiaMonSizeResponse> giaMonSizeResponses = new ArrayList<>();
        if (monSizeEntityList != null && !monSizeEntityList.isEmpty()) {
            for (GiaMonSizeEntity giaMonSizeEntity : monSizeEntityList) {
                giaMonSizeResponses.add(giaMonSizeEntityToDto.toDto(giaMonSizeEntity));
            }
        }
        productResponse.setGiaMonSizeResponses(giaMonSizeResponses);
        productResponse.setTenMon(monEntity.getTenMon());
        productResponse.setIdMon(monEntity.getIdMon());
        productResponse.setLoaiMon(monEntity.getLoaiMon());
        productResponse.setMoTa(monEntity.getMoTa());
        productResponse.setPath("/images/" + monEntity.getPath());
        return productResponse;
    }
}
