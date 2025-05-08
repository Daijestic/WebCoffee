package com.javaweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Setter
@Getter
@Entity
@Table(name="NhanXet")
public class NhanXetEntity {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long ID_NhanXet;

    @Column(name="SoSao")
    private int soSao;

    @Column(name="DanhGia")
    private String danhGia;

    @Column(name="NgayDanhGia")
    private Date ngayDanhGia;

    @ManyToOne
    @JoinColumn(name = "ID_User")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "ID_Mon")
    private MonEntity mon;
}
