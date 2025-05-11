package com.javaweb.converter.entity_to_dto;

import com.javaweb.dto.reponse.NhaCungCapResponse;
import com.javaweb.entity.NhaCungCapEntity;
import jakarta.persistence.Column;
import org.springframework.stereotype.Component;

@Component
public class NhaCungCapEntityToDto {

    public NhaCungCapResponse convertToDto(NhaCungCapEntity nhaCungCapEntity) {
        NhaCungCapResponse nhaCungCapResponse = new NhaCungCapResponse();
        nhaCungCapResponse.setIdNhaCungCap(nhaCungCapEntity.getIdNhaCungCap());
        nhaCungCapResponse.setTenNhaCungCap(nhaCungCapEntity.getTenNCC());
        nhaCungCapResponse.setSdt(nhaCungCapEntity.getSDT());
        nhaCungCapResponse.setDiaChi(nhaCungCapEntity.getDiaChi());
        return nhaCungCapResponse;
    }
}
