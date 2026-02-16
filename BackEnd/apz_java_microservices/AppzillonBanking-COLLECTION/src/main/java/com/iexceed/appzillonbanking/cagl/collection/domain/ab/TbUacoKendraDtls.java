package com.iexceed.appzillonbanking.cagl.collection.domain.ab;

import com.fasterxml.jackson.annotation.JsonProperty;

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
@Table(name = "TB_UACO_KENDRA_DETAILS")
@IdClass(TbUacoKendraDtlsId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TbUacoKendraDtls {

	@Id
	private String appId;

	@Id
	private String applicationId;

	@Id
	private String versionNum;

	@Id
	private String kendraId;

	@JsonProperty("kendraName")
	@Column(name = "KENDRA_NAME")
	private String kendraName;

	@JsonProperty("startTime")
	@Column(name = "STARTTIME")
	private String startTime;

	@JsonProperty("payload")
	@Column(name = "PAYLOAD")
	private String payload;

}