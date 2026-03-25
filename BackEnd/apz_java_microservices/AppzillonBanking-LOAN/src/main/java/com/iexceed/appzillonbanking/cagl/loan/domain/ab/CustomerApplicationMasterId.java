package com.iexceed.appzillonbanking.cagl.loan.domain.ab;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerApplicationMasterId {
	
	private static final long serialVersionUID = 1L;

	@Column(name = "app_id", nullable = false)
	private String app_id;

	@Column(name = "application_id", nullable = false)
	private String application_id;

	@Column(name = "latest_version_no", nullable = false)
	private String latest_version_no;

}
