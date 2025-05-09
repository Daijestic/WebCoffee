package com.javaweb.service.impl;

import com.javaweb.converter.entity_to_dto.CaLamViecEntityToDto;
import com.javaweb.dto.reponse.CaLamVienResponse;
import com.javaweb.repository.CaLamViecRepository;
import com.javaweb.service.CaLamViecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CaLamViecServiceImpl implements CaLamViecService {

    @Autowired
    private CaLamViecRepository caLamViecRepository;

    @Autowired
    private CaLamViecEntityToDto caLamViecEntityToDto;

    @Override
    public Page<CaLamVienResponse> findAll(Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 13);
        return caLamViecRepository.findAll(pageable)
                .map(caLamViecEntity -> caLamViecEntityToDto.convertToDto(caLamViecEntity));
    }
}
