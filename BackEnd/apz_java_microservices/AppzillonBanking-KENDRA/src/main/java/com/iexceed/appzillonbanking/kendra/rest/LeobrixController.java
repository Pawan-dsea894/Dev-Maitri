package com.iexceed.appzillonbanking.kendra.rest;

import com.iexceed.appzillonbanking.core.payload.Header;
import com.iexceed.appzillonbanking.core.payload.Response;

import com.iexceed.appzillonbanking.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.kendra.payload.LeobrixRequest;
import com.iexceed.appzillonbanking.kendra.payload.LeobrixRequestWrapper;
import com.iexceed.appzillonbanking.kendra.service.LeobrixService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@Tag(description = "application/leoBrix", name = "LEOBRIX")
@RequestMapping("application/leoBrix")
public class LeobrixController {

    private static final Logger logger = LoggerFactory.getLogger(LeobrixController.class);


    @Autowired
    private LeobrixService leobrixService;

    @PostMapping("/getLeobrixURL")
    public ResponseEntity<ResponseWrapper> getLeobrixURL(
            @RequestBody LeobrixRequestWrapper requestWrapper,
            @RequestHeader(defaultValue = "APZRMB") String appId,
            @RequestHeader(defaultValue = "GetLeobrixURL") String interfaceId,
            @RequestHeader(defaultValue = "000000000001") String userId,
            @RequestHeader(defaultValue = "12345678") String masterTxnRefNo,
            @RequestHeader(defaultValue = "abcd1234efgh5678") String deviceId) throws Exception {
        ResponseWrapper responseWrapper = new ResponseWrapper();
        logger.debug("Leobrix request data :: {}", requestWrapper);
        Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);

        LeobrixRequest apiRequest = requestWrapper.getApiRequest();
        logger.debug("APIRequest :: {}", apiRequest);

        Response response = leobrixService.generateLeobrixUrl(apiRequest);
        responseWrapper.setApiResponse(response);
        logger.debug("End : GetLeobrixURL response :: {}", response);
        return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
    }
}
