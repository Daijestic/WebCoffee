package com.javaweb.service;

import com.javaweb.dto.reponse.PhieuNhapKhoResponse;
import com.javaweb.dto.request.PhieuNhapKhoRequest;
import org.springframework.data.domain.Page;

import java.util.Date;
import java.util.List;

public interface PhieuNhapKhoService {
    PhieuNhapKhoResponse savePhieuNhapKho(PhieuNhapKhoRequest phieuNhapKhoRequest);

    List<PhieuNhapKhoResponse> getAllPhieuNhapKho();

    PhieuNhapKhoResponse getPhieuNhapKhoById(Long id);

    void deletePhieuNhapKho(Long id);

    List<PhieuNhapKhoResponse> searchPhieuNhapKho(String keyword);

    List<PhieuNhapKhoResponse> getPhieuNhapKhoByDate(Date startDate, Date endDate);

    Page<PhieuNhapKhoResponse> findAll(Integer pageNo);

    PhieuNhapKhoResponse findByIdPhieuNhapKho(Long idPhieuNhapKho);
}
