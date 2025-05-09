package com.javaweb.service.impl;

import com.javaweb.converter.entity_to_dto.LichLamEntityToDto;
import com.javaweb.dto.reponse.LichLamResponse;
import com.javaweb.repository.LichLamRepository;
import com.javaweb.service.LichLamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class LichLamServiceImpl implements LichLamService {

    @Autowired
    private LichLamRepository lichLamRepository;

    @Autowired
    private LichLamEntityToDto lichLamEntityToDto;

    @Override
    public Page<LichLamResponse> findAll(Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 13);
        return lichLamRepository.findAll(pageable).map(lichLamEntityToDto::convertToDto);
    }
}
