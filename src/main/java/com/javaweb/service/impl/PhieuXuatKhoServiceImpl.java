package com.javaweb.service.impl;

import com.javaweb.converter.dto_to_entity.PhieuXuatKhoDtoToEntity;
import com.javaweb.converter.entity_to_dto.PhieuXuatKhoEntityToDto;
import com.javaweb.dto.reponse.PhieuNhapKhoResponse;
import com.javaweb.dto.reponse.PhieuXuatKhoResponse;
import com.javaweb.dto.request.PhieuXuatKhoRequest;
import com.javaweb.entity.PhieuXuatKhoEntity;
import com.javaweb.repository.PhieuXuatKhoRepository;
import com.javaweb.service.PhieuNhapKhoService;
import com.javaweb.service.PhieuXuatKhoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PhieuXuatKhoServiceImpl implements PhieuXuatKhoService {

    @Autowired
    private PhieuXuatKhoEntityToDto phieuXuatKhoEntityToDto;

    @Autowired
    private PhieuXuatKhoRepository phieuXuatKhoRepository;

    @Autowired
    private PhieuXuatKhoDtoToEntity phieuXuatKhoDtoToEntity;


    public PhieuXuatKhoServiceImpl(PhieuXuatKhoRepository phieuXuatKhoRepository) {
        this.phieuXuatKhoRepository = phieuXuatKhoRepository;
    }

    @Override
    public List<PhieuXuatKhoResponse> getAllPhieuXuatKho() {
        return List.of();
    }

    @Override
    public PhieuXuatKhoResponse getPhieuXuatKhoById(Long id) {
        return null;
    }

    @Override
    public void deletePhieuXuatKho(Long id) {

    }

    @Override
    public Page<PhieuXuatKhoResponse> findAll(Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 13);
//        List<PhieuXuatKhoEntity> phieuXuatKhoEntityList = phieuXuatKhoRepository.findAll();
//        for (PhieuXuatKhoEntity phieuXuatKhoEntity : phieuXuatKhoEntityList) {
//            if (phieuXuatKhoEntity.getChiTietXuatKhoList().isEmpty() || phieuXuatKhoEntity.getUser() == null) {
//                phieuXuatKhoRepository.delete(phieuXuatKhoEntity);
//            }
//        }
        return phieuXuatKhoRepository.findAll(pageable)
                .map(phieuXuatKhoEntity -> {
                    return phieuXuatKhoEntityToDto.convertToDto(phieuXuatKhoEntity);
                });
    }

    @Override
    public PhieuXuatKhoResponse findById(Long id) {
        return phieuXuatKhoEntityToDto.convertToDto(phieuXuatKhoRepository.findById(id).get());
    }

    @Override
    public PhieuXuatKhoResponse savePhieuXuatKho(PhieuXuatKhoRequest phieuXuatKhoRequest) {
        return phieuXuatKhoEntityToDto.convertToDto(phieuXuatKhoRepository.save(phieuXuatKhoDtoToEntity.convertToEntity(phieuXuatKhoRequest)));
    }
}
