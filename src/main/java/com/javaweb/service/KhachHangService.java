package com.javaweb.service;

import com.javaweb.dto.repository.UserResponse;
import com.javaweb.dto.request.UserRequest;
import com.javaweb.entity.KhachHangEntity;

import java.util.List;

public interface KhachHangService {
    UserResponse save(UserRequest userRequest);
    List<KhachHangEntity> findAll();
    KhachHangEntity findById(Long id);
    void deleteById(Long id);
    UserResponse getMyInfo();
}
