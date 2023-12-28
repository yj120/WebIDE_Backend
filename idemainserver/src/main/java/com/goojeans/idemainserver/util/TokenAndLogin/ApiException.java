package com.goojeans.idemainserver.util.TokenAndLogin;

public class ApiException extends RuntimeException{
    private ResponseCode responseCode = ResponseCode.UNCATEGORIZED;


    public ApiException(ResponseCode responseCode, String message){
        super(message);
        this.responseCode = responseCode;
    }

    public ResponseCode getErrorCode(){
        return responseCode;
    }
}
