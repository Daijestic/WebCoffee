package com.javaweb.api;


import com.javaweb.dto.repository.UserRepository;
import com.javaweb.dto.request.UserRequest;
import com.javaweb.converter.UserEntityToDTO;
import com.javaweb.entity.KhachHangEntity;
import com.javaweb.service.KhachHangService;
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
    public UserRepository uses(@RequestBody UserRequest userRequest) {
        return userEntityToDTO.UserEntityToDTO(khachHangService.save(userRequest));
    }

    @GetMapping
    public List<UserRepository> all() {
        List<KhachHangEntity> khachHangEntities = khachHangService.findAll();
        List<UserRepository> userRepositoryList = new ArrayList<>();
        for(KhachHangEntity khachHangEntity : khachHangEntities) {
            userRepositoryList.add(userEntityToDTO.UserEntityToDTO(khachHangEntity));
        }
        return userRepositoryList;
    }
}
