package com.javaweb.converter.dto_to_entity;

import com.javaweb.dto.request.ChiTietXuatKhoRequest;
import com.javaweb.dto.request.PhieuXuatKhoRequest;
import com.javaweb.entity.ChiTietXuatKhoEntity;
import com.javaweb.entity.PhieuXuatKhoEntity;
import com.javaweb.repository.PhieuXuatKhoRepository;
import com.javaweb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PhieuXuatKhoDtoToEntity {

    @Autowired
    private PhieuXuatKhoRepository phieuXuatKhoRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChiTietXuatKhoDtoToEntity chiTietXuatKhoDtoToEntity;

    public PhieuXuatKhoEntity convertToEntity(PhieuXuatKhoRequest request) {
        PhieuXuatKhoEntity entity;

        if (request.getIdPhieuXuatKho() != null) {
            entity = phieuXuatKhoRepository.findById(request.getIdPhieuXuatKho()).orElse(new PhieuXuatKhoEntity());
        } else {
            entity = new PhieuXuatKhoEntity();
        }

        entity.setNgayXuat(request.getNgayXuat());
        entity.setUser(userRepository.findById(request.getIdNhanVien()).orElse(null));

        // Xử lý danh sách chi tiết
        if (entity.getChiTietXuatKhoList() == null) {
            entity.setChiTietXuatKhoList(new ArrayList<>());
        } else {
            entity.getChiTietXuatKhoList().clear(); // quan trọng
        }

        for (ChiTietXuatKhoRequest chiTiet : request.getChiTietXuatKhoList()) {
            ChiTietXuatKhoEntity chiTietEntity = chiTietXuatKhoDtoToEntity.convertToEntity(chiTiet, entity);
            entity.getChiTietXuatKhoList().add(chiTietEntity); // giữ reference gốc
        }

        return entity;
    }

}
