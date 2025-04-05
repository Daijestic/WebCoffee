package com.javaweb.api;

import com.javaweb.dto.repository.APIResponse;
import com.javaweb.dto.repository.AuthenticationResponse;
import com.javaweb.dto.repository.IntrospectResponse;
import com.javaweb.dto.request.AuthenticationRequest;
import com.javaweb.dto.request.IntrospectRequest;
import com.javaweb.service.AuthenticationService;
import com.nimbusds.jose.JOSEException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;

@RequestMapping
@RestController
public class Authentication {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public APIResponse<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) throws JOSEException {
        return APIResponse.<AuthenticationResponse>builder()
                .code(200)
                .result(authenticationService.authenticate(authenticationRequest))
                .build();
    }

    @PostMapping("/token")
    public APIResponse<IntrospectResponse> introspectResponseAPIResponse(@RequestBody IntrospectRequest introspectRequest) throws ParseException, JOSEException {
        return APIResponse.<IntrospectResponse>builder()
                .code(200)
                .message("Success")
                .result(authenticationService.introspect(introspectRequest))
                .build();
    }
}
