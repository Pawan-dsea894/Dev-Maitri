package com.iexceed.appzillonbanking.kendra.domain.cus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KendraUserId {
	
	private String userId;
	private String excludedFlag;

}
