package com.iexceed.appzillonbanking.kendra.payload;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeobrixRequestFields {

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("roleId")
    private String roleId;

    @JsonProperty("userName")
    private String userName;

    @JsonProperty("branchId")
    private String branchId;
}
