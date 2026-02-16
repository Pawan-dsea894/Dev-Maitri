package com.iexceed.appzillonbanking.cagl.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gk_unified_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GkUnifiedData {
	
	
	 	@Id
	    @Column(name = "ID")
	    private String id;
	 
	    @Column(name = "CUSTOMERID")
	    private String customerId;
	    
	    @Column(name = "KENDRAID")
	    private String kendraId;
	   
	    @Column(name = "loan_id")
	    private String loanId;
	    
	    @Column(name = "activation_date")
	    private String activationDate;

	    @Column(name = "ADDRESS")
	    private String address;

	    @Column(name = "BANKACCOUNTNAME")
	    private String bankAccountName;

	    @Column(name = "BANKACNO")
	    private String bankAccountNumber;

	    @Column(name = "BANKBRANCHNAME")
	    private String bankBranchName;

	    @Column(name = "BANKIFSCCODE")
	    private String bankIfscCode;

	    @Column(name = "BANKNAME")
	    private String bankName;

	   // @Column(name = "BRANCHID")
	   // private String branchId;

	    @Column(name = "BRANCHNAME")
	    private String branchName;

	   // @Column(name = "CASTE")
	   // private String caste;

	   // @Column(name = "CITY")
	   // private String city;

	    @Column(name = "COMMUNICATION_ADDRESS_LINE1")
	    private String communicationAddressLine1;

	    @Column(name = "COMMUNICATION_ADDRESS_LINE2")
	    private String communicationAddressLine2;

	    @Column(name = "COMMUNICATION_DISTRICT")
	    private String communicationDistrict;

	    @Column(name = "COMMUNICATION_PINCODE")
	    private String communicationPincode;

	    @Column(name = "COMMUNICATION_STATE")
	    private String communicationState;

	    @Column(name = "COMMUNICATION_TALUK")
	    private String communicationTaluk;

	    @Column(name = "COMMUNICATION_VILLAGE_LOCALITY")
	    private String communicationVillageLocality;

	    @Column(name = "CUST_QUALIFY")
	    private String customerQualification;

	    @Column(name = "CUSTSTATUS")
	    private String customerStatus;

	    @Column(name = "cust_vintage")
	    private String customerVintage;

	    @Column(name = "CUSTOMERNAME")
	    private String customerName;

	    @Column(name = "DEPDOB")
	    private String dependentDob;

	    @Column(name = "DEPDOCID")
	    private String dependentDocId;

	    @Column(name = "DEPDOCTYPE")
	    private String dependentDocType;

	    @Column(name = "DEPNAME")
	    private String dependentName;

	    @Column(name = "DOB")
	    private String dob;

	    @Column(name = "gender")
	    private String gender;

	    @Column(name = "GROUPID")
	    private String groupId;

	    //@Column(name = "KENDRANAME")
	    //private String kendraName;

	    @Column(name = "MARITALSTATUS")
	    private String maritalStatus;

	    @Column(name = "MEM_RELATION")
	    private String memberRelation;

	    @Column(name = "MOBILE_NUMBER")
	    private String mobileNumber;

	    @Column(name = "PERMANENT_ADDRESS_LINE1")
	    private String permanentAddressLine1;

	    @Column(name = "PERMANENT_ADDRESS_LINE2")
	    private String permanentAddressLine2;

	    @Column(name = "PERMANENT_DISTRICT")
	    private String permanentDistrict;

	    @Column(name = "PERMANENT_PINCODE")
	    private String permanentPincode;

	    @Column(name = "PERMANENT_STATE")
	    private String permanentState;

	    @Column(name = "PERMANENT_TALUK")
	    private String permanentTaluk;

	    @Column(name = "PERMANENT_VILLAGE_LOCALITY")
	    private String permanentVillageLocality;

	   // @Column(name = "PINCODE")
	   // private String pincode;

	    @Column(name = "PRIMARYID")
	    private String primaryId;

	    @Column(name = "PRIMARYTYPE")
	    private String primaryType;

	   // @Column(name = "RECORD_TYPE")
	   // private String recordType;

	  //  @Column(name = "RELIGION")
	   // private String religion;

	   // @Column(name = "STATE")
	   // private String state;

	   // @Column(name = "VILLAGE")
	   // private String village;

	    @Column(name = "amount")
	    private String amount;

	    @Column(name = "approved_amt")
	    private String approvedAmount;

	    @Column(name = "freq")
	    private String frequency;

	    @Column(name = "interest_rate")
	    private String interestRate;

	    @Column(name = "ln_mat_date")
	    private String loanMaturityDate;

	    @Column(name = "ln_Value_Date")
	    private String loanValueDate;

	    @Column(name = "outstanding_principal")
	    private String outstandingPrincipal;

	    @Column(name = "overdue_status")
	    private String overdueStatus;

	    @Column(name = "overdue_interest")
	    private String overdueInterest;

	    @Column(name = "overdue_principal")
	    private String overduePrincipal;

	    @Column(name = "product")
	    private String product;

	    @Column(name = "status")
	    private String status;

	    @Column(name = "term")
	    private String term;

	    @Column(name = "Eligible_CAGL_AMT")
	    private String eligibleCaglAmount;

	    @Column(name = "Overall_CB_Eligible_amount")
	    private String overallCbEligibleAmount;

	    @Column(name = "Eligible_CAGL_Product")
	    private String eligibleCaglProduct;

	    @Column(name = "REC_ID")
	    private String recordId;

	   // @Column(name = "AUDIT_DATE_TIME")
	   // private String auditDateTime;

	   // @Column(name = "AUDITOR_CODE")
	   // private String auditorCode;

	   // @Column(name = "AUTHORISER")
	   // private String authoriser;

	   // @Column(name = "CO_CODE")
	   // private String coCode;

	   // @Column(name = "CURR_NO")
	   // private String currentNumber;

	   // @Column(name = "DATE_TIME")
	   // private String dateTime;

	   // @Column(name = "DEPT_CODE")
	   // private String departmentCode;

	    @Column(name = "DOBE")
	    private String dobe;

	  //  @Column(name = "EFZ_LOAD_DATE")
	   // private String efzLoadDate;

	  //  @Column(name = "INCOME_FLAG")
	  //  private String incomeFlag;

	    @Column(name = "LEGAL_ID")
	    private String legalId;

	    @Column(name = "LEGAL_DOC_NAME")
	    private String legalDocumentName;

	    @Column(name = "MEM_RELATIONE")
	    private String memberRelationE;

	    @Column(name = "NAME")
	    private String name;

	   // @Column(name = "OVERRIDE")
	   // private String override;

	   // @Column(name = "T24_LOAD_DATE")
	   // private String t24LoadDate;

	   // @Column(name = "INPUTTER")
	   // private String inputter;

	   // @Column(name = "PRODUCT_ID")
	   // private String productId;

	   // @Column(name = "AMOUNT_DEFAULT")
	   // private String amountDefault;

	    //@Column(name = "AMOUNT_LIMIT")
	    //private String amountLimit;

	    //@Column(name = "AMOUNT_MAX")
	    //private String amountMax;

	    //@Column(name = "AMOUNT_MIN")
	    //private String amountMin;

	    //@Column(name = "AMOUNT_MULTIPLE")
	    //private String amountMultiple;

	    //@Column(name = "DESCRIPTION")
	    //private String description;

	   // @Column(name = "DISBURSEMENT_TYPE")
	   // private String disbursementType;

	   // @Column(name = "INSURANCE_PROVIDER")
	   // private String insuranceProvider;

	   // @Column(name = "loan_prod_type")
	   // private String loanProductType;

	   // @Column(name = "MAX_MEM_AGE")
	   // private String maxMemberAge;

	   // @Column(name = "MAX_SP_AGE")
	   // private String maxSpouseAge;

	   // @Column(name = "MIN_MEM_AGE")
	   // private String minMemberAge;

	   // @Column(name = "MIN_SP_AGE")
	   // private String minSpouseAge;

	   // @Column(name = "OS_VALIDATION")
	   // private String osValidation;

	   // @Column(name = "OS_VAL_AMOUNT")
	   // private String osValidationAmount;

	    @Column(name = "product_type")
	    private String productType;

	   // @Column(name = "product_status")
	   // private String productStatus;

	   // @Column(name = "SHORT_DESCRIPTION")
	   // private String shortDescription;

	  //  @Column(name = "SPOUSE_INSURANCE")
	  //  private String spouseInsurance;

	  //  @Column(name = "YEAR")
	  //  private String year;

	    @Column(name = "ASSESMENT_DATE")
	    private String assessmentDate;

	  //  @Column(name = "CB_EMI")
	  //  private String cbEmi;

	 //   @Column(name = "ELG_EMI")
	  //  private String eligibleEmi;

	  //  @Column(name = "QA_FLAG")
	  //  private String qaFlag;

	 //   @Column(name = "RECORD_STATUS")
	  //  private String recordStatus;

	  //  @Column(name = "STATUS_FLAG")
	  //  private String statusFlag;

	    @Column(name = "TOT_EXPENSES")
	    private String totalExpenses;

	    @Column(name = "TOT_INCOME")
	    private String totalIncome;
	    
	    
	    // New Colums for Passbook
	    
	    @Column(name = "LoanPurpose")
	    private String loanPurpose;
	    
	    @Column(name = "pf")
	    private String pf;
	    
	    @Column(name = "GST")
	    private String GST;
	    
	    @Column(name = "mem_insu")
	    private String mem_insu;
	    
	    @Column(name = "sp_insu")
	    private String sp_insu;
	    
	    @Column(name = "APR")
	    private String APR;	    
	    
}
