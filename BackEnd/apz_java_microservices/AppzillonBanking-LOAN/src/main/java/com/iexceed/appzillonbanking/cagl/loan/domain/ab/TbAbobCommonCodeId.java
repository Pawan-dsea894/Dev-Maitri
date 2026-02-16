package com.iexceed.appzillonbanking.cagl.loan.domain.ab;

import java.io.Serializable;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TbAbobCommonCodeId implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@Column(name = "CODE_TYPE", nullable = false)
	private String codeType;

	@Column(name = "CM_CODE", nullable = false)
	private String code;
	
}