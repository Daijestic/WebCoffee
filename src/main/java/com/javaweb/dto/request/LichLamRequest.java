package com.javaweb.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class LichLamRequest {
    private Long idLichLam;
    private Long idUser;
    private Date ngayLam;
    private Long idCa;
}
