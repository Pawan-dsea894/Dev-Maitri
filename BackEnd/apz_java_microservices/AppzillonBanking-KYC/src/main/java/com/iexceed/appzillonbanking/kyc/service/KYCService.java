package com.iexceed.appzillonbanking.kyc.service;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import com.iexceed.appzillonbanking.kyc.domain.cus.DedupeCustImageProperty;
import com.iexceed.appzillonbanking.kyc.payload.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.iexceed.appzillonbanking.core.payload.Header;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.core.utils.CommonUtilsCBS;
import com.iexceed.appzillonbanking.interfaceAdapter.service.InterfaceAdapter;
import com.iexceed.appzillonbanking.interfaceAdapter.utils.AdapterUtil;
import com.iexceed.appzillonbanking.kyc.constants.CommonConstants;
import com.iexceed.appzillonbanking.kyc.domain.cus.DedupeProperty;
import com.iexceed.appzillonbanking.kyc.entity.GkDedupeApiResponseData;
import com.iexceed.appzillonbanking.kyc.repository.cus.GkDedupeApiResponseRepository;

import reactor.core.publisher.Mono;

@Service
public class KYCService {

    private static final Logger logger = LogManager.getLogger(KYCService.class);

    @Autowired
    private InterfaceAdapter interfaceAdapter;

    @Autowired
    private AdapterUtil adapterUtil;

    @Autowired
    private CommonUtilsCBS commonUtilCBS;

    @Autowired
    private DedupeProperty dedupeProps;

    @Autowired
    private DedupeCustImageProperty dedupeCustImageProperty;

    @Autowired
    private GkDedupeApiResponseRepository dedupeRepo;

    public static final String EXCEPTION_MSG = "Something went wrong, Please try again!!";
    public static final String EXCEPTION_OCCURED = "Exception occurred";

    public static final String OCR_VOTER_FRONT_INTERFACEID = "OcrVoterFront";
    public static final String OCR_VOTER_BACK_INTERFACEID = "OcrVoterBack";
    public static final String OCR_AADHAR_FRONT_INTERFACEID = "OcrAadharFront";
    public static final String OCR_PAN_INTERFACEID = "OcrPan";
    public static final String OCR_AADHAR_BACK_INTERFACEID = "OcrAadharBack";
    public static final String OCR_DRIVING_LICENSE_INTERFACEID = "OcrDrivingLicense";

    public static final String DOC_TYPE_VOTER = "Voter ID";
    public static final String DOC_TYPE_PAN = "PAN";
    public static final String DOC_TYPE_AADHAR_FRONT = "Aadhar Front";
    public static final String DOC_TYPE_AADHAR_BACK = "Aadhar Back";
    public static final String VERIFY_VOTERID_INTERFACEID = "voterAuthentication";
    public static final String VERIFY_PAN_INTERFACEID = "panVerify";
    public static final String VERIFY_DRIVING_LICENSE_INTERFACEID = "DrivingLicenseVerify";
    public static final String MOBILENUMBERUPDATE_INTERFACEID = "MobileNumberUpdate";
    public static final String MOBILENUMBERUPDATEDEDUPE_INTERFACEID = "MobileNumberUpdateDedupe";


    public static final String DEDUPE_CHECK_INTERFACEID = "DedupeCheck";

    public static final String YES = "yes";

    public static final String DEDUPE_CUST_IMAGE_INTERFACEID = "DedupeCustImage";

    public static final String VERIFY_FACE_INTERFACEID = "faceVerify";


