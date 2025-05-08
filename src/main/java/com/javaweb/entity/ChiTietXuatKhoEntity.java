package com.javaweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
@Entity
@Table(name = "ChiTietXuatKho")
public class ChiTietXuatKhoEntity {

    @EmbeddedId
    private ChiTietXuatKhoId id;

    @Column(name="SoLuong")
    private Long soLuong;

    @ManyToOne
    @MapsId("nguyenLieuId")
    @JoinColumn(name = "ID_NguyenLieu")
    private NguyenLieuEntity nguyenLieu;

    @ManyToOne
    @MapsId("phieuXuatId")
    @JoinColumn(name = "ID_PhieuXuat")
    private PhieuXuatKhoEntity phieuXuatKho;

    @Embeddable
    @Getter
    @Setter
    public static class ChiTietXuatKhoId implements Serializable {
        
        @Column(name = "ID_NguyenLieu")
        private Long nguyenLieuId;
        
        @Column(name = "ID_PhieuXuat")
        private Long phieuXuatId;
        
        // Constructor mặc định là bắt buộc cho JPA
        public ChiTietXuatKhoId() {}
        
        public ChiTietXuatKhoId(Long nguyenLieuId, Long phieuXuatId) {
            this.nguyenLieuId = nguyenLieuId;
            this.phieuXuatId = phieuXuatId;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ChiTietXuatKhoId that = (ChiTietXuatKhoId) o;
            return Objects.equals(nguyenLieuId, that.nguyenLieuId) &&
                   Objects.equals(phieuXuatId, that.phieuXuatId);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(nguyenLieuId, phieuXuatId);
        }
    }
}