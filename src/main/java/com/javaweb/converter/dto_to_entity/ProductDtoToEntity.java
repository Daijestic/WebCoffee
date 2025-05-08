package com.javaweb.converter.dto_to_entity;

import com.javaweb.dto.request.ProductRequest;
import com.javaweb.entity.MonEntity;
import com.javaweb.model.FileUploads;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class ProductDtoToEntity {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileUploads fileUploads;
    
    public MonEntity toMonEntity(ProductRequest productRequest, MultipartFile multipartFile) throws IOException {
        MonEntity monEntity = modelMapper.map(productRequest, MonEntity.class);
        monEntity.setPath(fileUploads.fileUpload(multipartFile));
        return monEntity;
    }
}
