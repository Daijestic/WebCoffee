package com.javaweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "Mon")
public class MonEntity {

    @Id
    @Column(name = "ID_Mon")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMon;

    @Column(name = "TenMon")
    private String tenMon;

    @Column(name = "LoaiMon")
    private String loaiMon;

    @Column(name = "moTa")
    private String moTa;

    @Column(name = "path")
    private String path;

    @OneToMany(mappedBy = "mon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<GiaMonSizeEntity> giaMonSizeEntities;

    @OneToMany(mappedBy = "mon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietHoaDonEntity> chiTietHoaDonEntities;

    @OneToMany(mappedBy = "mon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietGioHangEntity> chiTietGioHangEntities;

    @OneToMany(mappedBy = "mon", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<NhanXetEntity> nhanXetEntities;
}
