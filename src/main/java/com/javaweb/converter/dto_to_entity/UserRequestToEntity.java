package com.javaweb.converter.dto_to_entity;

import com.javaweb.dto.request.UserRequest;
import com.javaweb.entity.UserEntity;
import com.javaweb.repository.UserRepository;
import com.javaweb.repository.TaiKhoanRespository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserRequestToEntity {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TaiKhoanRespository taiKhoanRespository;

    public UserEntity userRequestToEntity(UserRequest userRequest){
        UserEntity userEntity = userRepository.findById(Long.parseLong(userRequest.getId()))
                .orElse(null);
        userEntity.setMatKhau(userRequest.getPassword());
        userEntity.setDangNhap(userRequest.getUsername());
        userEntity.setLoaiUser(Optional.ofNullable(userRequest.getRoles())
                .filter(roles -> !roles.isEmpty())
                .map(roles -> roles.iterator().next())
                .orElse("ROLE_USER")); // Giá trị mặc định);
        modelMapper.map(userRequest,userEntity);
        return userEntity;
    }
}
