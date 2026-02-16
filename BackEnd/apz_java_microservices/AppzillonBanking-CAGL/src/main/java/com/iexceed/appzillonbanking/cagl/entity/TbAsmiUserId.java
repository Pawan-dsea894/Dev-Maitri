package com.iexceed.appzillonbanking.cagl.entity;

import java.io.Serializable;

import jakarta.persistence.Column;

public class TbAsmiUserId implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Column(name = "APP_ID", nullable = false)
	private String appId;

	@Column(name = "USER_ID", nullable = false)
	private String userId;

}
