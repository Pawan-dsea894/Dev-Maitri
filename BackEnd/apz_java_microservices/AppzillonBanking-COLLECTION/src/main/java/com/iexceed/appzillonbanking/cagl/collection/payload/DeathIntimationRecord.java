package com.iexceed.appzillonbanking.cagl.collection.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DeathIntimationRecord {

    @JsonProperty("memName")
    private String memName;

    @JsonProperty("memNum")
    private String memNum;

    @JsonProperty("spouseName")
    private String spouseName;

    @JsonProperty("spouseNum")
    private String spouseNum;
}
