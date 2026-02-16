package com.iexceed.appzillonbanking.cagl.loan.bulkupload.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cagl.loan.payload.ApplyLoanRequest;
import com.iexceed.appzillonbanking.cagl.loan.payload.ApplyLoanRequestFields;
import com.iexceed.appzillonbanking.cagl.loan.payload.ApplyLoanRequestWrapper;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkUploadRequest {

	@JsonProperty("appId")
	private String appId;

	@JsonProperty("interfaceName")
	private String interfaceName;

	@JsonProperty("userId")
	private String userId;

	@JsonProperty("requestObj")
	private BulkUploadRequestFields requestObj;
}
