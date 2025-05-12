package com.javaweb.dto.request;
import lombok.Data;

@Data

public class CartUpdateRequest {
    private Long idMon;
    private String tenSize;
    private int soLuongThayDoi;

    // Getters và Setters
    public Long getIdMon() {
        return idMon;
    }

    public void setIdMon(Long idMon) {
        this.idMon = idMon;
    }

    public String getTenSize() {
        return tenSize;
    }

    public void setTenSize(String tenSize) {
        this.tenSize = tenSize;
    }

    public int getSoLuongThayDoi() {
        return soLuongThayDoi;
    }

    public void setSoLuongThayDoi(int soLuongThayDoi) {
        this.soLuongThayDoi = soLuongThayDoi;
    }
}

