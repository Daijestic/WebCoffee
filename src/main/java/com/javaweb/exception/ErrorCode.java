package com.javaweb.exception;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum ErrorCode {
    UNCATEGORIZED_EXCEPTION(1001, "Lỗi chưa phân loại!"),
    USERNAME_INVALID(1003, "Tên đăng nhập tối thiểu 5 ký tự!"),
    PASSWORD_INVALID(1004, "Mật khẩu ít nhất 8 ký tự!"),
    USER_EXIST(1005, "Tên đăng nhập đã tồn tại!"),
    INVALID_KEY(1006, "Tham chiếu sai key ở phẩn INVALID!"),
    USER_NOT_EXIST(1007, "Tên đăng nhập không tồn tại!"),
    CLIENT_NOT_EXIST(1008, "Người dùng không tồn tại!"),
    UNAUTHENTICATED(1009, "Sai mật khẩu!"),
    FILE_IS_EMPTY(1010, "File rỗng!"),
    TENLOAIMON_NOT_EXIST(1011, "Không tồn tại loại món!"),
    MON_NOT_EXIST(1012, "Không tồn tại món!")
    ;

    private int code;
    private String message;
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

}
