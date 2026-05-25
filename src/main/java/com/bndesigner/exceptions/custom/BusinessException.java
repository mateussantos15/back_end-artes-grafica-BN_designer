package com.bndesigner.exceptions.custom;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	private final HttpStatus status;
	private final String title;
	
	public BusinessException(HttpStatus status, String title, String detail) {
        super(detail);
        this.status = status;
        this.title = title;
    }
	
	public HttpStatus getStatus() { return status; }
	public String getTitle() { return title; }
}
