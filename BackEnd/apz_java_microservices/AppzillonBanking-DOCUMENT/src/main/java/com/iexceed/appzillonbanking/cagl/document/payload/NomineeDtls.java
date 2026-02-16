package com.iexceed.appzillonbanking.cagl.document.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NomineeDtls {

	@JsonProperty("nomineeName")
	private String nomineeName;

	@JsonProperty("relationWithMember")
	private String relationWithMember;
	
	@JsonProperty("relationWithMemberCode")
	private String relationWithMemberCode;
}
