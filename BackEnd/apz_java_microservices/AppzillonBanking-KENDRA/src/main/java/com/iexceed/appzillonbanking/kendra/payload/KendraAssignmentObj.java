package com.iexceed.appzillonbanking.kendra.payload;


import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class KendraAssignmentObj {

	@JsonProperty("kendraAssignmentId")
	private String kendraAssignmentId;

	@JsonProperty("kmId")
	private String kmId;

	@JsonProperty("kendraId")
	private String kendraId;

	@JsonProperty("kmName")
	private String kmName;

	@JsonProperty("startDate")
	private Date startDate;

	@JsonProperty("endDate")
	private Date endDate;

	@JsonProperty("assignmentType")
	private String assignmentType;

	@JsonProperty("oldKmId")
	private String oldKmId;

	@JsonProperty("oldKmName")
	private String oldKmName;

	@JsonProperty("branchId")
	private String branchId;

	@JsonProperty("createdDate")
	private String createdDate;

	@JsonProperty("createdBy")
	private String createdBy;
	
	@JsonProperty("batchNo")
	private String batchNo;
	
	@JsonProperty("allocationType")
	private String allocationType;

	@JsonProperty("remarks")
	private String remarks;

	@JsonProperty("addInfo1")
	private String addInfo1;

	@JsonProperty("addInfo2")
	private String addInfo2;
}
