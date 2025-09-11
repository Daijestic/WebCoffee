package com.javaweb.service;

import com.javaweb.dto.reponse.PhieuXuatKhoResponse;
import com.javaweb.dto.request.PhieuXuatKhoRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PhieuXuatKhoService {
    List<PhieuXuatKhoResponse> getAllPhieuXuatKho();

    PhieuXuatKhoResponse getPhieuXuatKhoById(Long id);

//    PhieuXuatKhoResponse createPhieuXuatKho(PhieuXuatKhoRequest phieuXuatKhoRequest);
//
//    PhieuXuatKhoResponse updatePhieuXuatKho(Long id, PhieuXuatKhoRequest phieuXuatKhoRequest);

    void deletePhieuXuatKho(Long id);

    Page<PhieuXuatKhoResponse> findAll(Integer pageNo);

    PhieuXuatKhoResponse findById(Long id);

    PhieuXuatKhoResponse savePhieuXuatKho(PhieuXuatKhoRequest phieuXuatKhoRequest);
}
