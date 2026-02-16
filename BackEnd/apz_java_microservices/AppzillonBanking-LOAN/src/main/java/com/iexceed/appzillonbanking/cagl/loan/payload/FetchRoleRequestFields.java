package com.iexceed.appzillonbanking.cagl.loan.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FetchRoleRequestFields {

	@JsonProperty("roleId")
	private String roleId;

	@JsonProperty("userId")
	private String userId;
}