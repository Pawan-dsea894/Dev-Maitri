package com.iexceed.appzillonbanking.cagl.collection.rest;

import com.iexceed.appzillonbanking.cagl.collection.payload.*;
import com.iexceed.appzillonbanking.cagl.collection.service.PreClosureService;
import com.iexceed.appzillonbanking.core.payload.Header;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;


@RestController
    @Tag(description = "application/collection", name = "Pushback / Approve Collections")
    @RequestMapping("application/collection/preClosure")
    public class PreClosureController {

        @Autowired
        private PreClosureService preClosureService;

    private static final Logger logger = LogManager.getLogger(PreClosureController.class);

        @ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
                @ApiResponse(responseCode = "408", description = "Service Timed Out"),
                @ApiResponse(responseCode = "500", description = "Internal Server Error"),
                @ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
        @Operation(summary = "PreClosure simulate", description = "PreClosure simulate")
        @PostMapping(value = "/simulate",
                consumes = MediaType.APPLICATION_JSON_VALUE,
                produces = MediaType.APPLICATION_JSON_VALUE)
        public Mono<ResponseEntity<ResponseWrapper>> simulate(
                @RequestBody PreClosureSimulateRequestWrapper requestWrapper,
                @RequestHeader String appId,
                @RequestHeader String interfaceId,
                @RequestHeader String userId,
                @RequestHeader String masterTxnRefNo,
                @RequestHeader String deviceId) {

            Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);

            Mono<Response> monoResponse = preClosureService
                    .simulatePreClosure(requestWrapper.getApiRequest(), header);

            return monoResponse.flatMap(response -> {
                ResponseWrapper resWrapper = new ResponseWrapper();
                resWrapper.setApiResponse(response);
                logger.warn("Simulation Response Wrapper ==> {}", resWrapper);
                return Mono.just(new ResponseEntity<>(resWrapper, HttpStatus.OK));
            });
        }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
            @ApiResponse(responseCode = "408", description = "Service Timed Out"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable")
    })
    @Operation(summary = "Preclose Save", description = "Single or Bulk Save Preclose Details")
    @PostMapping(value = "/initiate",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ResponseWrapper>> savePreclose(
            @RequestBody PrecloseSaveRequestWrapper requestWrapper,
            @RequestHeader String appId,
            @RequestHeader String interfaceId,
            @RequestHeader String userId,
            @RequestHeader String masterTxnRefNo,
            @RequestHeader String deviceId) {

        Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);

        return preClosureService.savePrecloseDetails(requestWrapper, header)
                .map(response -> {
                    ResponseWrapper wrapper = new ResponseWrapper();
                    wrapper.setApiResponse(response);
                    return new ResponseEntity<>(wrapper, HttpStatus.OK);
                });
    }

    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Preclosure Dedupe API reachable"),
            @ApiResponse(responseCode = "408", description = "Service Timed Out"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "404", description = "Service not reachable")
    })
    @Operation(summary = "Preclosure Dedupe Check", description = "Check if preclosure request already exists for given customerId")
    @PostMapping(value = "/preClosureDedupe", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ResponseWrapper> preclosureDedupe(
            @RequestBody PreclosureDedupeRequestWrapper requestWrapper,
            @RequestHeader String appId, @RequestHeader String interfaceId,
            @RequestHeader String userId, @RequestHeader String masterTxnRefNo,
            @RequestHeader String deviceId) {

        ResponseWrapper responseWrapper = new ResponseWrapper();
        logger.warn("Start : preclosureDedupe with request :: {}", requestWrapper);
        Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);

        PreclosureDedupeRequest request = requestWrapper.getApiRequest();
        Response response = preClosureService.dedupeCheck(request);
        responseWrapper.setApiResponse(response);
        logger.warn("End : preclosureDedupe response :: {}", response);

        return new ResponseEntity<>(responseWrapper, HttpStatus.OK);
    }

    @ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
            @ApiResponse(responseCode = "408", description = "Service Timed Out"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
    @Operation(summary = "PreClosure Fetch for BM/Cashier", description = "PreClosure Fetch")
    @PostMapping(value = "/preCloseFetch", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ResponseWrapper>> preCloseFetch(
            @RequestBody FetchPrecloseRequestWrapper request, @RequestHeader String appId,
            @RequestHeader String interfaceId, @RequestHeader String userId,
            @RequestHeader String masterTxnRefNo, @RequestHeader String deviceId) {
        Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
        return preClosureService.fetchPrecloseDetails(request, header)
                .map(response -> {
                    ResponseWrapper wrapper = new ResponseWrapper();
                    wrapper.setApiResponse(response);
                    return new ResponseEntity<>(wrapper, HttpStatus.OK);
                });
    }

    @ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
            @ApiResponse(responseCode = "408", description = "Service Timed Out"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error"),
            @ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
    @Operation(summary = "PreClosure approve reject", description = "PreClosure approve reject")
    @PostMapping(value = "/processPreclose", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<ResponseWrapper>> processPreclose(
            @RequestBody PrecloseActionRequestWrapper request, @RequestHeader String appId,
            @RequestHeader String interfaceId, @RequestHeader String userId,
            @RequestHeader String masterTxnRefNo, @RequestHeader String deviceId) {

        Header header = CommonUtils.obtainHeader(appId, interfaceId, userId, masterTxnRefNo, deviceId);
        return preClosureService.processPrecloseAction(request, header)
                .map(response -> {
                    ResponseWrapper wrapper = new ResponseWrapper();
                    wrapper.setApiResponse(response);
                    return new ResponseEntity<>(wrapper, HttpStatus.OK);
                });
    }
}
