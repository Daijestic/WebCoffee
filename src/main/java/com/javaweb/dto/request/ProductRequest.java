package com.javaweb.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ProductRequest {
    private String tenMon;
    private String loaiMon;
    private Long giaBan;
    private String moTa;
}