    public Mono<ResponseWrapper> callOCR(OCRRequest apiRequest, Header header) {

        try {
            OCRRequestFields ocrRequest = apiRequest.getRequestObj();
            ocrRequest.setRequestID("" + commonUtilCBS.generateRandomId(1, Integer.MAX_VALUE));
            ocrRequest.setConfidence(YES);
            ocrRequest.setFraudCheck(YES);
            ocrRequest.setIsBlackWhiteCheck(YES);
            ocrRequest.setIsCompleteImageCheck(YES);

            switch (ocrRequest.getDoctype()) {
                case CommonConstants.OCR_VOTER:
                    ocrRequest.setDoctype(DOC_TYPE_VOTER);
                    if (StringUtils.isNotBlank(ocrRequest.getIsFront()) && ocrRequest.getIsFront().equalsIgnoreCase("N"))
                        header.setInterfaceId(OCR_VOTER_BACK_INTERFACEID);
                    else
                        header.setInterfaceId(OCR_VOTER_FRONT_INTERFACEID);
                    break;
                case CommonConstants.OCR_AADHAR:
                    if (StringUtils.isNotBlank(ocrRequest.getIsFront()) && ocrRequest.getIsFront().equalsIgnoreCase("N")) {
                        header.setInterfaceId(OCR_AADHAR_BACK_INTERFACEID);
                        ocrRequest.setDoctype(DOC_TYPE_AADHAR_BACK);
                    } else {
                        ocrRequest.setDoctype(DOC_TYPE_AADHAR_FRONT);
                        header.setInterfaceId(OCR_AADHAR_FRONT_INTERFACEID);
                    }
                    break;
                case CommonConstants.OCR_PAN:
                    header.setInterfaceId(OCR_PAN_INTERFACEID);
                    ocrRequest.setDoctype(DOC_TYPE_PAN);
                    break;
                case CommonConstants.DRIVING_LICENSE:
                    header.setInterfaceId(OCR_DRIVING_LICENSE_INTERFACEID);
                    ocrRequest.setDoctype(OCR_DRIVING_LICENSE_INTERFACEID);
                    break;
            }

            Mono<Object> dedupeResponse = interfaceAdapter.callExternalService(header, apiRequest.getRequestObj(),
                    header.getInterfaceId(), true);
            dedupeResponse.doOnNext(resWrapper -> {
                logger.debug("Received Response Wrapper of OCR: {}" + " of " + ocrRequest.getDoctype(), resWrapper);
            }).subscribe();

            Mono<ResponseWrapper> monoResWrapper = adapterUtil.generateRespWrapper(dedupeResponse,
                    header.getInterfaceId(), header, true);
            return monoResWrapper;

        } catch (Exception e) {
            logger.error("exception at callOCR: ", e);
            Response response = new Response();
            ResponseHeader respHeader = new ResponseHeader();
            ResponseBody respBody = new ResponseBody();
            respBody.setResponseObj(EXCEPTION_MSG);
            CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
            response.setResponseBody(respBody);
            response.setResponseHeader(respHeader);
            ResponseWrapper resWrapper = new ResponseWrapper();
            resWrapper.setApiResponse(response);
            return Mono.just(resWrapper);
        }
    }

    public Mono<ResponseWrapper> dedupeCheck(DedupeRequest apiRequest, Header header, String serviceName) {
    	logger.debug("Received apiRequest:" +apiRequest);
        GkDedupeApiResponseData deudpeData = new GkDedupeApiResponseData();
        Gson gson = new Gson();
        try {
            DedupeRequestFields dedupeReq = apiRequest.getRequestObj();
            dedupeReq.setGkv(dedupeProps.getGkv());
            dedupeReq.setId(dedupeProps.getId());
            dedupeReq.setMethod(dedupeProps.getMethod());

            deudpeData.setDedupeType(serviceName);
            deudpeData.setStartTime(LocalDateTime.now());
            deudpeData.setRequest(gson.toJson(apiRequest.getRequestObj()));
            deudpeData.setCustomerId(apiRequest.getRequestObj().getCustomerId());
            
            logger.debug("Received dedupeReq:" +dedupeReq);

            switch (serviceName) {
                case CommonConstants.DEDUPE_BANK:
                    dedupeReq.setTypesrch(dedupeProps.getBankTypeSearch());
                    deudpeData.setDedupeId(apiRequest.getRequestObj().getAccountNo());
                    apiRequest.getRequestObj().setDedupeId(apiRequest.getRequestObj().getAccountNo());
                    break;
                case CommonConstants.DEDUPE_KYCID:
                    dedupeReq.setTypesrch(dedupeProps.getKycIdTypeSearch());
                    deudpeData.setDedupeId(apiRequest.getRequestObj().getKycId());
                    apiRequest.getRequestObj().setDedupeId(apiRequest.getRequestObj().getKycId());
                    break;
                case CommonConstants.DEDUPE_MOBILE:
                    dedupeReq.setTypesrch(dedupeProps.getMobileypeSearch());
                    deudpeData.setDedupeId(apiRequest.getRequestObj().getMobileNo());
                    apiRequest.getRequestObj().setDedupeId(apiRequest.getRequestObj().getMobileNo());
                    break;
            }
            logger.debug("Received deudpeData:" +deudpeData);

            String authToken = generateBasicAuthHeader(dedupeProps.getUsername(), dedupeProps.getPassword());
            logger.debug("Set dauthToken:" +authToken);
            dedupeReq.setAuthToken(authToken);
            header.setInterfaceId(DEDUPE_CHECK_INTERFACEID);

            Mono<Object> dedupeResponse = interfaceAdapter.callExternalService(header, apiRequest.getRequestObj(),
                    apiRequest.getInterfaceName(), true);

            Mono<ResponseWrapper> monoResWrapper = adapterUtil.generateRespWrapper(dedupeResponse,
                    apiRequest.getInterfaceName(), header, true);

            deudpeData.setRecievedTime(LocalDateTime.now());

            return monoResWrapper.flatMap(responseMono -> {
                deudpeData.setResponse(gson.toJson(responseMono));
                logger.debug("Saved deudpeData:" +deudpeData);
                dedupeRepo.save(deudpeData);
                return Mono.just(responseMono);
            });

        } catch (Exception e) {
            logger.error("exception at dedupeCheck: ", e);
            Response response = new Response();
            ResponseHeader respHeader = new ResponseHeader();
            ResponseBody respBody = new ResponseBody();

            respBody.setResponseObj(EXCEPTION_MSG);
            CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
            response.setResponseBody(respBody);
            response.setResponseHeader(respHeader);
            ResponseWrapper resWrapper = new ResponseWrapper();
            resWrapper.setApiResponse(response);

            deudpeData.setRecievedTime(LocalDateTime.now());
            deudpeData.setResponse(gson.toJson(resWrapper));
            logger.debug("Saved deudpeData2:" +deudpeData);
            dedupeRepo.save(deudpeData);

            return Mono.just(resWrapper);
        }
    }

