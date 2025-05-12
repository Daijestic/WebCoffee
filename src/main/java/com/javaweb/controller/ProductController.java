package com.javaweb.controller;

import com.javaweb.dto.reponse.ProductResponse;
import com.javaweb.dto.request.ProductRequest;
import com.javaweb.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/{productId}")
    @ResponseBody
    public ProductResponse getProduct(@PathVariable Long productId) {
        return productService.getProductById(productId);
    }

    @PostMapping("/add")
    @ResponseBody
    public ResponseEntity<?> addProduct(@ModelAttribute ProductRequest productRequest,
                                        @RequestParam(value = "productImage", required = false) MultipartFile productImage)
            throws IOException {
        try {
            // Debug dữ liệu nhận được
            System.out.println("Received data for new product: " + productRequest);
            if (productImage != null) {
                System.out.println("Received file: " + productImage.getOriginalFilename());
            } else {
                System.out.println("No file received");
            }

            ProductResponse savedProduct = productService.save(productRequest, productImage);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Thêm sản phẩm thành công!");
            response.put("product", savedProduct);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi thêm sản phẩm: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/update")
    @ResponseBody
    public ResponseEntity<?> updateProduct(@ModelAttribute ProductRequest productRequest,
                                           @RequestParam(value = "productImage", required = false) MultipartFile productImage)
            throws IOException {
        try {
            // Debug dữ liệu nhận được
            System.out.println("Received data for update: " + productRequest);
            if (productImage != null) {
                System.out.println("Received file: " + productImage.getOriginalFilename());
            } else {
                System.out.println("No file received for update");
            }

            ProductResponse updatedProduct = productService.update(productRequest, productImage);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Cập nhật sản phẩm thành công!");
            response.put("product", updatedProduct);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi cập nhật sản phẩm: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @DeleteMapping("/delete/{deleteProductId}")
    @ResponseBody
    public ResponseEntity<?> deleteProduct(@PathVariable Long deleteProductId) {
        try {
            productService.delete(deleteProductId);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Xóa sản phẩm thành công!");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Lỗi khi xóa sản phẩm: " + e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    /**
     * Endpoint for exporting products to Excel
     */
//    @GetMapping("/export")
//    public ResponseEntity<?> exportToExcel(@RequestParam(required = false, defaultValue = "all") String category) {
//        try {
//            // You would implement this method in your ProductService
//            byte[] excelBytes = productService.generateExcelReport(category);
//
//            return ResponseEntity
//                    .ok()
//                    .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
//                    .header("Content-Disposition", "attachment; filename=products_" + category + ".xlsx")
//                    .body(excelBytes);
//        } catch (Exception e) {
//            Map<String, Object> errorResponse = new HashMap<>();
//            errorResponse.put("success", false);
//            errorResponse.put("message", "Lỗi khi xuất Excel: " + e.getMessage());
//            return ResponseEntity.badRequest().body(errorResponse);
//        }
//    }
}