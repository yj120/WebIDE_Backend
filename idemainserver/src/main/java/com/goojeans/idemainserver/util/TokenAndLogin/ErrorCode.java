package com.goojeans.idemainserver.util.TokenAndLogin;

import jakarta.servlet.http.HttpServletResponse;


public enum ErrorCode {

    OK(HttpServletResponse.SC_OK,200,"REQUEST SUCCESS"),

    // 회원 가입 관련 1
    ALEADY_EXIST_EMAIL(HttpServletResponse.SC_BAD_REQUEST, 4001, "aleady Exist"),
    ALEADY_EXIST_NICKNAME(HttpServletResponse.SC_BAD_REQUEST, 4011, "aleady Exist"),

    // 로그인 관련 2
    LOGIN_FAIL(HttpServletResponse.SC_BAD_REQUEST,4012,"general login failed"),
    LOGIN_FAIL_SOCIAL(HttpServletResponse.SC_BAD_REQUEST,4022,"social login failed"),

    // 마이페이지 수정 4
    EDIT_FAIL(HttpServletResponse.SC_BAD_REQUEST,4014,"edit failed"),


    ENTITY_NOT_FOUND(4002, HttpServletResponse.SC_BAD_REQUEST,"Entity Not Found"),
    ACCESS_DENIED(4003,HttpServletResponse.SC_BAD_REQUEST,"Access is Denied"),
    NOT_FOUND(HttpServletResponse.SC_NOT_FOUND, 4004,"Not Found"),
    LOGIN_NEEDED(HttpServletResponse.SC_FORBIDDEN, 4005, "Login Required"),


    INTERNAL_SERVER_ERROR(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 5000, "Server Error"),
    UNCATEGORIZED(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, 5001, "Uncategorized");



    private final int code;
    private int status;
    private final String message;


    ErrorCode(int code, int status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }


    public int getCode() {
        return code;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
