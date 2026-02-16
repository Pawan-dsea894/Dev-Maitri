package com.iexceed.appzillonbanking.cagl.customer.payload;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseHeader {
	
	@Schema(example = "0(SUCCESS)/1(FAILURE)", allowableValues = "0, 1")
	@JsonProperty("ResponseCode")
	private String responseCode;
	
	@Schema(hidden=true)
	@JsonProperty("ErrorCode")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String errorCode;
	
	@Schema(example = "This field contains the error message if the API execution fails")
	@JsonProperty("ResponseMessage")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String responseMessage;
	
	@JsonIgnore
	private HttpStatus httpStatus;

	@JsonProperty("ErrorMessage")
	@JsonInclude(JsonInclude.Include.NON_NULL)
	private String errorMessage;

}