    public Mono<ResponseWrapper> voterAuthentication(VoterAuthenticateRequest apiRequest, Header header) {

        try {

            header.setInterfaceId(VERIFY_VOTERID_INTERFACEID);

            Mono<Object> dedupeResponse = interfaceAdapter.callExternalService(header, apiRequest.getRequestObj(),
                    VERIFY_VOTERID_INTERFACEID, true);

            Mono<ResponseWrapper> monoResWrapper = adapterUtil.generateRespWrapper(dedupeResponse,
                    VERIFY_VOTERID_INTERFACEID, header, true);

            return monoResWrapper;

        } catch (Exception e) {
            logger.error("exception at voterAuthentication: ", e);
            Response response = new Response();
            ResponseHeader respHeader = new ResponseHeader();
            ResponseBody respBody = new ResponseBody();
            respBody.setResponseObj(EXCEPTION_MSG);
            CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
            response.setResponseBody(respBody);
            response.setResponseHeader(respHeader);
            ResponseWrapper resWrapper = new ResponseWrapper();
            resWrapper.setApiResponse(response);
            return Mono.just(resWrapper);
        }
    }

    public Mono<ResponseWrapper> panVerify(PanVerifyRequest apiRequest, Header header) {

        try {

            header.setInterfaceId(VERIFY_PAN_INTERFACEID);

            Mono<Object> dedupeResponse = interfaceAdapter.callExternalService(header, apiRequest.getRequestObj(),
                    VERIFY_PAN_INTERFACEID, true);

            Mono<ResponseWrapper> monoResWrapper = adapterUtil.generateRespWrapper(dedupeResponse,
                    VERIFY_PAN_INTERFACEID, header, true);

            return monoResWrapper;

        } catch (Exception e) {
            logger.error("exception at Pan Verify : ", e);
            Response response = new Response();
            ResponseHeader respHeader = new ResponseHeader();
            ResponseBody respBody = new ResponseBody();
            respBody.setResponseObj(EXCEPTION_MSG);
            CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
            response.setResponseBody(respBody);
            response.setResponseHeader(respHeader);
            ResponseWrapper resWrapper = new ResponseWrapper();
            resWrapper.setApiResponse(response);
            return Mono.just(resWrapper);
        }
    }

    public static String generateBasicAuthHeader(String username, String password) {
        String credentials = username + ":" + password;
        String encodedCredentials = Base64.getEncoder().encodeToString(credentials.getBytes());
        return "Basic " + encodedCredentials;
    }

