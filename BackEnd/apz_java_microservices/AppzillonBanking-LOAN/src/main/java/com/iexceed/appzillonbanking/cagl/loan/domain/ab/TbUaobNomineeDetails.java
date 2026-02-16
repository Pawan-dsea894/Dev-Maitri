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
@Table(name = "TB_UAOB_NOMINEE_DETAILS")
@IdClass(TbUaobNomineeDetailsId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TbUaobNomineeDetails {
	
	@Id
	private String nomineeDtlsId;
	
	@JsonProperty("custDtlId")
	@Column(name = "CUST_DTL_ID")
	private String custDtlId;
	
	@JsonProperty("appId")
	@Column(name = "APP_ID")
	private String appId;
	
	@JsonProperty("applicationId")
	@Column(name = "APPLICATION_ID")
	private String applicationId;
	
	@JsonProperty("versionNo")
	@Column(name = "VERSION_NO")
	private String versionNo;

	@JsonProperty("payload")
	@Column(name = "PAYLOAD")
	private String payload;
	
	@JsonProperty("status")
	@Column(name = "STATUS")
	private String status;
	
}
