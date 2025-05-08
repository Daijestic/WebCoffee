package com.javaweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name="GioHang")
public class GioHangEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long ID_GioHang;

    @Column(name="NgayTao")
    private Date ngayTao;

    @ManyToOne
    @JoinColumn(name="ID_User")
    private UserEntity user;

    @OneToMany(mappedBy = "gioHang", cascade = CascadeType.ALL)
    private List<ChiTietGioHangEntity> chiTietGioHangEntityList;
}
