package com.iexceed.appzillonbanking.cagl.loan.domain.ab;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_UAOB_ADDRESS_DETAILS")
@IdClass(TbUaobAddressDetailsId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TbUaobAddressDetails {

	@Id
	private String addressDtlId;

	@JsonProperty("appId")
	@Column(name = "APP_ID")
	private String appId;

	@JsonProperty("applicationId")
	@Column(name = "APPLICATION_ID")
	private String applicationId;

	@JsonProperty("kendraId")
	@Column(name = "VERSION_NO")
	private String versionNum;

	@JsonProperty("kendraId")
	@Column(name = "KENDRA_ID")
	private String kendraId;

	@JsonProperty("customerId")
	@Column(name = "CUSTOMER_ID")
	private String customerId;

	@JsonProperty("custDtlId")
	@Column(name = "CUST_DTL_ID")
	private String custDtlId;

	@JsonProperty("payload")
	@Column(name = "PAYLOAD")
	private String payload;

	@JsonProperty("addressType")
	@Column(name = "ADDRESS_TYPE")
	private String addressType;
}
