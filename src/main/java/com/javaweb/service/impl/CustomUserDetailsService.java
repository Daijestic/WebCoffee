package com.javaweb.service.impl;

import com.javaweb.custom.CustomUserDetails;
import com.javaweb.entity.UserEntity;
import com.javaweb.exception.ApplicationException;
import com.javaweb.exception.ErrorCode;
import com.javaweb.repository.TaiKhoanRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private TaiKhoanRespository taiKhoanRespository;
    
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CustomUserDetailsService(@Lazy PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }
    
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Optional<UserEntity> userOpt = taiKhoanRespository.findByDangNhap(username);
//        if (userOpt.isEmpty()) {
//            throw new UsernameNotFoundException("Không tìm thấy người dùng: " + username);
//        }
//
//        UserEntity user = userOpt.get();
//        List<GrantedAuthority> authorities = user.getLoaiUser().stream()
//                .map(role -> new SimpleGrantedAuthority(role))
//                .collect(Collectors.toList());
//
//        // Kiểm tra và cập nhật mật khẩu nếu chưa được mã hóa
//        String password = user.getMatKhau();
//        if (!password.startsWith("$2a$") && !password.startsWith("$2b$") && !password.startsWith("$2y$")) {
//            // Mật khẩu chưa mã hóa, thực hiện mã hóa và cập nhật
//            String encodedPassword = passwordEncoder.encode(password);
//            user.setMatKhau(encodedPassword);
//            taiKhoanRespository.save(user);
//            // Sử dụng mật khẩu đã mã hóa
//            password = encodedPassword;
//        }
//
//        return new org.springframework.security.core.userdetails.User(
//                user.getDangNhap(),
//                password,
//                authorities
//        );
//    }
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity taiKhoanEntity = taiKhoanRespository.findByDangNhap(username).orElseThrow(
                () -> new ApplicationException(ErrorCode.USER_NOT_EXIST));
        Collection<GrantedAuthority> grantedAuthoritySet = new HashSet<>();
//         Kiểm tra và cập nhật mật khẩu nếu chưa được mã hóa
        String password = taiKhoanEntity.getMatKhau();
        if (!password.startsWith("$2a$") && !password.startsWith("$2b$") && !password.startsWith("$2y$")) {
            // Mật khẩu chưa mã hóa, thực hiện mã hóa và cập nhật
            String encodedPassword = passwordEncoder.encode(password);
            taiKhoanEntity.setMatKhau(encodedPassword);
            taiKhoanRespository.save(taiKhoanEntity);
        }
        grantedAuthoritySet.add(new SimpleGrantedAuthority("ROLE_" + taiKhoanEntity.getLoaiUser()));
        return new CustomUserDetails(taiKhoanEntity, grantedAuthoritySet);
    }
}