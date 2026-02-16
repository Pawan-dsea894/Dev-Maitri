package com.iexceed.appzillonbanking.cagl.document.domain.ab;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_ABOB_LOV_MASTER")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LovMaster {

	@JsonProperty("lovId")
	@Id
	@Column(name = "LOV_ID")
	private int lovId;
	
	@JsonProperty("appId")
	@Column(name = "APP_ID")
	private String appId;
	
	@JsonProperty("lovName")
	@Column(name = "LOV_NAME")
	private String lovName;
	
	@JsonProperty("lovDtls")
	@Column(name = "LOV_DTLS")
	private String lovDtls;
	
	@JsonProperty("language")
	@Column(name = "LANGUAGE")
	private String language;

}