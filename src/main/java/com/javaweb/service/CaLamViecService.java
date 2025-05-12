package com.javaweb.service;

import com.javaweb.dto.reponse.CaLamVienResponse;
import com.javaweb.dto.request.CaLamViecRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CaLamViecService {
    Page<CaLamVienResponse> findAll(Integer pageNo);

    List<CaLamVienResponse> findAll();

    CaLamVienResponse findById(long idCa);

    CaLamVienResponse save(CaLamViecRequest caLamViecRequest);
}
