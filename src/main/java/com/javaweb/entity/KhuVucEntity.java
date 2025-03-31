package com.javaweb.entity;

import javax.persistence.*;

import java.util.List;

@Entity
@Table(name = "KhuVuc")
public class KhuVucEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TenKhuVuc")
    private String tenKhuVuc;

    @OneToMany(mappedBy = "khuVuc",cascade = CascadeType.ALL)
    private List<TableEntity> tables;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTenKhuVuc() {
        return tenKhuVuc;
    }

    public void setTenKhuVuc(String tenKhuVuc) {
        this.tenKhuVuc = tenKhuVuc;
    }

    public List<TableEntity> getTables() {
        return tables;
    }

    public void setTables(List<TableEntity> tables) {
        this.tables = tables;
    }
}
