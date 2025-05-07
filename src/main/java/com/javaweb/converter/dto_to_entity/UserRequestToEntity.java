package com.javaweb.converter.dto_to_entity;

import com.javaweb.dto.request.UserRequest;
import com.javaweb.entity.KhachHangEntity;
import com.javaweb.entity.TaiKhoanEntity;
import com.javaweb.repository.KhachHangRepository;
import com.javaweb.repository.TaiKhoanRespository;
import com.javaweb.service.impl.KhachHangServiceImpl;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserRequestToEntity {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    KhachHangRepository khachHangRepository;

    @Autowired
    TaiKhoanRespository taiKhoanRespository;

    public KhachHangEntity userRequestToEntity(UserRequest userRequest){
        KhachHangEntity khachHangEntity = khachHangRepository.findById(Long.parseLong(userRequest.getId()))
                .orElse(null);
        TaiKhoanEntity taiKhoanEntity = taiKhoanRespository.findByUsername(userRequest.getUsername()).get();
        taiKhoanEntity.setPassword(userRequest.getPassword());
        taiKhoanEntity.setUsername(userRequest.getUsername());
        taiKhoanEntity.setRoles(userRequest.getRoles());
        taiKhoanRespository.save(taiKhoanEntity);
        khachHangEntity.setTaiKhoan(taiKhoanEntity);
        modelMapper.map(userRequest,khachHangEntity);
        return khachHangEntity;
    }
}
