package br.com.eddydata.minhacidade.util;

public enum ErrorCode {
    CLIENT_ERROR(400),
    SERVER_ERROR(500);

    private final int code;

    ErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
