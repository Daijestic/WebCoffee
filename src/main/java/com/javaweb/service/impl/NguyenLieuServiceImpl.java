package com.javaweb.service.impl;

import com.javaweb.converter.dto_to_entity.NguyenLieuDtoToEntity;
import com.javaweb.converter.entity_to_dto.NguyenLieuEntityToDto;
import com.javaweb.converter.entity_to_dto.NhapXuatEntityToDto;
import com.javaweb.dto.reponse.LichSuNhapXuatNguyenLieuResponse;
import com.javaweb.dto.reponse.NguyenLieuResponse;
import com.javaweb.dto.request.NguyenLieuRequest;
import com.javaweb.entity.*;
import com.javaweb.repository.NguyenLieuRepository;
import com.javaweb.repository.PhieuNhapKhoRepository;
import com.javaweb.repository.PhieuXuatKhoRepository;
import com.javaweb.service.NguyenLieuService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class NguyenLieuServiceImpl implements NguyenLieuService {

    @Autowired
    private NguyenLieuRepository nguyenLieuRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private NhapXuatEntityToDto nhapXuatEntityToDto;

    @Autowired
    private NguyenLieuDtoToEntity nguyenLieuDtoToEntity;

    @Autowired
    private NguyenLieuEntityToDto nguyenLieuEntityToDto;

    @Autowired
    private PhieuNhapKhoRepository phieuNhapKhoRepository;

    @Autowired
    private PhieuXuatKhoRepository phieuXuatKhoRepository;

    @Override
    public NguyenLieuEntity findByTenNguyenLieu(String tenNguyenLieu) {
        return null;
    }

    @Override
    public List<NguyenLieuResponse> findAll() {
        return nguyenLieuRepository.findAll()
                .stream()
                .map(nguyenLieuEntity -> {
                    NguyenLieuResponse nguyenLieuResponse = modelMapper.map(nguyenLieuEntity, NguyenLieuResponse.class);
                    return nguyenLieuResponse;
                }).toList();
    }

    @Override
    public NguyenLieuResponse findById(Long idNguyenLieu) {
        return nguyenLieuRepository.findById(idNguyenLieu)
                .map(nguyenLieuEntity -> {
                    return nguyenLieuEntityToDto.convertToDto(nguyenLieuEntity);
                }).get();
    }

    @Override
    public NguyenLieuResponse save(NguyenLieuRequest nguyenLieuRequest) {
        return nguyenLieuEntityToDto.convertToDto(nguyenLieuRepository.save(nguyenLieuDtoToEntity.convertToEntity(nguyenLieuRequest)));
    }

    @Override
    public void deleteById(Long idNguyenLieu) {
        nguyenLieuRepository.deleteById(idNguyenLieu);
    }

    @Override
    public List<NguyenLieuEntity> searchByName(String name) {
        return List.of();
    }

    @Override
    public List<NguyenLieuEntity> searchByNameAndPage(String name, int page, int size) {
        return List.of();
    }

    @Override
    public Page<NguyenLieuResponse> findAll(Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 13);
        return this.nguyenLieuRepository.findAll(pageable)
                .map(nguyenLieuEntity -> {
                    return nguyenLieuEntityToDto.convertToDto(nguyenLieuEntity);
                });
    }

    @Override
    public List<LichSuNhapXuatNguyenLieuResponse> getLichSuNhapXuatNguyenLieu(Long idNguyenLieu) {
        return nhapXuatEntityToDto.convertToDto(nguyenLieuRepository.findById(idNguyenLieu).get());
    }

    @Override
    public Page<NguyenLieuResponse> findBySoLuongLessThanEqual(Long soLuong, Integer pageNo) {
        Pageable pageable = PageRequest.of(pageNo - 1, 13);
        return nguyenLieuRepository.findBySoLuongLessThanEqual(soLuong, pageable)
                .map(nguyenLieuEntity -> {;
                    return nguyenLieuEntityToDto.convertToDto(nguyenLieuEntity);
                });
    }

    /**
     * Lấy báo cáo biến động kho
     */
    @Override
    public List<Map<String, Object>> getInventoryMovementReport(String startDate, String endDate) {
        Date start = parseDate(startDate);
        Date end = parseDate(endDate);

        // Nếu không có ngày bắt đầu, lấy đầu tháng
        if (start == null) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            start = calendar.getTime();
        }

        // Nếu không có ngày kết thúc, lấy ngày hiện tại
        if (end == null) {
            end = new Date();
        }

        // Lấy tất cả nguyên liệu
        List<NguyenLieuEntity> nguyenLieus = nguyenLieuRepository.findAll();

        // Lấy phiếu nhập trong khoảng thời gian
        List<PhieuNhapKhoEntity> phieuNhaps = phieuNhapKhoRepository.findByNgayNhapBetween(start, end);

        // Lấy phiếu xuất trong khoảng thời gian
        List<PhieuXuatKhoEntity> phieuXuats = phieuXuatKhoRepository.findByNgayXuatBetween(start, end);

        // Chuẩn bị kết quả
        List<Map<String, Object>> result = new ArrayList<>();

        // Tính tồn đầu kỳ trước khoảng thời gian
        for (NguyenLieuEntity nguyenLieu : nguyenLieus) {
            Long idNguyenLieu = nguyenLieu.getIdNguyenLieu();

            // Tính tổng nhập trước startDate
            long totalImportBeforePeriod = phieuNhapKhoRepository.sumNhapTruocNgay(idNguyenLieu, start);

            // Tính tổng xuất trước startDate
            long totalExportBeforePeriod = phieuXuatKhoRepository.sumXuatTruocNgay(idNguyenLieu, start);

            // Tồn đầu kỳ = tổng nhập - tổng xuất trước khoảng thời gian
            long initialStock = totalImportBeforePeriod - totalExportBeforePeriod;
            if (initialStock < 0) initialStock = 0; // Đảm bảo không có số âm

            // Tính tổng nhập trong khoảng thời gian
            long importAmount = 0;
            for (PhieuNhapKhoEntity phieuNhap : phieuNhaps) {
                for (ChiTietNhapKhoEntity chiTiet : phieuNhap.getChiTietNhapKhoList()) {
                    if (chiTiet.getIdNguyenLieu().getIdNguyenLieu().equals(idNguyenLieu)) {
                        importAmount += chiTiet.getSoLuong();
                    }
                }
            }

            // Tính tổng xuất trong khoảng thời gian
            long exportAmount = 0;
            for (PhieuXuatKhoEntity phieuXuat : phieuXuats) {
                for (ChiTietXuatKhoEntity chiTiet : phieuXuat.getChiTietXuatKhoList()) {
                    if (chiTiet.getNguyenLieu().getIdNguyenLieu().equals(idNguyenLieu)) {
                        exportAmount += chiTiet.getSoLuong();
                    }
                }
            }

            // Tồn cuối kỳ = tồn đầu kỳ + nhập - xuất
            long finalStock = initialStock + importAmount - exportAmount;
            if (finalStock < 0) finalStock = 0; // Đảm bảo không có số âm

            // Thêm vào kết quả
            Map<String, Object> data = new HashMap<>();
            data.put("id", idNguyenLieu);
            data.put("name", nguyenLieu.getTenNguyenLieu());
            data.put("initialStock", initialStock);
            data.put("import", importAmount);
            data.put("export", exportAmount);
            data.put("finalStock", finalStock);
            data.put("unit", nguyenLieu.getDonViTinh());

            result.add(data);
        }

        return result;
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
