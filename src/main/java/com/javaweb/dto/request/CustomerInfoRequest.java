package com.javaweb.dto.request;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CustomerInfoRequest {
    String email, fullName, phone, address, city, district, ward, note;
}
