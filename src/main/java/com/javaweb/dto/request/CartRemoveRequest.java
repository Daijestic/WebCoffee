package com.javaweb.dto.request;
import lombok.Data;

@Data

public class CartRemoveRequest {
    private Long idMon;
    private String tenSize;

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
}