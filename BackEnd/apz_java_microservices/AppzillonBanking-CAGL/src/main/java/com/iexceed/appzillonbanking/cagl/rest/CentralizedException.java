package com.iexceed.appzillonbanking.cagl.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.iexceed.appzillonbanking.cagl.custom.exception.KendraFetchException;

@ControllerAdvice
public class CentralizedException {
	
	@ExceptionHandler(value = KendraFetchException.class)
	public ResponseEntity<KendraFetchException> getException(KendraFetchException ex)
	{
		
		return new ResponseEntity<KendraFetchException>(ex, HttpStatus.NOT_FOUND);
	}
	
}
