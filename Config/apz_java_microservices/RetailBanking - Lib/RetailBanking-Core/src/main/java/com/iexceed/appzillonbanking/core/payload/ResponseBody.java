package com.iexceed.appzillonbanking.core.payload;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseBody {

	@Schema(example = "This field contains the actual api response")
	public String responseObj;
	
}
