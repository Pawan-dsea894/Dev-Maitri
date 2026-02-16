package com.iexceed.appzillonbanking.cagl.document.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.iexceed.appzillonbanking.cagl.document.domain.ab.TbUaobCbResponseHis;
import com.iexceed.appzillonbanking.cagl.document.domain.ab.WorkflowDefinitionHis;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ApplyLoanRequestHisFields {
    @JsonProperty("applicationdtls")
    private List<ApplicationDtls> applicationdtls;

    @JsonProperty("applnWfDefinitionHisList")
    private List<WorkflowDefinitionHis> applnWfDefinitionHisList;

    @JsonProperty("cbResponseHis")
    private TbUaobCbResponseHis cbResponseHis;

    @JsonProperty("precloserLoanResponse")
    private PrecloserLoanResponse precloserLoanResponse;

    @JsonProperty("userRole")
    private String userRole;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("appVersion")
    private String appVersion;

    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("userId")
    private String userId;
}
