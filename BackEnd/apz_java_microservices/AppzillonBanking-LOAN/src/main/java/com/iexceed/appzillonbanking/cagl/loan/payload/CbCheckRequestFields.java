package com.iexceed.appzillonbanking.cagl.loan.payload;

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
public class CbCheckRequestFields {

	@JsonProperty("address")
	private List<AddressPayload> address;
	
	@JsonProperty("document")
	private List<DocumentPayload> document;

	@JsonProperty("depType")
	private String depType;
	
	@JsonProperty("depName")
	private String depName;
	
	@JsonProperty("categoryID")
	private String categoryID;
	
	@JsonProperty("productcode")
	private String productcode;
	
	@JsonProperty("product_code")
	private String product_code;
	
	@JsonProperty("dob")
	private String dob;
	
	@JsonProperty("durationOfAgreement")
	private String durationOfAgreement;
	
	@JsonProperty("bankProductId")
	private String bankProductId;
	
	@JsonProperty("custName")
	private String custName;
	
	@JsonProperty("gender")
	private String gender;
	
	@JsonProperty("phone")
	private String phone;
	
	@JsonProperty("loanType")
	private String loanType;
	
	@JsonProperty("appId")
	private String appId;

	@JsonProperty("losIndicator")
	private String losIndicator;
	
	@JsonProperty("losIndex")
	private String losIndex;
	
	@JsonProperty("slNo")
	private String slNo;
	
	@JsonProperty("email")
	private String email;
	
	@JsonProperty("maritalstatus")
	private String maritalstatus;
	
	@JsonProperty("kendra")
	private String kendra;
	
	@JsonProperty("groupID")
	private String groupID;
	
	@JsonProperty("branch")
	private String branch;
	
	@JsonProperty("stateBranch")
	private String stateBranch;
	
	@JsonProperty("prospectID")
	private String prospectID;
	
	@JsonProperty("custId")
	private String custId;
	
	@JsonProperty("source")
	private String source;
	
	@JsonProperty("enquiryType")
	private String enquiryType;
	
	@JsonProperty("loanId")
	private String loanId;
	
	@JsonProperty("digiAgilDFAFlag")
	private String digiAgilDFAFlag;
	
	@JsonProperty("customerEnquiryFlag")
	private String customerEnquiryFlag;
	
	@JsonProperty("hhAnnualIncome")
	private String hhAnnualIncome;
	
	@JsonProperty("lastAssessedIncome")
	private String lastAssessedIncome;
	
	@JsonProperty("lastAssessedIncomeDate")
	private String lastAssessedIncomeDate;
	
	@JsonProperty("earningFlag")
	private String earningFlag;
	
	@JsonProperty("loanProductType")
	private String loanProductType;
	
	@JsonProperty("loanAmount")
	private String loanAmount;
	
	@JsonProperty("appliedTenure")
	private String appliedTenure;
	
	@JsonProperty("appliedFrequency")
	private String appliedFrequency;

	@JsonProperty("activationDate")
	private String activationDate;
	
	@JsonProperty("loanProductcode")
	private String loanProductcode;
	
	@JsonProperty("householdMember")
	private List<HouseholdMemberPayload> householdMember;

	@JsonProperty("caglOutstandingAmount")
	private String caglOutstandingAmount;

	@JsonProperty("maximumEligibleAmount")
	private String maximumEligibleAmount;

	@JsonProperty("lastEligibilityAssessmentDate")
	private String lastEligibilityAssessmentDate;

	@JsonProperty("creditLimit")
	private String creditLimit;
	
	@JsonProperty("creditLimitActivationDate")
	private String creditLimitActivationDate;
	
	@JsonProperty("creditLimitRefreshDate")
	private String creditLimitRefreshDate;
	
	@JsonProperty("creditLimitExpiryDate")
	private String creditLimitExpiryDate;
	
	@JsonProperty("ocrVoterIdName")
	private String ocrVoterIdName;
	
	@JsonProperty("ocrVoterRelationName")
	private String ocrVoterRelationName;
	
	@JsonProperty("ocrVoterRelationType")
	private String ocrVoterRelationType;
	
	@JsonProperty("ocrVoterIdAddress")
	private String ocrVoterIdAddress;
	
	@JsonProperty("aadharName")
	private String aadharName ;
	
	@JsonProperty("aadharAddress")
	private String aadharAddress;
	
	@JsonProperty("customerIncome")
	private String customerIncome;
	
	@JsonProperty("applicantSalariedBusinessHomemaker")
	private String applicantSalariedBusinessHomemaker;
	
	@JsonProperty("applicantOccupation")
	private String applicantOccupation;
	
	@JsonProperty("applicantEducationalQualification")
	private String applicantEducationalQualification;
	
	@JsonProperty("earningMembersInFamily")
	private String earningMembersInFamily ;
	
	@JsonProperty("earningMembersIncome")
	private String earningMembersIncome ;
	
	@JsonProperty("earningMembersSalariedBusiness")
	private String earningMembersSalariedBusiness ;
	
	@JsonProperty("earningMembersOccupation")
	private String earningMembersOccupation ;
	
	@JsonProperty("appliancesAtHomeVar1")
	private String appliancesAtHomeVar1 ;
	
	@JsonProperty("appliancesAtHomeVar2")
	private String appliancesAtHomeVar2;
	
	@JsonProperty("appliancesAtHomeVar3")
	private String appliancesAtHomeVar3 ;
	
	@JsonProperty("applicantInsurance")
	private String applicantInsurance;

	@JsonProperty("spouseInsurance")
	private String spouseInsurance;

	@JsonProperty("applicantInsuranceAmt")
	private String applicantInsuranceAmt;
	
	@JsonProperty("spouseInsuranceAmt")
	private String spouseInsuranceAmt;
	
	@JsonProperty("applicationId")
	private String applicationId;
	
	@JsonProperty("versionNo")
	private String versionNo;
	
	@JsonProperty("custDtlId")
	private String custDtlId;
	
	@JsonProperty("crtFlag")
	private String crtFlag;
	
	@JsonProperty("crtIdentifier")
	private String crtIdentifier;
	
	@JsonProperty("crtApprovedAmount")
	private String crtApprovedamount;
}