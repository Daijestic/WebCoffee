package com.javaweb.dto.reponse;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class LichLamResponse {
    private Long idLichLam;
    private Date ngayLam;
    private Long idUser;
    private String tenUser;
    private Long idCa;
    private Date gioVao;
    private Date gioRa;
}
