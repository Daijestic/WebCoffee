package com.javaweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Entity
@Table(name="GiaMonSize")
public class GiaMonSizeEntity {

    @EmbeddedId
    private GiaMonSizeId id;

    @Column(name="GiaBan")
    private Long giaBan;

    @ManyToOne
    @MapsId("monId")
    @JoinColumn(name = "ID_Mon")
    private MonEntity mon;

    @ManyToOne
    @MapsId("sizeId")
    @JoinColumn(name = "ID_Size")
    private SizeEntity size;

    @Embeddable
    @Getter
    @Setter
    public static class GiaMonSizeId implements Serializable {
        
        @Column(name = "ID_Mon")
        private Long monId;
        
        @Column(name = "ID_Size")
        private Long sizeId;
        
        // Constructor mặc định là bắt buộc cho JPA
        public GiaMonSizeId() {}
        
        public GiaMonSizeId(Long monId, Long sizeId) {
            this.monId = monId;
            this.sizeId = sizeId;
        }
        
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            GiaMonSizeId that = (GiaMonSizeId) o;
            return Objects.equals(monId, that.monId) &&
                   Objects.equals(sizeId, that.sizeId);
        }
        
        @Override
        public int hashCode() {
            return Objects.hash(monId, sizeId);
        }
    }
}