package com.iexceed.appzillonbanking.cagl.collection.payload;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EmergencyLoanReqFields {
    @JsonProperty("kendraId")
    private List<String> kendraId;

    @JsonProperty("meetingDate")
    private String meetingDate;

    @JsonProperty("productCode")
    private String productCode;
}
