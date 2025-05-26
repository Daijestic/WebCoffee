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
            phieuNhapKhoEntity = phieuNhapKhoRepository.findById(phieuNhapKhoRequest.getIdPhieuNhapKho()).orElse(new PhieuNhapKhoEntity());
        } else {
            phieuNhapKhoEntity = new PhieuNhapKhoEntity();
        }

        phieuNhapKhoEntity.setNgayNhap(phieuNhapKhoRequest.getNgayNhap());

        UserEntity userEntity = userRepository.findByIdUser(phieuNhapKhoRequest.getIdNhanVien())
                .orElseThrow(() -> new RuntimeException("Nhân viên không tồn tại"));
        phieuNhapKhoEntity.setUser(userEntity);

        NhaCungCapEntity nhaCungCapEntity = nhaCungCapRepository.findById(phieuNhapKhoRequest.getIdNhaCungCap())
                .orElseThrow(() -> new RuntimeException("Nhà cung cấp không tồn tại"));
        phieuNhapKhoEntity.setNhaCungCap(nhaCungCapEntity);

        // Tạo danh sách mới trước
        List<ChiTietNhapKhoEntity> newList = new ArrayList<>();
        List<ChiTietNhapKhoRequest> requestList = phieuNhapKhoRequest.getChiTietNhapKhoList();

        if (requestList != null && !requestList.isEmpty()) {
            for (ChiTietNhapKhoRequest req : requestList) {
                newList.add(chiTietPhieuNhapDtoToEntity.toChiTietPhieuNhapEntity(req, phieuNhapKhoEntity));
            }
        }

        // Xử lý danh sách cũ và gán danh sách mới trong một bước
        if (phieuNhapKhoEntity.getChiTietNhapKhoList() != null) {
            phieuNhapKhoEntity.getChiTietNhapKhoList().clear();
            phieuNhapKhoEntity.getChiTietNhapKhoList().addAll(newList);
        } else {
            phieuNhapKhoEntity.setChiTietNhapKhoList(newList);
        }

        // Không lưu entity ở đây, để cho service lưu sau khi hoàn thành toàn bộ chuyển đổi
        return phieuNhapKhoEntity;
    }

}
