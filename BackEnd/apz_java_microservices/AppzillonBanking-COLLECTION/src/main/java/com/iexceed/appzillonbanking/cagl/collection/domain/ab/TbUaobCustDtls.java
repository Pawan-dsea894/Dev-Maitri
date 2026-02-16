package com.iexceed.appzillonbanking.cagl.collection.domain.ab;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_UAOB_CUSTOMER_DETAILS")
@IdClass(TbUaobCustDtlsId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TbUaobCustDtls {

	@Id
	private String custDtlId;
	
	@Id
	private String appId;
	
	@Id
	private String applicationId;
	
	@Id
	private String versionNo;
	
	@JsonProperty("kendraId")
	@Column(name = "KENDRA_ID")
	private String kendraId;

	@JsonProperty("groupId")
	@Column(name = "GROUP_ID")
	private String groupId;

	@JsonProperty("customerId")
	@Column(name = "CUSTOMER_ID")
	private String customerId;

	@JsonProperty("customerName")
	@Column(name = "CUSTOMER_NAME")
	private String customerName;
	
	@JsonProperty("eligibleLoanAmt")
	@Column(name = "ELIGIBLE_LOAN_AMOUNT")
	private String eligibleLoanAmt;

	@JsonProperty("kycDetails")
	@Column(name = "KYC_DETAILS")
	private String kycDetails;

	@JsonProperty("bankDtls")
	@Column(name = "BANK_DETAILS")
	private String bankDtls;
	
	@JsonProperty("payload")
	@Column(name = "PAYLOAD")
	private String payload;
}
