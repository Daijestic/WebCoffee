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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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

    /**
     * Tính tổng doanh thu và số lượng đơn hàng trong khoảng thời gian
     */
    @Override
    public Map<String, Object> calculateTotalRevenueAndOrders(String startDate, String endDate) {
        Map<String, Object> result = new HashMap<>();

        Date start = parseDate(startDate);
        Date end = parseDate(endDate);

        // Lấy tất cả hóa đơn trong khoảng thời gian
        List<HoaDonEntity> hoaDons = hoaDonRepository.findByNgayGioLapHoaDonBetween(start, end);

        // Đếm tổng đơn hàng
        long totalOrders = hoaDons.size();
        result.put("totalOrders", totalOrders);

        // Tính tổng doanh thu
        long totalRevenue = hoaDons.stream()
                .filter(h -> "HOÀN THÀNH".equals(h.getTrangThai()))
                .mapToLong(this::calculateOrderTotal)
                .sum();
        result.put("totalRevenue", totalRevenue);

        // Lấy số lượng khách hàng mới từ UserService
        result.put("totalCustomers", usersService.countNewCustomers(startDate, endDate));

        // Tính tăng trưởng
        double growthRate = calculateGrowthRate(startDate, endDate);
        result.put("growthRate", growthRate);

        return result;
    }

    /**
     * Tính tổng doanh thu
     */
    @Override
    public Long calculateTotalRevenue(String startDate, String endDate) {
        Date start = parseDate(startDate);
        Date end = parseDate(endDate);

        List<HoaDonEntity> hoaDons = hoaDonRepository.findByNgayGioLapHoaDonBetweenAndTrangThai(
                start, end, "HOÀN THÀNH");

        return hoaDons.stream()
                .mapToLong(this::calculateOrderTotal)
                .sum();
    }

    /**
     * Đếm số đơn hàng trong khoảng thời gian
     */
    @Override
    public Long countHoaDonsByDateRange(String startDate, String endDate) {
        Date start = parseDate(startDate);
        Date end = parseDate(endDate);

        return hoaDonRepository.countByNgayGioLapHoaDonBetween(start, end);
    }

    /**
     * Tính tỷ lệ tăng trưởng so với kỳ trước
     */
    @Override
    public Double calculateGrowthRate(String startDate, String endDate) {
        try {
            // Chuyển đổi string thành date
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date start = (startDate != null) ? format.parse(startDate) : null;
            Date end = (endDate != null) ? format.parse(endDate) : new Date();

            if (start == null) {
                // Nếu không có ngày bắt đầu, lấy 30 ngày trước end
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(end);
                calendar.add(Calendar.DAY_OF_MONTH, -30);
                start = calendar.getTime();
            }

            // Tính khoảng thời gian hiện tại
            long currentPeriodDays = (end.getTime() - start.getTime()) / (24 * 60 * 60 * 1000);

            // Tính khoảng thời gian trước đó
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(start);
            calendar.add(Calendar.DAY_OF_MONTH, -1 * (int)currentPeriodDays);
            Date previousPeriodEnd = new Date(start.getTime() - 1);
            Date previousPeriodStart = calendar.getTime();

            // Tính doanh thu kỳ hiện tại
            List<HoaDonEntity> currentPeriodOrders = hoaDonRepository.findByNgayGioLapHoaDonBetweenAndTrangThai(
                    start, end, "HOÀN THÀNH");
            long currentRevenue = currentPeriodOrders.stream()
                    .mapToLong(this::calculateOrderTotal)
                    .sum();

            // Tính doanh thu kỳ trước
            List<HoaDonEntity> previousPeriodOrders = hoaDonRepository.findByNgayGioLapHoaDonBetweenAndTrangThai(
                    previousPeriodStart, previousPeriodEnd, "HOÀN THÀNH");
            long previousRevenue = previousPeriodOrders.stream()
                    .mapToLong(this::calculateOrderTotal)
                    .sum();

            // Tính tỷ lệ tăng trưởng
            if (previousRevenue > 0) {
                return Math.round(((double) (currentRevenue - previousRevenue) / previousRevenue) * 100 * 100.0) / 100.0;
            } else {
                return 100.0; // Nếu kỳ trước = 0, tính là tăng 100%
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    /**
     * Lấy báo cáo doanh thu theo thời gian
     */
    @Override
    public List<Map<String, Object>> getSalesReportByTimeRange(
            String startDate, String endDate, String groupBy) {

        Date start = parseDate(startDate);
        Date end = parseDate(endDate);

        // Lấy dữ liệu hóa đơn hoàn thành
        List<HoaDonEntity> completedOrders = hoaDonRepository.findByNgayGioLapHoaDonBetweenAndTrangThai(
                start, end, "HOÀN THÀNH");

        Map<String, List<HoaDonEntity>> groupedOrders = new HashMap<>();

        // Nhóm theo ngày/tuần/tháng
        SimpleDateFormat formatter;
        Calendar calendar = Calendar.getInstance();

        switch (groupBy) {
            case "weekly":
                // Nhóm theo tuần
                formatter = new SimpleDateFormat("yyyy-'W'ww");
                groupedOrders = completedOrders.stream()
                        .collect(Collectors.groupingBy(order -> {
                            calendar.setTime(order.getNgayGioLapHoaDon());
                            return formatter.format(calendar.getTime());
                        }));
                break;
            case "monthly":
                // Nhóm theo tháng
                formatter = new SimpleDateFormat("yyyy-MM");
                groupedOrders = completedOrders.stream()
                        .collect(Collectors.groupingBy(order -> formatter.format(order.getNgayGioLapHoaDon())));
                break;
            default:
                // Mặc định nhóm theo ngày
                formatter = new SimpleDateFormat("yyyy-MM-dd");
                groupedOrders = completedOrders.stream()
                        .collect(Collectors.groupingBy(order -> formatter.format(order.getNgayGioLapHoaDon())));
                break;
        }

        // Chuyển đổi thành dữ liệu báo cáo
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map.Entry<String, List<HoaDonEntity>> entry : groupedOrders.entrySet()) {
            Map<String, Object> data = new HashMap<>();
            data.put("date", entry.getKey());
            data.put("orders", entry.getValue().size());

            long revenue = entry.getValue().stream()
                    .mapToLong(this::calculateOrderTotal)
                    .sum();

            data.put("revenue", revenue);
            result.add(data);
        }

        // Sắp xếp theo ngày tăng dần
        result.sort((a, b) -> ((String) a.get("date")).compareTo((String) b.get("date")));

        return result;
    }

    /**
     * Lấy báo cáo phương thức thanh toán
     */
    @Override
    public List<Map<String, Object>> getPaymentMethodsReport(String startDate, String endDate) {
        Date start = parseDate(startDate);
        Date end = parseDate(endDate);

        // Lấy dữ liệu hóa đơn hoàn thành
        List<HoaDonEntity> completedOrders = hoaDonRepository.findByNgayGioLapHoaDonBetweenAndTrangThai(
                start, end, "HOÀN THÀNH");

        // Tính tổng doanh thu
        long totalRevenue = completedOrders.stream()
                .mapToLong(this::calculateOrderTotal)
                .sum();

        // Nhóm theo phương thức thanh toán
        Map<String, List<HoaDonEntity>> groupedByMethod = completedOrders.stream()
                .collect(Collectors.groupingBy(HoaDonEntity::getPhuongThucThanhToan));

        // Chuyển đổi thành dữ liệu báo cáo
        List<Map<String, Object>> result = new ArrayList<>();

        for (Map.Entry<String, List<HoaDonEntity>> entry : groupedByMethod.entrySet()) {
            Map<String, Object> data = new HashMap<>();
            data.put("method", entry.getKey());
            data.put("orders", entry.getValue().size());

            long revenue = entry.getValue().stream()
                    .mapToLong(this::calculateOrderTotal)
                    .sum();

            data.put("revenue", revenue);

            // Tính phần trăm
            double percentage = (totalRevenue > 0)
                    ? Math.round((double) revenue / totalRevenue * 100 * 100.0) / 100.0
                    : 0.0;

            data.put("percentage", percentage);
            result.add(data);
        }

        // Sắp xếp theo doanh thu giảm dần
        result.sort((a, b) -> Long.compare(
                (Long) b.get("revenue"),
                (Long) a.get("revenue")
        ));

        return result;
    }

    /**
     * Lấy báo cáo so sánh theo kỳ
     */
    @Override
    public Map<String, List<Map<String, Object>>> getComparisonReportByPeriod(String period) {
        Calendar calendar = Calendar.getInstance();
        Date now = new Date();
        calendar.setTime(now);

        Date currentPeriodStart, currentPeriodEnd, previousPeriodStart, previousPeriodEnd;

        // Thiết lập khoảng thời gian dựa trên period
        switch (period) {
            case "week":
                // Tuần này
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                currentPeriodStart = calendar.getTime();
                calendar.add(Calendar.DAY_OF_WEEK, 6);
                currentPeriodEnd = calendar.getTime();

                // Tuần trước
                calendar.setTime(currentPeriodStart);
                calendar.add(Calendar.DAY_OF_YEAR, -7);
                previousPeriodStart = calendar.getTime();
                calendar.add(Calendar.DAY_OF_WEEK, 6);
                previousPeriodEnd = calendar.getTime();
                break;

            case "month":
                // Tháng này
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                currentPeriodStart = calendar.getTime();
                calendar.add(Calendar.MONTH, 1);
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                currentPeriodEnd = calendar.getTime();

                // Tháng trước
                calendar.setTime(currentPeriodStart);
                calendar.add(Calendar.MONTH, -1);
                previousPeriodStart = calendar.getTime();
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.add(Calendar.MONTH, 1);
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                previousPeriodEnd = calendar.getTime();
                break;

            case "quarter":
                // Quý này
                int currentQuarter = (calendar.get(Calendar.MONTH) / 3) + 1;
                calendar.set(Calendar.MONTH, (currentQuarter - 1) * 3);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                currentPeriodStart = calendar.getTime();
                calendar.add(Calendar.MONTH, 3);
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                currentPeriodEnd = calendar.getTime();

                // Quý trước
                calendar.setTime(currentPeriodStart);
                calendar.add(Calendar.MONTH, -3);
                previousPeriodStart = calendar.getTime();
                calendar.add(Calendar.MONTH, 3);
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                previousPeriodEnd = calendar.getTime();
                break;

            case "year":
                // Năm này
                calendar.set(Calendar.DAY_OF_YEAR, 1);
                currentPeriodStart = calendar.getTime();
                calendar.add(Calendar.YEAR, 1);
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                currentPeriodEnd = calendar.getTime();

                // Năm trước
                calendar.setTime(currentPeriodStart);
                calendar.add(Calendar.YEAR, -1);
                previousPeriodStart = calendar.getTime();
                calendar.add(Calendar.YEAR, 1);
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                previousPeriodEnd = calendar.getTime();
                break;

            default:
                throw new IllegalArgumentException("Khoảng thời gian không hợp lệ: " + period);
        }

        // Lấy dữ liệu cho kỳ hiện tại
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        List<Map<String, Object>> currentPeriodData = getSalesReportByTimeRange(
                dateFormat.format(currentPeriodStart),
                dateFormat.format(currentPeriodEnd),
                "daily"
        );

        // Lấy dữ liệu cho kỳ trước
        List<Map<String, Object>> previousPeriodData = getSalesReportByTimeRange(
                dateFormat.format(previousPeriodStart),
                dateFormat.format(previousPeriodEnd),
                "daily"
        );

        // Trả về kết quả
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        result.put("current", currentPeriodData);
        result.put("previous", previousPeriodData);

        return result;
    }

    /**
     * Phương thức hỗ trợ tính tổng giá trị đơn hàng
     */
    private long calculateOrderTotal(HoaDonEntity hoaDon) {
        long total = hoaDon.getChiTietHoaDons().stream()
                .mapToLong(detail -> {
                    // Tìm giá bán của món theo size
                    Long price = monRepository.findGiaBanByMonAndSize(
                            detail.getMon().getIdMon(),
                            detail.getSize().getIdSize()
                    );
                    return price * detail.getSoLuong();
                })
                .sum();

        // Trừ giảm giá nếu có
        total -= (hoaDon.getGiamGia() != null ? hoaDon.getGiamGia() : 0);

        // Cộng phí ship nếu có
        total += (hoaDon.getPhiShip() != null ? hoaDon.getPhiShip() : 0);

        return total;
    }

    /**
     * Hàm hỗ trợ chuyển đổi từ String sang Date
     */
    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }

        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            return format.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


}
