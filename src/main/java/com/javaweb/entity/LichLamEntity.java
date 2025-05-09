package com.javaweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@Table(name = "LichLam")
public class LichLamEntity {

    @Id
    @Column(name="ID_LichLam")
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private long idLichLam;

    @Column(name="NgayLam")
    private Date ngayLam;

    @ManyToOne
    @JoinColumn(name="ID_User")
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name="ID_Ca")
    private CaLamViecEntity caLamViec;
}
