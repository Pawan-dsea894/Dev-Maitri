package com.iexceed.appzillonbanking.cagl.document.domain.ab;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table( name = "TB_UALN_LOAN_DTLS_HISTORY")
public class TbUalnLoanDtlsHis {
    @Id
    private String loanDtlId;

    @JsonProperty("applicationId")
    @Column(name = "APPLICATION_ID")
    private String applicationId;

    @JsonProperty("appId")
    @Column(name = "APP_ID")
    private String appId;

    @JsonProperty("versionNum")
    @Column(name = "VERSION_NO")
    private String versionNum;

    @JsonProperty("loanAmount")
    @Column(name = "LOAN_AMOUNT")
    private String loanAmount;

    @JsonProperty("term")
    @Column(name = "TERM")
    private String term;

    @JsonProperty("frequency")
    @Column(name = "FREQUENCY")
    private String frequency;

    @JsonProperty("interestRate")
    @Column(name = "INTEREST_RATE")
    private String interestRate;

    @JsonProperty("payload")
    @Column(name = "PAYLOAD")
    private String payload;

    @JsonProperty("activeLoanDtls")
    @Column(name = "ACTIVELOANDTLS")
    private String activeLoanDtls;

    @JsonProperty("installmentDetails")
    @Column(name = "INSTALLMENTDETAILS")
    private String installmentDetails;

}
