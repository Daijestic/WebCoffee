package com.javaweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "TongQuatMon")
public class TongQuatMonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tenLoaiMon", unique = true, nullable = false)
    private String tenLoaiMon;

    @Column(name = "MoTa")
    private String moTa;

    @OneToMany(mappedBy = "loaiMon")
    private List<MonEntity> listMon;
}
