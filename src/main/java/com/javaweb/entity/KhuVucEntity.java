package com.javaweb.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@Table(name = "KhuVuc")
public class KhuVucEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TenKhuVuc")
    private String tenKhuVuc;

    @OneToMany(mappedBy = "khuVuc")
    private List<TableEntity> tables;
}
