package com.javaweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "ChiTietNhapKho")
@IdClass(ChiTietNhapKhoEntity.ChiTietNhapKhoId.class)
public class ChiTietNhapKhoEntity {

    @Id
    @ManyToOne
    @JoinColumn(name = "ID_PhieuNhap")
    private PhieuNhapKhoEntity phieuNhapKho;

    @Id
    @ManyToOne
    @JoinColumn(name = "ID_NguyenLieu")
    private NguyenLieuEntity nguyenLieu;

    @Column(name = "SoLuong")
    private Long soLuong;

    @Column(name = "GiaTien")
    private Long giaTien;

    @Getter
    @Setter
    public static class ChiTietNhapKhoId implements Serializable {
        
        private Long phieuNhapKho;
        private Long nguyenLieu;
        
        public ChiTietNhapKhoId() {
        }
        
        public ChiTietNhapKhoId(Long phieuNhapKho, Long nguyenLieu) {
            this.phieuNhapKho = phieuNhapKho;
            this.nguyenLieu = nguyenLieu;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ChiTietNhapKhoId that = (ChiTietNhapKhoId) o;
            return Objects.equals(phieuNhapKho, that.phieuNhapKho) &&
                   Objects.equals(nguyenLieu, that.nguyenLieu);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(phieuNhapKho, nguyenLieu);
        }
    }
}