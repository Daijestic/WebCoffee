package com.javaweb.service;

import com.javaweb.dto.reponse.UserResponse;
import com.javaweb.dto.request.UserRequest;
import com.javaweb.entity.KhachHangEntity;

import java.util.List;

public interface KhachHangService {
    UserResponse save(UserRequest userRequest);
    List<UserResponse> findAll();
    KhachHangEntity findById(Long id);
    void deleteById(Long id);
    UserResponse getMyInfo();
}
