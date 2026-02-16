package com.iexceed.appzillonbanking.kendra.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResponseUserDetails {
	private String userId;
	private String userName;
	private String role;
}
