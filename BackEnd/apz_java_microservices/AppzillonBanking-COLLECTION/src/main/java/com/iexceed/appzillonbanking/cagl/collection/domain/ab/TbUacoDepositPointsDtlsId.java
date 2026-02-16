package com.iexceed.appzillonbanking.cagl.collection.domain.ab;

import java.io.Serializable;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TbUacoDepositPointsDtlsId implements Serializable {

	private static final long serialVersionUID = 1L;

	
	@Column(name = "APP_ID")
	private String appId;

	@Column(name = "APPLICATION_ID")
	private String applicationId;

	@Column(name = "LATEST_VERSION_NO")
	private String versionNo;

	@Column(name = "REF_NO")
	private String refNo;

	@Column(name = "KM_ID")
	private String kmId;
}
