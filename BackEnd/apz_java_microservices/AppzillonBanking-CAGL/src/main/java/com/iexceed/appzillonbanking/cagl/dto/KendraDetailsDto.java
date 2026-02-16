package com.iexceed.appzillonbanking.cagl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class KendraDetailsDto {
	
	private int kendraId;
	
	private String kendraName;
	
	private String kendraAddr;

	private String kmId;
	
	private String kmName;
}