    public Mono<ResponseWrapper> updateMobileNumService(MobileNumberUpdateRequest apiRequest, Header header,
                                                        String serviceName) {
    	logger.debug("Mobile Number Update Request,"+apiRequest);
        String phone = apiRequest.getRequestObj().getPhone();
        logger.debug("Received phone,"+phone);
        JSONObject serviceRequest = new JSONObject();
        serviceRequest.put("phone", phone);
        try {
            header.setInterfaceId(MOBILENUMBERUPDATE_INTERFACEID);
            Mono<Object> externalServiceResponse1 = interfaceAdapter.callExternalService(header, apiRequest,
                    MOBILENUMBERUPDATE_INTERFACEID, true);


            MobileNumberUpdateDedupeFields dedupeRequest = new MobileNumberUpdateDedupeFields();
            dedupeRequest.setId("17");
            dedupeRequest.setGkv("1.1");
            dedupeRequest.setAppid("7");
            dedupeRequest.setMethod("mobileDedupe.mobileNoUpdateSwap");
            dedupeRequest.setCustomerId(apiRequest.getRequestObj().getMemberId());
            dedupeRequest.setCustomerName("");
            dedupeRequest.setPhoneNum1(phone);

            MobileNumberUpdateDedupe dedupe = new MobileNumberUpdateDedupe();
            dedupe.setRequestObj(dedupeRequest);
            logger.debug("dedupe,"+dedupe);
            serviceRequest.put("dedupeRequest", dedupeRequest);
            logger.debug("serviceRequest",serviceRequest);

            header.setInterfaceId(MOBILENUMBERUPDATEDEDUPE_INTERFACEID);
            Mono<Object> externalServiceResponse2 = interfaceAdapter.callExternalService(header, dedupe,
                    MOBILENUMBERUPDATEDEDUPE_INTERFACEID, true);

            externalServiceResponse2.doOnNext(response -> logger.debug("Received response from external service for mobileNumUpdate: {}", response)).subscribe();

            return externalServiceResponse1.flatMap(response -> {
                logger.warn("Response of mobileNumUpdate : {}" + response);
                Response res = new Response();
                ResponseWrapper respWrapper = new ResponseWrapper();
                ResponseBody resBody = new ResponseBody();
                resBody.setResponseObj(new Gson().toJson(response));
                res.setResponseBody(resBody);
                ResponseHeader resHeader = new ResponseHeader();
                CommonUtils.generateHeaderForSuccess(resHeader);
                res.setResponseHeader(resHeader);
                respWrapper.setApiResponse(res);
                return Mono.just(respWrapper);
            });


//			return Mono.zip(externalServiceResponse1, externalServiceResponse2).flatMap(resp -> {
//				Object response1 = resp.getT1();
//				Object response2 = resp.getT2();
//				Map<String, Object> combinedResponse = new HashMap<>();
//				combinedResponse.put("MobileNumberUpdate", response1);
//				combinedResponse.put("MobileNumberUpdateDedupe", response2);
//				ObjectMapper objectMapper = new ObjectMapper();
//				String combinedResponseJson;
//				try {
//					combinedResponseJson = objectMapper.writeValueAsString(combinedResponse);
//				} catch (Exception e) {
//					logger.error("Error serializing combined response to JSON: ", e);
//					combinedResponseJson = "{}";
//				}
//				logger.warn("Combined response: {}", combinedResponseJson);
//
//				Response res = new Response();
//				ResponseWrapper respWrapper = new ResponseWrapper();
//				ResponseBody resBody = new ResponseBody();
//				resBody.setResponseObj(combinedResponseJson);
//				res.setResponseBody(resBody);
//				ResponseHeader resHeader = new ResponseHeader();
//				CommonUtils.generateHeaderForSuccess(resHeader);
//				res.setResponseHeader(resHeader);
//				respWrapper.setApiResponse(res);
//				return Mono.just(respWrapper);
//			});
        } catch (Exception e) {
            logger.error("exception at mobileNumberUpdate: ", e);
            Response response = new Response();
            ResponseHeader respHeader = new ResponseHeader();
            ResponseBody respBody = new ResponseBody();
            respBody.setResponseObj(EXCEPTION_MSG);
            CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
            response.setResponseBody(respBody);
            response.setResponseHeader(respHeader);
            ResponseWrapper resWrapper = new ResponseWrapper();
            resWrapper.setApiResponse(response);
            return Mono.just(resWrapper);
        }
    }

