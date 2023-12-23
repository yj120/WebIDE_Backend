package com.goojeans.idemainserver.util.TokenAndLogin;

public class ApiException extends RuntimeException{
    private ErrorCode errorCode = ErrorCode.UNCATEGORIZED;


    public ApiException(ErrorCode errorCode, String message){
        super(message);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode(){
        return errorCode;
    }
}
