package com.iexceed.appzillonbanking.cagl.loan.domain.ab;

import java.io.Serializable;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RoleAccessMapId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "APP_ID", nullable = false)
	private String appId;

	@Column(name = "ROLE_ID", nullable = false)
	private String roleId;
}