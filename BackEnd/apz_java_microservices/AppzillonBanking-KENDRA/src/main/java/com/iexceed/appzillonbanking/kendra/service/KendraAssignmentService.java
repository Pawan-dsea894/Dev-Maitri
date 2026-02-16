package com.iexceed.appzillonbanking.kendra.service;

import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.iexceed.appzillonbanking.core.payload.Header;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.kendra.domain.cus.KendraAssignment;
import com.iexceed.appzillonbanking.kendra.payload.EditOrDeleteKendraAssignmentRequest;
import com.iexceed.appzillonbanking.kendra.payload.KendraAssignmentFetchRequest;
import com.iexceed.appzillonbanking.kendra.payload.KendraAssignmentObj;
import com.iexceed.appzillonbanking.kendra.payload.KendraAssignmentRequest;
import com.iexceed.appzillonbanking.kendra.payload.KendraDetailsUpdateRequest;
import com.iexceed.appzillonbanking.kendra.payload.KendraDetailsUpdateRequestFields;
import com.iexceed.appzillonbanking.kendra.payload.KendraId;
import com.iexceed.appzillonbanking.kendra.payload.KendraIds;
import com.iexceed.appzillonbanking.kendra.payload.MultiKendraDetailsUpdateRequest;
import com.iexceed.appzillonbanking.kendra.payload.MultiKendraDetailsUpdateRequestFields;
import com.iexceed.appzillonbanking.kendra.repository.cus.KendraAssignmentRepository;

import reactor.core.publisher.Mono;

@Service
public class KendraAssignmentService {

	private static final Logger logger = LogManager.getLogger(KendraAssignmentService.class);

	public static final String EXCEPTION_MSG = "Something went wrong, Please try again!! :";
	public static final String EXCEPTION_OCCURED = "Exception occurred :";
	public static final String INVALID_REQUEST = "Incoming Request is not correct";
	//private static final Random random = new Random();
	private static final SecureRandom secureRandom = new SecureRandom();

	@Autowired
	private KendraAssignmentRepository kendraAssignmentRepository;

	@Autowired
	private KendraService kendraService;

