package com.iexceed.appzillonbanking.cagl.collection.service;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iexceed.appzillonbanking.cagl.collection.constants.CommonConstants;
import com.iexceed.appzillonbanking.cagl.collection.domain.ab.TbUacoQRDtls;
import com.iexceed.appzillonbanking.cagl.collection.payload.QRPaymentCBRequest;
import com.iexceed.appzillonbanking.cagl.collection.repository.ab.TbUacoQRDtlsRepo;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;

@Service
public class ExternalAPIService {

	private static final Logger logger = LogManager.getLogger(ExternalAPIService.class);

	@Autowired
	private TbUacoQRDtlsRepo tbUacoQRDtlsRepo;

	public Response qrPaymentCB(QRPaymentCBRequest qrPaymentCBRequest) {
		ResponseHeader responseHeader = new ResponseHeader();
		try {
			logger.debug("Request to qrPaymentCB::{}", qrPaymentCBRequest);
			if (!CommonUtils.checkStringNullOrEmpty(qrPaymentCBRequest.getBillNumber())) {
				Optional<TbUacoQRDtls> tbUacoQRDtlsOpt = tbUacoQRDtlsRepo
						.findByBillNumber(qrPaymentCBRequest.getBillNumber());
				logger.debug(tbUacoQRDtlsOpt);
				if (tbUacoQRDtlsOpt.isPresent()) {
					if(null != qrPaymentCBRequest.getTransactionstatus()) {
						tbUacoQRDtlsOpt.get().setStatus(qrPaymentCBRequest.getTransactionstatus().toUpperCase());	
					} else {
						tbUacoQRDtlsOpt.get().setStatus(qrPaymentCBRequest.getTransactionstatus());
					}
					tbUacoQRDtlsRepo.save(tbUacoQRDtlsOpt.get());
					CommonUtils.generateHeaderForSuccess(responseHeader);
				} else {
					CommonUtils.generateHeaderForFailure(responseHeader,
							"No record found for the matching bill Number");
					responseHeader.setResponseCode("NR-BILLNUM");
				}
			} else {
				CommonUtils.generateHeaderForFailure(responseHeader, "Invalid Request");
				responseHeader.setResponseCode("INVL-BILLNUM");
			}
		} catch (Exception ex) {
			logger.error(CommonConstants.EXCEP_OCCURED, ex);
			CommonUtils.generateHeaderForGenericError(responseHeader);
		}
		return Response.builder().responseHeader(responseHeader).responseBody(null).build();
	}
}
