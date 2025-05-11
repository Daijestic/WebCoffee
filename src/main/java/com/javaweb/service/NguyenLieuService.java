package com.javaweb.service;


import com.javaweb.dto.reponse.LichSuNhapXuatNguyenLieuResponse;
import com.javaweb.dto.reponse.NguyenLieuResponse;
import com.javaweb.dto.request.NguyenLieuRequest;
import com.javaweb.entity.NguyenLieuEntity;
import org.springframework.data.domain.Page;

import java.util.List;

public interface NguyenLieuService {
    NguyenLieuEntity findByTenNguyenLieu(String tenNguyenLieu);

    List<NguyenLieuResponse> findAll();

    NguyenLieuResponse findById(Long idNguyenLieu);

    NguyenLieuResponse save(NguyenLieuRequest nguyenLieuRequest);

    void deleteById(Long idNguyenLieu);

    List<NguyenLieuEntity> searchByName(String name);

    List<NguyenLieuEntity> searchByNameAndPage(String name, int page, int size);

    Page<NguyenLieuResponse> findAll(Integer pageNo);

    List<LichSuNhapXuatNguyenLieuResponse> getLichSuNhapXuatNguyenLieu(Long idNguyenLieu);
}
