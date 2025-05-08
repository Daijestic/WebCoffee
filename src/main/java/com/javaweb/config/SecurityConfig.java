package com.javaweb.config;

import com.javaweb.service.impl.CustomUserDetailsService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;

@Configuration
@EnableWebSecurity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SecurityConfig {

    public final String[] PUBLIC_URL = {"/user/**", "/users", "/css/**",
                                        "/js/**", "/images/**", "/web/**","/webbuy/**",
                                        "/login"};
    public final String[] PRIVATE_URL = {"/staff"};

    @Value("${jwt.signerKey}")
    String signerKey;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(requests ->
                requests.requestMatchers("/*").permitAll()
                        .requestMatchers("/webbuy/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/admin/users/**").hasRole("ADMIN")
                        .requestMatchers("/products/**").hasRole("ADMIN")
                        .requestMatchers("/products/update/{id}").hasRole("ADMIN")
                        .requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/thanhtoan").hasRole("USER")
                        .requestMatchers("/thanhtoan/**").hasRole("USER")
                        .anyRequest().authenticated())
                .formLogin(login ->
                        login.loginPage("/login")
                                .loginProcessingUrl("/login")
                                .usernameParameter("username")
                                .passwordParameter("password")
                                .successHandler(authenticationSuccessHandler())
                                .failureUrl("/login?error")
                                .permitAll()
                )
                .logout(logout -> logout.logoutUrl("/logout")
                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .defaultSuccessUrl("/datdouong", true)
                        .failureUrl("/login?error=true")
                )
                .csrf(csrf ->
                        csrf.ignoringRequestMatchers("/dangky", "/admin/users/add")
                                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()));

        // Chỉ bật oauth2ResourceServer khi cần thiết
        if (signerKey != null && !signerKey.isEmpty()) {
            httpSecurity.oauth2ResourceServer(oauth2 ->
                    oauth2.jwt(jwtConfigurer ->
                            jwtConfigurer.decoder(jwtDecoder())
                                    .jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );
        }

        return httpSecurity.build();
    }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter);

        return jwtAuthenticationConverter;
    }

    @Bean
    JwtDecoder jwtDecoder() {
        if (signerKey == null || signerKey.isEmpty()) {
            throw new IllegalArgumentException("JWT signer key không được cấu hình. Vui lòng thêm jwt.signerKey vào file application.properties");
        }
        SecretKeySpec secretKeySpec = new SecretKeySpec(signerKey.getBytes(), "HS512");
        return NimbusJwtDecoder
                .withSecretKey(secretKeySpec)
                .macAlgorithm(MacAlgorithm.HS512)
                .build();
    }

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfig(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }


    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return (webSecurity) -> webSecurity.debug(true)
            .ignoring()
            .requestMatchers("/static/**", "/templates/**", 
                "/css/**", "/image/**", "/web/**", "/favicon.ico", "/images/**", "/js/**");
    }

    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return new AuthenticationSuccessHandler() {
            @Override
            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                for (GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
                    if ("ROLE_ADMIN".equals(grantedAuthority.getAuthority())) {
                        response.sendRedirect("/admin/");
                        return;
                    }
                }
                response.sendRedirect("/datdouong");
            }
        };
    }
}