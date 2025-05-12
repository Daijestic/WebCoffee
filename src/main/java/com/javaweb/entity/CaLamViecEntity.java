package com.javaweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "CaLamViec")
public class CaLamViecEntity {

    @Id
    @Column(name = "ID_Ca")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCa;

    @Column(name = "GioVao")
    private Date gioVao;

    @Column(name = "GioRa")
    private Date giaRa;

    @OneToMany(mappedBy = "caLamViec", cascade = CascadeType.ALL)
    private List<LichLamEntity> lichLams;
}
