package com.iexceed.appzillonbanking.cagl.collection.service;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iexceed.appzillonbanking.cagl.collection.constants.CommonConstants;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.TbUacoQRDtls;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.TbUacoQRDtlsId;
import com.iexceed.appzillonbanking.cagl.collection.payload.QRUPIPaymentRequest;
import com.iexceed.appzillonbanking.cagl.collection.payload.VerifyQRPaymentRequest;
import com.iexceed.appzillonbanking.cagl.collection.repository.ab.TbUacoQRDtlsRepo;
import com.iexceed.appzillonbanking.cagl.collection.util.GenerateQR;
import com.iexceed.appzillonbanking.core.payload.Header;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.interfaceAdapter.service.InterfaceAdapter;

import reactor.core.publisher.Mono;

@Service
public class UPIPaymentService {

	@Autowired
	private InterfaceAdapter interfaceAdapter;

	@Autowired
	private TbUacoQRDtlsRepo tbUacoQRDtlsRepo;

	private static final String QR_UPI_PAYMENT_INTF = "QRUPIPayment";

	private static final Logger logger = LogManager.getLogger(UPIPaymentService.class);

	public Mono<Response> qrUPIPayment(QRUPIPaymentRequest qrUPIPaymentRequest, Header header) {
		try {
			String custTxnId = qrUPIPaymentRequest.getRequestObj().getCustomerId() + System.currentTimeMillis();
			qrUPIPaymentRequest.getRequestObj().setCustomerTxnId(custTxnId);
			logger.debug("Request to QRUPIPayment::{}", qrUPIPaymentRequest);
			Mono<Object> responseMono = interfaceAdapter.callExternalService(header, qrUPIPaymentRequest,
					QR_UPI_PAYMENT_INTF, true);
			return responseMono.flatMap(val -> {
				logger.debug("API response::{}", val);
				return Mono.just(validateQRResponse(val, qrUPIPaymentRequest));
			});
		} catch (Exception ex) {
			logger.error(CommonConstants.EXCEP_OCCURED, ex);
			ResponseHeader responseHeader = new ResponseHeader();
			CommonUtils.generateHeaderForGenericError(responseHeader);
			Response response = Response.builder().responseHeader(responseHeader).responseBody(null).build();
			return Mono.just(response);
		}
	}

	private Response validateQRResponse(Object qrAPIResp, QRUPIPaymentRequest qrUPIPaymentRequest) {
		ResponseBody responseBody = new ResponseBody();
		ResponseHeader responseHeader = new ResponseHeader();
		if (qrAPIResp instanceof HashMap<?, ?> || qrAPIResp instanceof ArrayList<?>) {
			try {
				String extApiResponse = new ObjectMapper().writeValueAsString(qrAPIResp);

				TbUacoQRDtls tbUacoQRDtls = TbUacoQRDtls.builder().appId(qrUPIPaymentRequest.getAppId())
						.customerId(qrUPIPaymentRequest.getRequestObj().getCustomerId())
						.billNumber(qrUPIPaymentRequest.getRequestObj().getBillNumber()).intentLink(null)
						.remarks(qrUPIPaymentRequest.getRequestObj().getRemarks())
						.customerName(qrUPIPaymentRequest.getRequestObj().getCustomerName()).apiStatusCode(null)
						.customerTxnId(qrUPIPaymentRequest.getRequestObj().getCustomerTxnId())
						.createTs(new Timestamp(new Date().getTime())).status("INITIATED").payload(null).build();

				if (!CommonUtils.checkStringNullOrEmpty(extApiResponse)) {
					JSONObject responseJSON = new JSONObject(extApiResponse);
					tbUacoQRDtls.setApiStatusCode(String.valueOf(responseJSON.getInt("statusCode")));
					if (responseJSON.has("status") && Boolean.TRUE.equals(responseJSON.getBoolean("status"))
							&& responseJSON.has("statusCode") && responseJSON.getInt("statusCode") == 10000) {
						logger.debug("Success response from the API");
						tbUacoQRDtls.setIntentLink(responseJSON.getJSONObject("data").getString("intentLink"));
						String base64QR = GenerateQR
								.generateQRCode(responseJSON.getJSONObject("data").getString("intentLink"));
						logger.debug("base64QR::{}", base64QR);
						responseJSON.getJSONObject("data").put("intentBase64", base64QR);
						responseBody.setResponseObj(responseJSON.toString(0));
						CommonUtils.generateHeaderForSuccess(responseHeader);
					} else {
						logger.debug("Failure response status from the API.");
						CommonUtils.generateHeaderForFailure(responseHeader,
								responseJSON.has("message") ? responseJSON.getString("message") : "");
						responseBody.setResponseObj(extApiResponse);
					}
				}
				logger.debug("Final data to tbUacoQRDtls::{}", tbUacoQRDtls);
				tbUacoQRDtlsRepo.save(tbUacoQRDtls);
			} catch (Exception e) {
				logger.error(CommonConstants.EXCEP_OCCURED, e);
				CommonUtils.generateHeaderForGenericError(responseHeader);
			}
		}
		return Response.builder().responseHeader(responseHeader).responseBody(responseBody).build();
	}

