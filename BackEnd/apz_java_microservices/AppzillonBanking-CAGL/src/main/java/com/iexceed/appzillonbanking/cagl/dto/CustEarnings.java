package com.iexceed.appzillonbanking.cagl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustEarnings {
	
	private String recId;
	private String customerId;
	private String name;
	private String dob;
	private String memRelation;
	private String legaldocName;
	private String legaldocId;

}
