package com.javaweb.service.impl;

import com.javaweb.converter.entity_to_dto.NhanVienEntiryToDTO;
import com.javaweb.dto.reponse.StaffReponse;
import com.javaweb.entity.UserEntity;
import com.javaweb.repository.UserRepository;
import com.javaweb.service.NhanVienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NhanVienServiceImpl implements NhanVienService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NhanVienEntiryToDTO nhanVienEntiryToDTO;

    @Override
    public List<StaffReponse> findAll() {
        List<StaffReponse> userResponseList = new ArrayList<>();
        List<UserEntity> userEntityList = userRepository.findAll();
        for (UserEntity userEntity : userEntityList) {
            userResponseList.add(nhanVienEntiryToDTO.nhanVienEntiryToStaffReponse(userEntity));
        }
        return userResponseList;
    }
}
