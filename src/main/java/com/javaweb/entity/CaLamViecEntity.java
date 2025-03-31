package com.javaweb.entity;

import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "CaLamViec")
public class CaLamViecEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "Id_NhanVien")
    private NhanVienEntity nhanVien;

    @Column(name = "Giovao")
    private Date giovao;

    @Column(name = "GiaRa")
    private Date giaRa;

    public Date getGiaRa() {
        return giaRa;
    }

    public void setGiaRa(Date giaRa) {
        this.giaRa = giaRa;
    }

    public Date getGiovao() {
        return giovao;
    }

    public void setGiovao(Date giovao) {
        this.giovao = giovao;
    }

    public NhanVienEntity getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVienEntity nhanVien) {
        this.nhanVien = nhanVien;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}
