package com.javaweb.service;

import com.javaweb.dto.reponse.LichLamResponse;
import com.javaweb.dto.request.LichLamRequest;
import org.springframework.data.domain.Page;

public interface LichLamService {
    Page<LichLamResponse> findAll(Integer pageNo);
    LichLamResponse findById(Long id);
    LichLamResponse save(LichLamRequest lichLamRequest);
    void deleteLichLamViec(Long idLichLamViec);
}
