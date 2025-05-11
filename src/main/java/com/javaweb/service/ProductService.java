package com.javaweb.service;

import com.javaweb.dto.reponse.ProductResponse;
import com.javaweb.dto.request.ProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProductService {
    List<ProductResponse> getAllProducts();
    Boolean save(ProductRequest productRequest, MultipartFile multipartFile) throws IOException;
    void update(Long id, ProductRequest productRequest, MultipartFile multipartFile) throws IOException;
    void delete(Long id);
    Page<ProductResponse> findAll(Integer pageNo);
    List<ProductResponse> findAllByLoaiMon(String loaiMon);
}
