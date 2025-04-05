package com.javaweb.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequest {
    private String hoTen;
    private String gioiTinh;
    private String diaChi;
    private String sdt;
    private String email;
    @Size(min = 5, message = "USERNAME_INVALID")
    private String username;
    @Size(min = 8, message = "PASSWORD_INVALID")
    private String password;

}
