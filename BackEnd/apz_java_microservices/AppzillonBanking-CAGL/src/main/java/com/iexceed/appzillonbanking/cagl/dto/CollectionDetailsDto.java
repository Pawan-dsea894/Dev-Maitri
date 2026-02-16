package com.iexceed.appzillonbanking.cagl.dto;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
@Builder


public class CollectionDetailsDto {
	// gk_kendra_data

	private int kendraId;

	private String branchId;

	private String kendraName;

	private String nextMeetingDate;
	
	private String meetingDay;

	private String meetingStartingTime;

	// t24_collection_sheets

	private String collectionId;

	private Timestamp transactionDate;

	private String collectionType;

	private String handledBy;

	private double totalAmountCollected;

	private String groupId;

	private String groupName;

	private String groupTotalDue;

	private String groupTotalAdvance;

	private String groupNetDue;

	private String groupTotCollAmount;

	private String groupCustomerId;

	private String customerName;
	
	private String customerFlg;

	private String custTotalDue;

	private String custTotalAdvance;

	private String custNetDue;
	
	private String custCollectedAmount;

	private String custAttendance;

	private String loanId;

	private String loanDue;
	
	private String kmId;
	
	/*
	 * // gk_m_loan_product private String shortDescription;
	 */

	private String loanOutstandingAmount;

	private String loanCollectedAmount;
	
	private String loanProductId;
	
	public CollectionDetailsDto(int kendraId, String branchId, String kendraName, String nextMeetingDate,
            String meetingDay, String meetingStartingTime, String collectionId, Timestamp transactionDate,
            String collectionType, String handledBy, double totalAmountCollected, String groupId,
            String groupName, String groupTotalDue, String groupTotalAdvance, String groupNetDue,
            String groupTotCollAmount, String groupCustomerId, String customerName, String customerFlg,
            String custTotalDue, String custTotalAdvance, String custNetDue, String custCollectedAmount,
            String custAttendance, String loanId, String loanDue, String kmId , String loanOutstandingAmount,
            String loanCollectedAmount, String loanProductId ) {
this.kendraId = kendraId;
this.branchId = branchId;
this.kendraName = kendraName;
this.nextMeetingDate = nextMeetingDate;
this.meetingDay = meetingDay;
this.meetingStartingTime = meetingStartingTime;
this.collectionId = collectionId;
this.transactionDate = transactionDate;
this.collectionType = collectionType;
this.handledBy = handledBy;
this.totalAmountCollected = totalAmountCollected;
this.groupId = groupId;
this.groupName = groupName;
this.groupTotalDue = groupTotalDue;
this.groupTotalAdvance = groupTotalAdvance;
this.groupNetDue = groupNetDue;
this.groupTotCollAmount = groupTotCollAmount;
this.groupCustomerId = groupCustomerId;
this.customerName = customerName;
this.customerFlg = customerFlg;
this.custTotalDue = custTotalDue;
this.custTotalAdvance = custTotalAdvance;
this.custNetDue = custNetDue;
this.custCollectedAmount = custCollectedAmount;
this.custAttendance = custAttendance;
this.loanId = loanId;
this.loanDue = loanDue;
this.kmId = kmId ;
this.loanOutstandingAmount = loanOutstandingAmount;
this.loanCollectedAmount = loanCollectedAmount;
this.loanProductId = loanProductId;
}

}

