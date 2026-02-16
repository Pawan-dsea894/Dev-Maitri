package com.iexceed.appzillonbanking.cagl.document.domain.ab;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_UAOB_CUSTOMER_DETAILS_HISTORY")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TbUaobCustDtlsHis {

    @Id
    private String custDtlId;

    @JsonProperty("appId")
    @Column(name = "APP_ID")
    private String appId;

    @JsonProperty("applicationId")
    @Column(name = "APPLICATION_ID")
    private String applicationId;

    @JsonProperty("versionNo")
    @Column(name = "VERSION_NO")
    private String versionNo;

    @JsonProperty("kendraId")
    @Column(name = "KENDRA_ID")
    private String kendraId;

    @JsonProperty("groupId")
    @Column(name = "GROUP_ID")
    private String groupId;

    @JsonProperty("customerId")
    @Column(name = "CUSTOMER_ID")
    private String customerId;

    @JsonProperty("customerName")
    @Column(name = "CUSTOMER_NAME")
    private String customerName;

    @JsonProperty("eligibleLoanAmt")
    @Column(name = "ELIGIBLE_LOAN_AMOUNT")
    private String eligibleLoanAmt;

    @JsonProperty("kycDetails")
    @Column(name = "KYC_DETAILS")
    private String kycDetails;

    @JsonProperty("bankDtls")
    @Column(name = "BANK_DETAILS")
    private String bankDtls;
}
