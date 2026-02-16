package com.iexceed.appzillonbanking.cagl.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AsmiUserResponse {

	@JsonProperty("addinfo1")
	private String addInfo1;

	@JsonProperty("addinfo2")
	private String addInfo2;

}
