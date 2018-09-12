package br.com.eddydata.minhacidade.api;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorJWTMessage {

    private Integer statusCode;
    private String statusMessage;
    private String erro;

    public int getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getErro() {
        return erro;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private final ErrorJWTMessage errorMessage;

        Builder() {
            this.errorMessage = new ErrorJWTMessage();
        }

        public Builder addStatusCode(Integer statusCode) {
            this.errorMessage.statusCode = statusCode;
            return this;
        }

        public Builder addStatusMessage(String statusMessage) {
            this.errorMessage.statusMessage = statusMessage;
            return this;
        }

        public Builder addErro(String erro) {
            this.errorMessage.erro = erro;
            return this;
        }

        public ErrorJWTMessage build() {
            return this.errorMessage;
        }
    }
}
