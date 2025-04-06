package com.javaweb.api;

import com.javaweb.dto.reponse.APIResponse;
import com.javaweb.dto.reponse.StaffReponse;
import com.javaweb.service.impl.NhanVienServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/staff")
@RestController
public class Staff {

    @Autowired
    private NhanVienServiceImpl nhanVienService;

    @GetMapping
    public APIResponse<List<StaffReponse>> findAll() {
        List<StaffReponse> staffReponses = nhanVienService.findAll();
        return APIResponse.<List<StaffReponse>>builder()
                .code(200)
                .message("OK")
                .result(staffReponses)
                .build();
    }
}
