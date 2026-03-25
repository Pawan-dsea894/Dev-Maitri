package com.iexceed.appzillonbanking.cagl.loan.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class LoanApplicationResponseObj {

    @JsonProperty("applicationDetails")
    private List<ApplicationDetailsResponse> applicationDetails;

}