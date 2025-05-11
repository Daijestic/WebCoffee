package com.javaweb.config;

import com.javaweb.entity.UserEntity;
import com.javaweb.enums.Role;
import com.javaweb.repository.UserRepository;
import com.javaweb.repository.TaiKhoanRespository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ApplicationInitConfig {

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    TaiKhoanRespository taiKhoanRespository;

    @Autowired
    UserRepository userRepository;

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            if (!taiKhoanRespository.existsByDangNhap("admin")) {

                UserEntity userEntity = new UserEntity();
                userEntity.setDangNhap("admin");
                userEntity.setMatKhau(passwordEncoder.encode("admin"));
                userEntity.setLoaiUser(Role.ADMIN.name()); // Đảm bảo không null
                userRepository.save(userEntity);
                log.warn("admin user has been created with default password: admin, please don't change it!");
            }
            if (!taiKhoanRespository.existsByDangNhap("user")) {
                
                UserEntity userEntity = new UserEntity();
                userEntity.setDangNhap("user");
                userEntity.setMatKhau(passwordEncoder.encode("user"));
                userEntity.setLoaiUser(Role.USER.name()); // Đảm bảo không null
                userRepository.save(userEntity);
                log.warn("user has been created with default password: user, please don't change it!");
            }
        };
    }
}