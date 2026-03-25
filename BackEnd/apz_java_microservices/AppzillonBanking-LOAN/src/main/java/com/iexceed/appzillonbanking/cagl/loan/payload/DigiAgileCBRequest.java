package com.iexceed.appzillonbanking.cagl.loan.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DigiAgileCBRequest {

    @JsonProperty("appId")
    private String appId;

    @JsonProperty("interfaceName")
    private String interfaceName;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("loanAmount")
    private String loanAmount;

    @JsonProperty("loanProductType")
    private String loanProductType;

    @JsonProperty("applicantInsurance")
    private String applicantInsurance;

    @JsonProperty("spouseInsurance")
    private String spouseInsurance;

    @JsonProperty("applicantInsuranceAmt")
    private String applicantInsuranceAmt;

    @JsonProperty("spouseInsuranceAmt")
    private String spouseInsuranceAmt;

    @JsonProperty("applicationId")
    private String applicationId;

    @JsonProperty("versionNo")
    private String versionNo;

    @JsonProperty("custDtlId")
    private String custDtlId;




}
