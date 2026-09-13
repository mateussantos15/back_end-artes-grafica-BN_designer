package com.bndesigner.exceptions.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.bndesigner.exceptions.custom.BusinessException;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	private static final Map<Class<? extends Exception>, HttpStatus> STATUS_MAP = Map.of(
            HttpMessageNotReadableException.class, HttpStatus.BAD_REQUEST,
            MethodArgumentTypeMismatchException.class, HttpStatus.BAD_REQUEST,
            HttpMediaTypeNotSupportedException.class, HttpStatus.UNSUPPORTED_MEDIA_TYPE
    );
	
	
	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<ApiError> handleBusinessException (
			BusinessException ex) {
		return ResponseEntity.status(ex.getStatus())
				.body(new ApiError(
						ex.getStatus().value(),
						ex.getTitle(),
						ex.getMessage(),
						OffsetDateTime.now()));
	}

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex
    ) {
        var message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.badRequest()
                .body(new ApiError(
                        HttpStatus.BAD_REQUEST.value(),
                        "Erro de validação",
                        message,
                        OffsetDateTime.now()
                ));
    }
    
    /**
     * 
     * Handler genérico de fallback pra qualquer Exception não mapeada, 
     * evitando vazar stacktrace pro cliente
     * @param ex
     * @return
     */
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception ex) {
        HttpStatus status = STATUS_MAP.getOrDefault(ex.getClass(), HttpStatus.INTERNAL_SERVER_ERROR);
        String title = status == HttpStatus.INTERNAL_SERVER_ERROR ? "Erro interno" : "Requisição inválida";
        String message = status == HttpStatus.INTERNAL_SERVER_ERROR ? "Ocorreu um erro inesperado" : ex.getMessage();

        return ResponseEntity.status(status)
                .body(new ApiError(status.value(), title, message, OffsetDateTime.now()));
    }
}