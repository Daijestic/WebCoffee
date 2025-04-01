package com.javaweb.service.impl;

import com.javaweb.dto.request.UserRequest;
import com.javaweb.entity.KhachHangEntity;
import com.javaweb.entity.TaiKhoanEntity;
import com.javaweb.repository.KhachHangRepository;
import com.javaweb.service.KhachHangService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KhachHangServiceImpl implements KhachHangService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    KhachHangRepository khachHangRepository;

    @Override
    public KhachHangEntity save(UserRequest userRequest) {
        KhachHangEntity khachHangEntity = modelMapper.map(userRequest, KhachHangEntity.class);
        TaiKhoanEntity taiKhoanEntity = modelMapper.map(userRequest, TaiKhoanEntity.class);
        khachHangEntity.setTaiKhoan(taiKhoanEntity);
        return khachHangRepository.save(khachHangEntity);
    }

    @Override
    public List<KhachHangEntity> findAll() {
        return khachHangRepository.findAll();
    }
}
