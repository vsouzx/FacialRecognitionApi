package br.com.souza.facialrecognition.enums;

public enum InternalTypeErrorCodesEnum {

    E400001("Rosto não humano. Envie outra foto."),
    E400002("Rosto não cadastrado, tente novamente."),
    E400003("Múltiplos rostos na foto. Selecione uma foto onde tenha apenas 1 rosto.");

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