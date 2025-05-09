package com.javaweb.converter.entity_to_dto;

import com.javaweb.dto.reponse.HoaDonResponse;
import com.javaweb.dto.reponse.UserResponse;
import com.javaweb.entity.HoaDonEntity;
import com.javaweb.entity.KhachHangEntity;
import com.javaweb.entity.TaiKhoanEntity;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserEntityToDTO {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private HoaDonEntityToDTO hoaDonEntityToDTO;

    public UserResponse UserEntityToDTO(KhachHangEntity khachHangEntity) {
        UserResponse userResponse = modelMapper.map(khachHangEntity, UserResponse.class);
        TaiKhoanEntity taiKhoanEntity = khachHangEntity.getTaiKhoan();
        userResponse.setUsername(taiKhoanEntity.getUsername());
        userResponse.setRoles(taiKhoanEntity.getRoles());

        List<HoaDonResponse> hoaDonResponses = new ArrayList<>();
        if (khachHangEntity.getHoaDon() != null) {
            List<HoaDonEntity> hoaDonEntities = khachHangEntity.getHoaDon();
            for (HoaDonEntity hoaDonEntity : hoaDonEntities) {
                hoaDonResponses.add(hoaDonEntityToDTO.convert(hoaDonEntity));
            }
            userResponse.setListHoaDon(hoaDonResponses);
        }
        return userResponse;
    }
}
