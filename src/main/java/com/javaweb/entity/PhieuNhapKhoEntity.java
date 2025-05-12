package com.javaweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "PhieuNhapKho")
public class PhieuNhapKhoEntity {

    @Id
    @Column(name = "ID_PhieuNhap")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPhieuNhap;

    @Column(name = "NgayNhap")
    private Date ngayNhap;

    @ManyToOne
    @JoinColumn(name = "ID_User")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name="ID_NhaCungCap")
    private NhaCungCapEntity nhaCungCap;

    @OneToMany(mappedBy = "idPhieuNhapKho", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<ChiTietNhapKhoEntity> chiTietNhapKhoList;
}
