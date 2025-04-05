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

    @ManyToOne
    @JoinColumn(name = "LoaiMon")
    private TongQuatMonEntity loaiMon;

    public void setLoaiMon(TongQuatMonEntity loaiMon) {
        this.loaiMon = loaiMon;
    }

    @Column(name = "GiaBan")
    private Long giaBan;

    @OneToMany(mappedBy = "mon", cascade = CascadeType.ALL)
    private List<ChiTietHoaDonEntity> listChiTietHoaDon;
}
