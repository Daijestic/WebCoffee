package com.javaweb.service.impl;

import com.javaweb.converter.entity_to_dto.NhanVienEntiryToDTO;
import com.javaweb.dto.reponse.StaffReponse;
import com.javaweb.entity.NhanVienEntity;
import com.javaweb.repository.NhanVienRepository;
import com.javaweb.service.NhanVienService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NhanVienServiceImpl implements NhanVienService {

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private NhanVienEntiryToDTO nhanVienEntiryToDTO;

    @Override
    public List<StaffReponse> findAll() {
        List<StaffReponse> userResponseList = new ArrayList<>();
        List<NhanVienEntity> nhanVienEntityList = nhanVienRepository.findAll();
        for (NhanVienEntity nhanVienEntity : nhanVienEntityList) {
            userResponseList.add(nhanVienEntiryToDTO.nhanVienEntiryToStaffReponse(nhanVienEntity));
        }
        return userResponseList;
    }
}
