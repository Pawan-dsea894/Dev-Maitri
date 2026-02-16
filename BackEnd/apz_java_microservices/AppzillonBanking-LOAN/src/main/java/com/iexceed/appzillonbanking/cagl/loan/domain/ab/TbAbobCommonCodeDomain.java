package com.iexceed.appzillonbanking.cagl.loan.domain.ab;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_ABOB_COMMON_CODES")
@IdClass(TbAbobCommonCodeId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TbAbobCommonCodeDomain {
	
	@Id
	private String codeType;
	
	@Id
	private String code;
	
	@Column(name = "CODE_DESC")
	private String codeDesc;
	
	@Column(name = "LANGUAGE")
	private String language;
	
	@Column(name = "ACCESS_TYPE")
	private String accessType;

	

	@Override
	public String toString() {
		return "TbAbmiCommonCodeDomain [codeType=" + codeType + ", code=" + code + ", codeDesc=" + codeDesc
				+ ", language=" + language + ", accessType=" + accessType + "]";
	}
}
