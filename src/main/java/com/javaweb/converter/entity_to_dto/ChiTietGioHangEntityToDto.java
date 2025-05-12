package com.javaweb.converter.entity_to_dto;

import com.javaweb.dto.reponse.CartResponse;
import com.javaweb.entity.ChiTietGioHangEntity;
import com.javaweb.entity.GiaMonSizeEntity;
import com.javaweb.repository.GiaMonSizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChiTietGioHangEntityToDto {

    @Autowired
    private GiaMonSizeRepository giaMonSizeRepository;

    public CartResponse convertToDto(ChiTietGioHangEntity chiTietGioHangEntity) {
        CartResponse cartResponse = new CartResponse();
        cartResponse.setIdMon(chiTietGioHangEntity.getId().getMonId());
        cartResponse.setTenMon(chiTietGioHangEntity.getMon().getTenMon());
        cartResponse.setTenSize(chiTietGioHangEntity.getSize().getTenSize());
        GiaMonSizeEntity.GiaMonSizeId giaMonSizeId = new GiaMonSizeEntity.GiaMonSizeId(
                chiTietGioHangEntity.getId().getMonId(),
                chiTietGioHangEntity.getId().getSizeId()
        );
        GiaMonSizeEntity giaMonSizeEntity = giaMonSizeRepository.findById(giaMonSizeId).orElse(null);
        cartResponse.setHinhAnh(chiTietGioHangEntity.getMon().getPath());
        cartResponse.setGiaBan(giaMonSizeEntity.getGiaBan());
        cartResponse.setSoLuong(chiTietGioHangEntity.getSoLuong());
        cartResponse.setGhiChu(chiTietGioHangEntity.getGhiChu());
        return cartResponse;
    }
}
