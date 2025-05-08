package com.javaweb.service.impl;

import com.javaweb.dto.request.InvoiceRequest;
import com.javaweb.dto.request.ItemsRequest;
import com.javaweb.entity.ChiTietHoaDonEntity;
import com.javaweb.entity.HoaDonEntity;
import com.javaweb.entity.UserEntity;
import com.javaweb.repository.ChiTietHoaDonRepository;
import com.javaweb.repository.HoaDonRepository;
import com.javaweb.repository.UserRepository;
import com.javaweb.repository.MonRepository;
import com.javaweb.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class InvoiceServiceImpl implements InvoiceService {

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private ChiTietHoaDonRepository chiTietHoaDonRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MonRepository monRepository;

    @Override
    public void createInvoice(InvoiceRequest invoiceRequest) {
        HoaDonEntity hoaDonEntity = new HoaDonEntity();
        ChiTietHoaDonEntity chiTietHoaDonEntity = new ChiTietHoaDonEntity();
        UserEntity userEntity = userRepository.findByEmail(invoiceRequest.getCustomerInfo().getEmail())
                .orElseGet(() -> {
                    UserEntity newUser = new UserEntity();
                    newUser.setEmail(invoiceRequest.getCustomerInfo().getEmail());
                    newUser.setHoTen(invoiceRequest.getCustomerInfo().getFullName());
                    newUser.setSdt(invoiceRequest.getCustomerInfo().getPhone());
                    newUser.setDiaChi(invoiceRequest.getCustomerInfo().getAddress() + " - " + invoiceRequest.getCustomerInfo().getWard() + " - "
                    + invoiceRequest.getCustomerInfo().getDistrict() + " - " + invoiceRequest.getCustomerInfo().getCity());
                    return newUser;
                });


        hoaDonEntity.setUser(userEntity);

        hoaDonEntity.setDiaChi(invoiceRequest.getCustomerInfo().getAddress() + " - " + invoiceRequest.getCustomerInfo().getWard() + " - "
                + invoiceRequest.getCustomerInfo().getDistrict() + " - " + invoiceRequest.getCustomerInfo().getCity());

        hoaDonEntity.setNgayGioLapHoaDon(new Date());

        hoaDonEntity.setTrangThai("Chờ xác nhận");

        List<ChiTietHoaDonEntity> chiTietHoaDonEntities = new ArrayList<>();

        for (ItemsRequest item : invoiceRequest.getOrderDetails().getItems()) {
            ChiTietHoaDonEntity chiTietHoaDon = new ChiTietHoaDonEntity();
            chiTietHoaDon.setMon(monRepository.findById(Long.parseLong(item.getId())).get());
            chiTietHoaDon.setGhiChu(invoiceRequest.getCustomerInfo().getNote());
            chiTietHoaDon.setSoLuong(Long.parseLong(item.getQuantity()));
//            chiTietHoaDon.setGiamGia(Long.parseLong(item.getDiscount()));
            chiTietHoaDonEntities.add(chiTietHoaDon);
        }

        hoaDonEntity.setPhuongThucThanhToan(invoiceRequest.getPaymentMethod());

        hoaDonEntity.setChiTietHoaDons(chiTietHoaDonEntities);

        hoaDonRepository.save(hoaDonEntity);
    }
}
