package com.javaweb.converter;

import com.javaweb.dto.repository.UserRepository;
import com.javaweb.entity.KhachHangEntity;
import com.javaweb.entity.TaiKhoanEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserEntityToDTO {

    @Autowired
    ModelMapper modelMapper;


    public UserRepository UserEntityToDTO(KhachHangEntity khachHangEntity) {
        UserRepository userRepository = modelMapper.map(khachHangEntity, UserRepository.class);
        TaiKhoanEntity taiKhoanEntity = khachHangEntity.getTaiKhoan();
        userRepository.setUsername(taiKhoanEntity.getUsername());
        return userRepository;
    }
}
