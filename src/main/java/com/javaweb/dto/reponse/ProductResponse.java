package com.javaweb.dto.reponse;

import lombok.*;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponse {
    private long idMon;
    private String tenMon;
    private String loaiMon;
    private String moTa;
    private String path;
    private List<GiaMonSizeResponse> giaMonSizeResponses;
}
