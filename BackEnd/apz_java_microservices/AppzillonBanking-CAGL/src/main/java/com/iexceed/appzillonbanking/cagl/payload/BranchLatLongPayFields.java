package com.iexceed.appzillonbanking.cagl.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BranchLatLongPayFields {
	
	private String branchId;
	private String latitude;
	private String longitude;
	private String updatedBy;
	private String branch;
	private String UpdatedOn;

}
