package com.iexceed.appzillonbanking.cagl.document.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDtls {
     
    @JsonProperty("refId")
  	private String refId;
	
	@JsonProperty("applicationId")
	private String applicationId;
	
	@JsonProperty("versionNo")
 	private String versionNo;
	
 	@JsonProperty("applicationDate")
 	private String applicationDate;

 	@JsonProperty("createTs")
 	private Timestamp createTs;

 	@JsonProperty("modifyTs")
 	private Timestamp modifyTs;
 	
 	@JsonProperty("createdBy")
 	private String createdBy;

 	@JsonProperty("updatedBy")
 	private String updatedBy;
 	
 	@JsonProperty("applicationType")
 	private String applicationType;

 	@JsonProperty("kycType")
 	private String kycType;

 	@JsonProperty("status")
 	private String status;

 	@JsonProperty("productGroupCode")
 	private String productGroupCode;

 	@JsonProperty("productCode")
 	private String productCode;

 	@JsonProperty("productType")
 	private String productType;
 	
 	@JsonProperty("currentScreenId")
 	private String currentScreenId;

 	@JsonProperty("remarks")
 	private String remarks;

 	@JsonProperty("cbCheck")
 	private String cbCheck;

 	@JsonProperty("currentStage")
 	private String currentStage;
  	
	@JsonProperty("kendraId")
	private String kendraId;

	@JsonProperty("groupId")
	private String groupId;
	
	@JsonProperty("kmId")
	private String kmId;
	
	@JsonProperty("kendraName")
	private String kendraName;

	@JsonProperty("branchId")
	private String branchId;

	@JsonProperty("customerId")
	private String customerId;

 	@JsonProperty("leader")
 	private String leader;
 	
 	@JsonProperty("customerName")
	private String customerName;
	
	@JsonProperty("amount")
	private BigDecimal amount;
	
    @JsonProperty("loanMode")
  	private String loanMode;
 	
	@JsonProperty("customerDtls")
	private CustomerDtls customerDetails;
	
	@JsonProperty("addInfo")
	private Object addInfo;
	
	@JsonProperty("appVersion")
	private String appVersion;
	
	@JsonProperty("activeLoanCount")
	private String activeLoanCount;
	
	@JsonProperty("outstandingPrincipal")
	private String outstandingPrincipal;
	
	@JsonProperty("outstandingInterest")
	private String outstandingInterest;
	
	@JsonProperty("kendrafrequency")
	private String kendrafrequency;
	
	@JsonProperty("meetingday")
	private String meetingday;
	
	@JsonProperty("loanfrequency")
	private String loanfrequency;
	
}
