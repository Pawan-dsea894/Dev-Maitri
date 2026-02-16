package com.iexceed.appzillonbanking.cagl.service;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iexceed.appzillonbanking.cagl.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cagl.domain.cus.GkLoanData;
import com.iexceed.appzillonbanking.cagl.entity.GkCustDemoData;
import com.iexceed.appzillonbanking.cagl.payload.ApplyLoanRequest;
import com.iexceed.appzillonbanking.cagl.payload.CreateAppRequest;
import com.iexceed.appzillonbanking.cagl.payload.CustomerDtls;
import com.iexceed.appzillonbanking.cagl.repository.ab.ApplicationMasterRepository;
import com.iexceed.appzillonbanking.cagl.repository.cus.GkCustDemoDataRepository;
import com.iexceed.appzillonbanking.cagl.repository.cus.GkLoanDataRepo;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.core.utils.CommonUtilsCBS;

@Service
public class LoanService {

	private static final Logger logger = LogManager.getLogger(LoanService.class);

	@Autowired
	private ApplicationMasterRepository applicationMasterRepo;

	@Autowired
	private GkLoanDataRepo loanDtlsRepo;

	
	@Autowired
	private GkCustDemoDataRepository custDtlsRepo;

	public static final String EXCEPTION_MSG = "Something went wrong, Please try again!!";
	public static final String EXCEPTION_OCCURED = "Exception occurred";

	public Response createApplication(CreateAppRequest apiRequest) {
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();

		ApplicationMaster appMaster = new ApplicationMaster();
		CommonUtilsCBS commonUtilsCBS = new CommonUtilsCBS();
		logger.debug("inside createApplication:{} ", apiRequest);

		try {
			appMaster.setAppId(apiRequest.getAppId());
			int applicationId = commonUtilsCBS.generateRandomId(1, Integer.MAX_VALUE);
			appMaster.setApplicationDate(apiRequest.getRequestObj().getApplicationDate());
			appMaster.setApplicationId(String.valueOf(applicationId));
			appMaster.setApplicationType(apiRequest.getRequestObj().getApplicationType());
			appMaster.setCreatedBy(apiRequest.getRequestObj().getCreatedBy());
			appMaster.setCustomerId(apiRequest.getRequestObj().getCustomerId());
			appMaster.setKendraId(apiRequest.getRequestObj().getKendraId());
			applicationMasterRepo.save(appMaster);
			respBody.setResponseObj("Application created successfully!!");
			CommonUtils.generateHeaderForSuccess(respHeader);

			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
		} catch (Exception e) {
			logger.error("exception at createApplication: ", e);
			respBody.setResponseObj(EXCEPTION_MSG);
			CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);

			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
		}

		return response;
	}

	public Response applyLoan(ApplyLoanRequest apiRequest) {
		Response response = new Response();

		String currentScr = apiRequest.getRequestObj().getCurrentScr();
		logger.debug("inside applyLoan:{} and currentScr is:{}", apiRequest, currentScr);
		switch (currentScr) {

		case "LOAN":
			response = saveLoanDetails(apiRequest);
			break;

		case "CUSTOMER":
			response = saveCustomerDetails(apiRequest);
			break;

		default:
			break;

		}
		return response;
	}

	private Response saveCustomerDetails(ApplyLoanRequest apiRequest) {
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();
		CustomerDtls custDtlsPaylod = apiRequest.getRequestObj().getCustomerDetails();

		try {
			GkCustDemoData custDtlsDomain = new GkCustDemoData();
			custDtlsDomain.setCustomerId(custDtlsPaylod.getCustomerId());
			custDtlsDomain.setCustomerName(custDtlsPaylod.getCustomerName());
			custDtlsDomain.setGroupId(custDtlsPaylod.getGroupId());
			custDtlsDomain.setKendraId(custDtlsPaylod.getKendraId());
			custDtlsDomain.setKendraName(custDtlsPaylod.getKendraName());
			custDtlsDomain.setDob(custDtlsPaylod.getKycDetails().getDob());
			custDtlsDomain.setAddress(custDtlsPaylod.getKycDetails().getAddress());

			custDtlsRepo.save(custDtlsDomain);
			respBody.setResponseObj("Customer Details Saved successfully!!");
			CommonUtils.generateHeaderForSuccess(respHeader);

			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
		} catch (Exception e) {
			logger.error("exception at saveCustomerDetails: ", e);
			respBody.setResponseObj(EXCEPTION_MSG);
			CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);

			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
		}

		return response;

	}

	private Response saveLoanDetails(ApplyLoanRequest apiRequest) {
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();
		//LoanDtls loanDtlsPayload = apiRequest.getRequestObj().getLoanDetails();
		try {
			GkLoanData loanDtlsDomain = new GkLoanData();
			
			//create the fields for saving

			loanDtlsRepo.save(loanDtlsDomain);
			respBody.setResponseObj("Loan Details Saved successfully!!");
			CommonUtils.generateHeaderForSuccess(respHeader);

			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
		} catch (Exception e) {
			logger.error("exception at saveLoanDetails: ", e);
			respBody.setResponseObj(EXCEPTION_MSG);
			CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);

			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
		}

		return response;
	}

}