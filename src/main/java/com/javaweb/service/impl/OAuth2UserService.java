package com.javaweb.service.impl;

import com.javaweb.entity.UserEntity;
import com.javaweb.enums.Role;
import com.javaweb.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;

@Service
public class OAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oauth2User);
        } catch (AuthenticationException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oauth2User) {
        // Xác định provider (google hoặc facebook)
        String provider = userRequest.getClientRegistration().getRegistrationId();

        Map<String, Object> attributes = oauth2User.getAttributes();
        String email;

        // Lấy email từ thông tin người dùng (cách này khác nhau giữa các nhà cung cấp)
        if (provider.equals("google")) {
            email = (String) attributes.get("email");
        } else if (provider.equals("facebook")) {
            // Facebook có thể không trả về email trực tiếp
            email = (String) attributes.get("email");
            if (email == null) {
                // Xử lý trường hợp không có email
                // Có thể tạo email giả dựa trên ID người dùng
                String id = (String) attributes.get("id");
                email = id + "@facebook.com";
            }
        } else {
            throw new OAuth2AuthenticationException("Provider not supported: " + provider);
        }

        // Kiểm tra nếu người dùng đã tồn tại
        Optional<UserEntity> userOptional = userRepository.findByEmail(email);
        UserEntity userEntity;

        if (userOptional.isPresent()) {
            // Người dùng đã tồn tại, cập nhật thông tin nếu cần
            userEntity = userOptional.get();
            // Cập nhật thông tin người dùng từ OAuth2 nếu cần
        } else {
            // Tạo người dùng mới
            userEntity = createUser(email, attributes, provider);
        }

        // Tạo thông tin xác thực
        return new DefaultOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
                attributes,
                userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName()
        );
    }

    private UserEntity createUser(String email, Map<String, Object> attributes, String provider) {
        UserEntity user = new UserEntity();
        user.setEmail(email);

        // Tùy thuộc vào provider, lấy thông tin khác nhau
        if (provider.equals("google")) {
            user.setHoTen((String) attributes.get("name"));
            // Đặt các trường khác theo cần thiết
        } else if (provider.equals("facebook")) {
            user.setHoTen((String) attributes.get("name"));
            // Đặt các trường khác theo cần thiết
        }


        // Đặt role là USER
        user.setLoaiUser(Role.USER.name());

        // Lưu vào database
        return userRepository.save(user);
    }
}