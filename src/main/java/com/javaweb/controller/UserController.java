package com.javaweb.controller;

import com.javaweb.dto.request.PasswordChangeRequest;
import com.javaweb.dto.request.UserRequest;
import com.javaweb.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UserController {
//    @Autowired
//    UsersService usersService;
//
//    @GetMapping("/user/my-info")
//    public ModelAndView myInfo() {
//        ModelAndView mav = new ModelAndView("/web/user");
//        mav.addObject("user", usersService.getMyInfo());
//        return new ModelAndView("/index");
//    }
//
//    @GetMapping("/dangky")
//    public ModelAndView dangky() {
//        ModelAndView mav = new ModelAndView("/webbuy/dangky");
//        return mav;
//    }
//
//    /**
//     * Cập nhật thông tin hồ sơ người dùng
//     */
//    @PostMapping("/cap-nhat-ho-so")
//    public String updateProfile(
//            @ModelAttribute UserRequest userRequest,
//            RedirectAttributes redirectAttributes) {
//
//        try {
//            userService.updateUser(userRequest);
//            String successMessage = "Cập nhật thông tin thành công";
//            redirectAttributes.addAttribute("success", URLEncoder.encode(successMessage, StandardCharsets.UTF_8.toString()));
//        } catch (Exception e) {
//            String errorMessage = "Có lỗi xảy ra: " + e.getMessage();
//            redirectAttributes.addAttribute("error", URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.toString()));
//        }
//
//        return "redirect:/ho-so";
//    }
//
//    /**
//     * Đổi mật khẩu người dùng
//     */
//    @PostMapping("/doi-mat-khau")
//    public String changePassword(
//            @RequestParam("id") Long id,
//            @RequestParam("username") String username,
//            @RequestParam("currentPassword") String currentPassword,
//            @RequestParam("newPassword") String newPassword,
//            @RequestParam("confirmPassword") String confirmPassword,
//            RedirectAttributes redirectAttributes) {
//
//        try {
//            // Kiểm tra mật khẩu mới và xác nhận mật khẩu có khớp nhau không
//            if (!newPassword.equals(confirmPassword)) {
//                throw new Exception("Mật khẩu xác nhận không khớp với mật khẩu mới");
//            }
//
//            PasswordChangeRequest request = new PasswordChangeRequest();
//            request.setId(id);
//            request.setUsername(username);
//            request.setCurrentPassword(currentPassword);
//            request.setNewPassword(newPassword);
//
//            userService.changePassword(request);
//
//            String successMessage = "Đổi mật khẩu thành công";
//            redirectAttributes.addAttribute("success", URLEncoder.encode(successMessage, StandardCharsets.UTF_8.toString()));
//        } catch (Exception e) {
//            String errorMessage = "Có lỗi xảy ra: " + e.getMessage();
//            redirectAttributes.addAttribute("error", URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.toString()));
//        }
//
//        return "redirect:/ho-so";
//    }
//
//    /**
//     * Cập nhật avatar người dùng
//     */
//    @PostMapping("/cap-nhat-avatar")
//    public String updateAvatar(
//            @RequestParam("id") Long id,
//            @RequestParam("username") String username,
//            @RequestParam("avatarFile") MultipartFile avatarFile,
//            RedirectAttributes redirectAttributes) {
//
//        try {
//            if (avatarFile.isEmpty()) {
//                throw new Exception("Vui lòng chọn file hình ảnh");
//            }
//
//            // Kiểm tra loại file
//            String contentType = avatarFile.getContentType();
//            if (contentType == null || !contentType.startsWith("image/")) {
//                throw new Exception("Chỉ chấp nhận file hình ảnh");
//            }
//
//            // Kiểm tra kích thước file (max 5MB)
//            if (avatarFile.getSize() > 5 * 1024 * 1024) {
//                throw new Exception("Kích thước file không được vượt quá 5MB");
//            }
//
//            userService.updateAvatar(id, username, avatarFile);
//
//            String successMessage = "Cập nhật ảnh đại diện thành công";
//            redirectAttributes.addAttribute("success", URLEncoder.encode(successMessage, StandardCharsets.UTF_8.toString()));
//        } catch (Exception e) {
//            String errorMessage = "Có lỗi xảy ra: " + e.getMessage();
//            redirectAttributes.addAttribute("error", URLEncoder.encode(errorMessage, StandardCharsets.UTF_8.toString()));
//        }
//
//        return "redirect:/ho-so";
//    }
//
////    @GetMapping("/thucdon")
////    public String thucdon() {
////       return  "/web/thucdon";
////    }
}
