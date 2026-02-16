package com.iexceed.appzillonbanking.cagl.collection.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDetails {

	@JsonProperty("appId")
	private String appId;

	@JsonProperty("applnId")
	private String applnId;

	@JsonProperty("verNo")
	private String verNo;

	@JsonProperty("kendraId")
	private String kendraId;

	@JsonProperty("brnCode")
	private String brnCode;

	@JsonProperty("applnDate")
	private String applnDate;

	@JsonProperty("createTs")
	private String createTs;

	@JsonProperty("createdBy")
	private String createdBy;

	@JsonProperty("appType")
	private String appType;

	@JsonProperty("appStatus")
	private String appStatus;

	@JsonProperty("currStage")
	private String currStage;

	@JsonProperty("kmId")
	private String kmId;

	@JsonProperty("leader")
	private String leader;

	@JsonProperty("kendraName")
	private String kendraName;

	@JsonProperty("amount")
	private String amount;

	@JsonProperty("refNo")
	private String refNo;

}
