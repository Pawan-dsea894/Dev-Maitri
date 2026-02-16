package com.iexceed.appzillonbanking.cagl.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KendraLatLongFields {
	
	private int kendraId;
	private String lat;
	private String longit;
	private String updatedBy;
	private String UpdatedAt;
	private String address;

}
