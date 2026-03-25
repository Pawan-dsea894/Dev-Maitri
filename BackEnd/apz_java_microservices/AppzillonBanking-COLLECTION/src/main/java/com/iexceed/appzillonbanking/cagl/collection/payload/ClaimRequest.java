package com.iexceed.appzillonbanking.cagl.collection.payload;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ClaimRequest {

    @JsonProperty("memberId")
    private String memberId;

    @JsonProperty("deceasedPerson")
    private String deceasedPerson;

    @JsonProperty("deceasedDeathDate")
    private String deceasedDeathDate;

    @JsonProperty("deceasedDeathCause")
    private String deceasedDeathCause;

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("comment")
    private String comment;

    @JsonProperty("kMContactNumber")
    private String kmContactNumber;

    @JsonProperty("familyMemeberName")
    private String familyMemeberName;

    @JsonProperty("familyMemeberRelation")
    private String familyMemeberRelation;

    @JsonProperty("familyMemeberContactNo")
    private String familyMemeberContactNo;
}
