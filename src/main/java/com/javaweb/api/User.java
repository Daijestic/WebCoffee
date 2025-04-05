package com.javaweb.api;


import com.javaweb.dto.repository.APIResponse;
import com.javaweb.dto.repository.UserResponse;
import com.javaweb.dto.request.UserRequest;
import com.javaweb.converter.UserEntityToDTO;
import com.javaweb.entity.KhachHangEntity;
import com.javaweb.service.KhachHangService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/users")
public class User {

    @Autowired
    KhachHangService khachHangService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserEntityToDTO userEntityToDTO;

    @PostMapping
    public APIResponse<UserResponse> uses(@RequestBody @Valid UserRequest userRequest) {
        return APIResponse.<UserResponse>builder()
                .code(200)
                .message("success")
                .result(khachHangService.save(userRequest))
                .build();
    }

    @GetMapping
    public APIResponse<List<UserResponse>> all() {
        APIResponse<List<UserResponse>> apiResponse = new APIResponse<>();
        apiResponse.setCode(200);
        apiResponse.setMessage("OK");
        List<KhachHangEntity> khachHangEntities = khachHangService.findAll();
        List<UserResponse> userResponseList = new ArrayList<>();
        for(KhachHangEntity khachHangEntity : khachHangEntities) {
            userResponseList.add(userEntityToDTO.UserEntityToDTO(khachHangEntity));
        }
        apiResponse.setResult(userResponseList);
        return apiResponse;
    }

    @GetMapping("/{id}")
    public UserResponse uses(@PathVariable Long id) {
        return userEntityToDTO.UserEntityToDTO(khachHangService.findById(id));
    }

    @DeleteMapping("/{id}")
    public APIResponse delete(@PathVariable Long id) {
        khachHangService.deleteById(id);
        return APIResponse.builder()
                .code(200)
                .message("Xoá khách hàng thành công!")
                .build();
    }
}
