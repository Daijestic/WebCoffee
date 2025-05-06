package com.javaweb.api;


import com.javaweb.dto.reponse.APIResponse;
import com.javaweb.dto.reponse.UserResponse;
import com.javaweb.dto.request.UserRequest;
import com.javaweb.converter.entity_to_dto.UserEntityToDTO;
import com.javaweb.service.KhachHangService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
public class User {

    @Autowired
    KhachHangService khachHangService;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserEntityToDTO userEntityToDTO;

    @PostMapping("/dangky")
    public APIResponse<UserResponse> uses(@RequestBody @Valid UserRequest userRequest) {
        return APIResponse.<UserResponse>builder()
                .code(200)
                .message("success")
                .result(khachHangService.save(userRequest))
                .build();
    }

//    @GetMapping("/admin/users")
//    public APIResponse<List<UserResponse>> all() {
//        APIResponse<List<UserResponse>> apiResponse = new APIResponse<>();
//        apiResponse.setCode(200);
//        apiResponse.setMessage("OK");
//        List<KhachHangEntity> khachHangEntities = khachHangService.findAll();
//        List<UserResponse> userResponseList = new ArrayList<>();
//        for(KhachHangEntity khachHangEntity : khachHangEntities) {
//            userResponseList.add(userEntityToDTO.UserEntityToDTO(khachHangEntity));
//        }
//        apiResponse.setResult(userResponseList);
//        return apiResponse;
//    }

    @GetMapping("/admin/user/{id}")
    public UserResponse uses(@PathVariable Long id) {
        return khachHangService.findById(id);
    }

    @DeleteMapping("/admin/user/{id}")
    public APIResponse delete(@PathVariable Long id) {
        khachHangService.deleteById(id);
        return APIResponse.builder()
                .code(200)
                .message("Xoá khách hàng thành công!")
                .build();
    }
}
