package com.iexceed.appzillonbanking.cagl.loan.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import lombok.Builder;
 
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FetchCustApplicationRequest {
 
    @JsonProperty("requestObj")
    private FetchCustApplicationReqObj requestObj;
}