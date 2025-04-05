package com.javaweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "Ban")
public class TableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TrangThai")
    private String trangThai;

    @Column(name = "SoGhe")
    private Long soghe;

    @ManyToOne
    @JoinColumn(name = "Id_KhuVuc")
    private KhuVucEntity khuVuc;

    @OneToMany(mappedBy = "ban")
    private List<HoaDonEntity> hoaDon;

}
