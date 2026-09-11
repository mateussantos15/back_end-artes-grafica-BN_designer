package com.bndesigner.exceptions.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bndesigner.exceptions.custom.BusinessException;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
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
    public ResponseEntity<ApiError> handleGeneric(Exception ex) {
        return ResponseEntity.internalServerError()
                .body(new ApiError(
                        500,
                        "Erro interno",
                        "Ocorreu um erro inesperado",
                        OffsetDateTime.now()));
    }
}