package com.goojeans.idemainserver.util;

public enum SubmitResult {
    CORRECT("CORRECT"), WRONG("WRONG"), TIMEOUT("TIMEOUT"), ERROR("ERROR");

    private String submitResult;

    SubmitResult(String submitResult) {
        this.submitResult = submitResult;
    }

    public String toString() {
        return this.submitResult;
    }
}
