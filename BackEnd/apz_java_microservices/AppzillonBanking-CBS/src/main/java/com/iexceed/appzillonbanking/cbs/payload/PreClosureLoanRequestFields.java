package com.iexceed.appzillonbanking.cbs.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PreClosureLoanRequestFields {
	
	@JsonProperty("nominationDetails")
	private List<PreClosureLoanNomineeRequestFields> nominationDetails;

	@JsonProperty("customerId")
	private String customerId;

	@JsonProperty("loanAction")
	private String loanAction;

	@JsonProperty("loanProduct")
	private String loanProduct;

	@JsonProperty("currencyId")
	private String currencyId;

	@JsonProperty("mmflPreclosureAmt")
	private String mmflPreclosureAmt;

	@JsonProperty("borrowerInsurance")
	private String borrowerInsurance;

	@JsonProperty("spouseInsurance")
	private String spouseInsurance;

	@JsonProperty("modeOfPayment")
	private String modeOfPayment;

	@JsonProperty("term")
	private String term;

	@JsonProperty("amount")
	private String amount;

	@JsonProperty("paymentFreq")
	private String paymentFreq;

	@JsonProperty("loanPurpose")
	private String loanPurpose;

	@JsonProperty("loanSubPurpose")
	private String loanSubPurpose;

	@JsonProperty("custRelationOccupation")
	private String custRelationOccupation;

	@JsonProperty("NomineeUpi")
	private String NomineeUpi;

	@JsonProperty("cbValidTill")
	private String cbValidTill;

	@JsonProperty("creditAssessRecom")
	private String creditAssessRecom;

	@JsonProperty("creditAssessNotes")
	private String creditAssessNotes;

	@JsonProperty("cbReportDate")
	private String cbReportDate;

	@JsonProperty("cbStatus")
	private String cbStatus;

	@JsonProperty("cbSummary")
	private String cbSummary;

	@JsonProperty("cbReportLink")
	private String cbReportLink;

	@JsonProperty("cbRemarks")
	private String cbRemarks;

	@JsonProperty("cbScore")
	private String cbScore;

	@JsonProperty("creditAssessResult")
	private String creditAssessResult;

	@JsonProperty("creditAssessRemarks")
	private String creditAssessRemarks;

	@JsonProperty("creditAssessUpdBy")
	private String creditAssessUpdBy;

	@JsonProperty("creditAssessUpdDate")
	private String creditAssessUpdDate;

	@JsonProperty("precloseType")
	private String precloseType;

	@JsonProperty("reviewApprovalNotes")
	private String reviewApprovalNotes;

	@JsonProperty("status")
	private String status;

	@JsonProperty("declineReason")
	private String declineReason;

	@JsonProperty("remarks")
	private String remarks;

	@JsonProperty("referenceId")
	private String referenceId;

	@JsonProperty("interestRate")
	private String interestRate;

	@JsonProperty("annualPercentageRate")
	private String annualPercentageRate;
	
	@JsonProperty("branchId")
	private String branchId;
	
	@JsonProperty("insurerId")
	private String insurerId;

	@JsonProperty("closeAcctNo")
	private List<PreClosureLoanCloseAccRequestFields> closeAcctNo;

	@JsonProperty("applicationId")
	private String applicationId;
	
	@JsonProperty("versionNo")
	private String versionNo;
	
	@JsonProperty("custDtlId")
	private String custDtlId;
	
	@JsonProperty("foir")
	private String foir;
	
	@JsonProperty("earning_member")
	private String earning_member;
	
	@JsonProperty("family_income")
	private String family_income;
	
	@JsonProperty("processingFee")
	private String processingFee;
	
	@JsonProperty("gSTPF")
	private String gSTPF;
	
	@JsonProperty("insuranceChargeMember")
	private String insuranceChargeMember;
	
	@JsonProperty("insuranceChargeSpouse")
	private String insuranceChargeSpouse;

	@JsonProperty("upfrontInt")
	private String upfrontInt;
	
	@JsonProperty("userId")
	private String userId;
		
	@JsonProperty("userRole")
	private String userRole;
	
	@JsonProperty("userName")
	private String userName;
	
	@JsonProperty("appVersion")
	private String appVersion;

}
