package com.javaweb.converter.dto_to_entity;

import com.javaweb.dto.request.InvoiceRequest;
import com.javaweb.entity.HoaDonEntity;
import com.javaweb.entity.UserEntity;
import com.javaweb.repository.MonRepository;
import com.javaweb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InvoiceRequestToEntity {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRequestDtoToEntity orderRequestDtoToEntity;

    public HoaDonEntity convertToEntity(InvoiceRequest invoiceRequest) {
        HoaDonEntity hoaDonEntity = new HoaDonEntity();
        UserEntity userEntity = userRepository.findByIdUser(Long.valueOf(invoiceRequest.getCustomerInfo().getId())).get();
        hoaDonEntity.setUser(userEntity);
        orderRequestDtoToEntity.convertToEntity(invoiceRequest.getOrderDetails(), hoaDonEntity);
        return hoaDonEntity;
    }
}
