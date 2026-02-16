package com.iexceed.appzillonbanking.cagl.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KendraRequestField {

	@JsonProperty("KendraUserId")
	private String userId;
	
	private String latLongFlag;
	
	@JsonProperty("KendraRoleId")
	private String roleId;
	
	@JsonProperty("meetingDate")
	private String meetingDate;
	
	@JsonProperty("KendraId")
	private List<Integer> kendraId; 
	
}
