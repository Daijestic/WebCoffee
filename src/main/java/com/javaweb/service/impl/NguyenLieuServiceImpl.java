package com.javaweb.service.impl;

import com.javaweb.converter.dto_to_entity.NguyenLieuDtoToEntity;
import com.javaweb.converter.entity_to_dto.NguyenLieuEntityToDto;
import com.javaweb.converter.entity_to_dto.NhapXuatEntityToDto;
import com.javaweb.dto.reponse.LichSuNhapXuatNguyenLieuResponse;
import com.javaweb.dto.reponse.NguyenLieuResponse;
import com.javaweb.dto.request.NguyenLieuRequest;
import com.javaweb.entity.NguyenLieuEntity;
import com.javaweb.repository.NguyenLieuRepository;
import com.javaweb.service.NguyenLieuService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NguyenLieuServiceImpl implements NguyenLieuService {

    @Autowired
    private NguyenLieuRepository nguyenLieuRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private NhapXuatEntityToDto nhapXuatEntityToDto;

    @Autowired
    private NguyenLieuDtoToEntity nguyenLieuDtoToEntity;

    @Autowired
    private NguyenLieuEntityToDto nguyenLieuEntityToDto;

    @Override
    public NguyenLieuEntity findByTenNguyenLieu(String tenNguyenLieu) {
        return null;
    }

    @Override
    public List<NguyenLieuResponse> findAll() {
        return nguyenLieuRepository.findAll()
                .stream()
                .map(nguyenLieuEntity -> {
                    NguyenLieuResponse nguyenLieuResponse = modelMapper.map(nguyenLieuEntity, NguyenLieuResponse.class);
                    return nguyenLieuResponse;
                }).toList();
    }

    @Override
    public NguyenLieuResponse findById(Long idNguyenLieu) {
        return nguyenLieuRepository.findById(idNguyenLieu)
                .map(nguyenLieuEntity -> {
                    return nguyenLieuEntityToDto.convertToDto(nguyenLieuEntity);
                }).get();
    }

    @Override
    public NguyenLieuResponse save(NguyenLieuRequest nguyenLieuRequest) {
        return nguyenLieuEntityToDto.convertToDto(nguyenLieuRepository.save(nguyenLieuDtoToEntity.convertToEntity(nguyenLieuRequest)));
    }

    @Override
    public void deleteById(Long idNguyenLieu) {
        nguyenLieuRepository.deleteById(idNguyenLieu);
    }

    @Override
    public List<NguyenLieuEntity> searchByName(String name) {
        return List.of();
    }

    @Override
    public List<NguyenLieuEntity> searchByNameAndPage(String name, int page, int size) {
        return List.of();
    }

    @Override
    public Page<NguyenLieuResponse> findAll(Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 13);
        return this.nguyenLieuRepository.findAll(pageable)
                .map(nguyenLieuEntity -> {
                    return nguyenLieuEntityToDto.convertToDto(nguyenLieuEntity);
                });
    }

    @Override
    public List<LichSuNhapXuatNguyenLieuResponse> getLichSuNhapXuatNguyenLieu(Long idNguyenLieu) {
        return nhapXuatEntityToDto.convertToDto(nguyenLieuRepository.findById(idNguyenLieu).get());
    }

    @Override
    public Page<NguyenLieuResponse> findBySoLuongLessThanEqual(Long soLuong, Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 13);
        return nguyenLieuRepository.findBySoLuongLessThanEqual(soLuong, pageable)
                .map(nguyenLieuEntity -> {;
                    return nguyenLieuEntityToDto.convertToDto(nguyenLieuEntity);
                });
    }

}
