package com.iexceed.appzillonbanking.cagl.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AsmiUserData {
	
	private String userId;
	
	private String userdesignation;
	
	private String hierarchyId;

}
