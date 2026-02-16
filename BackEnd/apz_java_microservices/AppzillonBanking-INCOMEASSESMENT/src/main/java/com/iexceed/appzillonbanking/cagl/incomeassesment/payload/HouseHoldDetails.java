package com.iexceed.appzillonbanking.cagl.incomeassesment.payload;


import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HouseHoldDetails {
	
	@JsonProperty("questions")
	private List<Questions> questions;

}
