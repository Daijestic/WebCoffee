package com.javaweb.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@Setter
public class ProductRequest {
    private Long idMon;
    private String tenMon;
    private String loaiMon;
    private List<GiaMonSizeRequest> giaMonSizeRequests;
    private String moTa;
}
