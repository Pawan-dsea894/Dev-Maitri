package com.iexceed.appzillonbanking.core.domain.ab;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_ABMI_LOV_MASTER")
@IdClass(TbAbmiLovMasterId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TbAbmiLovMaster {

	@Id
	private int lovId;

	@Id
	private String appId;

	@Column(name = "LOV_NAME")
	private String lovName;

	@Column(name = "LOV_DTLS")
	private String lovDtls;
	
	@Column(name = "LANGUAGE")
	private String language;

}
