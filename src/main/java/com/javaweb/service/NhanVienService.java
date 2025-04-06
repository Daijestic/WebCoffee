package com.javaweb.service;

import com.javaweb.dto.reponse.StaffReponse;

import java.util.List;

public interface NhanVienService {
    List<StaffReponse> findAll();
}
