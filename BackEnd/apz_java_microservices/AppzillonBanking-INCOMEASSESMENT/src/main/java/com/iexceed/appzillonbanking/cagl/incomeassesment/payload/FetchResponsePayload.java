package com.iexceed.appzillonbanking.cagl.incomeassesment.payload;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@AllArgsConstructor
public class FetchResponsePayload {

	// Fields from ApplicationMaster
	private String appId;
	private String applicationId;
	private String latestVersionNo;
	private BigDecimal amount;
	private LocalDate applicationDate;
	private String applicationRefNo;
	private String applicationStatus;
	private String applicationType;
	private String branchCode;
	private String cbCheck;
	private Timestamp createTs;
	private String createdBy;
	private String currentScreenId;
	private String currentStage;
	private String customerId;
	private String customerName;
	private String kendraId;
	private String kendraName;
	private String kmId;
	private String kycType;
	private String leader;
	private String loanMode;
	private String productCode;
	private String productGroupCode;
	private String productshortDesc;
	private String remarks;
	private String addInfo;

	// Fields from TbUaobCbResponse
	private String reqPayload;
	private String resPayload;
	private String status;

	// Fields from TbUaobIncomeAssessment
	private String payload;

	private String incCreatedBy;

	private Timestamp incCreateTs;

	private String updatedBy;

	private Timestamp updateTs;

}
