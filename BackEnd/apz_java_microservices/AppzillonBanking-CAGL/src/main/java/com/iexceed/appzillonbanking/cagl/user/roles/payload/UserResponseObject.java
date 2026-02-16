package com.iexceed.appzillonbanking.cagl.user.roles.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cagl.entity.GkUserData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserResponseObject {
	
	@JsonProperty("userRoles")
	private GkUserData userRoles;

}
