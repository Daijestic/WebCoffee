package com.javaweb.entity;

import jakarta.persistence.*;

import java.util.List;

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

    public List<ChiTietHoaDonEntity> getListChiTietHoaDon() {
        return listChiTietHoaDon;
    }

    public void setListChiTietHoaDon(List<ChiTietHoaDonEntity> listChiTietHoaDon) {
        this.listChiTietHoaDon = listChiTietHoaDon;
    }

    public Long getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(Long giaBan) {
        this.giaBan = giaBan;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
