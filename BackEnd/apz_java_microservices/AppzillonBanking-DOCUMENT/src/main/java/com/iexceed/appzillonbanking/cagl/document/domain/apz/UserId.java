package com.iexceed.appzillonbanking.cagl.document.domain.apz;

import java.io.Serializable;
import jakarta.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserId implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Column(name = "APP_ID", nullable = false)
	private String appId;

	@Column(name = "USER_ID", nullable = false)
	private String userId;
}