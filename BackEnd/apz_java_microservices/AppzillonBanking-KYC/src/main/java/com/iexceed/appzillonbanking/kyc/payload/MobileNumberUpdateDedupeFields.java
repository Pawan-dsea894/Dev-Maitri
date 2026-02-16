package com.iexceed.appzillonbanking.kyc.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MobileNumberUpdateDedupeFields {

	@JsonProperty("id")
	private String id;

	@JsonProperty("gkv")
	private String gkv;

	@JsonProperty("method")
	private String method;

	@JsonProperty("customerId")
	private String customerId;

	@JsonProperty("custqualify")
	private String custqualify;

	@JsonProperty("kendraId")
	private String kendraId;

	@JsonProperty("groupId")
	private String groupId;

	@JsonProperty("name")
	private String name;

	@JsonProperty("recordtype")
	private String recordtype;

	@JsonProperty("phoneNum1")
	private String phoneNum1;

	@JsonProperty("spkycid")
	private String spkycid;

	@JsonProperty("spkycname")
	private String spkycname;

	@JsonProperty("bankAccNo")
	private String bankAccNo;

	@JsonProperty("bankname")
	private String bankname;

	@JsonProperty("bankBranchName")
	private String bankBranchName;

	@JsonProperty("ifscCode")
	private String ifscCode;

	@JsonProperty("accHolderName")
	private String accHolderName;

	@JsonProperty("cb_status")
	private String cb_status;

	@JsonProperty("branchId")
	private String branchId;

	@JsonProperty("customerName")
	private String customerName;
	
	@JsonProperty("appid")
	private String appid;

}
