package com.javaweb.dto.reponse;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CaLamVienResponse {
    private long idCa;
    private Date gioVao;
    private Date gioRa;
}
