package com.javaweb.service;

import com.javaweb.model.MonDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MonService {
    List<MonDTO> findAll();
    MonDTO findById(Long id);
    void deleteById(Long id);
}
