package br.com.souza.facialrecognition.handler;

import br.com.souza.facialrecognition.enums.InternalTypeErrorCodesEnum;

public class NotHumanFaceException extends ErrorCodeException{

    public NotHumanFaceException() {
        super(InternalTypeErrorCodesEnum.E400001);
    }

    public NotHumanFaceException(String message) {
        super(InternalTypeErrorCodesEnum.E400001, message);
    }
}