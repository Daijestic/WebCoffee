package com.javaweb.service.impl;

import com.javaweb.converter.dto_to_entity.PhieuNhapKhoRequestToDto;
import com.javaweb.converter.entity_to_dto.PhieuNhapKhoEntiryToDto;
import com.javaweb.dto.reponse.PhieuNhapKhoResponse;
import com.javaweb.dto.request.PhieuNhapKhoRequest;
import com.javaweb.entity.PhieuNhapKhoEntity;
import com.javaweb.repository.PhieuNhapKhoRepository;
import com.javaweb.service.PhieuNhapKhoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class PhieuNhapKhoServiceImpl implements PhieuNhapKhoService {

    @Autowired
    private PhieuNhapKhoRepository phieuNhapKhoRepository;

    @Autowired
    private PhieuNhapKhoEntiryToDto phieuNhapKhoEntiryToDto;

    @Autowired
    private PhieuNhapKhoRequestToDto phieuNhapKhoRequestToDto;

    @Override
    public PhieuNhapKhoResponse savePhieuNhapKho(PhieuNhapKhoRequest phieuNhapKhoRequest) {
        return phieuNhapKhoEntiryToDto.convertToDto(phieuNhapKhoRepository.save(phieuNhapKhoRequestToDto.toPhieuNhapKhoEntity(phieuNhapKhoRequest)));
    }

    @Override
    public List<PhieuNhapKhoResponse> getAllPhieuNhapKho() {
        return List.of();
    }

    @Override
    public PhieuNhapKhoResponse getPhieuNhapKhoById(Long id) {
        return null;
    }

    @Override
    public void deletePhieuNhapKho(Long id) {
        phieuNhapKhoRepository.deleteById(id);
    }

    @Override
    public List<PhieuNhapKhoResponse> searchPhieuNhapKho(String keyword) {
        return List.of();
    }

    @Override
    public List<PhieuNhapKhoResponse> getPhieuNhapKhoByDate(Date startDate, Date endDate) {
        return List.of();
    }

    @Override
    public Page<PhieuNhapKhoResponse> findAll(Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 13);
        return phieuNhapKhoRepository.findAll(pageable)
                .map(phieuNhapKhoEntity -> {
                    return phieuNhapKhoEntiryToDto.convertToDto(phieuNhapKhoEntity);
                });
    }

    @Override
    public PhieuNhapKhoResponse findByIdPhieuNhapKho(Long idPhieuNhapKho) {
        return phieuNhapKhoRepository.findById(idPhieuNhapKho)
                .map(phieuNhapKhoEntity -> {
                    return phieuNhapKhoEntiryToDto.convertToDto(phieuNhapKhoEntity);
                }).get();
    }
}
