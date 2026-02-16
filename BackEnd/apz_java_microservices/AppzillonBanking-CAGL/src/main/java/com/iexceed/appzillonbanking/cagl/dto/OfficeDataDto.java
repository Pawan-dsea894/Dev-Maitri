package com.iexceed.appzillonbanking.cagl.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfficeDataDto {
	
	private String branchId;
	private String branchName;
	private String areaName;
	private String regionName;
	private String zone;
	private String mifosOfficeId;
	private String type;
	private String state;
	private String district;
	private String subDistrict;
	private String branchOpeningDate;
	private String consentType;

}
