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
public class CashDepositPoints {

	@JsonProperty("appId")
	private String appId;

	@JsonProperty("applicationId")
	private String applicationId;

	@JsonProperty("versionNo")
	private String versionNo;

	@JsonProperty("kmId")
	private String kmId;

	@JsonProperty("depPointRefNo")
	private String depPointRefNo;

	@JsonProperty("createTs")
	private String createTs;

	@JsonProperty("base64FilePath")
	private String base64FilePath;
	
	@JsonProperty("depAmt")
	private int depAmt;

	@JsonProperty("depPoint")
	private String depPoint;
	
	@JsonProperty("fileType")
	private String fileType;
}
