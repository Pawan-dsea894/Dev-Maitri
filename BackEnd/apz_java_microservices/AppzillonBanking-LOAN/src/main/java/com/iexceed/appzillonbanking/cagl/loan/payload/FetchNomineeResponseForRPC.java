package com.iexceed.appzillonbanking.cagl.loan.payload;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FetchNomineeResponseForRPC {
	
	@JsonProperty("appId")
	private String appId;
	
	@JsonProperty("applicationId")
	private String applicationId;
	
	@JsonProperty("latestVersionNo")
	private String latestVersionNo;
	
	@JsonProperty("amount")
	private BigDecimal amount;
	
	@JsonProperty("applicationDate")
	private LocalDate applicationDate;
	
	@JsonProperty("applicationRefNo")
	private String applicationRefNo;
	
	@JsonProperty("applicationStatus")
	private String applicationStatus;
	
	@JsonProperty("applicationType")
	private String applicationType;
	
	@JsonProperty("branchCode")
	private String branchCode;
	
	@JsonProperty("cbCheck")
	private String cbCheck;
	
	@JsonProperty("createTs")
	private String createTs;
	
	@JsonProperty("createdBy")
	private String createdBy;
	
	@JsonProperty("currentScreenId")
	private String currentScreenId;
	
	@JsonProperty("currentStage")
	private String currentStage;
	
	@JsonProperty("customerId")
	private String customerId;
	
	@JsonProperty("customerName")
	private String customerName;
	
	@JsonProperty("kendraId")
	private String kendraId;
	
	@JsonProperty("kendraName")
	private String kendraName;
	
	@JsonProperty("kmId")
	private String kmId;
	
	@JsonProperty("kycType")
	private String kycType;
	
	@JsonProperty("leader")
	private String leader;
	
	@JsonProperty("loanMode")
	private String loanMode;
	
	@JsonProperty("productCode")
	private String productCode;
	
	@JsonProperty("productGroupCode")
	private String productGroupCode;
	
	@JsonProperty("productshortDesc")
	private String productshortDesc;
	
	@JsonProperty("remarks")
	private String remarks;
	
	@JsonProperty("addInfo")
	private String addInfo;
	
	@JsonProperty("reqPayload")
	private String reqPayload;
	
	@JsonProperty("resPayload")
	private String resPayload;
	
	@JsonProperty("status")
	private String status;
	
	@JsonProperty("payload")
	private String payload;
	
	@JsonProperty("incCreatedBy")
	private String incCreatedBy;
	
	@JsonProperty("incCreateTs")
	private Timestamp incCreateTs;
	
	@JsonProperty("updatedBy")
	private String updatedBy;
	
	@JsonProperty("updateTs")
	private String updateTs;
	
	@JsonProperty("branchName")
	private String branchName;
	
	@JsonProperty("requestType")
	private String requestType;
		
}
