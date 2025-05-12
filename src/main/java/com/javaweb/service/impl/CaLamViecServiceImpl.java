package com.javaweb.service.impl;

import com.javaweb.converter.dto_to_entity.CaLamViecDtoToEntity;
import com.javaweb.converter.entity_to_dto.CaLamViecEntityToDto;
import com.javaweb.dto.reponse.CaLamVienResponse;
import com.javaweb.dto.request.CaLamViecRequest;
import com.javaweb.repository.CaLamViecRepository;
import com.javaweb.service.CaLamViecService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CaLamViecServiceImpl implements CaLamViecService {

    @Autowired
    private CaLamViecRepository caLamViecRepository;

    @Autowired
    private CaLamViecEntityToDto caLamViecEntityToDto;

    @Autowired
    private CaLamViecDtoToEntity caLamViecDtoToEntity;

    @Override
    public Page<CaLamVienResponse> findAll(Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 13);
        return caLamViecRepository.findAll(pageable)
                .map(caLamViecEntity -> caLamViecEntityToDto.convertToDto(caLamViecEntity));
    }

    @Override
    public List<CaLamVienResponse> findAll() {
        return caLamViecRepository.findAll()
                .stream()
                .map(caLamViecEntity -> caLamViecEntityToDto.convertToDto(caLamViecEntity))
                .toList();
    }

    @Override
    public CaLamVienResponse findById(long idCa) {
        return caLamViecEntityToDto.convertToDto(caLamViecRepository.findById(idCa).get());
    }

    @Override
    public CaLamVienResponse save(CaLamViecRequest caLamViecRequest) {
        return caLamViecEntityToDto.convertToDto(caLamViecRepository.save(caLamViecDtoToEntity.convertToDto(caLamViecRequest)));
    }
}
