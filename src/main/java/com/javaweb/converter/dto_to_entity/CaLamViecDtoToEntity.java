package com.javaweb.converter.dto_to_entity;

import com.javaweb.dto.request.CaLamViecRequest;
import com.javaweb.entity.CaLamViecEntity;
import com.javaweb.repository.CaLamViecRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CaLamViecDtoToEntity {

    @Autowired
    private CaLamViecRepository caLamViecRepository;

    public CaLamViecEntity convertToDto(CaLamViecRequest caLamViecRequest) {
        CaLamViecEntity caLamViecEntity;
        if (caLamViecRequest.getIdCa() != null) {
            caLamViecEntity = caLamViecRepository.findById(caLamViecRequest.getIdCa()).orElse(null);
        } else {
            caLamViecEntity = new CaLamViecEntity();
        }
        caLamViecEntity.setGioVao(caLamViecRequest.getGioVao());
        caLamViecEntity.setGiaRa(caLamViecRequest.getGioRa());
        return caLamViecEntity;
    }
}
