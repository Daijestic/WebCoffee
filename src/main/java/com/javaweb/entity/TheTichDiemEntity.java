package com.javaweb.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "TheTichDiem")
public class TheTichDiemEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "DiemTichLuy")
    private Long diemTichLuy;

    @OneToOne(mappedBy = "theTichDiem")
    private KhachHangEntity khachHang;

}
