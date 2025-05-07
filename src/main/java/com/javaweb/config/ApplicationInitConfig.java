package com.javaweb.config;

import com.javaweb.entity.KhachHangEntity;
import com.javaweb.entity.NhanVienEntity;
import com.javaweb.entity.TaiKhoanEntity;
import com.javaweb.enums.Role;
import com.javaweb.repository.KhachHangRepository;
import com.javaweb.repository.NhanVienRepository;
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
    NhanVienRepository nhanVienRepository;

    @Autowired
    KhachHangRepository khachHangRepository;

    @Bean
    ApplicationRunner applicationRunner() {
        return args -> {
            if (!taiKhoanRespository.existsByUsername("admin")) {
                HashSet<String> roles = new HashSet<>();
                roles.add(Role.ADMIN.name());

                NhanVienEntity nhanVienEntity = new NhanVienEntity();
                TaiKhoanEntity taiKhoanEntity = new TaiKhoanEntity();
                taiKhoanEntity.setUsername("admin");
                taiKhoanEntity.setPassword(passwordEncoder.encode("admin"));
                taiKhoanEntity.setRoles(roles);
                nhanVienEntity.setTaiKhoan(taiKhoanEntity);
                nhanVienRepository.save(nhanVienEntity);
                log.warn("admin user has been  created with default password: admin, please don't change it!");
            }
            if (!taiKhoanRespository.existsByUsername("user")) {
                KhachHangEntity khachHangEntity = new KhachHangEntity();
                TaiKhoanEntity taiKhoanEntity2 = new TaiKhoanEntity();
                taiKhoanEntity2.setUsername("user");
                taiKhoanEntity2.setPassword(passwordEncoder.encode("user"));
                HashSet<String> roles2 = new HashSet<>();
                roles2.add(Role.USER.name());
                taiKhoanEntity2.setRoles(roles2);
                khachHangEntity.setTaiKhoan(taiKhoanEntity2);
                khachHangRepository.save(khachHangEntity);
                log.warn("user has been  created with default password: user, please don't change it!");
            }
        };
    }
}
