package com.iexceed.appzillonbanking.core.domain.ab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TbAbmiCommonCodeId implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(name = "CODE_TYPE", nullable = false)
	private String codeType;

	@Column(name = "CM_CODE", nullable = false)
	private String code;
	
	@Column(name = "LANGUAGE", nullable = false)
	private String language;
	
	@Column(name = "CHANNEL", nullable = false)
	private String channel;
	
}
