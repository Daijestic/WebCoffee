package com.javaweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name = "ChiTietGioHang")
public class ChiTietGioHangEntity {


    @EmbeddedId
    private ChiTietGioHangId id;

    @Column(name = "SoLuong")
    private Long soLuong;

    @Column(name = "GhiChu")
    private String ghiChu;

    // Quan hệ với MonEntity, khóa ngoại là ID_Mon
    @ManyToOne
    @MapsId("monId") // khớp với trường trong ChiTietGioHangId
    @JoinColumn(name = "ID_Mon")
    private MonEntity mon;

    // Quan hệ với SizeEntity, khóa ngoại là ID_Size
    @ManyToOne
    @MapsId("sizeId") // khớp với trường trong ChiTietGioHangId
    @JoinColumn(name = "ID_Size")
    private SizeEntity size;

    // Quan hệ với UserEntity, khóa ngoại là ID_User
    @ManyToOne
    @MapsId("userId") // khớp với trường trong ChiTietGioHangId
    @JoinColumn(name = "ID_User")
    private UserEntity user;

    public ChiTietGioHangEntity(ChiTietGioHangId chiTietGioHangId, Long soLuong, String ghiChu) {
        this.id = chiTietGioHangId;
        this.soLuong = soLuong;
        this.ghiChu = ghiChu;
    }



    public ChiTietGioHangEntity() {

    }

    /**
     * Lớp đại diện khóa chính phức hợp cho ChiTietGioHangEntity.
     */
    @Embeddable
    @Getter
    @Setter
    public static class ChiTietGioHangId implements Serializable {


        @Column(name = "ID_Mon")
        private Long monId;

        @Column(name = "ID_Size")
        private Long sizeId;

        @Column(name = "ID_User")
        private Long userId;


        public ChiTietGioHangId() {
        }

        public ChiTietGioHangId(Long monId, Long sizeId, Long userId) {
            this.monId = monId;
            this.sizeId = sizeId;
            this.userId = userId;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ChiTietGioHangId)) return false;
            ChiTietGioHangId that = (ChiTietGioHangId) o;
            return Objects.equals(monId, that.monId) &&
                    Objects.equals(sizeId, that.sizeId) &&
                    Objects.equals(userId, that.userId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(monId, sizeId, userId);
        }
    }
}
