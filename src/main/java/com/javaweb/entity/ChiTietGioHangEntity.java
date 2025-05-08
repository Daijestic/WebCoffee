package com.javaweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
@Table(name = "ChiTietGioHang")
@Entity
public class ChiTietGioHangEntity {

    @EmbeddedId
    private ChiTietGioHangId id;

    @Column(name = "SoLuong")
    private Long soLuong;

    @Column(name="GhiChu")
    private String ghiChu;

    @ManyToOne
    @MapsId("monId") // Tên trường trong ChiTietGioHangId
    @JoinColumn(name="ID_Mon")
    private MonEntity mon;

    @ManyToOne
    @MapsId("sizeId") // Tên trường trong ChiTietGioHangId
    @JoinColumn(name="ID_Size")
    private SizeEntity size;

    @ManyToOne
    @MapsId("gioHangId") // Tên trường trong ChiTietGioHangId
    @JoinColumn(name="ID_GioHang")
    private GioHangEntity gioHang;

    @Embeddable
    @Getter
    @Setter
    public static class ChiTietGioHangId implements Serializable {
        
        @Column(name = "ID_Mon")
        private Long monId;
        
        @Column(name = "ID_Size")
        private Long sizeId;
        
        @Column(name = "ID_GioHang")
        private Long gioHangId;
        
        // Constructor không tham số là bắt buộc
        public ChiTietGioHangId() {}
        
        public ChiTietGioHangId(Long monId, Long sizeId, Long gioHangId) {
            this.monId = monId;
            this.sizeId = sizeId;
            this.gioHangId = gioHangId;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ChiTietGioHangId that = (ChiTietGioHangId) o;
            return Objects.equals(monId, that.monId) &&
                   Objects.equals(sizeId, that.sizeId) &&
                   Objects.equals(gioHangId, that.gioHangId);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(monId, sizeId, gioHangId);
        }
    }
}