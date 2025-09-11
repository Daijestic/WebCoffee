package com.javaweb.dto.reponse;

import lombok.*;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {
    private Long id;
    private String hoTen;
    private String gioiTinh;
    private String diaChi;
    private String sdt;
    private String email;
    private String username;
    private Long diemTichLuy;
    private String role;
    private String avatar;
    private Set<String> roles;
    private List<HoaDonResponse> listHoaDon;
}
