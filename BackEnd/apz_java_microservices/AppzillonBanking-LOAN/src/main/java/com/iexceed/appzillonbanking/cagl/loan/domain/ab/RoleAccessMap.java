package com.iexceed.appzillonbanking.cagl.loan.domain.ab;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_ABOB_ROLE_ACCESS_MAP")
@IdClass(RoleAccessMapId.class)
@Getter
@Setter
public class RoleAccessMap {

	@Id
	private String appId;

	@Id
	private String roleId;

	@JsonProperty("accessPermission")
	@Column(name = "ACCESS_PERMISSION")
	private String accessPermission;

	@JsonProperty("allowedFeature")
	@Column(name = "ALLOWED_FEATURES")
	private String allowedFeature;
}