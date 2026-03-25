package com.iexceed.appzillonbanking.kendra.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.kendra.payload.LeobrixRequest;
import com.iexceed.appzillonbanking.kendra.utils.AESUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;

import static com.iexceed.appzillonbanking.kendra.service.KendraService.EXCEPTION_OCCURED;

@Service
public class LeobrixService {

    @Value("${leobrix.base-url}")
    private String baseUrl;
    @Value("${leobrix.expiry-seconds}")
    private long expirySeconds;
    @Value("${leobrix.secret-key}")
    private String secretKey;
    @Value("${leobrix.issuer}")
    private String ISSUER;
    @Value("${leobrix.audience}")
    private String AUDIENCE;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Response generateLeobrixUrl(LeobrixRequest apiRequest) throws Exception {

        Response response = new Response();
        ResponseHeader respHeader = new ResponseHeader();
        ResponseBody respBody = new ResponseBody();

    try{
        String userId = apiRequest.getRequestObj().getUserId();
        String userName = apiRequest.getRequestObj().getUserName();
        String roleId = apiRequest.getRequestObj().getRoleId();
        String branchId = apiRequest.getRequestObj().getBranchId();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyyHHmmss");

        String issuedAt = LocalDateTime.now().format(formatter);
        String expiryAt = LocalDateTime.now().plusSeconds(expirySeconds).format(formatter);

        // 1. Create payload JSON
        ObjectNode payload = objectMapper.createObjectNode();
        payload.put("sub", userId);
        payload.put("name", userName);
        payload.put("role", roleId);
        payload.put("branchId", branchId);
        payload.put("iss", ISSUER);
        payload.put("aud", AUDIENCE);
        payload.put("iat", issuedAt);
        payload.put("exp", expiryAt);

        String payloadJson = objectMapper.writeValueAsString(payload);
        // 2. AES encrypt
        String encryptedToken = AESUtils.encrypt(payloadJson, secretKey);

        String leobrixUrl = baseUrl + URLEncoder.encode(encryptedToken, StandardCharsets.UTF_8);

        LinkedHashMap<String, String> resp = new LinkedHashMap<>();
        resp.put("leobrixUrl", leobrixUrl);

        respBody.setResponseObj(new Gson().toJson(resp));

        CommonUtils.generateHeaderForSuccess(respHeader);
        respHeader.setResponseMessage("Leobrix URL generated successfully");

    } catch (Exception e) {
        respBody.setResponseObj(e.getMessage());
        CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
        respHeader.setResponseMessage(EXCEPTION_OCCURED + e.getMessage());
    }
        response.setResponseBody(respBody);
        response.setResponseHeader(respHeader);
        return response;
    }

}
