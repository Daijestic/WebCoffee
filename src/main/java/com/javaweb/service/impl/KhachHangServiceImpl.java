package com.javaweb.service.impl;

import com.javaweb.converter.dto_to_entity.UserRequestToEntity;
import com.javaweb.converter.entity_to_dto.UserEntityToDTO;
import com.javaweb.dto.reponse.UserResponse;
import com.javaweb.dto.request.UserRequest;
import com.javaweb.entity.KhachHangEntity;
import com.javaweb.entity.TaiKhoanEntity;
import com.javaweb.enums.Role;
import com.javaweb.exception.ApplicationException;
import com.javaweb.exception.ErrorCode;
import com.javaweb.repository.KhachHangRepository;
import com.javaweb.repository.TaiKhoanRespository;
import com.javaweb.service.KhachHangService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class KhachHangServiceImpl implements KhachHangService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    KhachHangRepository khachHangRepository;

    @Autowired
    TaiKhoanRespository taiKhoanRespository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    UserEntityToDTO userEntityToDTO;

    @Autowired
    UserRequestToEntity userRequestToEntity;

    @Override
    public UserResponse save(UserRequest userRequest) {
        if (taiKhoanRespository.existsByUsername(userRequest.getUsername())) {
            throw new ApplicationException(ErrorCode.USER_EXIST);
        }

        KhachHangEntity khachHangEntity = modelMapper.map(userRequest, KhachHangEntity.class);
        TaiKhoanEntity taiKhoanEntity = modelMapper.map(userRequest, TaiKhoanEntity.class);

        taiKhoanEntity.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        Set<String> roles = new HashSet<>();
        if (userRequest.getRoles().isEmpty()){
            roles.add(Role.USER.name());
        } else {
            roles = userRequest.getRoles();
        }
        taiKhoanEntity.setRoles(roles);

        khachHangEntity.setTaiKhoan(taiKhoanEntity);

        khachHangRepository.save(khachHangEntity);

        return userEntityToDTO.UserEntityToDTO(khachHangEntity);
    }

    @Override
    public List<UserResponse> findAll() {
        List<KhachHangEntity> khachHangEntities = khachHangRepository.findAll();
        List<UserResponse> userResponses = new ArrayList<>();
        for (KhachHangEntity khachHangEntity : khachHangEntities) {
            userResponses.add(userEntityToDTO.UserEntityToDTO(khachHangEntity));
        }
        return userResponses;
    }

    @Override
    public UserResponse findById(Long id) {
        return userEntityToDTO.UserEntityToDTO(khachHangRepository.findById(id).
                orElseThrow(() -> new RuntimeException("User not found")));
    }

    @Override
    public void deleteById(Long id) {
        if (khachHangRepository.existsById(id)) {
            khachHangRepository.deleteById(id);
        } else {
            throw new ApplicationException(ErrorCode.CLIENT_NOT_EXIST);
        }
    }

    @Override
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();
        TaiKhoanEntity taiKhoanEntity = taiKhoanRespository.findByUsername(username).orElseThrow(
                () -> new ApplicationException(ErrorCode.USER_NOT_EXIST)
        );
        KhachHangEntity khachHangEntity = taiKhoanEntity.getKhachHang();
        return userEntityToDTO.UserEntityToDTO(khachHangEntity);
    }

    @Override
    public UserResponse update(UserRequest userRequest) {
        userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        KhachHangEntity khachHangEntity = userRequestToEntity.userRequestToEntity(userRequest);
        return userEntityToDTO.UserEntityToDTO(khachHangRepository.save(khachHangEntity));
    }

    @Override
    public void deleteKhachHangById(Long id) {
        khachHangRepository.deleteById(id);
    }
}
