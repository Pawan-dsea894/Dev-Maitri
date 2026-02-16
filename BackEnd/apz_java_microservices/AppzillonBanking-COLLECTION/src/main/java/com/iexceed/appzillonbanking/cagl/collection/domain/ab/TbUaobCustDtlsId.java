package com.iexceed.appzillonbanking.cagl.collection.domain.ab;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;

public class TbUaobCustDtlsId implements Serializable {

	private static final long serialVersionUID = 1L;

	@JsonProperty("custDtlId")
	@Column(name = "CUST_DTL_ID")
	private String custDtlId;
	
	@JsonProperty("appId")
	@Column(name = "APP_ID")
	private String appId;
	
	@Column(name = "APPLICATION_ID")
	private String applicationId;
	
	@Column(name = "VERSION_NO")
	private String versionNo;
}
