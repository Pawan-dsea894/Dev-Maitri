package com.iexceed.appzillonbanking.cagl.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cagl.domain.cus.ProductResponseObject;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductResponseBody {
	
	@JsonProperty("ResponseHeader")
	private ResponseHeader responseHeader;
	
	private List<ProductResponseObject> responseObj;
	
	

}
