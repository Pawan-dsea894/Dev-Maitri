package com.iexceed.appzillonbanking.cagl.loan.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUaobCbResponse;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.WorkflowDefinition;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplyLoanRequestFields {

	@JsonProperty("applicationdtls")
	private List<ApplicationDtls> applicationdtls;
	
	@JsonProperty("applnWfDefinitionList")
	private List<WorkflowDefinition> applnWfDefinitionList;

	@JsonProperty("cbResponse")
	private TbUaobCbResponse cbResponse;
	
	@JsonProperty("precloserLoanResponse")
	private PrecloserLoanResponse precloserLoanResponse;
	
	@JsonProperty("userRole")
	private String userRole;
	
	@JsonProperty("userName")
	private String userName;
	
	@JsonProperty("appVersion")
	private String appVersion;
	
	@JsonProperty("remarks")
	private String remarks;
	
	@JsonProperty("userId")
	private String userId;
		
}