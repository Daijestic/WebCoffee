package com.javaweb.service.impl;

import com.javaweb.converter.entity_to_dto.MonDTOConverter;
import com.javaweb.model.MonDTO;
import com.javaweb.repository.MonRepository;
import com.javaweb.entity.MonEntity;
import com.javaweb.service.MonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MonServiceImpl implements MonService {

    @Autowired
    private MonRepository monRepository;

    @Autowired
    private MonDTOConverter monDTOConverter;

    @Override
    public List<MonDTO> findAll() {
        List<MonDTO> monDTOs = new ArrayList<>();
        List<MonEntity> monEntities = monRepository.findAll();
        for (MonEntity monEntity : monEntities) {
            MonDTO monDTO = monDTOConverter.convertMonDTO(monEntity);
            monDTOs.add(monDTO);
        }
        return monDTOs;
    }

    @Override
    public MonDTO findById(Long id) {
        return monDTOConverter.convertMonDTO(monRepository.findById(id).get());
    }

    @Override
    public void deleteById(Long id) {
        monRepository.deleteById(id);
    }
}
