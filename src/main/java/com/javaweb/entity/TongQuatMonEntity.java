package com.javaweb.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "TongQuatMon")
public class TongQuatMonEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "MoTa")
    private String moTa;

    @OneToMany(mappedBy = "loaiMon")
    private List<MonEntity> listMon;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public List<MonEntity> getListMon() {
        return listMon;
    }

    public void setListMon(List<MonEntity> listMon) {
        this.listMon = listMon;
    }
}
