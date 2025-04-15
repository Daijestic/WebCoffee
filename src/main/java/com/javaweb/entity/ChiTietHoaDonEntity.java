package com.javaweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "ChiTietHoaDon")
public class ChiTietHoaDonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "Id_Mon")
    private MonEntity mon;

    @ManyToOne
    @JoinColumn(name = "Id_HoaDon")
    private HoaDonEntity hoaDon;

    @Column(name = "SoLuong")
    private Long soLuong;

    @Column(name = "GiamGia")
    private Long giamGia;

    @Column(name = "GhiChu")
    private String ghiChu;
}
