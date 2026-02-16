package com.iexceed.appzillonbanking.cagl.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CdhData {
	
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
	private String customerId;
	private String customerName; 
	private int groupId; 
	private String branchName;
	private String primaryType; 
	private String primaryId; 
	private String dob; 
	private String maritalStatus; 
	private String address; 
	private String bankAccNo;
	private String bankAccName;
	private String bankBranchName;
	private String bankName; 
	private String bankIfscCode; 
	private String memRelation; 
	private String mobileNum; 
	private String permAddLine1; 
	private String permAddLine2;
	private String permanentState; 
	private String permanentDistrict;
	private String permanentVillageLocality;
	private String permanentPincode; 
	private String permanentTaluk;
	private String commAddLIne1;  
	private String commAddLIne2;  
	private String commState; 
	private String commDistrict; 
	private String commVillageLocality; 
	private String commPincode; 
	private String commTaluk;
	private String loanId; 
	private String amount;  
	private String approvedAmt; 
	private String status; 
	private String freq; 
	private String term;
	private String product;  
	private String lnValueDate; 
	private String lnMatDate; 
	private String interestRate; 
	private String overduePrincipal; 
	private String overdueInterest;
	private double cbAmt;
	private double caglAmt;
	private String totIncome;
	private String totExpense;
	private String assesmentDt;
	private String name;
	private String legaldocName;
	private String legaldocId;


}
