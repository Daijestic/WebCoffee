package com.javaweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "ChiTietHoaDon")
public class ChiTietHoaDonEntity {

    @EmbeddedId
    private ChiTietHoaDonId id;
    
    @Column(name = "SoLuong")
    private Long soLuong;

    @Column(name = "GhiChu")
    private String ghiChu;

    @ManyToOne
    @MapsId("monId")
    @JoinColumn(name="ID_Mon")
    private MonEntity mon;

    @ManyToOne
    @MapsId("sizeId")
    @JoinColumn(name="ID_Size")
    private SizeEntity size;

    @ManyToOne
    @MapsId("hoaDonId")
    @JoinColumn(name="ID_HoaDon")
    private HoaDonEntity hoaDon;

    @Embeddable
    @Getter
    @Setter
    public static class ChiTietHoaDonId implements Serializable {
        
        @Column(name = "ID_Mon")
        private Long monId;
        
        @Column(name = "ID_Size")
        private Long sizeId;
        
        @Column(name = "ID_HoaDon")
        private Long hoaDonId;
        
        // Constructor mặc định là bắt buộc cho JPA
        public ChiTietHoaDonId() {}
        
        public ChiTietHoaDonId(Long monId, Long sizeId, Long hoaDonId) {
            this.monId = monId;
            this.sizeId = sizeId;
            this.hoaDonId = hoaDonId;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            ChiTietHoaDonId that = (ChiTietHoaDonId) o;
            return Objects.equals(monId, that.monId) &&
                   Objects.equals(sizeId, that.sizeId) &&
                   Objects.equals(hoaDonId, that.hoaDonId);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(monId, sizeId, hoaDonId);
        }
    }
}