	/**
	 * Method to fetch the QR UPI Payment Status
	 * 
	 * @param qrUPIPaymentRequest
	 * @param header
	 * @return
	 */
	public Response refreshQRUPIPayment(QRUPIPaymentRequest qrUPIPaymentRequest, Header header) {
		ResponseHeader responseHeader = new ResponseHeader();
		Response apiResponse = new Response();
		try {
			logger.debug("Request to refreshQRUPIPayment::{}", qrUPIPaymentRequest);
			TbUacoQRDtlsId tbUacoQRDtlsId = TbUacoQRDtlsId.builder().appId(qrUPIPaymentRequest.getAppId())
					.billNumber(qrUPIPaymentRequest.getRequestObj().getBillNumber())
					.customerId(qrUPIPaymentRequest.getRequestObj().getCustomerId()).build();
			Optional<TbUacoQRDtls> tbUacoQRDtlsOpt = tbUacoQRDtlsRepo.findById(tbUacoQRDtlsId);
			logger.debug("refreshQRUPIPayment tbUacoQRDtlsOpt::{}", tbUacoQRDtlsOpt);
			if (tbUacoQRDtlsOpt.isPresent()) {
				CommonUtils.generateHeaderForSuccess(responseHeader);
				JSONObject responseJSON = new JSONObject();
				responseJSON.put("txnId", tbUacoQRDtlsOpt.get().getCustomerTxnId());
				responseJSON.put("status", tbUacoQRDtlsOpt.get().getStatus());
				ResponseBody responseBody = ResponseBody.builder().responseObj(responseJSON.toString()).build();
				apiResponse.setResponseHeader(responseHeader);
				apiResponse.setResponseBody(responseBody);
			} else {
				CommonUtils.generateHeaderForNoResult(responseHeader);
				ResponseBody responseBody = ResponseBody.builder().responseObj(null).build();
				apiResponse.setResponseHeader(responseHeader);
				apiResponse.setResponseBody(responseBody);
			}
		} catch (Exception ex) {
			logger.error(CommonConstants.EXCEP_OCCURED, ex);
			CommonUtils.generateHeaderForGenericError(responseHeader);
			ResponseBody responseBody = ResponseBody.builder().responseObj(null).build();
			apiResponse.setResponseHeader(responseHeader);
			apiResponse.setResponseBody(responseBody);
		}
		return apiResponse;
	}

