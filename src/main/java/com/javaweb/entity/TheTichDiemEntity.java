package com.javaweb.entity;


import javax.persistence.*;

@Entity
@Table(name = "TheTichDiem")
public class TheTichDiemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "DiemTichLuy")
    private Long diemTichLuy;

    @OneToOne(mappedBy = "theTichDiem")
    private KhachHangEntity khachHang;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getDiemTichLuy() {
        return diemTichLuy;
    }

    public void setDiemTichLuy(Long diemTichLuy) {
        this.diemTichLuy = diemTichLuy;
    }

    public KhachHangEntity getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHangEntity khachHang) {
        this.khachHang = khachHang;
    }
}
