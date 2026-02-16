package com.iexceed.appzillonbanking.cagl.custom.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KendraFetchException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	private String errorMessage;
	
	public KendraFetchException(String message,String errMessage)
	{
		super(message,null,false,false);
		this.errorMessage = errMessage;
		
	}
	
	

}
