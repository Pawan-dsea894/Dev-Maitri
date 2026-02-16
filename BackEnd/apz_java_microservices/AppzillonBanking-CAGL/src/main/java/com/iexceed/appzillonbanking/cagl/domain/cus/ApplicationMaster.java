package com.iexceed.appzillonbanking.cagl.domain.cus;

import java.time.LocalDate;
import java.time.LocalDateTime;


import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor; 

@Entity
@Table(name = "TB_ABOB_APPLICATION_MASTER")
@IdClass(ApplicationMasterId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationMaster {

	@Id
	private String appId;

	@Id
	private String applicationId;

	@Id
	private Integer versionNum;

	@JsonProperty("applicationDate")
	@Column(name = "APPLICATION_DATE")
	private LocalDate applicationDate;

	@CreationTimestamp
	@JsonProperty("createTs")
	@Column(name = "CREATE_TS")
	private LocalDateTime createTs;

	@JsonProperty("kendraId")
	@Column(name = "KENDRA_ID")
	private String kendraId;

	@JsonProperty("createdBy")
	@Column(name = "CREATED_BY")
	private String createdBy;

	@JsonProperty("applicationType")
	@Column(name = "APPLICATION_TYPE")
	private String applicationType;

	@JsonProperty("kycType")
	@Column(name = "KYC_TYPE")
	private String kycType;

	@JsonProperty("applicationStatus")
	@Column(name = "APPLICATION_STATUS")
	private String applicationStatus;

	@JsonProperty("customerId")
	@Column(name = "CUSTOMER_ID")
	private String customerId;

	@JsonProperty("mobileNumber")
	@Column(name = "MOBILE_NUMBER")
	private String mobileNumber;

	@JsonProperty("nationalId")
	@Column(name = "NATIONAL_ID")
	private String nationalId;

	@JsonProperty("pan")
	@Column(name = "PAN")
	private String pan;

	@JsonProperty("productGroupCode")
	@Column(name = "PRODUCT_GROUP_CODE")
	private String productGroupCode;

	@JsonProperty("productCode")
	@Column(name = "PRODUCT_CODE")
	private String productCode;

	@JsonProperty("searchCode1")
	@Column(name = "SEARCH_CODE1")
	private String searchCode1;

	@JsonProperty("searchCode2")
	@Column(name = "SEARCH_CODE2")
	private String searchCode2;

	@JsonProperty("assignedTo")
	@Column(name = "ASSIGNED_TO")
	private String assignedTo;

	@JsonProperty("emailId")
	@Column(name = "EMAILID")
	private String emailId;

	@JsonProperty("currentStage")
	@Column(name = "CURRENT_STAGE")
	private String currentStage;

	@JsonProperty("declarationFlag")
	@Column(name = "DECLARATION_FLAG")
	private String declarationFlag;

	@JsonProperty("accNumber")
	@Column(name = "ACCOUNT_NUMBER")
	private String accNumber;

	@Column(name = "MOBILE_VER_STATUS")
	private String mobileVerStatus;

	@Column(name = "EMAIL_VER_STATUS")
	private String emailVerStatus;

	@JsonProperty("currentScreenId")
	@Column(name = "CURRENT_SCREEN_ID")
	private String currentScreenId;

	@JsonProperty("remarks")
	@Column(name = "REMARKS")
	private String remarks;

	@JsonProperty("applicantsCount")
	@Column(name = "NUM_OF_APPLICANTS")
	private Integer applicantsCount;

	@JsonProperty("relatedApplicationId")
	@Column(name = "RELATED_APPLICATION_ID")
	private String relatedApplicationId;

}