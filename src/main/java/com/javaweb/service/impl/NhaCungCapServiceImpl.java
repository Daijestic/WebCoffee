package com.javaweb.service.impl;

import com.javaweb.converter.entity_to_dto.NhaCungCapEntityToDto;
import com.javaweb.dto.reponse.NhaCungCapResponse;
import com.javaweb.repository.NhaCungCapRepository;
import com.javaweb.service.NhaCungCapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NhaCungCapServiceImpl implements NhaCungCapService {

    @Autowired
    private NhaCungCapRepository nhaCungCapRepository;

    @Autowired
    private NhaCungCapEntityToDto nhaCungCapEntityToDto;

    @Override
    public List<NhaCungCapResponse> findAll() {
        return nhaCungCapRepository.findAll()
                .stream().map(nhaCungCapEntity -> {
                    return nhaCungCapEntityToDto.convertToDto(nhaCungCapEntity);
                }).toList();
    }
}
