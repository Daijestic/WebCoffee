package com.javaweb.service;

import com.javaweb.dto.reponse.HoaDonResponse;
import com.javaweb.dto.request.InvoiceRequest;
import org.springframework.data.domain.Page;

public interface HoaDonService {
    void createInvoice(InvoiceRequest invoiceRequest);
    Page<HoaDonResponse> getAllInvoice(Integer pageNo);
}
