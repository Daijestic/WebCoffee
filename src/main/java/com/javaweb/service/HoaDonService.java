package com.javaweb.service;

import com.javaweb.dto.reponse.HoaDonResponse;
import com.javaweb.dto.request.HoaDonRequest;
import com.javaweb.dto.request.InvoiceRequest;
import org.springframework.data.domain.Page;

import java.util.List;

public interface HoaDonService {
    void createInvoice(InvoiceRequest invoiceRequest);
    Page<HoaDonResponse> getAllInvoice(Integer pageNo);
    HoaDonResponse getInvoiceById(Long id);
    Page<HoaDonResponse> findByTrangThai(String trangThai, Integer pageNo);
    void updateStatus(HoaDonRequest hoaDonRequest);
}
