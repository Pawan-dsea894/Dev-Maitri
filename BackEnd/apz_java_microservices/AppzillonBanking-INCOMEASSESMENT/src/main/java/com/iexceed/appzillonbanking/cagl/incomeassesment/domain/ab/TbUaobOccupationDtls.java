package com.iexceed.appzillonbanking.cagl.incomeassesment.domain.ab;

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
@Table(name = "TB_UAOB_OCCUPATION_DETAILS")
@IdClass(TbUaobOccptDtlsId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TbUaobOccupationDtls {

	@Id
	private String occuPtDtlsId;

	@JsonProperty("appId")
	@Column(name = "APP_ID")
	private String appId;

	@JsonProperty("applicationId")
	@Column(name = "APPLICATION_ID")
	private String applicationId;

	@JsonProperty("versionNo")
	@Column(name = "VERSION_NO")
	private String versionNo;

	@JsonProperty("custDtlId")
	@Column(name = "CUST_DTL_ID")
	private String custDtlId;

	@JsonProperty("payload")
	@Column(name = "PAYLOAD")
	private String payload;
	
	@JsonProperty("incomePayload")
	@Column(name = "INCOME_PAYLOAD")
	private String incomePayload;
	
	@JsonProperty("earningsPayload")
	@Column(name = "EARNINGS_PAYLOAD")
	private String earningsPayload;
}
