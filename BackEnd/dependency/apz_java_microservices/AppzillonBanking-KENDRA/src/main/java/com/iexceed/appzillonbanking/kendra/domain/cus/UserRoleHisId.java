package com.iexceed.appzillonbanking.kendra.domain.cus;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Embeddable
public class UserRoleHisId implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Column(name = "user_id", nullable = false)
	private String userId;

	@Column(name = "role_id", nullable = false)
	private String roleId;

	@Column(name = "app_id", nullable = false)
	private String appId;

	@Column(name = "version_no", nullable = false)
	private Integer versionNo;
}
