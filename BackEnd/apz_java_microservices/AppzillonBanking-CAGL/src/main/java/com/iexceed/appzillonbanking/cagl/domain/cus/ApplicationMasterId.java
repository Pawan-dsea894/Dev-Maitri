package com.iexceed.appzillonbanking.cagl.domain.cus;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;
import java.io.Serializable;
import java.util.Objects;

@Getter @Setter
public class ApplicationMasterId implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(name = "APP_ID", nullable = false)
	private String appId;

	@Column(name = "APPLICATION_ID", nullable = false)
	private String applicationId;

	@Column(name = "LATEST_VERSION_NO", nullable = false)
	private Integer versionNum;

	@Override
	public int hashCode() {
		return Objects.hash(appId, applicationId, versionNum);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ApplicationMasterId other = (ApplicationMasterId) obj;
		return Objects.equals(appId, other.appId) && Objects.equals(applicationId, other.applicationId)
				&& Objects.equals(versionNum, other.versionNum);
	}
	
	
	
}