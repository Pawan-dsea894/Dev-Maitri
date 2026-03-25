package com.iexceed.appzillonbanking.cagl.loan.payload;


import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NomineeDetailsResponse {

    @JsonProperty("updatedTs")
    private Timestamp updated_ts;

    @JsonProperty("createTs")
    private Timestamp create_ts;

    @JsonProperty("customerId")
    private String customer_id;

    @JsonProperty("relationtype")
    private String relationtype;

    @JsonProperty("memrelation")
    private String memrelation;

    @JsonProperty("legaldocname")
    private String legaldocname;

    @JsonProperty("legaldocid")
    private String legaldocid;

    @JsonProperty("inputdata")
    private String inputdata;

    @JsonProperty("docunof")
    private String docunof;

    @JsonProperty("docunob")
    private String docunob;

    @JsonProperty("reason")
    private String reason;

    @JsonProperty("ocrresponsepayload")
    private String ocrresponsepayload;

    @JsonProperty("nomineedtls")
    private String nomineedtls;

    @JsonProperty("ocrdetails")
    private String ocrdetails;

    @JsonProperty("customerName")
    private String customer_name;

    @JsonProperty("applicationId")
    private String application_id;

    @JsonProperty("addInfo")
    private String add_info;

    @JsonProperty("latestVersionNo")
    private String latest_version_no;
}