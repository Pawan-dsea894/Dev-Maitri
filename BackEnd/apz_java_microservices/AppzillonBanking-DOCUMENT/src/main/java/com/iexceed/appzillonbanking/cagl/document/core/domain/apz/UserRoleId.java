package com.iexceed.appzillonbanking.cagl.document.core.domain.apz;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.io.Serializable;

@Getter @Setter
public class UserRoleId implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name = "USER_ID", nullable = false)
	private String userId;
	
	@Column(name = "ROLE_ID", nullable = false)
	private String roleId;
	
	@Column(name = "APP_ID", nullable = false)
	private String appId;
}