package com.javaweb.entity;

import javax.persistence.*;

import java.util.List;

@Entity
@Table(name = "KhachHang")
public class KhachHangEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "HoTen")
    private String hoTen;

    @Column(name = "GioiTinh")
    private String gioiTinh;

    @Column(name = "DiaChi")
    private String diaChi;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "theTichDiem_id", referencedColumnName = "id")
    private TheTichDiemEntity theTichDiem;

    @OneToMany(mappedBy = "khachHang")
    private List<HoaDonEntity> hoaDon;


    public List<HoaDonEntity> getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(List<HoaDonEntity> hoaDon) {
        this.hoaDon = hoaDon;
    }

    public TheTichDiemEntity getTheTichDiem() {
        return theTichDiem;
    }

    public void setTheTichDiem(TheTichDiemEntity theTichDiem) {
        this.theTichDiem = theTichDiem;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }
}
