package com.javaweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name="Size")
public class SizeEntity {

    @Id
    @Column(name="ID_Size")
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long idSize;

    @Column(name="TenSize")
    private String tenSize;

    @OneToMany(mappedBy = "size", cascade = CascadeType.ALL)
    private List<GiaMonSizeEntity> giaMonSizeEntityList;

    @OneToMany(mappedBy = "size", cascade = CascadeType.ALL)
    private List<ChiTietHoaDonEntity> chiTietHoaDonEntityList;

    @OneToMany(mappedBy = "size", cascade = CascadeType.ALL)
    private List<ChiTietGioHangEntity> chiTietGioHangEntityList;
}