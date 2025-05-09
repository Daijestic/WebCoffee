package com.javaweb.service;

import com.javaweb.dto.reponse.CaLamVienResponse;
import org.springframework.data.domain.Page;

public interface CaLamViecService {
    Page<CaLamVienResponse> findAll(Integer pageNo);
}
