package com.iexceed.appzillonbanking.cagl.collection.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationDedupeRequestFields {

	@JsonProperty("kmId")
	private String kmId;

	@JsonProperty("meetingDate")
	private String meetingDate;

	@JsonProperty("roleId")
	private String roleId;

	@JsonProperty("kendraId")
	private String kendraId;

}
