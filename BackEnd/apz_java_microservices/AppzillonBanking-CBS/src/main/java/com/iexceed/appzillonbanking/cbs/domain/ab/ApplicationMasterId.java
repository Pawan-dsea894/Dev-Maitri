package com.iexceed.appzillonbanking.cbs.domain.ab;

import java.io.Serializable;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationMasterId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "APP_ID", nullable = false)
	private String appId;

	@Column(name = "APPLICATION_ID", nullable = false)
	private String applicationId;

	@Column(name = "LATEST_VERSION_NO", nullable = false)
	private String versionNum;

}