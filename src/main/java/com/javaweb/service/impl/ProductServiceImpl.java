package com.javaweb.service.impl;

import com.javaweb.converter.dto_to_entity.ProductDtoToEntity;
import com.javaweb.converter.entity_to_dto.ProductEntiryToDto;
import com.javaweb.dto.reponse.ProductResponse;
import com.javaweb.dto.request.ProductRequest;
import com.javaweb.entity.GiaMonSizeEntity;
import com.javaweb.entity.MonEntity;
import com.javaweb.entity.SizeEntity;
import com.javaweb.exception.ApplicationException;
import com.javaweb.exception.ErrorCode;
import com.javaweb.model.FileUploads;
import com.javaweb.repository.MonRepository;
import com.javaweb.service.ProductService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
           ProductResponse productResponse = new ProductResponse();
           if (!monEntity.getGiaMonSizeEntities().isEmpty()) {
               List<GiaMonSizeEntity> sizeEntities = monEntity.getGiaMonSizeEntities();
               productResponse = productEntiryToDto.toProductReponse(monEntity, sizeEntities.get(0).getSize());
           } else {
               productResponse = productEntiryToDto.toProductReponse(monEntity, null);
               productResponse.setGiaBan(0L);
           }
           productRepons.add(productResponse);
       }
       return productRepons;
    }

    @Override
    public Boolean save(ProductRequest productRequest, MultipartFile multipartFile) throws IOException {
        MonEntity monEntity = productDtoToEntity.toMonEntity(productRequest, multipartFile);
        monRepository.save(monEntity);
        return true;
    }

    @Override
    public void update(Long id, ProductRequest productRequest, MultipartFile multipartFile) throws IOException {
        MonEntity monEntity = monRepository.findById(id)
                .orElseThrow(() -> new ApplicationException(ErrorCode.MON_NOT_EXIST));
        modelMapper.map(productRequest, monEntity);
        if (!multipartFile.isEmpty() && multipartFile.getName() != null && !multipartFile.getName().equals("")) {
            monEntity.setPath(fileUploads.fileUpload(multipartFile));
        }
        monRepository.save(monEntity);
    }

    @Override
    public void delete(Long id) {
        monRepository.deleteById(id);
    }
}
