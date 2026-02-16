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
public class FaceMatchRequestFields {

    @JsonProperty("clientRefId")
    private String clientRefId;

    @JsonProperty("person")
    private String person;

    @JsonProperty("card")
    private String card;
}
