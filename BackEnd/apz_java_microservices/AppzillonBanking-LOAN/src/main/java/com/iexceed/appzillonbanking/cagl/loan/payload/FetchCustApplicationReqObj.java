package com.iexceed.appzillonbanking.cagl.loan.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FetchCustApplicationReqObj {
	
    @JsonProperty("applicationId")
    private String applicationId;
    
    @JsonProperty("customerId")
    private String customerId;
}
 