	public Mono<ResponseWrapper> saveApplication(KendraAssignmentRequest apiRequest, Header header) {
		logger.debug("Inside saveApplication service layer of KendraAssignment");
		logger.debug("Printing Incoming apirequest:" + apiRequest);
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();
		ResponseWrapper respWrapper = new ResponseWrapper();
		JSONArray responseArray = new JSONArray();
		JSONArray t24ResponseArray = new JSONArray();
		KendraDetailsUpdateRequest kendraDetailsUpdateRequest = new KendraDetailsUpdateRequest();
		KendraDetailsUpdateRequestFields kendraDetailsUpdateRequestFields = new KendraDetailsUpdateRequestFields();
		int iterCount = 0;
		Mono<ResponseWrapper> t24ServiceResponse = Mono.just(new ResponseWrapper());
		List<Mono<ResponseWrapper>> serviceResponsesList = new ArrayList<>();

		try {
			String createdTime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss:SSSXXX")
					.format(new Timestamp(System.currentTimeMillis()));
			logger.debug("Current TimeStamp:" + createdTime);

			List<KendraAssignmentObj> kendraAssignmentListObj = apiRequest.getRequestObj().getKendraAssignmentListObj();
			logger.debug("list size:" + kendraAssignmentListObj.size());

			String batchNo = generateRandomNumber(17);
			logger.debug("Printing serialNo:" + batchNo);

			Set<String> uniqueKmIds = kendraAssignmentListObj.stream().map(KendraAssignmentObj::getKmId)
					.collect(Collectors.toSet());
			logger.debug("Printing uniqueKmIds:{}", uniqueKmIds);

			if (kendraAssignmentListObj.size() > 0) {
				for (KendraAssignmentObj obj : kendraAssignmentListObj) {
					logger.debug(iterCount + " object value: " + obj);
					KendraAssignment kendraRepoInstance = new KendraAssignment();
					String kendraAssignmentId = generateRandomNumber(17);
					logger.debug("kendraAssignmentId for " + obj.getKmId() + " is :" + kendraAssignmentId);

					kendraRepoInstance.setKendraAssignmentId(kendraAssignmentId);
					kendraRepoInstance.setKmId(obj.getKmId());
					kendraRepoInstance.setKendraId(obj.getKendraId());
					kendraRepoInstance.setKmName(obj.getKmName());
					kendraRepoInstance.setStartDate(obj.getStartDate());
					kendraRepoInstance.setEndDate(obj.getEndDate());
					kendraRepoInstance.setAssignmentType(obj.getAssignmentType());
					kendraRepoInstance.setOldKmId(obj.getOldKmId());
					kendraRepoInstance.setOldKmName(obj.getOldKmName());
					kendraRepoInstance.setBranchId(obj.getBranchId());
					kendraRepoInstance.setCreateTs(createdTime);
					kendraRepoInstance.setCreateBy(obj.getCreatedBy());
					kendraRepoInstance.setBatchNo(batchNo);
					kendraRepoInstance.setAllocationType(obj.getAssignmentType());
					kendraRepoInstance.setRemarks(obj.getRemarks());
					kendraRepoInstance.setAddInfo1(obj.getAddInfo1());
					kendraRepoInstance.setAddInfo2(obj.getAddInfo2());

					kendraAssignmentRepository.save(kendraRepoInstance);
					
					if ("Temporary".equalsIgnoreCase(obj.getAssignmentType())) {
						
						Optional<KendraAssignment> oldKmRecOp = kendraAssignmentRepository
								.findByKmIdAndKendraIdAndStartDateAndEndDateBetween(obj.getOldKmId(),
										obj.getAssignmentType(), obj.getKendraId(),obj.getStartDate(),
										obj.getEndDate());
						logger.debug("oldKmRecOp is :" + oldKmRecOp);
						if (oldKmRecOp.isPresent()) {
							logger.debug("new Km record is :" + kendraRepoInstance);
							KendraAssignment kendraAssignmentRec = oldKmRecOp.get();
							kendraAssignmentRec.setKmId(null);
							kendraAssignmentRepository.save(kendraAssignmentRec);
							logger.debug("Updated privious record :" + kendraAssignmentRec);
							//kendraAssignmentRepository.deleteByKendraIdAndkmId(obj.getKendraId(), obj.getOldKmId());
						
						} else {
							logger.info("No record found with same period");
						}
					}

					JSONObject resp = new JSONObject();
					resp.put("kendraAssignmentId", kendraAssignmentId);
					resp.put("batchNo", batchNo);
					responseArray.put(resp);
					iterCount++;
				}

				String assignmentType = kendraAssignmentListObj.get(0).getAssignmentType();
				if ("Temporary".equalsIgnoreCase(assignmentType)) {
					JSONObject internalResp = new JSONObject();
					internalResp.put("status", "success");
					internalResp.put("batchNo", batchNo);
					internalResp.put("message", "Temporary Kendra Assignment details saved successfully!!");
					respBody.setResponseObj(internalResp.toString());
					CommonUtils.generateHeaderForSuccess(respHeader);
					response.setResponseBody(respBody);
					response.setResponseHeader(respHeader);
					respHeader.setResponseMessage("Insertion Success");
					respWrapper.setApiResponse(response);
 
					return Mono.just(respWrapper);
				}
				//below code is to call t24 if allocation is permanant
				// one to one , one to many and many to many
				List<Response> serviceResponse = new ArrayList<>();
				for (String kmId : uniqueKmIds) {
					List<KendraAssignmentObj> individualKmKendraList = new ArrayList<>();
					logger.debug("Iterated KmId is :" + kmId);
					for (KendraAssignmentObj obj : kendraAssignmentListObj) {
						if ("Permanent".equalsIgnoreCase(obj.getAssignmentType())
								&& kmId.equalsIgnoreCase(obj.getKmId())) {
							individualKmKendraList.add(obj);
						}
					}
					logger.debug("Calling T24");
					// forming below request to call T24-API : START
					int itr = 0;
					String[] allKendras = new String[individualKmKendraList.size()];
					if (individualKmKendraList.size() > 0) {
						for (KendraAssignmentObj obj : individualKmKendraList) {
							if ("Permanent".equalsIgnoreCase(obj.getAssignmentType())) {
								allKendras[itr] = obj.getKendraId();
								itr++;
							}
						}
					}
					logger.debug("Printing List of all KendraId's :" + allKendras);
					logger.debug("assignment type is Permanent");
					KendraId KendraId = new KendraId();
					List<KendraId> kendraIdList = new ArrayList<>();

					KendraId.setKendraId(allKendras);
					KendraId.setFromKM(individualKmKendraList.get(0).getOldKmId());
					kendraIdList.add(KendraId);

					kendraDetailsUpdateRequestFields.setToKM(individualKmKendraList.get(0).getKmId());
					kendraDetailsUpdateRequestFields.setFromBranch(kendraAssignmentListObj.get(0).getBranchId());
					kendraDetailsUpdateRequestFields.setKendraIndicator("MARKED");
					kendraDetailsUpdateRequestFields.setReason("UNIFIEDKENDRA");
					kendraDetailsUpdateRequestFields.setBranchId(kendraAssignmentListObj.get(0).getBranchId());
					kendraDetailsUpdateRequestFields.setKendraIds(kendraIdList);
					kendraDetailsUpdateRequest.setRequestObj(kendraDetailsUpdateRequestFields);
					logger.debug("Final Constructed request is to call T24 API:" + kendraDetailsUpdateRequest);

					t24ServiceResponse = kendraService
							.kendraDetailsUpdate(kendraDetailsUpdateRequest, header).doOnSuccess(responseWrapper -> {
								logger.debug("Received Response Wrapper of Kendra Permanant allocation for kmId"+kmId+" is "+responseWrapper);
								Response apiResponse = responseWrapper.getApiResponse();
								serviceResponse.add(apiResponse);
								JSONObject t24Response = new JSONObject(responseWrapper.getApiResponse().getResponseBody().getResponseObj());
	                            t24ResponseArray.put(t24Response.toString()); 
							});
					serviceResponsesList.add(t24ServiceResponse);
				}
				
				  return Mono.when(serviceResponsesList)
			                .then(Mono.defer(() -> {
			                	
			                	JSONObject internalResp = new JSONObject();
			                	
			                    // After all service calls are completed, prepare the final response
			                	internalResp.put("t24Response", t24ResponseArray);
			                	internalResp.put("status", "success");
			                	internalResp.put("message", "Kendra Assignment details saved successfully!!");

			                    respBody.setResponseObj(internalResp.toString());
			                    CommonUtils.generateHeaderForSuccess(respHeader);
			                    response.setResponseBody(respBody);
			                    response.setResponseHeader(respHeader);
			                    respHeader.setResponseMessage("Insertion Success");
			                    respWrapper.setApiResponse(response);

			                    // Return the final response
			                    return Mono.just(respWrapper);
			                }));

				// T24-API Calling : END
			}
		} catch (Exception e) {
			logger.error(EXCEPTION_OCCURED + e);
			respBody.setResponseObj(e.getMessage().toString());
			CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
			respHeader.setResponseMessage(EXCEPTION_OCCURED + e.getMessage().toString());
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
			respWrapper.setApiResponse(response);
		}

		return Mono.just(respWrapper);
	}

