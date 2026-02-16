package com.iexceed.appzillonbanking.cbs.domain.ab;

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
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_UACO_APPLICATION_MASTER")
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
	private String versionNum;

	@JsonProperty("kendraId")
	@Column(name = "KENDRA_ID")
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

	@JsonProperty("amount")
	@Column(name = "AMOUNT")
	private BigDecimal amount;
	
}