package com.iexceed.appzillonbanking.cagl.incomeassesment.core;

public enum AppStatus {

	INPROGRESS("INPROGRESS"),
	PENDING("PENDING"),
	APPROVED("APPROVED"),
	DELETED("DELETED"),
	REJECTED("REJECTED"),
	ACTIVE_STATUS("A"),
	INACTIVESTATUS("I");
	
	private final String value;
	
	AppStatus(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}


