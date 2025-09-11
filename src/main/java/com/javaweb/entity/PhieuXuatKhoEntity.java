package com.javaweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "PhieuXuatKho")
public class PhieuXuatKhoEntity {
    @Id
    @Column(name = "ID_PhieuXuat")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPhieuXuatKho;

    @Column(name = "NgayXuat")
    private Date ngayXuat;

    @ManyToOne
    @JoinColumn(name = "ID_User")
    private UserEntity user;

    @OneToMany(mappedBy = "phieuXuatKho", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ChiTietXuatKhoEntity> chiTietXuatKhoList;

}
