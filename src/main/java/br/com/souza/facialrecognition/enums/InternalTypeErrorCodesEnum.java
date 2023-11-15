package br.com.souza.facialrecognition.enums;

public enum InternalTypeErrorCodesEnum {

    E400001("Rosto não humano. Envie outra foto."),
    E400002("Rosto não cadastrado, tente novamente.");

    private final String message;

    InternalTypeErrorCodesEnum(String message) {
        this.message = message;
    }

    public String getValue() {
        return this.name();
    }

    public String getMessage() {
        return message;
    }


    @Override
    public String toString() {
        return String.format("Fault code: %s = %s.", getMessage());
    }
}