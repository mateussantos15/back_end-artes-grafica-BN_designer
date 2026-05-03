package com.bndesigner.exceptions.handler;

import com.bndesigner.exceptions.BusinessException;
import com.bndesigner.exceptions.ResourceNotFoundException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;

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

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ApiError> handleResourceNotFoundException (
			ResourceNotFoundException ex) {
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
                .toList()
                .toString();

        return ResponseEntity.badRequest()
                .body(new ApiError(
                        400,
                        "Erro de validação",
                        message,
                        OffsetDateTime.now()
                ));
    }
}