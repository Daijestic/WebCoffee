package com.javaweb.dto.reponse;

import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String token;
    private boolean authenticated;
}
