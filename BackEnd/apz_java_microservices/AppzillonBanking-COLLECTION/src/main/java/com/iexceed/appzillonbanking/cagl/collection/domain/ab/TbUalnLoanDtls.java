package com.iexceed.appzillonbanking.cagl.collection.domain.ab;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_UALN_LOAN_DTLS")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TbUalnLoanDtls {

    @Id
    @Column(name = "LOAN_DTL_ID")
    private String loanDtlId;

    @JsonProperty("applicationId")
    @Column(name = "APPLICATION_ID")
    private String applicationId;

    @JsonProperty("payload")
    @Column(name = "PAYLOAD")
    private String payload;
}