    public Mono<ResponseWrapper> drivingLicenseVerify(DrivingLicenseVerifyRequest apiRequest, Header header) {
        if (apiRequest.getRequestObj() != null) {
            apiRequest.getRequestObj().setClientrefnum("" + commonUtilCBS.generateRandomId(1, Integer.MAX_VALUE));
        }
        try {
            header.setInterfaceId(VERIFY_DRIVING_LICENSE_INTERFACEID);
            logger.debug("Printing Final Request : {}", apiRequest);
            logger.debug("Printing Final Header : {}", header);
            Mono<Object> dedupeResponse = interfaceAdapter.callExternalService(header, apiRequest.getRequestObj(),
                    VERIFY_DRIVING_LICENSE_INTERFACEID, true);
            Mono<ResponseWrapper> monoResWrapper = adapterUtil.generateRespWrapper(dedupeResponse,
                    VERIFY_DRIVING_LICENSE_INTERFACEID, header, true);
            return monoResWrapper;
        } catch (Exception e) {
            logger.error("exception at drivinglicense Verify : ", e);
            Response response = new Response();
            ResponseHeader respHeader = new ResponseHeader();
            ResponseBody respBody = new ResponseBody();
            respBody.setResponseObj(EXCEPTION_MSG);
            CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
            response.setResponseBody(respBody);
            response.setResponseHeader(respHeader);
            ResponseWrapper resWrapper = new ResponseWrapper();
            resWrapper.setApiResponse(response);
            return Mono.just(resWrapper);
        }
    }

    public Mono<ResponseWrapper> faceVerify(FaceMatchRequest apiRequest, Header header) {

        try {
            FaceMatchRequestFields faceMatchRequestFields=new FaceMatchRequestFields();
            faceMatchRequestFields.setClientRefId(String.valueOf(CommonUtils.generateRandomNum()));
            faceMatchRequestFields.setCard(apiRequest.getRequestObj().getCard());
            faceMatchRequestFields.setPerson(apiRequest.getRequestObj().getPerson());
            apiRequest.setRequestObj(faceMatchRequestFields);
            header.setInterfaceId(VERIFY_FACE_INTERFACEID);

            Mono<Object> faceMatchResponse = interfaceAdapter.callExternalService(header, apiRequest.getRequestObj(),
                    VERIFY_FACE_INTERFACEID, true);

            Mono<ResponseWrapper> monoResWrapper = adapterUtil.generateRespWrapper(faceMatchResponse,
                    VERIFY_FACE_INTERFACEID, header, true);

            return monoResWrapper;

        } catch (Exception e) {
            logger.error("exception at Face Verify : ", e);
            Response response = new Response();
            ResponseHeader respHeader = new ResponseHeader();
            ResponseBody respBody = new ResponseBody();
            respBody.setResponseObj(EXCEPTION_MSG);
            CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
            response.setResponseBody(respBody);
            response.setResponseHeader(respHeader);
            ResponseWrapper resWrapper = new ResponseWrapper();
            resWrapper.setApiResponse(response);
            return Mono.just(resWrapper);
        }
    }

    public Mono<ResponseWrapper> dedupeCustImage(DedupeFaceRequest apiRequest, Header header) {
		try {
            DedupeFaceFields dedupeFaceFields = apiRequest.getRequestObj();
            dedupeFaceFields.setGkv("1.1");
            dedupeFaceFields.setId("1");
            dedupeFaceFields.setMethod("base64image");
            dedupeFaceFields.setIdType("4");
            dedupeFaceFields.setDedupeId(apiRequest.getRequestObj().getDedupeId());

            header.setInterfaceId(DEDUPE_CUST_IMAGE_INTERFACEID);
            DedupeFaceRequest dedupeFaceRequest = new DedupeFaceRequest();
            dedupeFaceRequest.setRequestObj(dedupeFaceFields);

            Mono<Object> dedupeResponse = interfaceAdapter.callExternalService(header, dedupeFaceRequest.getRequestObj(),
                    DEDUPE_CUST_IMAGE_INTERFACEID, true);

            Mono<ResponseWrapper> monoResWrapper = adapterUtil.generateRespWrapper(dedupeResponse,
                    apiRequest.getInterfaceName(), header, true);
            return monoResWrapper;

        } catch (Exception e) {
            logger.error("exception at dedupe Cust Image: ", e);
            Response response = new Response();
            ResponseHeader respHeader = new ResponseHeader();
            ResponseBody respBody = new ResponseBody();

            respBody.setResponseObj(EXCEPTION_MSG);
            CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
            response.setResponseBody(respBody);
            response.setResponseHeader(respHeader);
            ResponseWrapper resWrapper = new ResponseWrapper();
            resWrapper.setApiResponse(response);
            return Mono.just(resWrapper);
        }
    }

}
