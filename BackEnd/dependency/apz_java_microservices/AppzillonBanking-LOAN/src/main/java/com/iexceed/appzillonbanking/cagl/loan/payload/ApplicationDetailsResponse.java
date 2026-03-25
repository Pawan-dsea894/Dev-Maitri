package com.iexceed.appzillonbanking.cagl.loan.payload;


import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class ApplicationDetailsResponse {

    @JsonProperty("createTs")
    private Timestamp create_ts;

    @JsonProperty("updatedTs")
    private Timestamp updated_ts;

    @JsonProperty("latestVersionNo")
    private String latest_version_no;

    @JsonProperty("kendraId")
    private String kendra_id;

    @JsonProperty("customerId")
    private String customer_id;

    @JsonProperty("createdBy")
    private String created_by;

    @JsonProperty("updatedBy")
    private String updated_by;

    @JsonProperty("applicationType")
    private String application_type;

    @JsonProperty("kycType")
    private String kyc_type;

    @JsonProperty("applicationStatus")
    private String application_status;

    @JsonProperty("branchCode")
    private String branch_code;

    @JsonProperty("currentScreenId")
    private String current_screen_id;

    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("currentStage")
    private String current_stage;

    @JsonProperty("kmid")
    private String kmid;

    @JsonProperty("kendraname")
    private String kendraname;

    @JsonProperty("customerName")
    private String customer_name;

    @JsonProperty("addInfo1")
    private String add_info1;

    @JsonProperty("addInfo2")
    private String add_info2;

    @JsonProperty("appId")
    private String app_id;

    @JsonProperty("loanApplicationNo")
    private String loan_application_no;

    @JsonProperty("applicationId")
    private String application_id;

    @JsonProperty("nomineeDetails")
    private NomineeDetailsResponse nomineeDetails;
    
    @JsonProperty("branchName")
    private String branch_name;
    
    @JsonProperty("request_type")
	private String request_type;
      
}