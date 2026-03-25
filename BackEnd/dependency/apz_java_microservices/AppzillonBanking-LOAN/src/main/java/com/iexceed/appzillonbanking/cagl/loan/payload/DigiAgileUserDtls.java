package com.iexceed.appzillonbanking.cagl.loan.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Component
public class DigiAgileUserDtls {

    @JsonProperty("applicationId")
    private String applicationId;

    @JsonProperty("customerId")
    private String customerId;

    @JsonProperty("kendraId")
    private String kendraId;

    @JsonProperty("branchId")
    private String branchId;

    @JsonProperty("kmId")
    private String kmId;

    @JsonProperty("customerName")
    private String customerName;

    @JsonProperty("mobileNum")
    private String mobileNum;

    @JsonProperty("kmName")
    private String kmName;

    @JsonProperty("groupId")
    private String groupId;

    @JsonProperty("kendraName")
    private String kendraName;

    @JsonProperty("kendrafrequency")
    private String kendrafrequency;

    @JsonProperty("meetingday")
    private String meetingday;

    @JsonProperty("loanAmount")
    private BigDecimal loanAmount;

    @JsonProperty("kycDtls")
    private KYCDetails kycDtls; 

    @JsonProperty("income")
    private List<Income> income; 

    @JsonProperty("earnings")
    private List<Earnings> earnings; 

    @JsonProperty("bankDtls")
    private BankDetails bankDtls; 

    @JsonProperty("addressDtls")
    private List<AddressDtls> addressDtls; 

    @JsonProperty("loanDtls")
    private LoanDtls loanDtls;

    @JsonProperty("CBDetails")
    private CBDetails cbDetails;
    
    @JsonProperty("term")
    private String term;
    
    @JsonProperty("annualPercentageRate")
    private String annualPercentageRate;
}
