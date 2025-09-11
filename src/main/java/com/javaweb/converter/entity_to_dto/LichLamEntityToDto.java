package com.javaweb.converter.entity_to_dto;

import com.javaweb.dto.reponse.LichLamResponse;
import org.springframework.stereotype.Component;

@Component
public class LichLamEntityToDto {

    public LichLamResponse convertToDto(com.javaweb.entity.LichLamEntity lichLamEntity) {
        LichLamResponse lichLamResponse = new LichLamResponse();
        lichLamResponse.setIdLichLam(lichLamEntity.getIdLichLam());
        lichLamResponse.setNgayLam(lichLamEntity.getNgayLam());
        lichLamResponse.setIdUser(lichLamEntity.getUser().getIdUser());
        lichLamResponse.setTenUser(lichLamEntity.getUser().getHoTen());
        lichLamResponse.setIdCa(lichLamEntity.getCaLamViec().getIdCa());
        lichLamResponse.setGioVao(lichLamEntity.getCaLamViec().getGioVao());
        lichLamResponse.setGioRa(lichLamEntity.getCaLamViec().getGiaRa());
        return lichLamResponse;
    }
}
