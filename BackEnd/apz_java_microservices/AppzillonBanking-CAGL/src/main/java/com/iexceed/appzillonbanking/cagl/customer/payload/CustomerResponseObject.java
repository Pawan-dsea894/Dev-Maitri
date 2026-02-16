package com.iexceed.appzillonbanking.cagl.customer.payload;

import com.iexceed.appzillonbanking.cagl.entity.GkCustomerData;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponseObject {
	
	GkCustomerData custData;
 
}
