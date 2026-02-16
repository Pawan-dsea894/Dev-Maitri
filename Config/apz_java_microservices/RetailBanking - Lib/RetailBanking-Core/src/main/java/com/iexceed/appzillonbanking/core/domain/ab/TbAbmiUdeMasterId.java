package com.iexceed.appzillonbanking.core.domain.ab;

import java.io.Serializable;

import jakarta.persistence.Column;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TbAbmiUdeMasterId implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Column(name="APP_ID")
	private String appId;
	
	@Column(name="MODULE")
	private String module;
	
	@Column(name="UDE_NAME")
	private String udeName;
}
