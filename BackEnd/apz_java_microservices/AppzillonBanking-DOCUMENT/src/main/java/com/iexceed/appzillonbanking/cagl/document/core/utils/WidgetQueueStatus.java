package com.iexceed.appzillonbanking.cagl.document.core.utils;

public enum WidgetQueueStatus {

	PARTIAL_BY_CUSTOMER("Partial by customer"),
	PARTIAL_BY_SELF("Partial by Self"),
	PARTIAL_BY_OTHERS("Partial by Others"),
	COMPLETED("Completed"),
	REJECTED("Rejected"),
	DELETED("Deleted"),
	PENDING_IN_QUEUE("Pending in Queue"),
	ASSIGNED("Assigned"),
	PENDING_FOR_VERIFICATION("Pending for verification"),
	PENDING_FOR_APPROVAL("Pending for approval");
	
	private final String value;
	
	WidgetQueueStatus(String value) {
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}


