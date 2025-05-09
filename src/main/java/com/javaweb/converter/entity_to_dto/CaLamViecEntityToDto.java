package com.javaweb.converter.entity_to_dto;

import com.javaweb.dto.reponse.CaLamVienResponse;
import com.javaweb.entity.CaLamViecEntity;
import org.springframework.stereotype.Component;

@Component
public class CaLamViecEntityToDto {

    public CaLamVienResponse convertToDto(CaLamViecEntity caLamViecEntity) {
        CaLamVienResponse caLamVienResponse = new CaLamVienResponse();
        caLamVienResponse.setIdCa(caLamViecEntity.getIdCa());
        caLamVienResponse.setGioVao(caLamViecEntity.getGiovao());
        caLamVienResponse.setGioRa(caLamViecEntity.getGiaRa());
        return caLamVienResponse;
    }
}
