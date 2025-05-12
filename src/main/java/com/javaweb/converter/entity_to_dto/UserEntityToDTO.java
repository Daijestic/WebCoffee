package com.javaweb.converter.entity_to_dto;

import com.javaweb.dto.reponse.HoaDonResponse;
import com.javaweb.dto.reponse.UserResponse;
import com.javaweb.entity.HoaDonEntity;
import com.javaweb.entity.UserEntity;
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

    public UserResponse UserEntityToDTO(UserEntity userEntity) {
        UserResponse userResponse = modelMapper.map(userEntity, UserResponse.class);
        userResponse.setUsername(userEntity.getDangNhap());
        userResponse.setRole(userEntity.getLoaiUser());

        List<HoaDonResponse> hoaDonResponses = new ArrayList<>();
        if (userEntity.getHoaDons() != null) {
            List<HoaDonEntity> hoaDonEntities = userEntity.getHoaDons();
            for (HoaDonEntity hoaDonEntity : hoaDonEntities) {
                hoaDonResponses.add(hoaDonEntityToDTO.convert(hoaDonEntity));
            }
            userResponse.setListHoaDon(hoaDonResponses);
        }
        if (userEntity.getAvatar() != null && !userEntity.getAvatar().isEmpty()) {
            userResponse.setAvatar("/images/" + userEntity.getAvatar());
        } else {
            if (userEntity.getGioiTinh() != null && userEntity.getGioiTinh().equals("Nam")) {
                userResponse.setAvatar("/images/nam.jpg");
            } else {
                userResponse.setAvatar("/images/nu.jpg");
            }
        }
        return userResponse;
    }
}
