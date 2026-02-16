package com.iexceed.appzillonbanking.kendra.domain.cus;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.Column;

import java.io.Serializable;

@Getter @Setter
public class TbAsmiUserRoleId implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name = "USER_ID", nullable = false)
	private String userId;
	
	@Column(name = "ROLE_ID", nullable = false)
	private String roleId;
	
	@Column(name = "APP_ID", nullable = false)
	private String appId;
}