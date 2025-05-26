package com.javaweb.converter.dto_to_entity;

import com.javaweb.dto.request.LichLamRequest;
import com.javaweb.entity.CaLamViecEntity;
import com.javaweb.entity.LichLamEntity;
import com.javaweb.entity.UserEntity;
import com.javaweb.repository.CaLamViecRepository;
import com.javaweb.repository.LichLamRepository;
import com.javaweb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LichLamDtoToEntity {

    @Autowired
    private LichLamRepository lichLamRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CaLamViecRepository caLamViecRepository;

    public LichLamEntity convertToEntity(LichLamRequest lichLamRequest) {
        LichLamEntity lichLamEntity;
        if (lichLamRequest.getIdLichLam() != null) {
            lichLamEntity = lichLamRepository.findById(lichLamRequest.getIdLichLam()).orElse(null);
        } else {
            lichLamEntity = new LichLamEntity();
        }
        UserEntity userEntity = userRepository.findById(lichLamRequest.getIdUser()).orElse(null);
        CaLamViecEntity caLamViecEntity = caLamViecRepository.findById(lichLamRequest.getIdCa()).orElse(null);
        lichLamEntity.setUser(userEntity);
        lichLamEntity.setCaLamViec(caLamViecEntity);
        lichLamEntity.setNgayLam(lichLamRequest.getNgayLam());
        return lichLamEntity;
    }
}
