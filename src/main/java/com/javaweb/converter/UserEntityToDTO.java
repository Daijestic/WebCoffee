package com.javaweb.converter;

import com.javaweb.dto.repository.UserResponse;
import com.javaweb.entity.KhachHangEntity;
import com.javaweb.entity.NhanVienEntity;
import com.javaweb.entity.TaiKhoanEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserEntityToDTO {

    @Autowired
    ModelMapper modelMapper;


    public UserResponse UserEntityToDTO(KhachHangEntity khachHangEntity) {
        UserResponse userResponse = modelMapper.map(khachHangEntity, UserResponse.class);
        TaiKhoanEntity taiKhoanEntity = khachHangEntity.getTaiKhoan();
        userResponse.setUsername(taiKhoanEntity.getUsername());
        userResponse.setRoles(taiKhoanEntity.getRole());
        return userResponse;
    }
}
