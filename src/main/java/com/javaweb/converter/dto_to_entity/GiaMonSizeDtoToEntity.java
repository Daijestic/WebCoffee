package com.javaweb.converter.dto_to_entity;

import com.javaweb.dto.request.GiaMonSizeRequest;
import com.javaweb.entity.GiaMonSizeEntity;
import com.javaweb.entity.MonEntity;
import com.javaweb.entity.SizeEntity;
import com.javaweb.repository.GiaMonSizeRepository;
import com.javaweb.repository.SizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GiaMonSizeDtoToEntity {

    @Autowired
    private GiaMonSizeRepository giaMonSizeRepository;

    @Autowired
    private SizeRepository sizeRepository;

    public GiaMonSizeEntity dtoToEntity(GiaMonSizeRequest giaMonSizeRequest, MonEntity monEntity){
    	GiaMonSizeEntity.GiaMonSizeId giaMonSizeId = new GiaMonSizeEntity.GiaMonSizeId();
        giaMonSizeId.setMonId(monEntity.getIdMon());
        giaMonSizeId.setSizeId(giaMonSizeRequest.getIdSize());
        GiaMonSizeEntity giaMonSizeEntity = new GiaMonSizeEntity();
        SizeEntity sizeEntity = sizeRepository.findById(giaMonSizeRequest.getIdSize()).get();
        giaMonSizeEntity.setId(giaMonSizeId);
        giaMonSizeEntity.setGiaBan(giaMonSizeRequest.getGiaBan());
        giaMonSizeEntity.setMon(monEntity);
        giaMonSizeEntity.setSize(sizeEntity);
        return giaMonSizeEntity;
    }
}
