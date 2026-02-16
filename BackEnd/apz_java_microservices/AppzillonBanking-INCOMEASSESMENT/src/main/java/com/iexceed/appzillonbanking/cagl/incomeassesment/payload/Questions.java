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
public class Questions {
	
	    @JsonProperty("id")
		private String id;
	 
		@JsonProperty("title")
		private String title;
	 
		@JsonProperty("type")
		private String type;
	 
		@JsonProperty("mandatory")
		private boolean mandatory;
	 
		@JsonProperty("options")
		private List<String> options;
	 
		@JsonProperty("value")
		private Object value;

}
