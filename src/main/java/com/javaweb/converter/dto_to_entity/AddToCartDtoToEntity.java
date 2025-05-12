package com.javaweb.converter.dto_to_entity;

import com.javaweb.dto.request.AddToCartRequest;
import com.javaweb.entity.ChiTietGioHangEntity;
import com.javaweb.entity.MonEntity;
import com.javaweb.entity.SizeEntity;
import com.javaweb.entity.UserEntity;
import com.javaweb.repository.ChiTietGioHangRepository;
import com.javaweb.repository.MonRepository;
import com.javaweb.repository.SizeRepository;
import com.javaweb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AddToCartDtoToEntity {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MonRepository monRepository;

    @Autowired
    private SizeRepository sizeRepository;

    @Autowired
    private ChiTietGioHangRepository chiTietGioHangRepository;

    public ChiTietGioHangEntity convertToEntity(AddToCartRequest addToCartRequest) {
        ChiTietGioHangEntity.ChiTietGioHangId chiTietGioHangId = new ChiTietGioHangEntity.ChiTietGioHangId(
                addToCartRequest.getMonId(),
                addToCartRequest.getSizeId(),
                addToCartRequest.getUserId()
        );
        ChiTietGioHangEntity chiTietGioHangEntity;
        if (chiTietGioHangRepository.existsById(chiTietGioHangId)) {
            chiTietGioHangEntity = chiTietGioHangRepository.findById(chiTietGioHangId).get();
            chiTietGioHangEntity.setSoLuong(addToCartRequest.getSoLuong());
            chiTietGioHangEntity.setGhiChu(addToCartRequest.getGhiChu());
            return chiTietGioHangEntity;
        }
        chiTietGioHangEntity = new ChiTietGioHangEntity();
        chiTietGioHangEntity.setId(chiTietGioHangId);
        SizeEntity size = sizeRepository.findById(addToCartRequest.getSizeId())
                .orElseThrow(() -> new IllegalArgumentException("Size không tồn tại"));

        MonEntity mon = monRepository.findById(addToCartRequest.getMonId())
                .orElseThrow(() -> new IllegalArgumentException("Món không tồn tại"));

        chiTietGioHangEntity.setSize(size);
        chiTietGioHangEntity.setMon(mon);
        chiTietGioHangEntity.setGhiChu(addToCartRequest.getGhiChu());
        chiTietGioHangEntity.setSoLuong(addToCartRequest.getSoLuong());
        UserEntity userEntity = userRepository.findById(addToCartRequest.getUserId()).orElseThrow(
                () -> new IllegalArgumentException("Không tìm thấy user với ID: " + addToCartRequest.getUserId())
        );
        chiTietGioHangEntity.setUser(userEntity);
        return chiTietGioHangEntity;
    }
}
