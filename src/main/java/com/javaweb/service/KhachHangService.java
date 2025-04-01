package com.javaweb.service;

import com.javaweb.dto.request.UserRequest;
import com.javaweb.entity.KhachHangEntity;

import java.util.List;

public interface KhachHangService {
    KhachHangEntity save(UserRequest userRequest);
    List<KhachHangEntity> findAll();
}