	/**
	 * Method to fetch the QR UPI Payment Statuses based on list of memberIds.
	 * 
	 * @param qrUPIPaymentRequest
	 * @param header
	 * @return
	 */
	public Response verifyQRPaymentStatus(VerifyQRPaymentRequest verifyQRPaymentRequest, Header header) {
		ResponseHeader responseHeader = new ResponseHeader();
		Response apiResponse = new Response();
		try {
			logger.debug("Request to verifyQRPayment::{}", verifyQRPaymentRequest);
			if (!CommonUtils.checkStringNullOrEmpty(verifyQRPaymentRequest.getRequestObj().getCustIds())) {
				List<String> memberList = Arrays.asList(verifyQRPaymentRequest.getRequestObj().getCustIds().split("~"));
				List<TbUacoQRDtls> tbUacoQRDtlsLst = tbUacoQRDtlsRepo.findByCustomerIdIn(memberList);
				logger.debug("verifyQRPayment tbUacoQRDtlsList::{}", tbUacoQRDtlsLst);
				if (!tbUacoQRDtlsLst.isEmpty()) {
					CommonUtils.generateHeaderForSuccess(responseHeader);
					List<Map<String, String>> filteredQRDtlsLst = tbUacoQRDtlsLst.stream()
							.map(tbUacoQRDtls -> Map.of("customerId", tbUacoQRDtls.getCustomerId(), "status",
									tbUacoQRDtls.getStatus(), "billNumber", tbUacoQRDtls.getBillNumber()))
							.toList();
					String qrStatusesStr = new ObjectMapper().writeValueAsString(filteredQRDtlsLst);
					ResponseBody responseBody = ResponseBody.builder().responseObj(qrStatusesStr).build();
					apiResponse.setResponseHeader(responseHeader);
					apiResponse.setResponseBody(responseBody);
				} else {
					CommonUtils.generateHeaderForNoResult(responseHeader);
					ResponseBody responseBody = ResponseBody.builder().responseObj(null).build();
					apiResponse.setResponseHeader(responseHeader);
					apiResponse.setResponseBody(responseBody);
				}
			} else {
				CommonUtils.generateHeaderForFailure(responseHeader, "Invalid Request");
				ResponseBody responseBody = ResponseBody.builder().responseObj(null).build();
				apiResponse.setResponseHeader(responseHeader);
				apiResponse.setResponseBody(responseBody);
			}
		} catch (Exception ex) {
			logger.error(CommonConstants.EXCEP_OCCURED, ex);
			CommonUtils.generateHeaderForGenericError(responseHeader);
			ResponseBody responseBody = ResponseBody.builder().responseObj(null).build();
			apiResponse.setResponseHeader(responseHeader);
			apiResponse.setResponseBody(responseBody);
		}
		return apiResponse;
	}

	/**
	 * Method to update the status of the QR UPI Payment (To be used only for
	 * internal testing, Since the actual update happens from the Fingpay invoking
	 * exposed API)
	 * 
	 * @param qrUPIPaymentRequest
	 * @param header
	 * @return
	 */
	public Response updateQRUPIPaymentStatus(QRUPIPaymentRequest qrUPIPaymentRequest, Header header) {
		ResponseHeader responseHeader = new ResponseHeader();
		Response apiResponse = new Response();
		try {
			logger.debug("Request to updateQRUPIPaymentStatus::{}", qrUPIPaymentRequest);
			TbUacoQRDtlsId tbUacoQRDtlsId = TbUacoQRDtlsId.builder().appId(qrUPIPaymentRequest.getAppId())
					.billNumber(qrUPIPaymentRequest.getRequestObj().getBillNumber())
					.customerId(qrUPIPaymentRequest.getRequestObj().getCustomerId()).build();
			Optional<TbUacoQRDtls> tbUacoQRDtlsOpt = tbUacoQRDtlsRepo.findById(tbUacoQRDtlsId);
			logger.debug("updateQRUPIPaymentStatus tbUacoQRDtlsOpt::{}", tbUacoQRDtlsOpt);
			if (tbUacoQRDtlsOpt.isPresent()) {
				TbUacoQRDtls tbUacoQRDtls = tbUacoQRDtlsOpt.get();
				tbUacoQRDtls.setStatus(qrUPIPaymentRequest.getRequestObj().getStatus());
				tbUacoQRDtlsRepo.save(tbUacoQRDtlsOpt.get());
				CommonUtils.generateHeaderForSuccess(responseHeader);
			} else {
				CommonUtils.generateHeaderForNoResult(responseHeader);
			}
		} catch (Exception ex) {
			logger.error(CommonConstants.EXCEP_OCCURED, ex);
			CommonUtils.generateHeaderForGenericError(responseHeader);
		}
		ResponseBody responseBody = ResponseBody.builder().responseObj(null).build();
		apiResponse.setResponseHeader(responseHeader);
		apiResponse.setResponseBody(responseBody);
		return apiResponse;
	}
}
