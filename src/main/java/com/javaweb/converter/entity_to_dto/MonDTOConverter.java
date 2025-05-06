package com.javaweb.converter.entity_to_dto;

import com.javaweb.model.MonDTO;
import com.javaweb.entity.MonEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MonDTOConverter {

    @Autowired
    private ModelMapper modelMapper;

    public MonDTO convertMonDTO(MonEntity monEntity) {
        MonDTO monDTO = modelMapper.map(monEntity, MonDTO.class);
        return monDTO;
    }
}
