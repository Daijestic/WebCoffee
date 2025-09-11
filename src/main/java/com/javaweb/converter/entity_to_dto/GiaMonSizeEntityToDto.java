package com.javaweb.converter.entity_to_dto;

import com.javaweb.dto.reponse.GiaMonSizeResponse;
import com.javaweb.entity.GiaMonSizeEntity;
import com.javaweb.entity.SizeEntity;
import org.springframework.stereotype.Component;

@Component
public class GiaMonSizeEntityToDto {

    public GiaMonSizeResponse toDto(GiaMonSizeEntity giaMonSizeEntity){
        GiaMonSizeResponse giaMonSizeResponse = new GiaMonSizeResponse();
        giaMonSizeResponse.setGiaBan(giaMonSizeEntity.getGiaBan());
        giaMonSizeResponse.setTenSize(giaMonSizeEntity.getSize().getTenSize());
        giaMonSizeResponse.setIdSize(giaMonSizeEntity.getSize().getIdSize());
        return giaMonSizeResponse;
    }
}
