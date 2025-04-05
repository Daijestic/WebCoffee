package com.javaweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "TaiKhoan")
public class TaiKhoanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(name = "username", unique = true, nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "enabled")
    private boolean enabled;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "TaiKhoan_Roles", joinColumns = @JoinColumn(name = "taiKhoan_id"))
    @Column(name = "role")
    private Set<String> role;

    @OneToOne(mappedBy = "taiKhoan")
    private KhachHangEntity khachHang;

    @OneToOne(mappedBy = "taiKhoan")
    private NhanVienEntity nhanVien;
}
