package com.javaweb.service.impl;

import com.javaweb.custom.CustomUserDetails;
import com.javaweb.entity.TaiKhoanEntity;
import com.javaweb.exception.ApplicationException;
import com.javaweb.exception.ErrorCode;
import com.javaweb.repository.TaiKhoanRespository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomUserDetailService implements UserDetailsService {

    @Autowired
    private TaiKhoanRespository taiKhoanRespository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        TaiKhoanEntity taiKhoanEntity = taiKhoanRespository.findByUsername(username).orElseThrow(
                () -> new ApplicationException(ErrorCode.USER_NOT_EXIST));
        Collection<GrantedAuthority> grantedAuthoritySet = new HashSet<>();
        for (String role : taiKhoanEntity.getRole()) {
            grantedAuthoritySet.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        return new CustomUserDetails(taiKhoanEntity, grantedAuthoritySet);
    }
}
