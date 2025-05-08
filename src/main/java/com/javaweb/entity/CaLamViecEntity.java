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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID_Ca;

    @Column(name = "Giovao")
    private Date giovao;

    @Column(name = "GiaRa")
    private Date giaRa;

    @OneToMany(mappedBy = "caLamViec", cascade = CascadeType.ALL)
    private List<LichLamEntity> lichLams;
}