	public Response fetchKendraAssignmentRecord(KendraAssignmentFetchRequest apiRequest) {
		logger.debug("Inside fetchKendraAssignmentRecord service layer of KendraAssignment");
		logger.debug("Printing Incoming apiRequest:" + apiRequest);
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();

		try {
			String branchId = apiRequest.getRequestObj().getBranchId();
			if (branchId != null) {
				Optional<List<KendraAssignment>> kendraAssignRecordOp = kendraAssignmentRepository
						.findByBranchId(branchId);
				if (kendraAssignRecordOp.isPresent()) {
					List<KendraAssignment> KendraAssignmentRecords = kendraAssignRecordOp.get();
					String jsonResponse = new Gson().toJson(KendraAssignmentRecords);
					respBody.setResponseObj(jsonResponse);
					CommonUtils.generateHeaderForSuccess(respHeader);
					respHeader.setResponseMessage("Fetching Success");
					response.setResponseBody(respBody);
					response.setResponseHeader(respHeader);
				}
			}
		} catch (Exception e) {
			logger.error(EXCEPTION_OCCURED + e);
			respBody.setResponseObj(e.getMessage().toString());
			CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
			respHeader.setResponseMessage(EXCEPTION_OCCURED + e.getMessage().toString());
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
		}
		return response;
	}
	
