package com.iexceed.appzillonbanking.cagl.loan.payload;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateAppRequestFields {

	@JsonProperty("customerId")
	private String customerId; 
    
	@JsonProperty("kendraId")
	private String kendraId;
    	
	@JsonProperty("kmId")
	private String kmId;
	
	@JsonProperty("applicationId")
	private String applicationId;

	@JsonProperty("versionNum")
	private Integer versionNum;

	@JsonProperty("applicationDate")
	private LocalDate applicationDate;

	@CreationTimestamp
	@JsonProperty("createTs")
	private LocalDateTime createTs;

	@JsonProperty("createdBy")
	private String createdBy;
	
	@JsonProperty("applicationType")
	private String applicationType;

	@JsonProperty("kycType")
	private String kycType;
	
	@JsonProperty("applicationStatus")
	private String applicationStatus;
	
	@JsonProperty("productGrpCode")
	private String productGrpCode;
	
	@JsonProperty("productCode")
	private String productCode;

	@JsonProperty("currentStage")
	private String currentStage;

	@JsonProperty("currentScrId")
	private String currentScrId;
		
	@JsonProperty("branchCode")
	private String branchCode;
	
	@JsonProperty("cbCheck")
	private String cbCheck;
	
}
