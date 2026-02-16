package com.iexceed.appzillonbanking.cagl.loan.payload;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class ApplicationDetailsDto {
	
	private String appId;
	private String applicationId;
	private Date applicationDate;
	private String applicationStatus;
	private String applicationType;
	private String branchCode;
	private String cbCheck;
	private Timestamp createTs;
	private String createdBy;
	private String currentScreenId;
	private String currentStage;
	private String customerId;
	private String kendraId;
	private String kendraName;
	private String kmid;
	private String kycType;
	private String leader;
	private String loanMode;
	private String productCode;
	private String productGroupCode;
	private String remarks;
	private String customerName;
	private BigDecimal amount;
	private String versionNo;
	private int workflowSeqNo;
	private String wfStatus;
	private Timestamp wfCreateTs;
	private String wfCreatedBy;
	private String queueStatus;
	private String addInfo;
	private String presentRole;

}
