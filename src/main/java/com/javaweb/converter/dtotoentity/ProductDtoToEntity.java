package com.javaweb.converter.dtotoentity;

import com.javaweb.dto.request.ProductRequest;
import com.javaweb.entity.MonEntity;
import com.javaweb.entity.TongQuatMonEntity;
import com.javaweb.exception.ErrorCode;
import com.javaweb.model.FileUploads;
import com.javaweb.repository.TongQuatMonRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Component
public class ProductDtoToEntity {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileUploads fileUploads;

    @Autowired
    TongQuatMonRepository tongQuatMonRepository;

    public MonEntity toMonEntity(ProductRequest productRequest, MultipartFile multipartFile) throws IOException {
        MonEntity monEntity = modelMapper.map(productRequest, MonEntity.class);
        monEntity.setPath(fileUploads.fileUpload(multipartFile));
        if (!tongQuatMonRepository.existsByTenLoaiMon(productRequest.getLoaiMon())) {
            TongQuatMonEntity tongQuatMonEntity = new TongQuatMonEntity();
            tongQuatMonEntity.setTenLoaiMon(productRequest.getLoaiMon());
            tongQuatMonRepository.save(tongQuatMonEntity);
        }
        TongQuatMonEntity tongQuatMonEntity = tongQuatMonRepository.findByTenLoaiMon(productRequest.getLoaiMon());
        monEntity.setLoaiMon(tongQuatMonEntity);
        return monEntity;
    }
}
