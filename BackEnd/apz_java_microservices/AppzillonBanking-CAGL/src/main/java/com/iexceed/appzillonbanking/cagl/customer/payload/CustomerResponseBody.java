package com.iexceed.appzillonbanking.cagl.customer.payload;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CustomerResponseBody {
	
	List<CustomerResponseObject> responseObj;

}
