package com.javaweb.service.impl;

import com.javaweb.converter.dto_to_entity.UserRequestToEntity;
import com.javaweb.converter.entity_to_dto.UserEntityToDTO;
import com.javaweb.dto.reponse.UserResponse;
import com.javaweb.dto.request.UserRequest;
import com.javaweb.entity.UserEntity;
import com.javaweb.enums.Role;
import com.javaweb.exception.ApplicationException;
import com.javaweb.exception.ErrorCode;
import com.javaweb.repository.UserRepository;
import com.javaweb.repository.TaiKhoanRespository;
import com.javaweb.service.KhachHangService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class KhachHangServiceImpl implements KhachHangService {

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TaiKhoanRespository taiKhoanRespository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    public KhachHangServiceImpl(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }


    @Autowired
    UserEntityToDTO userEntityToDTO;

    @Autowired
    UserRequestToEntity userRequestToEntity;

    @Override
    public UserResponse save(UserRequest userRequest) {
        if (taiKhoanRespository.existsByDangNhap(userRequest.getUsername())) {
            throw new ApplicationException(ErrorCode.USER_EXIST);
        }

        UserEntity userEntity = modelMapper.map(userRequest, UserEntity.class);

        userEntity.setMatKhau(passwordEncoder.encode(userRequest.getPassword()));
        String role = new String();
        if (userRequest.getRoles().isEmpty()) {
            role = Role.USER.name();
        } else {
            role = Optional.ofNullable(userRequest.getRoles())
                    .filter(roles -> !roles.isEmpty())
                    .map(roles -> roles.iterator().next())
                    .orElse("ROLE_USER"); // Giá trị mặc định

        }
        userEntity.setLoaiUser(role);

        userRepository.save(userEntity);

        return userEntityToDTO.UserEntityToDTO(userEntity);
    }

    @Override
    public List<UserResponse> findAll() {
        List<UserEntity> userEntities = userRepository.findAll();
        List<UserResponse> userResponses = new ArrayList<>();
        int cnt = 0;
        for (UserEntity userEntity : userEntities) {
            ++cnt;
            UserResponse userResponse = userEntityToDTO.UserEntityToDTO(userEntity);
            Set<String> roles = new HashSet<>();
            roles.add(userResponse.getRole());
            userResponse.setRoles(roles);
            userResponses.add(userResponse);
            if (cnt == 5) {
                break;
            }
        }
        return userResponses;
    }

    @Override
    public UserResponse findById(Long id) {
        return userEntityToDTO.UserEntityToDTO(userRepository.findById(id).
                orElseThrow(() -> new RuntimeException("User not found")));
    }

    @Override
    public void deleteById(Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
        } else {
            throw new ApplicationException(ErrorCode.CLIENT_NOT_EXIST);
        }
    }

    @Override
    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        String username = context.getAuthentication().getName();
        UserEntity userEntity = taiKhoanRespository.findByDangNhap(username).orElseThrow(
                () -> new ApplicationException(ErrorCode.USER_NOT_EXIST)
        );
        return userEntityToDTO.UserEntityToDTO(userEntity);
    }

    @Override
    public UserResponse update(UserRequest userRequest) {
        userRequest.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        UserEntity userEntity = userRequestToEntity.userRequestToEntity(userRequest);
        return userEntityToDTO.UserEntityToDTO(userRepository.save(userEntity));
    }

    @Override
    public void deleteKhachHangById(Long id) {
        userRepository.deleteById(id);
    }
}
