package com.javaweb.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CaLamViecRequest {
    private Long idCa;
    private Date gioVao;
    private Date gioRa;
}
