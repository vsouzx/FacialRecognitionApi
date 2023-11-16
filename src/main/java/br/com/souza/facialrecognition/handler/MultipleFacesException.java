package br.com.souza.facialrecognition.handler;

import br.com.souza.facialrecognition.enums.InternalTypeErrorCodesEnum;

public class MultipleFacesException extends ErrorCodeException{

    public MultipleFacesException() {
        super(InternalTypeErrorCodesEnum.E400003);
    }

    public MultipleFacesException(String message) {
        super(InternalTypeErrorCodesEnum.E400003, message);
    }
}