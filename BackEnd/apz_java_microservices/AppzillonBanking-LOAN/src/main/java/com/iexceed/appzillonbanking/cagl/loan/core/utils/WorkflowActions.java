package com.iexceed.appzillonbanking.cagl.loan.core.utils;

public enum WorkflowActions {
	
	INITIATED_BY("Initiated By"),
	ASSIGNED_TO("Assigned to"),
	REVIEWED_BY("Reviewed By"),
	VERIFIED_BY("Verified By"),
	APPROVED_BY("Approved By"),
	REJECTED_BY("Rejected By");
	
	private final String value;
	
	WorkflowActions(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}