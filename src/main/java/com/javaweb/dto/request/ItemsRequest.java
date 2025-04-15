package com.javaweb.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemsRequest {
    String id;
    String name;
    String quantity;
    String price;
    String totalPrice;
    String discount;
}
