package com.javaweb.converter.entitytodto;

import com.javaweb.dto.reponse.StaffReponse;
import com.javaweb.entity.NhanVienEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NhanVienEntiryToDTO {

    @Autowired
    ModelMapper modelMapper;

    public StaffReponse nhanVienEntiryToStaffReponse(NhanVienEntity nhanVienEntity) {
        StaffReponse staffReponse = modelMapper.map(nhanVienEntity, StaffReponse.class);
        staffReponse.setUsername(nhanVienEntity.getTaiKhoan().getUsername());
        staffReponse.setRoles(nhanVienEntity.getTaiKhoan().getRole());
        return staffReponse;
    }
}
