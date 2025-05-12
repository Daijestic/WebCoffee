package com.javaweb.service.impl;

import com.javaweb.converter.dto_to_entity.ProductDtoToEntity;
import com.javaweb.converter.entity_to_dto.ProductEntiryToDto;
import com.javaweb.dto.reponse.ProductResponse;
import com.javaweb.dto.request.ProductRequest;
import com.javaweb.entity.MonEntity;
import com.javaweb.model.FileUploads;
import com.javaweb.repository.MonRepository;
import com.javaweb.service.ProductService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductDtoToEntity productDtoToEntity;

    @Autowired
    MonRepository monRepository;

    @Autowired
    ProductEntiryToDto productEntiryToDto;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    FileUploads fileUploads;

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
    public ProductResponse save(ProductRequest productRequest, MultipartFile multipartFile) throws IOException {
        return productEntiryToDto.toProductReponse(monRepository.save(productDtoToEntity.toMonEntity(productRequest, multipartFile)));
    }

    @Override
    public ProductResponse update(ProductRequest productRequest, MultipartFile multipartFile) throws IOException {
        return productEntiryToDto.toProductReponse(monRepository.save(productDtoToEntity.toMonEntity(productRequest, multipartFile)));
    }

    @Override
    public void delete(Long id) {
        monRepository.deleteById(id);
    }

    @Override
    public Page<ProductResponse> findAll(Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 9);
        return monRepository.findAll(pageable)
                .map(monEntity -> {
                    return productEntiryToDto.toProductReponse(monEntity);
                });
    }

    @Override
    public List<ProductResponse> findAllByLoaiMon(String loaiMon) {
        return monRepository.findAllByLoaiMon(loaiMon)
                .stream().map(monEntity -> {
                    return productEntiryToDto.toProductReponse(monEntity);
                }).toList();
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        return productEntiryToDto.toProductReponse(monRepository.findById(productId).get());
    }

    @Override
    public Page<ProductResponse> findAllByLoaiMon(String name, Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 9);
        return monRepository.findAllByLoaiMon(name, pageable)
                .map(monEntity -> {;
                    return productEntiryToDto.toProductReponse(monEntity);
                });
    }
}
