package com.javaweb.service.impl;

import com.javaweb.dto.request.InvoiceRequest;
import com.javaweb.dto.request.ItemsRequest;
import com.javaweb.entity.ChiTietHoaDonEntity;
import com.javaweb.entity.HoaDonEntity;
import com.javaweb.entity.KhachHangEntity;
import com.javaweb.repository.ChiTietHoaDonRepository;
import com.javaweb.repository.HoaDonRepository;
import com.javaweb.repository.KhachHangRepository;
import com.javaweb.repository.MonRepository;
import com.javaweb.service.InvoiceService;
import com.javaweb.service.MonService;
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
    private KhachHangRepository khachHangRepository;

    @Autowired
    private MonRepository monRepository;

    @Override
    public void createInvoice(InvoiceRequest invoiceRequest) {
        HoaDonEntity hoaDonEntity = new HoaDonEntity();
        ChiTietHoaDonEntity chiTietHoaDonEntity = new ChiTietHoaDonEntity();
        KhachHangEntity khachHangEntity = khachHangRepository.findByEmail(invoiceRequest.getCustomerInfo().getEmail())
                .orElseGet(() -> {
                    KhachHangEntity newKhachHang = new KhachHangEntity();
                    newKhachHang.setEmail(invoiceRequest.getCustomerInfo().getEmail());
                    newKhachHang.setHoTen(invoiceRequest.getCustomerInfo().getFullName());
                    newKhachHang.setSdt(invoiceRequest.getCustomerInfo().getPhone());
                    newKhachHang.setDiaChi(invoiceRequest.getCustomerInfo().getAddress() + " - " + invoiceRequest.getCustomerInfo().getWard() + " - "
                    + invoiceRequest.getCustomerInfo().getDistrict() + " - " + invoiceRequest.getCustomerInfo().getCity());
                    return newKhachHang;
                });


        hoaDonEntity.setKhachHang(khachHangEntity);

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
            chiTietHoaDon.setGiamGia(Long.parseLong(item.getDiscount()));
            chiTietHoaDonEntities.add(chiTietHoaDon);
        }

        hoaDonEntity.setPhuongThucThanhToan(invoiceRequest.getPaymentMethod());

        hoaDonEntity.setListChiTietHoaDon(chiTietHoaDonEntities);

        hoaDonRepository.save(hoaDonEntity);
    }
}
