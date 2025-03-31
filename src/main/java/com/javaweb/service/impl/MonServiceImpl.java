package com.javaweb.service.impl;

import com.javaweb.converter.MonDTOConverter;
import com.javaweb.model.MonDTO;
import com.javaweb.repository.RepositoryMon;
import com.javaweb.entity.MonEntity;
import com.javaweb.service.MonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class MonServiceImpl implements MonService {

    @Autowired
    private RepositoryMon repositoryMon;

    @Autowired
    private MonDTOConverter monDTOConverter;

    @Override
    public List<MonDTO> findAll(String name) {
        List<MonDTO> monDTOs = new ArrayList<>();
        List<MonEntity> monEntities = repositoryMon.findByTenMonContaining(name);
        for (MonEntity monEntity : monEntities) {
            MonDTO monDTO = monDTOConverter.convertMonDTO(monEntity);
            monDTOs.add(monDTO);
        }
        return monDTOs;
    }
}
