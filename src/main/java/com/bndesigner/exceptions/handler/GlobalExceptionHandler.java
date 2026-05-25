package com.bndesigner.exceptions.handler;

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
                        400,
                        "Erro de validação",
                        message,
                        OffsetDateTime.now()
                ));
    }    
}