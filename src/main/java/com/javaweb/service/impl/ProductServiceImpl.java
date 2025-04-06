package com.javaweb.service.impl;

import com.javaweb.converter.dtotoentity.ProductDtoToEntity;
import com.javaweb.converter.entitytodto.ProductEntiryToDto;
import com.javaweb.dto.reponse.ProductResponse;
import com.javaweb.dto.request.ProductRequest;
import com.javaweb.entity.MonEntity;
import com.javaweb.repository.MonRepository;
import com.javaweb.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductDtoToEntity productDtoToEntity;

    @Autowired
    MonRepository monRepository;

    @Autowired
    ProductEntiryToDto productEntiryToDto;

    @Override
    public List<ProductResponse> getAllProducts() {
       List<ProductResponse> productRepons = new ArrayList<>();
       List<MonEntity> monEntities = monRepository.findAll();
       for (MonEntity monEntity : monEntities) {
           productRepons.add(productEntiryToDto.toProductReponse(monEntity));
       }
       return productRepons;
    }

    @Override
    public Boolean save(ProductRequest productRequest, MultipartFile multipartFile) throws IOException {
        MonEntity monEntity = productDtoToEntity.toMonEntity(productRequest, multipartFile);
        monRepository.save(monEntity);
        return true;
    }
}
