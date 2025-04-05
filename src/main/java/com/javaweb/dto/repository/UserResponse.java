package com.javaweb.dto.repository;

import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
public class UserResponse {
    private Long id;
    private String hoTen;
    private String gioiTinh;
    private String diaChi;
    private String sdt;
    private String email;
    private String username;
    private Set<String> roles;
}
