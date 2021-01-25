package com.cdsi.emr.exception;

import org.springframework.http.HttpStatus;

public class EmrException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	private HttpStatus status;

	public EmrException () {
		super();
	}

	public EmrException (HttpStatus status, String errorMessage) {
		super(errorMessage);
		this.status = status;
	}

	public HttpStatus getStatus () {
		return  this.status;
	}

}
