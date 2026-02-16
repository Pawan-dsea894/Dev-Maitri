package com.iexceed.appzillonbanking.kendra.domain.cus;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_UACO_APPLICATION_MASTER")
@IdClass(ApplicationMasterId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationMaster {

	@Id
	private String appId;

	@Id
	private String applicationId;

	@Id
	private String versionNum;
	
	@Id
	private String kendraId;

	@JsonProperty("customerId")
	@Column(name = "CUSTOMER_ID")
	private String customerId;

	@JsonProperty("applicationDate")
	@Column(name = "APPLICATION_DATE")
	private LocalDate applicationDate;

	@CreationTimestamp
	@JsonProperty("createTs")
	@Column(name = "CREATE_TS")
	private Timestamp createTs;

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

	@JsonProperty("productGroupCode")
	@Column(name = "PRODUCT_GROUP_CODE")
	private String productGroupCode;

	@JsonProperty("productCode")
	@Column(name = "PRODUCT_CODE")
	private String productCode;

	@JsonProperty("branchCode")
	@Column(name = "BRANCH_CODE")
	private String branchCode;

	@JsonProperty("currentScreenId")
	@Column(name = "CURRENT_SCREEN_ID")
	private String currentScreenId;

	@JsonProperty("remarks")
	@Column(name = "REMARKS")
	private String remarks;

	@JsonProperty("cbCheck")
	@Column(name = "CB_CHECK")
	private String cbCheck;

	@JsonProperty("currentStage")
	@Column(name = "CURRENT_STAGE")
	private String currentStage;

	@JsonProperty("kmId")
	@Column(name = "KMID")
	private String kmId;

	@JsonProperty("leader")
	@Column(name = "LEADER")
	private String leader;

	@JsonProperty("kendraName")
	@Column(name = "KENDRANAME")
	private String kendraName;

	@JsonProperty("loanMode")
	@Column(name = "LOANMODE")
	private String loanMode;

	@JsonProperty("customerName")
	@Column(name = "CUSTOMER_NAME")
	private String customerName;

	@JsonProperty("amount")
	@Column(name = "AMOUNT")
	private BigDecimal amount;

	@JsonProperty("addInfo")
	@Column(name = "ADD_INFO")
	private String addInfo;

	@JsonProperty("applicationRefNo")
	@Column(name = "APPLICATION_REF_NO")
	private String applicationRefNo;

	@JsonProperty("cbApproveManual")
	@Column(name = "CB_APPROVE_MANUAL")
	private String cbApproveManual;
	
	@Transient
	private String errorType;
	
	@Transient
	private String errorCode;
	
	@Transient
	private String errorMessage;

}

