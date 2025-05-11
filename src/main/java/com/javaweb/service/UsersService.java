package com.javaweb.service;

import com.javaweb.dto.reponse.UserResponse;
import com.javaweb.dto.request.UserRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UsersService {
    UserResponse save(UserRequest userRequest);
    List<UserResponse> findAll();
    UserResponse findById(Long id);
    void deleteById(Long id);
    UserResponse getMyInfo();
    UserResponse update(UserRequest userRequest);
    void deleteKhachHangById(Long id);
    Page<UserResponse> findAll(Integer pageNo);
    Page<UserResponse> findAllByRole(Integer pageNo, String role);
    UserResponse findByUsername(String username);
    List<UserResponse> findAllByRole(String role);
}
