package com.iexceed.appzillonbanking.kendra.domain.cus;

import java.io.Serializable;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class TbAshsUserId implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Column(name = "APP_ID", nullable = false)
	private String appId;

	@Column(name = "USER_ID", nullable = false)
	private String userId;
	
	@Column(name = "VERSION_NO", nullable = false)
	private int versionNum;
}