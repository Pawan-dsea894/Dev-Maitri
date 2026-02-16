package com.iexceed.appzillonbanking.core.payload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorParameterValues {
	private String apzErrorCode;
	private String apzErrorDescription;
	private String hostErrorDescription;

}
