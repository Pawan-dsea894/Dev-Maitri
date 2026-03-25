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
public class ApplicationStatusFields {
	@JsonProperty("applicationId")
	private String applicationId;

	@JsonProperty("kendraId")
	private String kendraId;

	@JsonProperty("branchId")
	private String branchId;

	@JsonProperty("seqNo")
	private int seqNo;

	@JsonProperty("payload")
	private Object payload;

	@JsonProperty("status")
	private String status;
	
	@JsonProperty("meetingDate")
	private String meetingDate;

}
