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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID_PhieuXuat;

    @Column(name = "NgayXuat")
    private Date ngayXuat;

    @ManyToOne
    @JoinColumn(name = "ID_Uer")
    private UserEntity user;

    @OneToMany(mappedBy = "phieuXuatKho", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ChiTietXuatKhoEntity> chiTietXuatKhoList;

}
