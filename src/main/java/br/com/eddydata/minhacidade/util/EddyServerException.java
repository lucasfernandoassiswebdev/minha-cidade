package br.com.eddydata.minhacidade.util;

public class EddyServerException extends RuntimeException {

    private static final long serialVersionUID = 3965087475900464946L;

    private int code;

    public EddyServerException(String message) {
        super(message);
        this.code = 400;
    }

    public EddyServerException(String message, int code) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
