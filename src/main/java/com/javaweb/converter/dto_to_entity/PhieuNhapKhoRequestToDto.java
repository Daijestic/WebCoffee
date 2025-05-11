package com.javaweb.converter.dto_to_entity;

import com.javaweb.dto.request.ChiTietNhapKhoRequest;
import com.javaweb.dto.request.PhieuNhapKhoRequest;
import com.javaweb.entity.ChiTietNhapKhoEntity;
import com.javaweb.entity.NhaCungCapEntity;
import com.javaweb.entity.PhieuNhapKhoEntity;
import com.javaweb.entity.UserEntity;
import com.javaweb.repository.*;
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

    @Autowired
    private ChiTietNhapKhoRepository chiTietNhapKhoRepository;

    public PhieuNhapKhoEntity toPhieuNhapKhoEntity(PhieuNhapKhoRequest phieuNhapKhoRequest){
        PhieuNhapKhoEntity phieuNhapKhoEntity;
        if (phieuNhapKhoRequest.getIdPhieuNhapKho() != null){
            phieuNhapKhoEntity = phieuNhapKhoRepository.findById(phieuNhapKhoRequest.getIdPhieuNhapKho()).get();
        } else {
            phieuNhapKhoEntity = new PhieuNhapKhoEntity();
        }
        phieuNhapKhoEntity.setNgayNhap(phieuNhapKhoRequest.getNgayNhap());
        UserEntity userEntity = userRepository.findByIdUser(phieuNhapKhoRequest.getIdNhanVien()).get();
        phieuNhapKhoEntity.setUser(userEntity);

        NhaCungCapEntity nhaCungCapEntity = nhaCungCapRepository.findById(phieuNhapKhoRequest.getIdNhaCungCap()).get();
        phieuNhapKhoEntity.setNhaCungCap(nhaCungCapEntity);

        List<ChiTietNhapKhoRequest> requestList = phieuNhapKhoRequest.getChiTietNhapKhoList();
        List<ChiTietNhapKhoEntity> updatedList = new ArrayList<>();

        // Xoá chi tiết cũ đúng cách
        if (phieuNhapKhoEntity.getChiTietNhapKhoList() != null && !phieuNhapKhoEntity.getChiTietNhapKhoList().isEmpty()) {
            List<ChiTietNhapKhoEntity> oldList = new ArrayList<>(phieuNhapKhoEntity.getChiTietNhapKhoList());
            for (ChiTietNhapKhoEntity old : oldList) {
                phieuNhapKhoEntity.getChiTietNhapKhoList().remove(old); // Gỡ khỏi list cha
                chiTietNhapKhoRepository.deleteById(old.getId());        // Xoá DB
            }
        }

        // Thêm mới
        if (requestList != null && !requestList.isEmpty()) {
            for (ChiTietNhapKhoRequest req : requestList) {
                updatedList.add(chiTietPhieuNhapDtoToEntity.toChiTietPhieuNhapEntity(req, phieuNhapKhoEntity));
            }
        }

        phieuNhapKhoEntity.setChiTietNhapKhoList(updatedList);
        return phieuNhapKhoRepository.save(phieuNhapKhoEntity);
    }

}
