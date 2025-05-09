package com.javaweb.controller;

import com.javaweb.converter.entity_to_dto.UserEntityToDTO;
import com.javaweb.custom.CustomUserDetails;
import com.javaweb.dto.reponse.*;
import com.javaweb.dto.request.NguyenLieuRequest;
import com.javaweb.dto.request.PhieuNhapKhoRequest;
import com.javaweb.dto.request.UserRequest;
import com.javaweb.entity.MonEntity;
import com.javaweb.repository.MonRepository;
import com.javaweb.service.*;
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
        modelAndView.addObject("products", productResponsePage);
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
    public ResponseEntity<?> addOrUpdatePhieuNhapKho(
            @ModelAttribute PhieuNhapKhoRequest phieuNhapKhoRequest) {
        try {
            PhieuNhapKhoResponse savedPhieuNhapKho = phieuNhapKhoService.savePhieuNhapKho(phieuNhapKhoRequest);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cập nhật phiếu nhập kho thành công");
            response.put("phieuNhapKhoId", savedPhieuNhapKho.getIdPhieuNhapKho());
            return ResponseEntity.status(HttpStatus.OK).body(response);
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

    @GetMapping("/donhang")
    public ModelAndView donHang(@RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo) {
        ModelAndView modelAndView = new ModelAndView("admin/donhang");
        Page<HoaDonResponse> hoaDonResponses = hoaDonService.getAllInvoice(pageNo);
        modelAndView.addObject("hoaDons", hoaDonResponses);
        modelAndView.addObject("totalPages", hoaDonResponses.getTotalPages());
        modelAndView.addObject("totalElements", hoaDonResponses.getTotalElements());
        modelAndView.addObject("currentPage", pageNo);
        return modelAndView;
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