	// Commenting below code SONAR Fix
/*
	public String generateRandomNumber(int lengthOfCode) {
		
		StringBuilder sb = new StringBuilder("1");
		for (int i = 0; i < lengthOfCode - 1; i++) {
			sb.append(random.nextInt(10));
		}
		logger.debug("Printing generated random number -> " + sb);
		return sb.toString();
	}
	*/
	public String generateRandomNumber(int lengthOfCode) {
	    StringBuilder sb = new StringBuilder("1");
	    for (int i = 0; i < lengthOfCode - 1; i++) {
	        sb.append(secureRandom.nextInt(10)); // 0 to 9
	    }
	    logger.debug("Generated secure random number -> {}", sb);
	    return sb.toString();
	}
	

	public Response modifyKendraAssignemetRecordService(EditOrDeleteKendraAssignmentRequest apiRequest) {
		logger.debug("Inside modifyKendraAssignemetRecordService service layer of KendraAssignment");
		logger.debug("Printing Incoming apiRequest: {}", apiRequest);
		String action = apiRequest.getRequestObj().getAction();
		String batchNo = apiRequest.getRequestObj().getBatchNo();
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();
		List<KendraAssignment> kendraAssignmentRecords;
		Optional<List<KendraAssignment>> opKendraAssignmentRecords = kendraAssignmentRepository.findByBatchNo(batchNo);

		if (opKendraAssignmentRecords.isPresent()) {

			kendraAssignmentRecords = opKendraAssignmentRecords.get();
			if ("edit".equalsIgnoreCase(action)) {
				for (KendraAssignment kendraAssignmentRec : kendraAssignmentRecords) {
					kendraAssignmentRec.setStartDate(apiRequest.getRequestObj().getStartDate());
					kendraAssignmentRec.setEndDate(apiRequest.getRequestObj().getEndDate());
					kendraAssignmentRec.setRemarks("EDITED");
					kendraAssignmentRepository.save(kendraAssignmentRec);
				}
				respBody.setResponseObj("Record Status remarked as EDITED and date updated!!!");
			} else if ("delete".equalsIgnoreCase(action)) {
				for (KendraAssignment kendraAssignmentRec : kendraAssignmentRecords) {
					kendraAssignmentRec.setRemarks("DELETED");
					kendraAssignmentRepository.save(kendraAssignmentRec);
				}
				respBody.setResponseObj("Record Status remarked as DELETED!!!");
			} else {
				respBody.setResponseObj("Invalid action!!! Action Should be either edit or delete");
				CommonUtils.generateHeaderForFailure(respHeader, INVALID_REQUEST);
				respHeader.setResponseMessage(INVALID_REQUEST);
				response.setResponseBody(respBody);
				response.setResponseHeader(respHeader);
			}
		} else {
			respBody.setResponseObj("Requested batchNo recrords not present in DB");
			CommonUtils.generateHeaderForFailure(respHeader, INVALID_REQUEST);
			respHeader.setResponseMessage(INVALID_REQUEST);
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
		}
		ResponseHeader resHeader = new ResponseHeader();
		CommonUtils.generateHeaderForSuccess(resHeader);
		response.setResponseBody(respBody);
		response.setResponseHeader(resHeader);
		return response;
	}
}
