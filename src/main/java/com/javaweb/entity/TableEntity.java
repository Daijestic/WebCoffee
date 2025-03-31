package com.javaweb.entity;

import javax.persistence.*;

import java.util.List;

@Entity
@Table(name = "Ban")
public class TableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TrangThai")
    private String trangThai;

    @Column(name = "SoGhe")
    private Long soghe;

    @ManyToOne
    @JoinColumn(name = "Id_KhuVuc")
    private KhuVucEntity khuVuc;

    @OneToMany(mappedBy = "ban")
    private List<HoaDonEntity> hoaDon;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public Long getSoghe() {
        return soghe;
    }

    public void setSoghe(Long soghe) {
        this.soghe = soghe;
    }

    public KhuVucEntity getKhuVuc() {
        return khuVuc;
    }

    public void setKhuVuc(KhuVucEntity khuVuc) {
        this.khuVuc = khuVuc;
    }

    public List<HoaDonEntity> getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(List<HoaDonEntity> hoaDon) {
        this.hoaDon = hoaDon;
    }
}
