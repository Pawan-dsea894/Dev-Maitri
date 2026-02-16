package com.iexceed.appzillonbanking.cagl.user.roles.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseWrapper {
	
	@JsonProperty("apiResponse")
	private UserResponse apiResponse;

}
