package com.javaweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "Kho")
public class KhoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TenKho")
    private String tenKho;

    @OneToMany(mappedBy = "kho", cascade = CascadeType.ALL)
    private List<PhieuNhapKhoEntity> listPhieuNhap;

    @OneToMany(mappedBy = "kho", cascade = CascadeType.ALL)
    private List<PhieuXuatKhoEntity> listPhieuXuat;
}
