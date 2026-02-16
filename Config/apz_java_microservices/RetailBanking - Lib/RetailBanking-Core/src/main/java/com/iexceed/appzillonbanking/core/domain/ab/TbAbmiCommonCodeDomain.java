package com.iexceed.appzillonbanking.core.domain.ab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Entity
@Table(name = "TB_ABMI_COMMON_CODES")
@IdClass(TbAbmiCommonCodeId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TbAbmiCommonCodeDomain {

	@Id
	private String codeType;

	@Id
	private String code;

	@Id
	private String channel;

	@Id
	private String language;

	@Column(name = "CODE_DESC")
	private String codeDesc;

	@Column(name = "ACCESS_TYPE")
	private String accessType;

}
