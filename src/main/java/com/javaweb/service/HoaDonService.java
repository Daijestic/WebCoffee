package com.javaweb.service;

import com.javaweb.dto.reponse.HoaDonResponse;
import com.javaweb.dto.request.HoaDonRequest;
import com.javaweb.dto.request.InvoiceRequest;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface HoaDonService {
    void createInvoice(InvoiceRequest invoiceRequest);
    Page<HoaDonResponse> getAllInvoice(Integer pageNo);
    HoaDonResponse getInvoiceById(Long id);
    Page<HoaDonResponse> findByTrangThai(String trangThai, Integer pageNo);
    void updateStatus(HoaDonRequest hoaDonRequest);
    void deleteHoaDon(Long idHoaDon);
    Long countHoaDonsByDateRange(String startDate, String endDate);
    Long calculateTotalRevenue(String startDate, String endDate);
    Double calculateGrowthRate(String startDate, String endDate);
    Map<String, Object> calculateTotalRevenueAndOrders(String startDate, String endDate);
    List<Map<String, Object>> getSalesReportByTimeRange(
            String startDate, String endDate, String timeUnit);
    List<Map<String, Object>> getPaymentMethodsReport(String startDate, String endDate);
    Map<String, List<Map<String, Object>>> getComparisonReportByPeriod(String period);
}
