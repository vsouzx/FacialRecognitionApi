package br.com.souza.facialrecognition.handler;

import br.com.souza.facialrecognition.enums.InternalTypeErrorCodesEnum;

public class NotRegisteredFaceException extends ErrorCodeException{

    public NotRegisteredFaceException() {
        super(InternalTypeErrorCodesEnum.E400002);
    }

    public NotRegisteredFaceException(String message) {
        super(InternalTypeErrorCodesEnum.E400002, message);
    }
}