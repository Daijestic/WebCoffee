package com.javaweb.converter.entity_to_dto;

import com.javaweb.dto.reponse.SizeResponse;
import com.javaweb.entity.SizeEntity;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
public class SizeEntityToDto {

    @Autowired
    ModelMapper modelMapper;

    public SizeResponse toSizeResponse(SizeEntity sizeEntity) {
        SizeResponse sizeResponse = new SizeResponse();
        sizeResponse.setIdSize(sizeEntity.getIdSize());
        sizeResponse.setTenSize(sizeEntity.getTenSize());
        return sizeResponse;
    }
}
