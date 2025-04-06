package com.javaweb.service.impl;

import com.javaweb.converter.UserEntityToDTO;
import com.javaweb.dto.repository.UserResponse;
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

    @Override
    public UserResponse save(UserRequest userRequest) {
        if (taiKhoanRespository.existsByUsername(userRequest.getUsername())) {
            throw new ApplicationException(ErrorCode.USER_EXIST);
        }

        KhachHangEntity khachHangEntity = modelMapper.map(userRequest, KhachHangEntity.class);
        TaiKhoanEntity taiKhoanEntity = modelMapper.map(userRequest, TaiKhoanEntity.class);

        taiKhoanEntity.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        Set<String> roles = new HashSet<>();
        roles.add(Role.USER.name());
        taiKhoanEntity.setRole(roles);

        khachHangEntity.setTaiKhoan(taiKhoanEntity);

        khachHangRepository.save(khachHangEntity);

        return userEntityToDTO.UserEntityToDTO(khachHangEntity);
    }

    @Override
    public List<KhachHangEntity> findAll() {
        return khachHangRepository.findAll();
    }

    @Override
    public KhachHangEntity findById(Long id) {
        return khachHangRepository.findById(id).
                orElseThrow(() -> new RuntimeException("User not found"));
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
}
