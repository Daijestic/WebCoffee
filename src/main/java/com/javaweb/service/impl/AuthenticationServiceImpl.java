package com.javaweb.service.impl;

import com.javaweb.dto.repository.AuthenticationResponse;
import com.javaweb.dto.repository.IntrospectResponse;
import com.javaweb.dto.request.AuthenticationRequest;
import com.javaweb.dto.request.IntrospectRequest;
import com.javaweb.entity.TaiKhoanEntity;
import com.javaweb.exception.ApplicationException;
import com.javaweb.exception.ErrorCode;
import com.javaweb.repository.TaiKhoanRespository;
import com.javaweb.service.AuthenticationService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Set;
import java.util.StringJoiner;

@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    TaiKhoanRespository taiKhoanRespository;

    @NonFinal
    @Value("${jwt.singerKey}")
    private String SINGER_KEY;

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) throws JOSEException {
        var taiKhoanEntity = taiKhoanRespository.findByUsername(authenticationRequest.getUsername())
                .orElseThrow(() -> new ApplicationException(ErrorCode.USER_NOT_EXIST));
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean authenticated =  passwordEncoder.matches(authenticationRequest.getPassword(), taiKhoanEntity.getPassword());

        if (!authenticated){
            throw new ApplicationException(ErrorCode.UNAUTHENTICATED);
        }

        String token = generateToken(taiKhoanEntity);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .build();
    }

    @Override
    public IntrospectResponse introspect(IntrospectRequest introspectRequest) throws JOSEException, ParseException {
        var token = introspectRequest.getToken();

        JWSVerifier verifier = new MACVerifier(SINGER_KEY.getBytes());


        SignedJWT signedJWT = SignedJWT.parse(token);

        //check hạn sử dụng token
        Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        return IntrospectResponse.builder()
                .introspectSuccess(verified && expiration.after(new Date()))
                .build();
    }


    private String generateToken(TaiKhoanEntity taiKhoanEntity) throws JOSEException {
        JWSHeader jweHeader = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimNames = new JWTClaimsSet.Builder()
                .subject(taiKhoanEntity.getUsername())
                .claim("username", taiKhoanEntity.getUsername())
                .claim("scope", buildScope(taiKhoanEntity))
                .issuer("daibui")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(1, ChronoUnit.HOURS).toEpochMilli()
                ))
                .build();

        Payload payload = new Payload(jwtClaimNames.toJSONObject());

        JWSObject jweObject = new JWSObject(jweHeader, payload);

        jweObject.sign(new MACSigner(SINGER_KEY.getBytes()));
        return jweObject.serialize();
    }

    private String buildScope(TaiKhoanEntity taiKhoanEntity) {
        StringJoiner stringJoiner = new StringJoiner(" ");
        if (!CollectionUtils.isEmpty(taiKhoanEntity.getRole())) {
            taiKhoanEntity.getRole().forEach(stringJoiner::add);
        }
        return stringJoiner.toString();
    }
}
