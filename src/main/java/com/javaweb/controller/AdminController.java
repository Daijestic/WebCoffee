package com.javaweb.controller;

import com.javaweb.converter.entity_to_dto.UserEntityToDTO;
import com.javaweb.custom.CustomUserDetails;
import com.javaweb.dto.reponse.*;
import com.javaweb.dto.request.*;
import com.javaweb.entity.MonEntity;
import com.javaweb.repository.MonRepository;
import com.javaweb.service.*;
import com.javaweb.service.impl.PowerBiServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private ProductService productService;

    @Autowired
    private UserEntityToDTO userEntityToDTO;

    @Autowired
    private UsersService usersService;

    @Autowired
    private MonRepository monRepository;

    @Autowired
    private NguyenLieuService nguyenLieuService;

    @Autowired
    private PhieuNhapKhoService phieuNhapKhoService;

    @Autowired
    private PhieuXuatKhoService phieuXuatKhoService;

    @Autowired
    private CaLamViecService caLamViecService;

    @Autowired
    private LichLamService lichLamViecService;

    @Autowired
    private HoaDonService hoaDonService;

    @Autowired
    private NhaCungCapService nhaCungCapService;

    @GetMapping
    public ModelAndView index() {
        return new ModelAndView ("redirect:/admin/");
    }

    @GetMapping("/")
    public ModelAndView showAdminIndex(@AuthenticationPrincipal CustomUserDetails userDetails) {
        ModelAndView modelAndView = new ModelAndView("admin/index");
        UserResponse userResponse = usersService.findByUsername(userDetails.getUsername());
        modelAndView.addObject("user", userResponse);
        return modelAndView;
    }

    @GetMapping("/products")
    public ModelAndView findAllProducts(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {
        ModelAndView modelAndView = new ModelAndView("admin/productview");
        Page<ProductResponse> productResponsePage = productService.findAll(pageNo);
        Set<String> categories = productResponsePage.stream()
                .map(ProductResponse::getLoaiMon)
                .collect(Collectors.toSet());
        Page<ProductResponse> productResponsePage1 = productService.findAllByLoaiMon("Bánh Ngọt", pageNo);
        modelAndView.addObject("products", productResponsePage);
        modelAndView.addObject("DoAn", productResponsePage1.getTotalElements());
        modelAndView.addObject("totalPages", productResponsePage.getTotalPages());
        modelAndView.addObject("currentPage", pageNo);
        modelAndView.addObject("categories", categories);
        return modelAndView;
    }

    @GetMapping("/users")
    public ModelAndView all(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {
        ModelAndView modelAndView = new ModelAndView("admin/users");
        Page<UserResponse> userResponsePage = usersService.findAll(pageNo);
        modelAndView.addObject("users", userResponsePage);
        modelAndView.addObject("totalPages", userResponsePage.getTotalPages());
        modelAndView.addObject("currentPage", pageNo);
        return modelAndView;
    }

    @GetMapping("/employee")
    public ModelAndView allEmployee(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {
        ModelAndView modelAndView = new ModelAndView("admin/employee");
        Page<UserResponse> userResponsePage = usersService.findAllByRole(pageNo, "STAFF");
        modelAndView.addObject("users", userResponsePage);
        modelAndView.addObject("totalPages", userResponsePage.getTotalPages());
        modelAndView.addObject("currentPage", pageNo);
        return modelAndView;
    }

    @GetMapping("/employee/all")
    @ResponseBody
    public List<UserResponse> allEmployee() {
        return usersService.findAllByRole("STAFF");
    }

    @GetMapping("/client")
    public ModelAndView allClient(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {
        ModelAndView modelAndView = new ModelAndView("admin/client");
        Page<UserResponse> userResponsePage = usersService.findAllByRole(pageNo, "USER");
        modelAndView.addObject("users", userResponsePage);
        modelAndView.addObject("totalPages", userResponsePage.getTotalPages());
        modelAndView.addObject("currentPage", pageNo);
        return modelAndView;
    }


    @PostMapping("/users/add")
    @ResponseBody
    public ResponseEntity<?> addOrUpdateUser(@RequestBody UserRequest userRequest) {
        try {
            UserResponse userResponse = new UserResponse();
            Map<String, Object> response = new HashMap<>();
            if (userRequest.getId() == null) {
                userResponse = usersService.save(userRequest);
                response.put("message", "Thêm người dùng thành công");
            } else {
                userResponse = usersService.update(userRequest);
                response.put("message", "Cập nhật người dùng thành công");
            }

            // Trả về dữ liệu JSON với thông tin chi tiết hơn
            response.put("success", true);
            response.put("userId", userResponse.getId());  // Nếu có

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            // Trả về lỗi
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }


    @GetMapping("/users/{userId}")
    @ResponseBody
    public UserResponse userDetail(@PathVariable Long userId) {
        return usersService.findById(userId);
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<?> userDelete(@PathVariable Long userId) {
        try {
            usersService.deleteKhachHangById(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Xóa người dùng thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }


    @GetMapping("/datmon")
    public String hienThiSanPhamTheoLoai(
            @RequestParam(name = "loai", defaultValue = "") String loai,
            Model model) {
        Map<String, List<MonEntity>> categorizedMenu = new LinkedHashMap<>();
        System.out.println("Loại món được yêu cầu: " + loai);
        if (loai.isEmpty()) {
            // Nếu không truyền 'loai' thì lấy tất cả món
            categorizedMenu.put("CÀ PHÊ PHIN", monRepository.findMonByLoaiMonId("CÀ PHÊ PHIN"));
        } else {
            switch (loai) {
                case "CÀ PHÊ PHIN":
                    categorizedMenu.put("CÀ PHÊ PHIN", monRepository.findMonByLoaiMonId("CÀ PHÊ PHIN"));
                    break;
                case "PHINDI":
                    categorizedMenu.put("PHINDI", monRepository.findMonByLoaiMonId("PHINDI"));
                    break;
                case "TRÀ":
                    categorizedMenu.put("TRÀ", monRepository.findMonByLoaiMonId("TRÀ"));
                    break;
                case "FREEZE":
                    categorizedMenu.put("FREEZE", monRepository.findMonByLoaiMonId("FREEZE"));
                    break;
                case "BÁNH MỲ QUE":
                    categorizedMenu.put("BÁNH MỲ QUE", monRepository.findMonByLoaiMonId("BÁNH MỲ QUE"));
                    break;
                default:
                    categorizedMenu.put("BÁNH", monRepository.findMonByLoaiMonId("BÁNH"));
            }
        }

        model.addAttribute("menuMap", categorizedMenu);
        return "admin/datmon";
    }

    @GetMapping("/quanlikho")
    public ModelAndView quanLyKho(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {
        ModelAndView modelAndView = new ModelAndView("admin/quanlikho");
        Page<NguyenLieuResponse> nguyenLieuResponsePage = nguyenLieuService.findAll(pageNo);
        Page<NguyenLieuResponse> nguyenLieuResponsePage1 = nguyenLieuService.findBySoLuongLessThanEqual(0L, pageNo);
        Page<NguyenLieuResponse> nguyenLieuResponsePage2 = nguyenLieuService.findBySoLuongLessThanEqual(10L, pageNo);
        Page<NguyenLieuResponse> nguyenLieuResponsePage3 = nguyenLieuService.findBySoLuongLessThanEqual(Long.MAX_VALUE, pageNo);
        modelAndView.addObject("DaHet", nguyenLieuResponsePage1.getTotalElements());
        modelAndView.addObject("SapHet", nguyenLieuResponsePage2.getTotalElements() - nguyenLieuResponsePage1.getTotalElements());
        modelAndView.addObject("Du", nguyenLieuResponsePage3.getTotalElements() - nguyenLieuResponsePage2.getTotalElements() - nguyenLieuResponsePage1.getTotalElements());
        modelAndView.addObject("nguyenLieus", nguyenLieuResponsePage);
        modelAndView.addObject("totalElements", nguyenLieuResponsePage.getTotalElements());
        modelAndView.addObject("totalPages", nguyenLieuResponsePage.getTotalPages());
        modelAndView.addObject("currentPage", pageNo);
        return modelAndView;
    }

    @GetMapping("/quanlikho/{id}")
    @ResponseBody
    public NguyenLieuResponse quanLyKhoDetail(@PathVariable Long id) {
        return nguyenLieuService.findById(id);
    }

    @GetMapping("/quanlikho/history/{nguyenLieuId}")
    @ResponseBody
    public List<LichSuNhapXuatNguyenLieuResponse> lichSuNhapXuatNguyenLieu(@PathVariable Long nguyenLieuId) {
        return nguyenLieuService.getLichSuNhapXuatNguyenLieu(nguyenLieuId);
    }

    @PostMapping("/quanlikho/add")
    public ResponseEntity<?> addOrUpdateNguyenLieu(
            @RequestBody NguyenLieuRequest nguyenLieuRequest,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            UserResponse userResponse = usersService.findByUsername(userDetails.getUsername());
            NguyenLieuResponse savedNguyenLieu = nguyenLieuService.save(nguyenLieuRequest);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Thêm nguyên liệu thành công");
            response.put("nguyenLieuId", savedNguyenLieu.getIdNguyenLieu());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @DeleteMapping("/nguyenlieu/{id}")
    public ResponseEntity<?> deleteNguyenLieu(@PathVariable Long id) {
        try {
            nguyenLieuService.deleteById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Xóa nguyên liệu thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }


    @GetMapping("/nhapkho")
    public ModelAndView nhapKho(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {
        ModelAndView modelAndView = new ModelAndView("admin/nhapkho");
        Page<PhieuNhapKhoResponse> phieuNhapKhoResponses = phieuNhapKhoService.findAll(pageNo);
        modelAndView.addObject("phieuNhapKhos", phieuNhapKhoResponses);
        modelAndView.addObject("totalPages", phieuNhapKhoResponses.getTotalPages());
        modelAndView.addObject("totalElements", phieuNhapKhoResponses.getTotalElements());
        modelAndView.addObject("currentPage", pageNo);
        return modelAndView;
    }

    @GetMapping("/nhapkho/{id}")
    @ResponseBody
    public PhieuNhapKhoResponse phieuNhapKhoDetail(@PathVariable Long id) {
        return phieuNhapKhoService.findByIdPhieuNhapKho(id);
    }

    @PostMapping("/nhapkho/update")
    public ResponseEntity<?> updatePhieuNhapKho(@RequestBody PhieuNhapKhoRequest phieuNhapKhoRequest) {
        try {
            PhieuNhapKhoResponse updatedPhieuNhapKho = phieuNhapKhoService.savePhieuNhapKho(phieuNhapKhoRequest);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cập nhật phiếu nhập kho thành công",
                    "phieuNhapKho", updatedPhieuNhapKho
            ));
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/nhapkho/add")
    public ResponseEntity<?> addPhieuNhapKho(@RequestBody PhieuNhapKhoRequest phieuNhapKhoRequest) {
        try {
            PhieuNhapKhoResponse savedPhieuNhapKho = phieuNhapKhoService.savePhieuNhapKho(phieuNhapKhoRequest);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Thêm phiếu nhập kho thành công");
            response.put("savedPhieuNhapKho", savedPhieuNhapKho);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @DeleteMapping("/nhapkho/{id}")
    public ResponseEntity<?> deletePhieuNhapKho(@PathVariable Long id) {
        try {
            phieuNhapKhoService.deletePhieuNhapKho(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Xóa phiếu nhập kho thành công");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }



    @GetMapping("/xuatkho")
    public ModelAndView xuatKho(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {
        ModelAndView modelAndView = new ModelAndView("admin/xuatkho");
        Page<PhieuXuatKhoResponse> phieuXuatKhoResponses = phieuXuatKhoService.findAll(pageNo);
        modelAndView.addObject("phieuXuatKhos", phieuXuatKhoResponses);
        modelAndView.addObject("totalPages", phieuXuatKhoResponses.getTotalPages());
        modelAndView.addObject("totalElements", phieuXuatKhoResponses.getTotalElements());
        modelAndView.addObject("currentPage", pageNo);
        return modelAndView;
    }


    @GetMapping("/xuatkho/{id}")
    @ResponseBody
    public PhieuXuatKhoResponse phieuXuatKhoDetail(@PathVariable Long id) {
        return phieuXuatKhoService.findById(id);
    }

    @PostMapping("/xuatkho/add")
    public ResponseEntity<?> addPhieuXuatKho(@RequestBody PhieuXuatKhoRequest phieuXuatKhoRequest) {
        try {
            PhieuXuatKhoResponse savedPhieuXuatKho = phieuXuatKhoService.savePhieuXuatKho(phieuXuatKhoRequest);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Thêm phiếu xuất kho thành công");
            response.put("phieuXuatKhoId", savedPhieuXuatKho.getIdPhieuXuatKho());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PutMapping("/xuatkho/update")
    public ResponseEntity<?> updatePhieuXuatKho(@RequestBody PhieuXuatKhoRequest phieuXuatKhoRequest) {
        try {
            PhieuXuatKhoResponse updatedPhieuXuatKho = phieuXuatKhoService.savePhieuXuatKho(phieuXuatKhoRequest);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cập nhật phiếu xuất kho thành công"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/calamviec")
    public ModelAndView caLamViec(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {
        ModelAndView modelAndView = new ModelAndView("admin/calamviec");
        Page<CaLamVienResponse> caLamViecResponses = caLamViecService.findAll(pageNo);
        modelAndView.addObject("caLamViecs", caLamViecResponses);
        modelAndView.addObject("totalPages", caLamViecResponses.getTotalPages());
        modelAndView.addObject("totalElements", caLamViecResponses.getTotalElements());
        modelAndView.addObject("currentPage", pageNo);
        return modelAndView;
    }

    @GetMapping("/calamviec/{caId}")
    @ResponseBody
    public CaLamVienResponse caLamViecDetail(@PathVariable Long caId) {
        return caLamViecService.findById(caId);
    }

    @PostMapping("/calamviec/add")
    public ResponseEntity<?> addCaLamViec(@RequestBody CaLamViecRequest caLamViecRequest) {
        try {
            CaLamVienResponse savedCaLamViec = caLamViecService.save(caLamViecRequest);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Thêm ca làm việc thành công");
            response.put("caLamViecId", savedCaLamViec.getIdCa());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("message", "Lỗi: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/calamviec/update")
    public ResponseEntity<?> updateCaLamViec(@RequestBody CaLamViecRequest caLamViecRequest) {
        try {
            CaLamVienResponse updatedCaLamViec = caLamViecService.save(caLamViecRequest);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cập nhật ca làm việc thành công"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/calamviec/all")
    @ResponseBody
    public List<CaLamVienResponse> getAllCaLamViec() {
        return caLamViecService.findAll();
    }

    @GetMapping("/lichlamviec")
    public ModelAndView lichLamViec(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {
        ModelAndView modelAndView = new ModelAndView("admin/lichlamviec");
        Page<LichLamResponse> lichLamResponses = lichLamViecService.findAll(pageNo);
        modelAndView.addObject("lichLams", lichLamResponses);
        modelAndView.addObject("totalPages", lichLamResponses.getTotalPages());
        modelAndView.addObject("totalElements", lichLamResponses.getTotalElements());
        modelAndView.addObject("currentPage", pageNo);
        return modelAndView;
    }

    @GetMapping("/lichlamviec/{scheduleId}")
    @ResponseBody
    public LichLamResponse lichLamViecDetail(@PathVariable Long scheduleId) {
        return lichLamViecService.findById(scheduleId);
    }

    @GetMapping("/donhang")
    public ModelAndView donHang(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {
        ModelAndView modelAndView = new ModelAndView("admin/donhang");
        Page<HoaDonResponse> hoaDonResponses = hoaDonService.getAllInvoice(pageNo);
        Page<HoaDonResponse> hoaDonResponsesByStatus1 = hoaDonService.findByTrangThai("CHỜ XÁC NHẬN", pageNo);
        Page<HoaDonResponse> hoaDonResponsesByStatus2 = hoaDonService.findByTrangThai("ĐÃ XÁC NHẬN", pageNo);
        Page<HoaDonResponse> hoaDonResponsesByStatus3 = hoaDonService.findByTrangThai("ĐANG GIAO", pageNo);
        Page<HoaDonResponse> hoaDonResponsesByStatus4 = hoaDonService.findByTrangThai("HOÀN THÀNH", pageNo);
        Page<HoaDonResponse> hoaDonResponsesByStatus5 = hoaDonService.findByTrangThai("ĐÃ HUỶ", pageNo);
        modelAndView.addObject("hoaDons", hoaDonResponses);
        modelAndView.addObject("totalPages", hoaDonResponses.getTotalPages());
        modelAndView.addObject("totalElements", hoaDonResponses.getTotalElements());
        modelAndView.addObject("hoaDonResponsesByStatus1", hoaDonResponsesByStatus1.getTotalElements()
        + hoaDonResponsesByStatus2.getTotalElements() + hoaDonResponsesByStatus3.getTotalElements());
        modelAndView.addObject("hoaDonResponsesByStatus3", hoaDonResponsesByStatus4.getTotalElements());
        modelAndView.addObject("hoaDonResponsesByStatus4", hoaDonResponsesByStatus5.getTotalElements());
        modelAndView.addObject("currentPage", pageNo);
        return modelAndView;
    }

    @PostMapping("/donhang/update-status")
    @ResponseBody
    public ResponseEntity<?> updateStatus(@RequestBody HoaDonRequest requestBody) {
        try {
            hoaDonService.updateStatus(requestBody);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Cập nhật trạng thái đơn hàng thành công"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", e.getMessage()
            ));
        }
    }

    @GetMapping("/donhang/{orderId}")
    @ResponseBody
    public HoaDonResponse donHangDetail(@PathVariable Long orderId) {
        return hoaDonService.getInvoiceById(orderId);
    }

    @GetMapping("/nhacungcap")
    @ResponseBody
    public List<NhaCungCapResponse> getNhaCungCap() {
        return nhaCungCapService.findAll();
    }

    @GetMapping("/nguyenlieu/all")
    @ResponseBody
    public List<NguyenLieuResponse> getAllNguyenLieu() {
        return nguyenLieuService.findAll();
    }
}
