package com.javaweb.service;

import com.javaweb.dto.reponse.CaLamVienResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CaLamViecService {
    Page<CaLamVienResponse> findAll(Integer pageNo);
    List<CaLamVienResponse> findAll();
    CaLamVienResponse findById(long idCa);
}
