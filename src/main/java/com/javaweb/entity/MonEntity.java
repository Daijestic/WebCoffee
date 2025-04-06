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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TenMon")
    private String tenMon;

    @Column(name = "moTa")
    private String moTa;

    @ManyToOne
    @JoinColumn(name = "LoaiMon")
    private TongQuatMonEntity loaiMon;

    @Column(name = "GiaBan")
    private Long giaBan;

    @Column(name = "path")
    private String path;

    @OneToMany(mappedBy = "mon", cascade = CascadeType.ALL)
    private List<ChiTietHoaDonEntity> listChiTietHoaDon;
}
