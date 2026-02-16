package com.iexceed.appzillonbanking.cagl.dto;

import java.sql.Timestamp;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KendraData {
	
	private int kendraId; 
	private String kendraName;
	private String kmName;
	private String branchId;
	private String villageType;
	private String kendraAddr;
	private String state;
	private String district;
	private String taluk;
	private String areaType;
	private String village;
	private String pincode;
	private String meetingFrequency;
	private String firstMeetingDate;
	private String nextMeetingDate;
	private String meetingDay;
	private String meetingPlace;
	private String meetingStartTime;
	private String endingTime;
	private String distance;
	private String leader;
	private String secretary;
	private String createdBy;
	private Timestamp createdTS;
	private String updatedBy;
	private String kendraStatus;
	private String activationDate;
	private String kmId;
	
	//private List<GroupDetails> groupDtls;
	
	private List<CustData> customerDtls;
	
	private OfficeDataDto officeData;

}
