package com.javaweb.entity;

import javax.persistence.*;

@Entity
@Table(name = "ChiTietHoaDon")
public class ChiTietHoaDonEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "Id_Mon")
    private MonEntity mon;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "Id_HoaDon")
    private HoaDonEntity hoaDon;

    @Column(name = "SoLuong")
    private Long soLuong;

    @Column(name = "GhiChu")
    private Long ghiChu;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public MonEntity getMon() {
        return mon;
    }

    public void setMon(MonEntity mon) {
        this.mon = mon;
    }

    public HoaDonEntity getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDonEntity hoaDon) {
        this.hoaDon = hoaDon;
    }

    public Long getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(Long soLuong) {
        this.soLuong = soLuong;
    }

    public Long getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(Long ghiChu) {
        this.ghiChu = ghiChu;
    }
}
