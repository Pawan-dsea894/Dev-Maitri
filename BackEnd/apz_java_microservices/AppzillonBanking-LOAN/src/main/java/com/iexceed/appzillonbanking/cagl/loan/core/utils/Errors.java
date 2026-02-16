package com.iexceed.appzillonbanking.cagl.loan.core.utils;

public enum Errors {
	NORECORD("NR100", "No Record/Data Found"),
	PROCESSINGREQUESTERROR("600", "Error Processing Request"), 
	AUTHTOKENFAILURE("TOKEN_FAIL","Failure response from Token Generation service, Please try after sometime."),
	PROCESSING_REQ_ERROR("ABS_COM_600",""),
	DATAEXISTS("DATA_EXISTS","Data already exists"),
	DATAUPDATIONFAILURE("ERR1", "Unable to update the record(s)");
	
	private final String errorCode;
	private final String errorMessage;

	Errors(String id, String msg) {
		this.errorCode = id;
		this.errorMessage = msg;
	}

	public String getErrorCode() {
		return this.errorCode;
	}

	public String getErrorMessage() {
		return this.errorMessage;
	}
}