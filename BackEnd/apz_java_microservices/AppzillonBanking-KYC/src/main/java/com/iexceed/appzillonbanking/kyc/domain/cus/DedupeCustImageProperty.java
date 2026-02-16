package com.iexceed.appzillonbanking.kyc.domain.cus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "dedupe.cust.img")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DedupeCustImageProperty {

	private String id;
    private String gkv;
    private String method;
    private String idType;

}