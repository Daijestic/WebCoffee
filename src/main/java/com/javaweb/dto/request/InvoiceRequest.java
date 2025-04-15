package com.javaweb.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceRequest {
    OrderRequest orderDetails;
    CustomerInfoRequest customerInfo;
    String paymentMethod;
}
