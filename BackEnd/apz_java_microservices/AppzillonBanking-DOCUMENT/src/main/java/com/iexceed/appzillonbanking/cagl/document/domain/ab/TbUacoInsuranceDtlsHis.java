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
@Table(name = "TB_UACO_INSURANCE_DETAILS_HISTORY")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TbUacoInsuranceDtlsHis {

    @Id
    private String insuranceDtlId;

    @JsonProperty("appId")
    @Column(name = "APP_ID")
    private String appId;

    @JsonProperty("applicationId")
    @Column(name = "APPLICATION_ID")
    private String applicationId;

    @JsonProperty("versionNo")
    @Column(name = "VERSION_NO")
    private String versionNo;

    @JsonProperty("custDtlId")
    @Column(name = "CUST_DTL_ID")
    private String custDtlId;

    @JsonProperty("payload")
    @Column(name = "PAYLOAD")
    private String payload;
}
