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
public class EditOrDeleteKendraAssignmentRequestFields {
	
	@JsonProperty("action")
	private String action;
	
	@JsonProperty("batchNo")
	private String batchNo;
	
	@JsonProperty("startDate")
	private Date startDate;
	
	@JsonProperty("endDate")
	private Date endDate;

}
