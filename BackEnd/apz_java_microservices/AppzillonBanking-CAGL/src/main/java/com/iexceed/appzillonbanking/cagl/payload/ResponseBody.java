package com.iexceed.appzillonbanking.cagl.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.iexceed.appzillonbanking.cagl.dto.CollectionsData;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseBody {
	
	private List<ResponseObject> responseObj;
	
	private List<CollectionsData> collectionsObj;
	
	@JsonInclude(Include.NON_NULL)
	private String serverDate;
}
