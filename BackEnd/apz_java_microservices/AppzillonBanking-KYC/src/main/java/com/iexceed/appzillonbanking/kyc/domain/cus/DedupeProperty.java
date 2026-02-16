package com.iexceed.appzillonbanking.kyc.domain.cus;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Component
@ConfigurationProperties(prefix = "dedupe")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DedupeProperty {

	private String id;
    private String gkv;
    private String method;
    private String username;
    private String password;
    private String kycIdTypeSearch;
    private String mobileypeSearch;
    private String bankTypeSearch;
}