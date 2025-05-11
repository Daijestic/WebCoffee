package com.javaweb.converter.entity_to_dto;

import com.javaweb.dto.reponse.StaffReponse;
import com.javaweb.entity.UserEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NhanVienEntiryToDTO {

    @Autowired
    ModelMapper modelMapper;

    public StaffReponse nhanVienEntiryToStaffReponse(UserEntity userEntity) {
        StaffReponse staffReponse = modelMapper.map(userEntity, StaffReponse.class);
        staffReponse.setUsername(userEntity.getDangNhap());
        staffReponse.setRole(userEntity.getLoaiUser());
        return staffReponse;
    }
}
