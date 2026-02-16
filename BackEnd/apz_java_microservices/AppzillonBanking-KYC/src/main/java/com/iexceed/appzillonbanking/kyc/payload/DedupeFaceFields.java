package com.iexceed.appzillonbanking.kyc.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DedupeFaceFields {

    @JsonProperty("id")
    private String id;

    @JsonProperty("gkv")
    private String gkv;

    @JsonProperty("method")
    private String method;

    @JsonProperty("idType")
    private String idType;

    @JsonProperty("dedupeId")
    private String dedupeId;

}
