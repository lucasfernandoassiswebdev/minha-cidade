package br.com.eddydata.minhacidade.util;

public class ErrorMessage {

    private String message;
    private int code;

    public ErrorMessage() {
    }

    public ErrorMessage(String message, int code) {
        super();
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
