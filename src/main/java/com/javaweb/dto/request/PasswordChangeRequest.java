package com.javaweb.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordChangeRequest {
    private Long id;
    private String username;
    private String currentPassword;
    private String newPassword;
}
