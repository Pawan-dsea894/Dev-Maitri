package com.iexceed.appzillonbanking.cagl.collection.domain.ab;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRoleId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "USER_ID", nullable = false)
	private String userId;

	@Column(name = "ROLE_ID", nullable = false)
	private String roleId;

	@Column(name = "APP_ID", nullable = false)
	private String appId;

}
