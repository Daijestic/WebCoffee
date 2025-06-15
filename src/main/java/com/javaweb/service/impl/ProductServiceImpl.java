package com.javaweb.service.impl;

import com.javaweb.converter.dto_to_entity.ProductDtoToEntity;
import com.javaweb.converter.entity_to_dto.ProductEntiryToDto;
import com.javaweb.dto.reponse.ProductResponse;
import com.javaweb.dto.request.InvoiceRequest;
import com.javaweb.dto.request.ProductRequest;
import com.javaweb.entity.ChiTietHoaDonEntity;
import com.javaweb.entity.HoaDonEntity;
import com.javaweb.entity.MonEntity;
import com.javaweb.model.FileUploads;
import com.javaweb.repository.HoaDonRepository;
import com.javaweb.repository.MonRepository;
import com.javaweb.service.ProductService;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    ProductDtoToEntity productDtoToEntity;

    @Autowired
    MonRepository monRepository;

    @Autowired
    ProductEntiryToDto productEntiryToDto;

    @Autowired
    HoaDonRepository hoaDonRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    FileUploads fileUploads;

    @Override
    public List<ProductResponse> getAllProducts() {
       List<ProductResponse> productRepons = new ArrayList<>();
       List<MonEntity> monEntities = monRepository.findAll();
       for (MonEntity monEntity : monEntities) {
           productRepons.add(productEntiryToDto.toProductReponse(monEntity));
       }
       return productRepons;
    }

    @Override
    public ProductResponse save(ProductRequest productRequest, MultipartFile multipartFile) throws IOException {
        return productEntiryToDto.toProductReponse(monRepository.save(productDtoToEntity.toMonEntity(productRequest, multipartFile)));
    }

    @Override
    public ProductResponse update(ProductRequest productRequest, MultipartFile multipartFile) throws IOException {
        return productEntiryToDto.toProductReponse(monRepository.save(productDtoToEntity.toMonEntity(productRequest, multipartFile)));
    }

    @Override
    public void delete(Long id) {
        monRepository.deleteById(id);
    }

    @Override
    public Page<ProductResponse> findAll(Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 9);
        return monRepository.findAll(pageable)
                .map(monEntity -> {
                    return productEntiryToDto.toProductReponse(monEntity);
                });
    }

    @Override
    public List<ProductResponse> findAllByLoaiMon(String loaiMon) {
        return monRepository.findAllByLoaiMon(loaiMon)
                .stream().map(monEntity -> {
                    return productEntiryToDto.toProductReponse(monEntity);
                }).toList();
    }

    @Override
    public ProductResponse getProductById(Long productId) {
        return productEntiryToDto.toProductReponse(monRepository.findById(productId).get());
    }

    @Override
    public Page<ProductResponse> findAllByLoaiMon(String name, Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 9);
        return monRepository.findAllByLoaiMon(name, pageable)
                .map(monEntity -> {;
                    return productEntiryToDto.toProductReponse(monEntity);
                });
    }

    @Override
    public ProductResponse findById(Long id) {
        return productEntiryToDto.toProductReponse(monRepository.findById(id).get());
    }
    @Override
    public ProductResponse findByTenMon(String tenMon) {
        MonEntity mon = monRepository.findByTenMon(tenMon)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món: " + tenMon));
        return productEntiryToDto.toProductReponse(mon);
    }

    @Override
    public ProductResponse muaHang(InvoiceRequest invoiceRequest) {

        return null;
    }

    /**
     * Lấy báo cáo doanh thu theo danh mục
     */
    @Override
    public List<Map<String, Object>> getCategorySalesReport(String startDate, String endDate) {
        Date start = parseDate(startDate);
        Date end = parseDate(endDate);

        // Lấy tất cả hóa đơn hoàn thành trong khoảng thời gian
        List<HoaDonEntity> completedOrders = hoaDonRepository.findByNgayGioLapHoaDonBetweenAndTrangThai(
                start, end, "HOÀN THÀNH");

        // Tổng hợp doanh thu theo loại món
        Map<String, Long> categoryQuantity = new HashMap<>();
        Map<String, Long> categoryRevenue = new HashMap<>();
        long totalRevenue = 0;

        for (HoaDonEntity hoaDon : completedOrders) {
            for (ChiTietHoaDonEntity detail : hoaDon.getChiTietHoaDons()) {
                MonEntity mon = detail.getMon();
                String category = mon.getLoaiMon();

                // Tính số lượng
                categoryQuantity.put(category,
                        categoryQuantity.getOrDefault(category, 0L) + detail.getSoLuong());

                // Tìm giá bán của món theo size
                Long price = monRepository.findGiaBanByMonAndSize(
                        mon.getIdMon(),
                        detail.getSize().getIdSize()
                );

                // Tính doanh thu
                long revenue = price * detail.getSoLuong();
                categoryRevenue.put(category,
                        categoryRevenue.getOrDefault(category, 0L) + revenue);

                totalRevenue += revenue;
            }
        }

        // Chuyển đổi thành dữ liệu báo cáo
        List<Map<String, Object>> result = new ArrayList<>();

        for (String category : categoryRevenue.keySet()) {
            Map<String, Object> data = new HashMap<>();
            data.put("category", category);
            data.put("quantity", categoryQuantity.get(category));
            data.put("revenue", categoryRevenue.get(category));

            // Tính phần trăm
            double percentage = (totalRevenue > 0)
                    ? Math.round((double) categoryRevenue.get(category) / totalRevenue * 100 * 100.0) / 100.0
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
     * Lấy danh sách sản phẩm bán chạy nhất
     */
    @Override
    public List<Map<String, Object>> getTopSellingProducts(String startDate, String endDate, Integer limit) {
        Date start = parseDate(startDate);
        Date end = parseDate(endDate);

        // Lấy tất cả hóa đơn hoàn thành trong khoảng thời gian
        List<HoaDonEntity> completedOrders = hoaDonRepository.findByNgayGioLapHoaDonBetweenAndTrangThai(
                start, end, "HOÀN THÀNH");

        // Tổng hợp dữ liệu theo sản phẩm
        Map<Long, Map<String, Object>> productMap = new HashMap<>();
        long totalRevenue = 0;

        for (HoaDonEntity hoaDon : completedOrders) {
            for (ChiTietHoaDonEntity detail : hoaDon.getChiTietHoaDons()) {
                MonEntity mon = detail.getMon();
                Long monId = mon.getIdMon();

                // Tìm giá bán của món theo size
                Long price = monRepository.findGiaBanByMonAndSize(
                        monId,
                        detail.getSize().getIdSize()
                );

                // Tính doanh thu
                long revenue = price * detail.getSoLuong();
                totalRevenue += revenue;

                // Thêm hoặc cập nhật thông tin sản phẩm
                if (!productMap.containsKey(monId)) {
                    Map<String, Object> product = new HashMap<>();
                    product.put("id", monId);
                    product.put("name", mon.getTenMon());
                    product.put("category", mon.getLoaiMon());
                    product.put("quantity", detail.getSoLuong());
                    product.put("revenue", revenue);
                    productMap.put(monId, product);
                } else {
                    Map<String, Object> product = productMap.get(monId);
                    product.put("quantity", (Long) product.get("quantity") + detail.getSoLuong());
                    product.put("revenue", (Long) product.get("revenue") + revenue);
                }
            }
        }

        // Chuyển đổi thành dữ liệu báo cáo và tính phần trăm
        List<Map<String, Object>> result = new ArrayList<>(productMap.values());

        final long total = totalRevenue; // ✅ copy giá trị sang final
        result.forEach(product -> {
            double percentage = (total > 0)
                    ? Math.round((double) (Long) product.get("revenue") / total * 100 * 100.0) / 100.0
                    : 0.0;
            product.put("percentage", percentage);
        });

        // Sắp xếp theo doanh thu giảm dần
        result.sort((a, b) -> Long.compare(
                (Long) b.get("revenue"),
                (Long) a.get("revenue")
        ));

        // Giới hạn số lượng sản phẩm trả về
        return result.stream()
                .limit(limit)
                .collect(Collectors.toList());
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
