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
    private Long id;

    @Column(name = "TenNCC")
    private String tenNCC;

    @Column(name = "SDT")
    private String SDT;

    @Column(name = "DiaChi")
    private String diaChi;

    @OneToMany(mappedBy = "nhaCungCap")
    private List<PhieuNhapKhoEntity> listPhieuNhapKho;
}
