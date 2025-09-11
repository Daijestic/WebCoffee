package com.javaweb.converter.dto_to_entity;

import com.javaweb.dto.request.GiaMonSizeRequest;
import com.javaweb.dto.request.ProductRequest;
import com.javaweb.entity.GiaMonSizeEntity;
import com.javaweb.entity.MonEntity;
import com.javaweb.model.FileUploads;
import com.javaweb.repository.MonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class ProductDtoToEntity {

    @Autowired
    private FileUploads fileUploads;

    @Autowired
    private GiaMonSizeDtoToEntity giaMonSizeDtoToEntity;

    @Autowired
    private MonRepository monRepository;

    public MonEntity toMonEntity(ProductRequest productRequest, MultipartFile multipartFile) throws IOException {
        MonEntity monEntity;

        if (productRequest.getIdMon() != null) {
            monEntity = monRepository.findById(productRequest.getIdMon())
                    .orElse(new MonEntity());
        } else {
            monEntity = new MonEntity();
        }

        // Set các thuộc tính cơ bản
        monEntity.setLoaiMon(productRequest.getLoaiMon());
        monEntity.setMoTa(productRequest.getMoTa());
        monEntity.setTenMon(productRequest.getTenMon());

        if (multipartFile != null && !multipartFile.isEmpty()) {
            monEntity.setPath(fileUploads.fileUpload(multipartFile));
        }

        // Xử lý danh sách Giá theo Size
        List<GiaMonSizeEntity> giaMonSizeEntityList = new ArrayList<>();

        List<GiaMonSizeRequest> giaMonSizeRequests = productRequest.getGiaMonSizeRequests();
        if (giaMonSizeRequests != null && !giaMonSizeRequests.isEmpty()) {
            for (GiaMonSizeRequest giaMonSizeRequest : giaMonSizeRequests) {
                GiaMonSizeEntity giaMonSizeEntity = giaMonSizeDtoToEntity.dtoToEntity(giaMonSizeRequest, monEntity);
                giaMonSizeEntityList.add(giaMonSizeEntity);
            }
        }

        // Cập nhật list (JPA sẽ tự động xoá các bản cũ nhờ orphanRemoval nếu cấu hình đúng)
        monEntity.setGiaMonSizeEntities(giaMonSizeEntityList);

        return monEntity;
    }
}
