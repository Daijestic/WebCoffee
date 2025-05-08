package com.javaweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "NhaCungCap")
public class NhaCungCapEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID_NhaCungCap;

    @Column(name = "TenNCC")
    private String tenNCC;

    @Column(name = "SDT")
    private String SDT;

    @Column(name = "DiaChi")
    private String diaChi;

    @OneToMany(mappedBy = "nhaCungCap", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PhieuNhapKhoEntity> phieuNhapKhoList;
}
