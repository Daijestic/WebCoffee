package com.javaweb.service.impl;

import com.javaweb.converter.dto_to_entity.InvoiceRequestToEntity;
import com.javaweb.converter.dto_to_entity.ItemsRequestDtoToEntity;
import com.javaweb.converter.entity_to_dto.HoaDonEntityToDTO;
import com.javaweb.dto.reponse.HoaDonResponse;
import com.javaweb.dto.request.HoaDonRequest;
import com.javaweb.dto.request.InvoiceRequest;
import com.javaweb.dto.request.ItemsRequest;
import com.javaweb.entity.ChiTietHoaDonEntity;
import com.javaweb.entity.HoaDonEntity;
import com.javaweb.entity.UserEntity;
import com.javaweb.repository.ChiTietHoaDonRepository;
import com.javaweb.repository.HoaDonRepository;
import com.javaweb.repository.UserRepository;
import com.javaweb.repository.MonRepository;
import com.javaweb.service.HoaDonService;
import com.javaweb.service.UsersService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class HoaDonServiceImpl implements HoaDonService {

    @Autowired
    private HoaDonRepository hoaDonRepository;

    @Autowired
    private ChiTietHoaDonRepository chiTietHoaDonRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MonRepository monRepository;

    @Autowired
    private HoaDonEntityToDTO hoaDonEntityToDTO;

    @Autowired
    private InvoiceRequestToEntity invoiceRequestToEntity;

    @Autowired
    private UsersService usersService;

    @Autowired
    private ItemsRequestDtoToEntity itemsRequestDtoToEntity;

    @Override
    @Transactional
    public void createInvoice(InvoiceRequest invoiceRequest) {
        // Xóa giỏ hàng người dùng
        usersService.xoaGioHang(Long.valueOf(invoiceRequest.getCustomerInfo().getId()));

        // Tạo đối tượng HoaDonEntity (chưa có chi tiết)
        HoaDonEntity hoaDon = new HoaDonEntity();
        UserEntity userEntity = userRepository.findById(Long.valueOf(invoiceRequest.getCustomerInfo().getId())).get();
        hoaDon.setUser(userEntity);

        // 👉 Lưu để phát sinh ID
        hoaDon = hoaDonRepository.save(hoaDon);

        // Duyệt danh sách chi tiết
        List<ChiTietHoaDonEntity> chiTietList = new ArrayList<>();
        for (ItemsRequest item : invoiceRequest.getOrderDetails().getItems()) {
            ChiTietHoaDonEntity chiTiet = itemsRequestDtoToEntity.convertToEntity(item, hoaDon);
            chiTietList.add(chiTiet);
        }

        hoaDon.setPhiShip(Long.valueOf(invoiceRequest.getOrderDetails().getShippingFee()));

        // Gán ngược lại chi tiết vào hóa đơn
        hoaDon.setChiTietHoaDons(chiTietList);

        hoaDon.setTrangThai("CHỜ XÁC NHẬN");
        hoaDon.setHinhThuc("Online");
        hoaDon.setPhuongThucThanhToan("Tiền mặt");
        hoaDon.setNgayGioLapHoaDon(new Date());

        // 👉 Lưu lại hóa đơn kèm danh sách chi tiết
        hoaDonRepository.save(hoaDon);
    }

    @Override
    public Page<HoaDonResponse> getAllInvoice(Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 6);
        return hoaDonRepository.findAll(pageable)
                .map(hoaDonEntity -> {
                    return hoaDonEntityToDTO.convert(hoaDonEntity);
                });
    }

    @Override
    public HoaDonResponse getInvoiceById(Long id) {
        return hoaDonEntityToDTO.convert(hoaDonRepository.findById(id).get());
    }

    @Override
    public Page<HoaDonResponse> findByTrangThai(String trangThai, Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 6);
        return hoaDonRepository.findByTrangThai(trangThai, pageable)
                .map(hoaDonEntity -> {;
                    return hoaDonEntityToDTO.convert(hoaDonEntity);
                });
    }

    @Override
    public void updateStatus(HoaDonRequest hoaDonRequest) {
        HoaDonEntity hoaDonEntity = hoaDonRepository.findByIdHoaDon(hoaDonRequest.getIdHoaDon());
        hoaDonEntity.setTrangThai(hoaDonRequest.getTrangThai());
        hoaDonRepository.save(hoaDonEntity);
    }

    @Override
    public void deleteHoaDon(Long idHoaDon) {
        hoaDonRepository.deleteById(idHoaDon);
    }


}
