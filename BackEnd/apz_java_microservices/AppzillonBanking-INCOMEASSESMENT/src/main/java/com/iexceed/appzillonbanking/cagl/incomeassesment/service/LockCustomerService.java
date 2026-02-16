package com.iexceed.appzillonbanking.cagl.incomeassesment.service;

import java.sql.Timestamp;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iexceed.appzillonbanking.cagl.incomeassesment.domain.ab.LockCustomerDtls;
import com.iexceed.appzillonbanking.cagl.incomeassesment.payload.LockCustomerRequest;
import com.iexceed.appzillonbanking.cagl.incomeassesment.repository.ab.TbLockCustomerRepository;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;

@Service
public class LockCustomerService {

	private static final Logger logger = LogManager.getLogger(IncomeAssesmentService.class);

	@Autowired
	private TbLockCustomerRepository customerRepository;

	public static final String EXCEPTION_MSG = "Something went wrong, Please try again!!";
	public static final String EXCEPTION_OCCURED = "Exception occurred";

	public Response saveApplication(LockCustomerRequest apiRequest) {
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();

		
		logger.debug("inside createApplication:{} ", apiRequest);
		try {
			String applicationId = apiRequest.getRequestObj().getApplicationId();
			String roleId = apiRequest.getRequestObj().getRoleId();
			String userId = apiRequest.getRequestObj().getCreatedBy();
			
			LockCustomerDtls customerDtls = new LockCustomerDtls();
			customerDtls.setApplicationId(applicationId);
			customerDtls.setCreatedBy(apiRequest.getRequestObj().getCreatedBy());
			customerDtls.setRoleId(roleId);
			
			LockCustomerDtls lockCustomerDtls = customerRepository.fetchApplications(applicationId);
			if (lockCustomerDtls != null) {
				String lockedUserId = lockCustomerDtls.getCreatedBy();
				if(lockedUserId.equals(userId)) {
					customerDtls.setCreatedTs(new Timestamp(System.currentTimeMillis()));
					customerRepository.save(customerDtls);
					respBody.setResponseObj("Application is locked successfully!!");
					CommonUtils.generateHeaderForSuccess(respHeader);
				} else {
					logger.error("Application is locked", applicationId);
					respBody.setResponseObj("Application is locked by user:" + lockCustomerDtls.getCreatedBy());
					CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
				}
			} else {
				
				customerDtls.setCreatedTs(new Timestamp(System.currentTimeMillis()));
				customerRepository.save(customerDtls);
			respBody.setResponseObj("Application is locked successfully!!");
			CommonUtils.generateHeaderForSuccess(respHeader);
		}
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

	public Response deleteApplicationLockCustomer(String applicationId) {
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();
		JSONObject responeObject = new JSONObject();

		customerRepository.deleteByApplicationId(applicationId);
		responeObject.put("message", "Record Deleted Successfully!!!");
		respBody.setResponseObj(responeObject.toString());
		CommonUtils.generateHeaderForSuccess(respHeader);
		response.setResponseBody(respBody);
		response.setResponseHeader(respHeader);
		return response;
	}

	public Response fetchApplicationLockCustomer(String applicationId) {
		return null;
//		Response response = new Response();
//		ResponseHeader respHeader = new ResponseHeader();
//		ResponseBody respBody = new ResponseBody();
//		JSONObject responeObject = new JSONObject();
//		
//		Optional<LockCustomerDtls> lockCustomerDtls=customerRepository.findByApplicationId(applicationId);
//		if(lockCustomerDtls.isPresent()) {
//			LockCustomerDtls customerDtls = lockCustomerDtls.get();
//			
//		}
//		return customerDtls;

//		
	}

}
