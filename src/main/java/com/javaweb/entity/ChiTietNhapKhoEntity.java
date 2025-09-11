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
public class ChiTietNhapKhoEntity {

    @EmbeddedId
    private ChiTietNhapKhoId id;

    @ManyToOne
    @MapsId("idPhieuNhapKho")
    @JoinColumn(name = "ID_PhieuNhap")
    private PhieuNhapKhoEntity idPhieuNhapKho;

    @ManyToOne
    @MapsId("idNguyenLieu")
    @JoinColumn(name = "ID_NguyenLieu")
    private NguyenLieuEntity idNguyenLieu;

    @Column(name = "SoLuong")
    private Long soLuong;

    @Column(name = "GiaTien")
    private Long giaTien;

    @Embeddable
    @Getter
    @Setter
    public static class ChiTietNhapKhoId implements Serializable {

        @Column(name = "ID_PhieuNhap")
        private Long idPhieuNhapKho;

        @Column(name = "ID_NguyenLieu")
        private Long idNguyenLieu;

        public ChiTietNhapKhoId() {
        }

        public ChiTietNhapKhoId(Long idPhieuNhapKho, Long idNguyenLieu) {
            this.idPhieuNhapKho = idPhieuNhapKho;
            this.idNguyenLieu = idNguyenLieu;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ChiTietNhapKhoId that = (ChiTietNhapKhoId) o;
            return Objects.equals(idPhieuNhapKho, that.idPhieuNhapKho) &&
                    Objects.equals(idNguyenLieu, that.idNguyenLieu);
        }

        @Override
        public int hashCode() {
            return Objects.hash(idPhieuNhapKho, idNguyenLieu);
        }
    }
}