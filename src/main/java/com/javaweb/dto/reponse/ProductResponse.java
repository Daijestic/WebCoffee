package com.javaweb.dto.reponse;

import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private long idMon;
    private String tenMon;
    private String loaiMon;
    private Long giaBan;
    private String moTa;
    private String path;
}
