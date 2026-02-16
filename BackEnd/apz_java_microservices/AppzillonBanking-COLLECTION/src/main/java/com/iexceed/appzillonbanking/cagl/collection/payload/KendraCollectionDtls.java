package com.iexceed.appzillonbanking.cagl.collection.payload;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class KendraCollectionDtls {
	
	private String kmId;
	
	private String kmName;
	
	private int kendraCount;
	
	private List<KendraDetailsDto> kendraInfo;

	
}
