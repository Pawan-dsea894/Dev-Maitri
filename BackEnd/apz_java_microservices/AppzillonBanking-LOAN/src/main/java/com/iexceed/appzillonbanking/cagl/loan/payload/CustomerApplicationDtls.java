package com.iexceed.appzillonbanking.cagl.loan.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomerApplicationDtls {
	
	    @JsonProperty("refId")
	  	private String refId;
	
	    @JsonProperty("appId")
	  	private String appId;
		
		@JsonProperty("applicationId")
		private String applicationId;
		
		@JsonProperty("latestVersionNo")
	 	private String latestVersionNo;

		@JsonProperty("amount")
	 	private String amount;
		
		@JsonProperty("applicationDate")
	 	private String applicationDate;
		
		@JsonProperty("applicationRefNo")
	 	private String applicationRefNo;
		
		@JsonProperty("kendraId")
	 	private String kendraId;
		
		@JsonProperty("customerId")
	 	private String customerId;

		@JsonProperty("createTs")
		@JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
		private String createTs;
		
	 	@JsonProperty("updated_ts")
	 	private String updated_ts;
	 	
	 	@JsonProperty("createdBy")
	 	private String createdBy;

	 	@JsonProperty("updated_by")
	 	private String updated_by;
	 	
	 	@JsonProperty("applicationType")
	 	private String applicationType;

	 	@JsonProperty("kycType")
	 	private String kycType;
	 	
	 	@JsonProperty("applicationStatus")
	 	private String applicationStatus;
	 	
	 	@JsonProperty("branchCode")
	 	private String branchCode;
	 	
	 	@JsonProperty("branchName")
	 	private String branchName;

	 	@JsonProperty("cbCheck")
	 	private String cbCheck;
	 	
	 	@JsonProperty("currentScreenId")
	 	private String currentScreenId;

	 	@JsonProperty("currentStage")
	 	private String currentStage;

	 	@JsonProperty("remarks")
	 	private String remarks; 	
	 	
	 	@JsonProperty("productCode")
	 	private String productCode;
	 	
	 	@JsonProperty("productGroupCode")
	 	private String productGroupCode;
	 	
	 	@JsonProperty("productshortDesc")
	 	private String productshortDesc;
		
		@JsonProperty("kmId")
		private String kmId;

		@JsonProperty("kendraName")
		private String kendraName;

		@JsonProperty("customerName")
		private String customerName;
								
		@JsonProperty("addInfo")
		private JsonNode addInfo;
		
		@JsonProperty("add_info1")
		private JsonNode add_info1;
		
		@JsonProperty("loan_application_no")
		private String loan_application_no;

		@JsonProperty("status")
		private String status;

		@JsonProperty("requestType")
		private String requestType;
		
		@JsonProperty("nomineeDetails")
		private NomineeDetails nomineeDetails;
			
}
