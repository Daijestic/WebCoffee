package com.javaweb.converter.dto_to_entity;

import com.javaweb.dto.request.GiaMonSizeRequest;
import com.javaweb.dto.request.ProductRequest;
import com.javaweb.entity.GiaMonSizeEntity;
import com.javaweb.entity.MonEntity;
import com.javaweb.model.FileUploads;
import com.javaweb.repository.GiaMonSizeRepository;
import com.javaweb.repository.MonRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductDtoToEntity {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private FileUploads fileUploads;

    @Autowired
    private GiaMonSizeDtoToEntity giaMonSizeDtoToEntity;

    @Autowired
    private MonRepository monRepository;

    @Autowired
    private GiaMonSizeRepository giaMonSizeRepository;

    public MonEntity toMonEntity(ProductRequest productRequest, MultipartFile multipartFile) throws IOException {
        MonEntity monEntity;
        if (productRequest.getIdMon() != null) {
            monEntity = monRepository.findById(productRequest.getIdMon()).get();
        } else {
            monEntity = new MonEntity();
        }
        List<GiaMonSizeRequest> giaMonSizeRequests = productRequest.getGiaMonSizeRequests();
        List<GiaMonSizeEntity> giaMonSizeEntityList = new ArrayList<>();
        if (monEntity.getGiaMonSizeEntities() != null && !monEntity.getGiaMonSizeEntities().isEmpty()) {
            List<GiaMonSizeEntity> giaMonSizeEntityListOld = monEntity.getGiaMonSizeEntities();
            for (GiaMonSizeEntity giaMonSizeEntityOld : giaMonSizeEntityListOld) {
                monEntity.getGiaMonSizeEntities().remove(giaMonSizeEntityOld);
                giaMonSizeRepository.deleteById(giaMonSizeEntityOld.getId());
            }
        }
        if (giaMonSizeRequests != null && !giaMonSizeRequests.isEmpty()) {
            for (GiaMonSizeRequest giaMonSizeRequest : giaMonSizeRequests) {
                giaMonSizeEntityList.add(giaMonSizeDtoToEntity.dtoToEntity(giaMonSizeRequest, monEntity));
            }
        }
        monEntity.setLoaiMon(productRequest.getLoaiMon());
        monEntity.setMoTa(productRequest.getMoTa());
        monEntity.setTenMon(productRequest.getTenMon());
        monEntity.setGiaMonSizeEntities(giaMonSizeEntityList);
        monEntity.setPath(fileUploads.fileUpload(multipartFile));
        return monEntity;
    }
}
