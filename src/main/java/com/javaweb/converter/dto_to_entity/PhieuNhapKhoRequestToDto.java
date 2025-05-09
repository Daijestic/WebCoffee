package com.javaweb.converter.dto_to_entity;

import com.javaweb.dto.request.ChiTietNhapKhoRequest;
import com.javaweb.dto.request.PhieuNhapKhoRequest;
import com.javaweb.entity.ChiTietNhapKhoEntity;
import com.javaweb.entity.NhaCungCapEntity;
import com.javaweb.entity.PhieuNhapKhoEntity;
import com.javaweb.entity.UserEntity;
import com.javaweb.repository.NhaCungCapRepository;
import com.javaweb.repository.PhieuNhapKhoRepository;
import com.javaweb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PhieuNhapKhoRequestToDto {

    @Autowired
    private PhieuNhapKhoRepository phieuNhapKhoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private NhaCungCapRepository nhaCungCapRepository;

    @Autowired
    private ChiTietPhieuNhapDtoToEntity chiTietPhieuNhapDtoToEntity;

    public PhieuNhapKhoEntity toPhieuNhapKhoEntity(PhieuNhapKhoRequest phieuNhapKhoRequest){
        PhieuNhapKhoEntity phieuNhapKhoEntity = phieuNhapKhoRepository.findById(phieuNhapKhoRequest.getIdPhieuNhapKho()).get();
        phieuNhapKhoEntity.setNgayNhap(phieuNhapKhoRequest.getNgayNhap());
        UserEntity userEntity = userRepository.findByIdUser(phieuNhapKhoRequest.getIdNhanVien()).get();
        phieuNhapKhoEntity.setUser(userEntity);
        NhaCungCapEntity nhaCungCapEntity = nhaCungCapRepository.findById(phieuNhapKhoRequest.getIdNhaCungCap()).get();
        phieuNhapKhoEntity.setNhaCungCap(nhaCungCapEntity);
        List<ChiTietNhapKhoRequest> chiTietNhapKhoEntityList = phieuNhapKhoRequest.getChiTietNhapKhoList();
        List<ChiTietNhapKhoEntity> chiTietNhapKhoEntities = new ArrayList<>();
        for (ChiTietNhapKhoRequest chiTietNhapKhoRequest : chiTietNhapKhoEntityList) {
            chiTietNhapKhoEntities.add(chiTietPhieuNhapDtoToEntity.toChiTietPhieuNhapEntity(chiTietNhapKhoRequest, phieuNhapKhoEntity));
        }
        phieuNhapKhoEntity.setChiTietNhapKhoList(chiTietNhapKhoEntities);
        return phieuNhapKhoEntity;
    }
}
