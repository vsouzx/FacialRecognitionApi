package br.com.souza.facialrecognition.handler;

import br.com.souza.facialrecognition.dto.CustomErrorResponse;
import jakarta.annotation.Resource;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @Resource
    private MessageSource messageSource;

    @ExceptionHandler({ErrorCodeException.class})
    private ResponseEntity<Object> handleCodigoDeErro(Exception e, WebRequest request) {
        ErrorCodeException errorCodeException = (ErrorCodeException) e;
        CustomErrorResponse error = new CustomErrorResponse();
        error.setError(e.getMessage());
        error.setStatus(HttpStatus.BAD_REQUEST.value());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        return handleExceptionInternal(e, error, headers, HttpStatus.BAD_REQUEST, request);
    }

}