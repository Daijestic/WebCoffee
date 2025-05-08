package com.javaweb.service;

import com.javaweb.dto.reponse.UserResponse;
import com.javaweb.dto.request.UserRequest;
import java.util.List;

public interface KhachHangService {
    UserResponse save(UserRequest userRequest);
    List<UserResponse> findAll();
    UserResponse findById(Long id);
    void deleteById(Long id);
    UserResponse getMyInfo();
    UserResponse update(UserRequest userRequest);
    void deleteKhachHangById(Long id);
}
