package com.javaweb.service;

import com.javaweb.dto.request.InvoiceRequest;

public interface InvoiceService {
    void createInvoice(InvoiceRequest invoiceRequest);
}
