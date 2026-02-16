package com.iexceed.appzillonbanking.cbs.service;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import com.iexceed.appzillonbanking.cbs.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cbs.domain.ab.AuditTrailEntity;
import com.iexceed.appzillonbanking.cbs.domain.ab.MisReport;
import com.iexceed.appzillonbanking.cbs.domain.ab.TbUalnLoanDtls;
import com.iexceed.appzillonbanking.cbs.domain.ab.TbUaobCbResponse;
import com.iexceed.appzillonbanking.core.domain.ab.TbUaobApiAuditLogs;
import com.iexceed.appzillonbanking.cbs.payload.EarningMemberUpdateRequest;
import com.iexceed.appzillonbanking.cbs.payload.EarningMemberUpdateRequestFields;
import com.iexceed.appzillonbanking.cbs.payload.EmergencyLoanRequest;
import com.iexceed.appzillonbanking.cbs.payload.FetchCustomerLoanRequest;
import com.iexceed.appzillonbanking.cbs.payload.FetchCustomerPayOffDetailRequest;
import com.iexceed.appzillonbanking.cbs.payload.PennyCheckRequest;
import com.iexceed.appzillonbanking.cbs.payload.PennyCheckRequestFields;
import com.iexceed.appzillonbanking.cbs.payload.PreClosureLoanRequest;
import com.iexceed.appzillonbanking.cbs.payload.UpdateCustomerGeoLocationRequest;
import com.iexceed.appzillonbanking.cbs.payload.UpdateCustomerGeoLocationRequestFields;
import com.iexceed.appzillonbanking.cbs.payload.UpdateIncomeAssessmentRequest;
import com.iexceed.appzillonbanking.cbs.payload.UpdateIncomeAssessmentRequestFields;
import com.iexceed.appzillonbanking.cbs.payload.UpdateKendraGeoLocationRequest;
import com.iexceed.appzillonbanking.cbs.payload.UpdateKendraGeoLocationRequestFields;
import com.iexceed.appzillonbanking.cbs.repository.ab.ApplicationMasterRepository;
import com.iexceed.appzillonbanking.cbs.repository.ab.AuditTrailRepo;
import com.iexceed.appzillonbanking.cbs.repository.ab.MisReportRepository;
import com.iexceed.appzillonbanking.cbs.repository.ab.TbUalLoanDtlsRepository;
import com.iexceed.appzillonbanking.cbs.repository.ab.TbUaobCbResponseRepository;
import com.iexceed.appzillonbanking.core.repository.ab.TbUaobApiAuditLogsRepository;
import com.iexceed.appzillonbanking.core.payload.Header;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.interfaceAdapter.service.InterfaceAdapter;
import com.iexceed.appzillonbanking.interfaceAdapter.utils.AdapterUtil;

import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import reactor.core.publisher.Mono;

@Service
public class CBSService {

	private static final Logger logger = LogManager.getLogger(CBSService.class);

	@Autowired
	private InterfaceAdapter interfaceAdapter;

	@Autowired
	private AdapterUtil adapterUtil;

	@Value("${customer.payoff.apiKey}")
	private String customerPayOffApiKey;

	@Value("${customer.payoff.apiUrl}")
	private String customerPayOffApiUrl;
	
	@Autowired
	private MisReportRepository misReportRepository;
	
	@PersistenceContext
	private final EntityManager entityManager;

	public CBSService(EntityManager entityManager) {
		this.entityManager = entityManager;
	}
	

	@Autowired
    private CircuitBreakerRegistry circuitBreakerRegistry; // Inject the CircuitBreakerRegistry

    
	public static final String EXCEPTION_MSG = "Something went wrong, Please try again!!";
	public static final String EXCEPTION_OCCURED = "Exception occurred";
	public static final String PRE_CLOSER_LOAN_INTERFACEID = "preClosureLoan";
	public static final String EMERGENCY_LOAN_INTERFACEID = "emergencyLoan";
	public static final String INCREMENT_LOAN_INTERFACEID = "incrementLoan";
	public static final String FETCH_CUSTOMER_LOAN_INTERFACEID = "fetchCustomerLoan";
	public static final String PENNY_CHECK_INTERFACEID = "pennyCheck";

	public static final String EARNING_MEMBER_UPDATE_INTERFACEID = "earningMemberUpdate";
	public static final String CUSTOMER_GRO_UPDATE_INTERFACEID = "customerGeoUpdate";
	public static final String KENDRA_GRO_UPDATE_INTERFACEID = "kendraGeoUpdate";
	public static final String UPDATE_INCOME_ASSESSMENT_INTERFACEID = "updateIncomeAssessment";
	public static final String FETCH_CUSTOMER_PAYOFF_DETAILS = "fetchCustomerPayOffDetail";
	public static final String FETCH_DISBURSEMENT_STATUS_INTERFACEDID = "disbursementStatus";
	 	
	@Autowired
	ApplicationMasterRepository appMasterRepo;
	
	@Autowired
	TbUalLoanDtlsRepository tbualLoanDtlRepo;
	
	@Autowired
	TbUaobApiAuditLogsRepository auditLogsRepo;
	
	@Autowired
	private AuditTrailRepo auditTrailRepo;

	@Autowired
	private TbUaobCbResponseRepository cbResRepository;
	
	public Mono<ResponseWrapper> callPreClosureLoanApi(PreClosureLoanRequest apiRequest, Header header) {
		logger.debug("Printing PreClosure Loan apiRequest:" + apiRequest);
		try {
			header.setInterfaceId(PRE_CLOSER_LOAN_INTERFACEID);
			String applicationId = apiRequest.getRequestObj().getReferenceId();
			String customerId = apiRequest.getRequestObj().getCustomerId();
			Gson gson = new Gson();
			TbUaobApiAuditLogs tbAudtiLog = new TbUaobApiAuditLogs();
			tbAudtiLog.setSeqId(customerId + "" + CommonUtils.generateRandomNumStr());
			tbAudtiLog.setApiName(PRE_CLOSER_LOAN_INTERFACEID);
			tbAudtiLog.setReqTs(new Timestamp(System.currentTimeMillis()));
			tbAudtiLog.setAppId(apiRequest.getAppId());
			tbAudtiLog.setApplicationId(applicationId);
			tbAudtiLog.setCustDtlId(customerId);
			tbAudtiLog.setVersionNo(apiRequest.getVersionNum());
			
			BigDecimal applicationMasterAmount = BigDecimal.ZERO; 
			String LoanDtlsAmount = "";
			String applicationStatus = "";
			

			String cbApprovedinsuranceChargeMember = "";
			String cbApprovedinsuranceChargeSpouse = "";
			
			if(applicationId != null) {
				List<ApplicationMaster> appMasterList = appMasterRepo.findAllByApplicationId(applicationId);
				logger.debug("Printing appMasterList:" + appMasterList);
				if(appMasterList != null && appMasterList.size() > 0) {
					ApplicationMaster appMaster = appMasterList.get(0);
					apiRequest.getRequestObj().setBranchId(appMaster.getBranchCode());
					apiRequest.setVersionNum(appMaster.getVersionNum());
					applicationMasterAmount = appMaster.getAmount() != null 
						    ? appMaster.getAmount() 
						    	    : BigDecimal.ZERO;
					applicationStatus = appMaster.getApplicationStatus();
				}
				// Before Starting Disbursment checking if amount mismatch in the 2 table.
				
				List<TbUalnLoanDtls> tbUalnLoanDtlsList = tbualLoanDtlRepo.findAllByApplicationId(applicationId);
				logger.debug("Printing tbUalnLoanDtlsList:" + tbUalnLoanDtlsList);
				if(tbUalnLoanDtlsList != null && tbUalnLoanDtlsList.size() > 0) {
					TbUalnLoanDtls TbUalnLoan = tbUalnLoanDtlsList.get(0);
					LoanDtlsAmount = TbUalnLoan.getLoanAmount();
				}
				
				logger.error("Going to compare both Amount");
				logger.error("applicationMasterAmount is "+ applicationMasterAmount );
				logger.error("LoanDtlsAmount is "+ LoanDtlsAmount );
				try {
					logger.debug("Printing applicationMasterAmount:" + applicationMasterAmount);
					logger.debug("Printing LoanDtlsAmount:" + LoanDtlsAmount);
					List<String> invalidStatuses = Arrays.asList("REJECTED", "DISBURSED", "CANCELLED");
					if (!(applicationMasterAmount.compareTo(new BigDecimal(LoanDtlsAmount)) == 0)
							|| invalidStatuses.contains(applicationStatus)) {
						logger.error("There is mismatch. Stop the disbursment");
						logger.error("exception at callPreCloserLoanApi: {}");
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
					} else {
						logger.error("amount is same proceeding");
					}
				} catch (Exception e) {

				}
				
				try {
				// Adding one more condition if master application amount and approved amount from cb mismatch
				TbUaobCbResponse cbRes = cbResRepository.findByAppIdAndApplicationId(applicationId);
				logger.debug("Printing cbRes " + cbRes);
				if (cbRes != null) {
				String respPayload = cbRes.getResPayload();
				logger.debug("respPayload " + respPayload);
				JSONObject responPayloadObj = new JSONObject(respPayload);
				String cbApprovedAmount = responPayloadObj.get("Approved_Loan_Amount")+"";
				 cbApprovedinsuranceChargeMember = responPayloadObj.get("Insurance_Charge_Member")+"";
				 cbApprovedinsuranceChargeSpouse = responPayloadObj.get("Insurance_Charge_Spouse")+"";
				
				logger.debug("CB Approved Amount is " + cbApprovedAmount);
				logger.debug("CB Approved applicationMasterAmount is " + applicationMasterAmount);
				if(!(applicationMasterAmount.compareTo(new BigDecimal(cbApprovedAmount)) == 0))
				{
					logger.error("There is mismatch. Stop the disbursment");
					logger.error("exception at callPreCloserLoanApi: {}");
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
				else
				{
					logger.error("amount is same proceeding");
				}
				}
				}
				catch(Exception e)
				{
					
				}

			}
			
			try {
			    // Default insurance values if blank or null
			    String insuranceChargeMember = apiRequest.getRequestObj().getInsuranceChargeMember();
			    if (insuranceChargeMember == null || insuranceChargeMember.isBlank()) {
			        apiRequest.getRequestObj().setInsuranceChargeMember("0");
			    }

			    String insuranceChargeSpouse = apiRequest.getRequestObj().getInsuranceChargeSpouse();
			    if (insuranceChargeSpouse == null || insuranceChargeSpouse.isBlank()) {
			        apiRequest.getRequestObj().setInsuranceChargeSpouse("0");
			    }
			    
			    // Override member insurance from CB response if mismatch
			    if (cbApprovedinsuranceChargeMember != null && !cbApprovedinsuranceChargeMember.isBlank()) {
			        if (!cbApprovedinsuranceChargeMember.equalsIgnoreCase(apiRequest.getRequestObj().getInsuranceChargeMember())) {
			            logger.error("Member insurance different, using CB response");
			            apiRequest.getRequestObj().setInsuranceChargeMember(cbApprovedinsuranceChargeMember);
			        }
			    }

			    // Override spouse insurance from CB response if mismatch
			    if (cbApprovedinsuranceChargeSpouse != null && !cbApprovedinsuranceChargeSpouse.isBlank()) {
			        if (!cbApprovedinsuranceChargeSpouse.equalsIgnoreCase(apiRequest.getRequestObj().getInsuranceChargeSpouse())) {
			            logger.error("Spouse insurance different, using CB response");
			            apiRequest.getRequestObj().setInsuranceChargeSpouse(cbApprovedinsuranceChargeSpouse);
			        }
			    }
			} catch (Exception e) {
			    logger.error("Exception while processing insurance charges", e);
			}

			tbAudtiLog.setRequestPayload(gson.toJson(apiRequest.getRequestObj()));
			try {
				logger.debug("Printing term before removing extra W:" + apiRequest.getRequestObj().getTerm());
				String termVal = apiRequest.getRequestObj().getTerm();
				String validatedTerm = termVal.replaceAll("W+", "W");
				logger.debug("Printing term after removing extra W:" + validatedTerm);
				logger.debug("Printing Final Request formed ::" + apiRequest.getRequestObj());
			} catch (Exception e) {
				logger.error("Error Occured while removing extra W:" + e);
			}
			//Need to check for Disbursement status
			
			List<TbUaobApiAuditLogs> tbUaobApiAuditLogs = auditLogsRepo
					.findByApplicationId(applicationId);
			logger.debug("Printing audit response+++:" + tbUaobApiAuditLogs);
			List<TbUaobApiAuditLogs> tbUaobApifailedApplication = auditLogsRepo
					.findByApplicationIdAndStatusFailure(applicationId);
			logger.debug("Printing audit failure response+++:" + tbUaobApifailedApplication);
			
			if ((tbUaobApiAuditLogs == null || tbUaobApiAuditLogs.isEmpty())|| !tbUaobApifailedApplication.isEmpty()) {

				Mono<Object> dedupeResponse = interfaceAdapter.callExternalService(header, apiRequest.getRequestObj(),
						PRE_CLOSER_LOAN_INTERFACEID, true);

				Mono<ResponseWrapper> monoResWrapper = adapterUtil.generateRespWrapper(dedupeResponse,
						PRE_CLOSER_LOAN_INTERFACEID, header, true);

				return monoResWrapper.flatMap(responseMono -> {					
					logger.debug("Printing responseMono:" + responseMono);
					tbAudtiLog.setResTs(new Timestamp(System.currentTimeMillis()));
					String resObj = responseMono.getApiResponse().getResponseBody().getResponseObj();
					logger.debug("Printing resObj:" + resObj);
					tbAudtiLog.setResponsePayload(resObj);
					String status = "FAILED";
					List<ApplicationMaster> appMasterList = appMasterRepo.findAllByApplicationId(applicationId);
					logger.debug("Printing appMasterList:" + appMasterList);
					ApplicationMaster appMaster = null;
					try {
						JSONObject jsonObj = new JSONObject(resObj);
						logger.debug("PreCloserLoanApi JSONOBject Response : {}", jsonObj);
						String headerNode = "header";
						if (jsonObj.has(headerNode) && jsonObj.getJSONObject(headerNode) != null
								&& jsonObj.getJSONObject(headerNode).has("status")
								&& "success".equalsIgnoreCase(jsonObj.getJSONObject(headerNode).getString("status"))) {
							status = "SUCCESS";	
							tbAudtiLog.setApiStatus("SUCCESS");
							tbAudtiLog.setStatus("SUCCESS");
							if (appMasterList != null && appMasterList.size() > 0) {
								appMaster = appMasterList.get(0);
								appMaster.setApplicationStatus("DISBURSED");
								logger.debug("appMaster : {}", appMaster);
								appMasterRepo.save(appMaster);
								// Audit Trail changes
								AuditTrailEntity auditTrailEntity = auditTrailRepo.findApplicationId(applicationId);
								logger.debug("Exiting AuditTrailEntity for disbursement: {}", auditTrailEntity);
								if (auditTrailEntity != null) {
									auditTrailEntity.setAppId(auditTrailEntity.getAppId());
									auditTrailEntity.setApplicationId(auditTrailEntity.getApplicationId());
									auditTrailEntity.setBranchId(auditTrailEntity.getBranchId());
									auditTrailEntity.setAddInfo1(auditTrailEntity.getAddInfo1());
									auditTrailEntity.setAddInfo2(apiRequest.getRequestObj().getRemarks());
									auditTrailEntity.setAddInfo3(auditTrailEntity.getAddInfo3());
									auditTrailEntity.setAddInfo4(auditTrailEntity.getAddInfo4());
									auditTrailEntity.setCustomerId(auditTrailEntity.getCustomerId());
									auditTrailEntity.setCustomerName(auditTrailEntity.getCustomerName());
									auditTrailEntity.setKendraName(auditTrailEntity.getKendraName());
									auditTrailEntity.setLoanAmount(auditTrailEntity.getLoanAmount());
									auditTrailEntity.setMobileNumber(auditTrailEntity.getMobileNumber());
									auditTrailEntity.setPayload(auditTrailEntity.getPayload());
									auditTrailEntity.setProductId(auditTrailEntity.getProductId());
									auditTrailEntity.setCreateDate(auditTrailEntity.getCreateDate());
									auditTrailEntity.setUserName(apiRequest.getRequestObj().getUserName());
									Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
									String timestampString = currentTimestamp.toString();
									auditTrailEntity.setCreateTs(timestampString);
									auditTrailEntity.setPurpose(auditTrailEntity.getPurpose());
									auditTrailEntity.setRepaymentFrequency(auditTrailEntity.getRepaymentFrequency());
									auditTrailEntity.setSpouse(auditTrailEntity.getSpouse());
									auditTrailEntity.setAppVersion(apiRequest.getRequestObj().getAppVersion());
									auditTrailEntity.setStageid("17");
									auditTrailEntity.setUserId(apiRequest.getRequestObj().getUserId());
									auditTrailEntity.setUserRole(apiRequest.getRequestObj().getUserRole());
									logger.debug("Saving AuditTrailEntity for DISBURSED : {}", auditTrailEntity);
									auditTrailRepo.save(auditTrailEntity);
									// Mis Report changes
									Optional<MisReport> optionalExisting = misReportRepository
											.findByApplicationId(appMaster.getApplicationId());
									logger.debug("Exiting MisReport for Updating data: {}", optionalExisting);
									if (optionalExisting.isPresent()) {
										MisReport misReport = optionalExisting.get();
										misReport.setUpdateDate(timestampString);
										misReport.setModifyBy(apiRequest.getRequestObj().getUserId());
										misReport.setStageID("17");
										misReport.setRemarks(auditTrailEntity.getAddInfo2());
										misReport.setApplicationStatus("DISBURSED");
										logger.debug("Updating MIS Report for stageId :17: {}", misReport);
										misReportRepository.save(misReport);
									} else {
										logger.warn("No MIS Report record found for applicationId: {}",
												appMaster.getApplicationId());
									}
								} 
								else {
									logger.warn("No AuditTrailEntity record found for applicationId: {}",
											applicationId);
								}
							}
						} else {
							if (appMasterList != null && appMasterList.size() > 0) {
								appMaster = appMasterList.get(0);
								Optional<MisReport> optionalExisting = misReportRepository
										.findByApplicationId(appMaster.getApplicationId());
								logger.debug("Existing MisReport details for updating: {}", optionalExisting);
								if (optionalExisting.isPresent()) {
									MisReport misReport = optionalExisting.get();
									Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
									String timestampString = currentTimestamp.toString();
									misReport.setUpdateDate(timestampString);
									misReport.setRemarks("T24 disbursement rejected");
									logger.debug("MIS Report T24 Reject flow :15---: {}", misReport);
									misReportRepository.save(misReport);
								} else {
									logger.warn("No MIS Report found for applicationId: {}",
											appMaster.getApplicationId());
								}
							}
							
							if (resObj != null && resObj.trim().startsWith("{")) {
								JSONObject json = new JSONObject(resObj);
								if (json.has("error")) {
									//String errorMsg = json.getString("error");
									Object errorObj = json.opt("error");
									if(errorObj instanceof String) {
										 String errorMsg = (String) errorObj;
										 if("3166-A timeout error occurred whilst performing an SSL socket operation"
								                    .equalsIgnoreCase(errorMsg)
								                    || "SOCKET error whilst invoking a web service".equalsIgnoreCase(errorMsg)) {
											    tbAudtiLog.setResTs(new Timestamp(System.currentTimeMillis()));
												tbAudtiLog.setResponsePayload(resObj);
												//tbAudtiLog.setStatus("INPROGRESS");
												if (appMasterList != null && appMasterList.size() > 0) {
													appMaster = appMasterList.get(0);
													Optional<MisReport> optionalExisting = misReportRepository
															.findByApplicationId(appMaster.getApplicationId());
													logger.debug("Fetched applicationId for timeout error  : {}", optionalExisting);
													if (optionalExisting.isPresent()) {
														MisReport misReport = optionalExisting.get();
														Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
														String timestampString = currentTimestamp.toString();
														misReport.setUpdateDate(timestampString);
														misReport.setRemarks("T24 disbursement timeout error");
														misReportRepository.save(misReport);
													} else {
														logger.warn("No MIS Report found for applicationId: {}",
																appMaster.getApplicationId());
													}
												}	
												return callDisbursementStatusApi(applicationId, header).flatMap(resWrapper -> {
													logger.debug("Fetching disbursement status API with applicationId : {}", applicationId);	
													tbAudtiLog.setResTs(new Timestamp(System.currentTimeMillis()));
													String disburseResPayload = resWrapper.getApiResponse().getResponseBody()
															.getResponseObj();
													boolean isvalidresponsepayload=false;
													logger.debug("Fetching disbursementStatus for check loan status : {}", disburseResPayload);													
													try {
														if (disburseResPayload != null) {
															JSONObject jsonObject = new JSONObject(disburseResPayload);
															logger.debug("Printing jsonObject for check laon status : {}", jsonObject);
															JSONObject resultObj = null;
															if (jsonObject.has("body")
																	&& jsonObject.getJSONArray("body").length() > 0) {
																JSONObject bodyObj = jsonObject.getJSONArray("body")
																		.getJSONObject(0);
																if (bodyObj.has("result")
																		&& bodyObj.getJSONArray("result").length() > 0) {
																	resultObj = bodyObj.getJSONArray("result").getJSONObject(0);
																	
																	if (resultObj.has("loanStatus") && "DISBURSED"
																			.equalsIgnoreCase(resultObj.optString("loanStatus"))) {
																		List<ApplicationMaster> appMasterListnew = appMasterRepo.findAllByApplicationId(applicationId);
																		logger.debug("Printing appMasterListnew:" + appMasterListnew);
																		ApplicationMaster appMasternew = null;
																		if (appMasterListnew != null && appMasterListnew.size() > 0) {
																			appMasternew = appMasterList.get(0);
																			appMasternew.setApplicationStatus("DISBURSED");
																			appMasterRepo.save(appMasternew);
																		}	
																		tbAudtiLog.setResponsePayload(disburseResPayload);																		
																		tbAudtiLog.setApiStatus("SUCCESS");
																		tbAudtiLog.setStatus("SUCCESS");
																		// Audit trail changes
																		AuditTrailEntity auditTrailEntity = auditTrailRepo.findApplicationId(applicationId);
																		logger.debug("Exiting AuditTrailEntity details for update disbursement status : {}", auditTrailEntity);
																		if (auditTrailEntity != null) {
																			auditTrailEntity.setAppId(auditTrailEntity.getAppId());
																			auditTrailEntity.setApplicationId(auditTrailEntity.getApplicationId());
																			auditTrailEntity.setBranchId(auditTrailEntity.getBranchId());
																			auditTrailEntity.setAddInfo1(auditTrailEntity.getAddInfo1());
																			auditTrailEntity.setAddInfo2(apiRequest.getRequestObj().getRemarks()+"~withDisburesestatusAPI~Disbursed");
																			auditTrailEntity.setAddInfo3(auditTrailEntity.getAddInfo3());
																			auditTrailEntity.setAddInfo4(auditTrailEntity.getAddInfo4());
																			auditTrailEntity.setCustomerId(auditTrailEntity.getCustomerId());
																			auditTrailEntity.setCustomerName(auditTrailEntity.getCustomerName());
																			auditTrailEntity.setKendraName(auditTrailEntity.getKendraName());
																			auditTrailEntity.setLoanAmount(auditTrailEntity.getLoanAmount());
																			auditTrailEntity.setMobileNumber(auditTrailEntity.getMobileNumber());
																			auditTrailEntity.setPayload(auditTrailEntity.getPayload());
																			auditTrailEntity.setProductId(auditTrailEntity.getProductId());
																			auditTrailEntity.setCreateDate(auditTrailEntity.getCreateDate());
																			auditTrailEntity.setUserName(apiRequest.getRequestObj().getUserName());
																			Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
																			String timestampString = currentTimestamp.toString();
																			auditTrailEntity.setCreateTs(timestampString);
																			auditTrailEntity.setPurpose(auditTrailEntity.getPurpose());
																			auditTrailEntity.setRepaymentFrequency(auditTrailEntity.getRepaymentFrequency());
																			auditTrailEntity.setSpouse(auditTrailEntity.getSpouse());
																			auditTrailEntity.setAppVersion(apiRequest.getRequestObj().getAppVersion());
																			auditTrailEntity.setStageid("17");
																			auditTrailEntity.setUserId(apiRequest.getRequestObj().getUserId());
																			auditTrailEntity.setUserRole(apiRequest.getRequestObj().getUserRole());
																			logger.debug("Saving AuditTrailEntity for dibuserment status : {}", auditTrailEntity);
																			auditTrailRepo.save(auditTrailEntity);
																			// Mis Report changes
																			Optional<MisReport> optionalExisting = misReportRepository
																					.findByApplicationId(applicationId);
																			logger.debug("Exiting MisReport for Updating disbursed status data: {}", optionalExisting);
																			if (optionalExisting.isPresent()) {
																				MisReport misReport = optionalExisting.get();
																				misReport.setUpdateDate(timestampString);
																				misReport.setModifyBy(apiRequest.getRequestObj().getUserId());
																				misReport.setStageID("17");
																				misReport.setRemarks(auditTrailEntity.getAddInfo2());
																				misReport.setApplicationStatus("DISBURSED");
																				logger.debug("Updating MIS Report for stageId :17: {}", misReport);
																				misReportRepository.save(misReport);
																			} else {
																				logger.warn("No MIS Report record found for applicationId: {}",
																						applicationId);
																			}
																		} 		
																	}
																	else if (resultObj != null && resultObj.has("loanStatus") && "LOAN.CREATED"
																			.equalsIgnoreCase(resultObj.optString("loanStatus"))) {
																		logger.debug("Entering inside loan created:");
																		tbAudtiLog.setResponsePayload(disburseResPayload);
																		tbAudtiLog.setApiStatus("FAILED");
																		tbAudtiLog.setStatus("INPROGRESS");
																		// need to check Audit trail and Mis Report																		
																		AuditTrailEntity auditTrailEntity = auditTrailRepo.findApplicationId(applicationId);
																		logger.debug("Exiting AuditTrailEntity for disbursement for loan created: {}", auditTrailEntity);
																		if (auditTrailEntity != null) {
																			auditTrailEntity.setAppId(auditTrailEntity.getAppId());
																			auditTrailEntity.setApplicationId(auditTrailEntity.getApplicationId());
																			auditTrailEntity.setBranchId(auditTrailEntity.getBranchId());
																			auditTrailEntity.setAddInfo1(auditTrailEntity.getAddInfo1());
																			auditTrailEntity.setAddInfo2(apiRequest.getRequestObj().getRemarks()+"~withDisburesestatusAPI~loancreated");
																			auditTrailEntity.setAddInfo3(auditTrailEntity.getAddInfo3());
																			auditTrailEntity.setAddInfo4(auditTrailEntity.getAddInfo4());
																			auditTrailEntity.setCustomerId(auditTrailEntity.getCustomerId());
																			auditTrailEntity.setCustomerName(auditTrailEntity.getCustomerName());
																			auditTrailEntity.setKendraName(auditTrailEntity.getKendraName());
																			auditTrailEntity.setLoanAmount(auditTrailEntity.getLoanAmount());
																			auditTrailEntity.setMobileNumber(auditTrailEntity.getMobileNumber());
																			auditTrailEntity.setPayload(auditTrailEntity.getPayload());
																			auditTrailEntity.setProductId(auditTrailEntity.getProductId());
																			auditTrailEntity.setCreateDate(auditTrailEntity.getCreateDate());
																			auditTrailEntity.setUserName(apiRequest.getRequestObj().getUserName());
																			Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
																			String timestampString = currentTimestamp.toString();
																			auditTrailEntity.setCreateTs(timestampString);
																			auditTrailEntity.setPurpose(auditTrailEntity.getPurpose());
																			auditTrailEntity.setRepaymentFrequency(auditTrailEntity.getRepaymentFrequency());
																			auditTrailEntity.setSpouse(auditTrailEntity.getSpouse());
																			auditTrailEntity.setAppVersion(apiRequest.getRequestObj().getAppVersion());
																			auditTrailEntity.setStageid("16");
																			auditTrailEntity.setUserId(apiRequest.getRequestObj().getUserId());
																			auditTrailEntity.setUserRole(apiRequest.getRequestObj().getUserRole());
																			auditTrailRepo.save(auditTrailEntity);
																			// Mis Report changes
																			Optional<MisReport> optionalExisting = misReportRepository
																					.findByApplicationId(applicationId);
																			logger.debug("Exiting MisReport for Updating disbusered data: {}", optionalExisting);
																			if (optionalExisting.isPresent()) {
																				MisReport misReport = optionalExisting.get();
																				misReport.setUpdateDate(timestampString);
																				misReport.setModifyBy(apiRequest.getRequestObj().getUserId());
																				misReport.setRemarks(auditTrailEntity.getAddInfo2());
																				logger.debug("Updating MIS Report for stageId :17: {}", misReport);
																				misReportRepository.save(misReport);
																			} else {
																				logger.warn("No MIS Report record found for applicationId: {}",
																						applicationId);
																			}
																		} 				
																	}else {
																		ApplicationMaster apMaster = null;
																		if (appMasterList != null && appMasterList.size() > 0) {
																			apMaster = appMasterList.get(0);
																			Optional<MisReport> optionalExisting = misReportRepository
																					.findByApplicationId(apMaster.getApplicationId());
																			logger.debug("Existing MisReport details fetch : {}", optionalExisting);
																			if (optionalExisting.isPresent()) {
																				MisReport misReport = optionalExisting.get();
																				Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
																				String timestampString = currentTimestamp.toString();
																				misReport.setUpdateDate(timestampString);
																				misReport.setRemarks("T24 disbursement rejected~withDisburesestatusAPI");
																				logger.debug("MIS Report T24 Reject flow :15---: {}", misReport);
																				misReportRepository.save(misReport);
																			} else {
																				logger.warn("No MIS Report found for applicationId: {}",
																						apMaster.getApplicationId());
																			}
																		}
																		tbAudtiLog.setResponsePayload(disburseResPayload);													
																		tbAudtiLog.setApiStatus("FAILED");
																		tbAudtiLog.setStatus("FAILURE");
																	}
																}
																isvalidresponsepayload=true;
														} else {															
															
														}
													} else {
															logger.error("Invalid JSON response: {}", disburseResPayload);	
													}
													} catch (Exception e) {
														e.printStackTrace();
														logger.error("Exception while parsing disbursement response", e);
													}
													if(!isvalidresponsepayload) {
														tbAudtiLog.setResponsePayload(disburseResPayload);
														tbAudtiLog.setApiStatus("FAILED");
														tbAudtiLog.setStatus("FAILURE");
														ApplicationMaster applMaster = null;
														if (appMasterList != null && appMasterList.size() > 0) {
															applMaster = appMasterList.get(0);
															Optional<MisReport> optionalExisting = misReportRepository
																	.findByApplicationId(applMaster.getApplicationId());
															logger.debug("Printing misreport details for T24 disbursement invalid response  : {}", optionalExisting);
															if (optionalExisting.isPresent()) {
																MisReport misReport = optionalExisting.get();
																Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
																String timestampString = currentTimestamp.toString();
																misReport.setUpdateDate(timestampString);
																misReport.setRemarks("T24 disbursement invalid response~withDisburesestatusAPI");
																misReportRepository.save(misReport);
															} else {
																logger.warn("No MIS Report found for applicationId: {}",
																		applMaster.getApplicationId());
															}
														}
													}
													auditLogsRepo.save(tbAudtiLog);
													return Mono.just(resWrapper);
												});
										 }																	
									}
								}
							} 
					    }
					} catch (Exception exp) {
						logger.error("exception Occurred while extracting Status from PreCloserLoanApi: {}", exp);
					}
					logger.debug("Printing tbAudtiLog : {}", tbAudtiLog);
					tbAudtiLog.setApiStatus(status);
					auditLogsRepo.save(tbAudtiLog);
					return Mono.just(responseMono);
				});

			} else {
				List<TbUaobApiAuditLogs> auditLogsforStatusInProgress = auditLogsRepo
						.findByApplicationIdAndApiStatusInProgress(applicationId);
				logger.debug("Printing auditLogsforStatusInProgress : {}", auditLogsforStatusInProgress);	
				if (auditLogsforStatusInProgress != null) {
					//start
					return callDisbursementStatusApi(applicationId, header).flatMap(resWrapper -> {
						logger.debug("Printing InProgress applicationId : {}", applicationId);
						tbAudtiLog.setResTs(new Timestamp(System.currentTimeMillis()));
						String disburseResPayload = resWrapper.getApiResponse().getResponseBody()
								.getResponseObj();
						logger.debug("Printing inprgress disburseResPayload details : {}", disburseResPayload);
						boolean isvalidresponsepayload=false;													
						try {
							if (disburseResPayload != null) {
								JSONObject jsonObject = new JSONObject(disburseResPayload);
								logger.debug("Printing jsonObject for inprogress response: {}", jsonObject);
								JSONObject resultObj = null;
								if (jsonObject.has("body")
										&& jsonObject.getJSONArray("body").length() > 0) {
									JSONObject bodyObj = jsonObject.getJSONArray("body")
											.getJSONObject(0);
									if (bodyObj.has("result")
											&& bodyObj.getJSONArray("result").length() > 0) {
										resultObj = bodyObj.getJSONArray("result").getJSONObject(0);
										
										if (resultObj.has("loanStatus") && "DISBURSED"
												.equalsIgnoreCase(resultObj.optString("loanStatus"))) {
											List<ApplicationMaster> appMasterListnew = appMasterRepo.findAllByApplicationId(applicationId);
											logger.debug("Printing appMasterListnew for inprogress:" + appMasterListnew);
											ApplicationMaster appMasternew = null;
											if (appMasterListnew != null && appMasterListnew.size() > 0) {
												appMasternew = appMasterListnew.get(0);
												appMasternew.setApplicationStatus("DISBURSED");
												appMasterRepo.save(appMasternew);
											}else {
											    logger.warn("No ApplicationMaster found for applicationId: {}", applicationId);
											}
											tbAudtiLog.setResponsePayload(disburseResPayload);																		
											tbAudtiLog.setApiStatus("SUCCESS");
											tbAudtiLog.setStatus("SUCCESS");
											// Audit trail changes
											AuditTrailEntity auditTrailEntity = auditTrailRepo.findApplicationId(applicationId);
											logger.debug("Exiting AuditTrailEntity for RetryCasewithDisburesestatusAPI : {}", auditTrailEntity);
											if (auditTrailEntity != null) {
												auditTrailEntity.setAppId(auditTrailEntity.getAppId());
												auditTrailEntity.setApplicationId(auditTrailEntity.getApplicationId());
												auditTrailEntity.setBranchId(auditTrailEntity.getBranchId());
												auditTrailEntity.setAddInfo1(auditTrailEntity.getAddInfo1());
												auditTrailEntity.setAddInfo2(apiRequest.getRequestObj().getRemarks()+"~RetryCasewithDisburesestatusAPI~Disbursed");
												auditTrailEntity.setAddInfo3(auditTrailEntity.getAddInfo3());
												auditTrailEntity.setAddInfo4(auditTrailEntity.getAddInfo4());
												auditTrailEntity.setCustomerId(auditTrailEntity.getCustomerId());
												auditTrailEntity.setCustomerName(auditTrailEntity.getCustomerName());
												auditTrailEntity.setKendraName(auditTrailEntity.getKendraName());
												auditTrailEntity.setLoanAmount(auditTrailEntity.getLoanAmount());
												auditTrailEntity.setMobileNumber(auditTrailEntity.getMobileNumber());
												auditTrailEntity.setPayload(auditTrailEntity.getPayload());
												auditTrailEntity.setProductId(auditTrailEntity.getProductId());
												auditTrailEntity.setCreateDate(auditTrailEntity.getCreateDate());
												auditTrailEntity.setUserName(apiRequest.getRequestObj().getUserName());
												Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
												String timestampString = currentTimestamp.toString();
												auditTrailEntity.setCreateTs(timestampString);
												auditTrailEntity.setPurpose(auditTrailEntity.getPurpose());
												auditTrailEntity.setRepaymentFrequency(auditTrailEntity.getRepaymentFrequency());
												auditTrailEntity.setSpouse(auditTrailEntity.getSpouse());
												auditTrailEntity.setAppVersion(apiRequest.getRequestObj().getAppVersion());
												auditTrailEntity.setStageid("17");
												auditTrailEntity.setUserId(apiRequest.getRequestObj().getUserId());
												auditTrailEntity.setUserRole(apiRequest.getRequestObj().getUserRole());
												logger.debug("Saving AuditTrailEntity for RetryCasewithDisburesestatusAPI: {}", auditTrailEntity);
												auditTrailRepo.save(auditTrailEntity);
												// Mis Report changes
												Optional<MisReport> optionalExisting = misReportRepository
														.findByApplicationId(applicationId);
												logger.debug("Exiting MisReport for Updating RetryCasewithDisburesestatusAPI: {}", optionalExisting);
												if (optionalExisting.isPresent()) {
													MisReport misReport = optionalExisting.get();
													misReport.setUpdateDate(timestampString);
													misReport.setModifyBy(apiRequest.getRequestObj().getUserId());
													misReport.setStageID("17");
													misReport.setRemarks(auditTrailEntity.getAddInfo2());
													misReport.setApplicationStatus("DISBURSED");
													logger.debug("Updating MIS Report for stageId :17: {}", misReport);
													misReportRepository.save(misReport);
												} else {
													logger.warn("No MIS Report record found for applicationId: {}",
															applicationId);
												}
											} 		
										}
										else if (resultObj != null && resultObj.has("loanStatus") && "LOAN.CREATED"
												.equalsIgnoreCase(resultObj.optString("loanStatus"))) {
											logger.debug("Entering inside loan created for inprogress:");
											tbAudtiLog.setResponsePayload(disburseResPayload);
											tbAudtiLog.setApiStatus("FAILED");
											tbAudtiLog.setStatus("INPROGRESS");
											// need to check Audit trail and Mis Report																		
											AuditTrailEntity auditTrailEntity = auditTrailRepo.findApplicationId(applicationId);
											logger.debug("Exiting AuditTrailEntity for withDisburesestatusAPI for loan created: {}", auditTrailEntity);
											if (auditTrailEntity != null) {
												auditTrailEntity.setAppId(auditTrailEntity.getAppId());
												auditTrailEntity.setApplicationId(auditTrailEntity.getApplicationId());
												auditTrailEntity.setBranchId(auditTrailEntity.getBranchId());
												auditTrailEntity.setAddInfo1(auditTrailEntity.getAddInfo1());
												auditTrailEntity.setAddInfo2(apiRequest.getRequestObj().getRemarks()+"~withDisburesestatusAPI~loancreated");
												auditTrailEntity.setAddInfo3(auditTrailEntity.getAddInfo3());
												auditTrailEntity.setAddInfo4(auditTrailEntity.getAddInfo4());
												auditTrailEntity.setCustomerId(auditTrailEntity.getCustomerId());
												auditTrailEntity.setCustomerName(auditTrailEntity.getCustomerName());
												auditTrailEntity.setKendraName(auditTrailEntity.getKendraName());
												auditTrailEntity.setLoanAmount(auditTrailEntity.getLoanAmount());
												auditTrailEntity.setMobileNumber(auditTrailEntity.getMobileNumber());
												auditTrailEntity.setPayload(auditTrailEntity.getPayload());
												auditTrailEntity.setProductId(auditTrailEntity.getProductId());
												auditTrailEntity.setCreateDate(auditTrailEntity.getCreateDate());
												auditTrailEntity.setUserName(apiRequest.getRequestObj().getUserName());
												Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
												String timestampString = currentTimestamp.toString();
												auditTrailEntity.setCreateTs(timestampString);
												auditTrailEntity.setPurpose(auditTrailEntity.getPurpose());
												auditTrailEntity.setRepaymentFrequency(auditTrailEntity.getRepaymentFrequency());
												auditTrailEntity.setSpouse(auditTrailEntity.getSpouse());
												auditTrailEntity.setAppVersion(apiRequest.getRequestObj().getAppVersion());
												auditTrailEntity.setStageid("16");
												auditTrailEntity.setUserId(apiRequest.getRequestObj().getUserId());
												auditTrailEntity.setUserRole(apiRequest.getRequestObj().getUserRole());
												auditTrailRepo.save(auditTrailEntity);
												// Mis Report changes
												Optional<MisReport> optionalExisting = misReportRepository
														.findByApplicationId(applicationId);
												logger.debug("Exiting MisReport for Updating withDisburese Status data: {}", optionalExisting);
												if (optionalExisting.isPresent()) {
													MisReport misReport = optionalExisting.get();
													misReport.setUpdateDate(timestampString);
													misReport.setModifyBy(apiRequest.getRequestObj().getUserId());
													//misReport.setStageID("17");
													misReport.setRemarks(auditTrailEntity.getAddInfo2());
													//misReport.setApplicationStatus("DISBURSED");
													logger.debug("Updating MIS Report for stageId :17: {}", misReport);
													misReportRepository.save(misReport);
												} else {
													logger.warn("No MIS Report record found for applicationId: {}",
															applicationId);
												}
											} 				
										}else {
											List<ApplicationMaster> appMasterList = appMasterRepo.findAllByApplicationId(applicationId);
											logger.debug("Printing appMasterList for rejected:" + appMasterList);
											ApplicationMaster apMaster = null;
											if (appMasterList != null && appMasterList.size() > 0) {
												apMaster = appMasterList.get(0);
												Optional<MisReport> optionalExisting = misReportRepository
														.findByApplicationId(apMaster.getApplicationId());
												logger.debug("Existing MisReport for disbursement rejected  : {}", optionalExisting);
												if (optionalExisting.isPresent()) {
													MisReport misReport = optionalExisting.get();
													Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
													String timestampString = currentTimestamp.toString();
													misReport.setUpdateDate(timestampString);
													misReport.setRemarks("T24 disbursement rejected~withDisburesestatusAPI");
													logger.debug("MIS Report T24 Reject flow :15---: {}", misReport);
													misReportRepository.save(misReport);
												} else {
													logger.warn("No MIS Report found for applicationId: {}",
															apMaster.getApplicationId());
												}
											}
											tbAudtiLog.setResponsePayload(disburseResPayload);													
											tbAudtiLog.setApiStatus("FAILED");
											tbAudtiLog.setStatus("FAILURE");
										}
									}
									isvalidresponsepayload=true;
								} /*
									 * else {
									 * 
									 * }
									 */
						} else {
								logger.error("Invalid JSON response: {}", disburseResPayload);	
						}
						} catch (Exception e) {
							e.printStackTrace();
							logger.error("Exception while parsing disbursement response", e);
						}
						if(!isvalidresponsepayload) {
							tbAudtiLog.setResponsePayload(disburseResPayload);
							tbAudtiLog.setApiStatus("FAILED");
							tbAudtiLog.setStatus("FAILURE");
							List<ApplicationMaster> appMasterList = appMasterRepo.findAllByApplicationId(applicationId);
							logger.debug("Printing appMasterList for T24 disbursement invalid response:" + appMasterList);
							ApplicationMaster applMaster = null;
							if (appMasterList != null && appMasterList.size() > 0) {
								applMaster = appMasterList.get(0);
								Optional<MisReport> optionalExisting = misReportRepository
										.findByApplicationId(applMaster.getApplicationId());
								if (optionalExisting.isPresent()) {
									MisReport misReport = optionalExisting.get();
									Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
									String timestampString = currentTimestamp.toString();
									misReport.setUpdateDate(timestampString);
									misReport.setRemarks("T24 disbursement invalid response~withDisburesestatusAPI");
									misReportRepository.save(misReport);
								} else {
									logger.warn("No MIS Report found for applicationId: {}",
											applMaster.getApplicationId());
								}
							}
						}
						auditLogsRepo.save(tbAudtiLog);
						return Mono.just(resWrapper);
					});	
					/// end
				}
			}

		} catch (Exception e) {
			logger.error("exception at callPreCloserLoanApi: {}", e);
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
		return null;

	}
	
	

	public Mono<ResponseWrapper> callPennyCheckApi(PennyCheckRequest apiRequest, Header header) {
		try {
			header.setInterfaceId(PENNY_CHECK_INTERFACEID);
			PennyCheckRequestFields fields = apiRequest.getRequestObj();
			fields.setAllowPartialMatch("True");
			fields.setUseCombinedSolution("N");
			fields.setNameMatchType("Entity");
			fields.setConsent("Y");
			apiRequest.setRequestObj(fields);
			Mono<Object> dedupeResponse = interfaceAdapter.callExternalService(header, apiRequest.getRequestObj(),
					PENNY_CHECK_INTERFACEID, true);

			Mono<ResponseWrapper> monoResWrapper = adapterUtil.generateRespWrapper(dedupeResponse,
					PENNY_CHECK_INTERFACEID, header, true);

			return monoResWrapper;

		} catch (Exception e) {
			logger.error("exception at callPennyCheckApi: {}", e);
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

	public Mono<ResponseWrapper> createEmergencyLoan(EmergencyLoanRequest apiRequest, Header header) {

		try {

			header.setInterfaceId(EMERGENCY_LOAN_INTERFACEID);

			Mono<Object> dedupeResponse = interfaceAdapter.callExternalService(header, apiRequest.getRequestObj(),
					EMERGENCY_LOAN_INTERFACEID, true);

			Mono<ResponseWrapper> monoResWrapper = adapterUtil.generateRespWrapper(dedupeResponse,
					EMERGENCY_LOAN_INTERFACEID, header, true);

			return monoResWrapper;

		} catch (Exception e) {
			logger.error("exception at EmergencyLoanRequest: {}", e);
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

	public Mono<ResponseWrapper> incrementLoan(EmergencyLoanRequest apiRequest, Header header) {

		try {

			header.setInterfaceId(INCREMENT_LOAN_INTERFACEID);

			Mono<Object> dedupeResponse = interfaceAdapter.callExternalService(header, apiRequest.getRequestObj(),
					INCREMENT_LOAN_INTERFACEID, true);

			Mono<ResponseWrapper> monoResWrapper = adapterUtil.generateRespWrapper(dedupeResponse,
					INCREMENT_LOAN_INTERFACEID, header, true);

			return monoResWrapper;

		} catch (Exception e) {
			logger.error("exception at incrementLoanRequest: {}", e);
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

	public Mono<ResponseWrapper> fetchCustomerLoan(FetchCustomerLoanRequest apiRequest, Header header) {

		try {

			header.setInterfaceId(FETCH_CUSTOMER_LOAN_INTERFACEID);

			Mono<Object> dedupeResponse = interfaceAdapter.callExternalService(header, apiRequest.getRequestObj(),
					FETCH_CUSTOMER_LOAN_INTERFACEID, true);

			Mono<ResponseWrapper> monoResWrapper = adapterUtil.generateRespWrapper(dedupeResponse,
					FETCH_CUSTOMER_LOAN_INTERFACEID, header, true);

			return monoResWrapper;

		} catch (Exception e) {
			logger.error("exception at fetchCustomerLoanRequest: {}", e);
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

	@CircuitBreaker(name = "fallbackfetchCustomerPayOffDetail", fallbackMethod = "fallbackfetchCustomerPayOffDetail")
	public Mono<ResponseWrapper> fetchCustomerPayOffDetail(FetchCustomerPayOffDetailRequest apiRequest, Header header) {
		

			header.setInterfaceId(FETCH_CUSTOMER_PAYOFF_DETAILS);

			Mono<Object> dedupeResponse = interfaceAdapter.callExternalService(header, apiRequest.getRequestObj(),
					FETCH_CUSTOMER_PAYOFF_DETAILS, true);

			Mono<ResponseWrapper> monoResWrapper = adapterUtil.generateRespWrapper(dedupeResponse,
					FETCH_CUSTOMER_PAYOFF_DETAILS, header, true);
			return monoResWrapper;
		
	}

	public Mono<ResponseWrapper> fallbackfetchCustomerPayOffDetail(FetchCustomerPayOffDetailRequest apiRequest, Header header , Exception e) {
		
	        // Log the exception if it occurred
	        if (e != null) {
	            logger.error("Exception at fetchCustomerPayOffDetail: {}", e.getMessage());
	        } else {
	            logger.warn("No exception information available");
	        }
	        
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
	
	
	public Mono<ResponseWrapper> fallbackfetchCustomerPayOffDetail(FetchCustomerPayOffDetailRequest apiRequest, Header header, Throwable e) {
	   logger.error("loggin in the fallbackfetchCustomerPayOffDetail");
		logger.error("Exception in fetchCustomerPayOffDetail for request: {}, header: {}", apiRequest, header, e);

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
	
	public Mono<ResponseWrapper> earningMemberUpdate(EarningMemberUpdateRequest apiRequest, Header header) {
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();
		try {

			EarningMemberUpdateRequestFields earningMemReq = apiRequest.getRequestObj();

			header.setInterfaceId(EARNING_MEMBER_UPDATE_INTERFACEID);

			Mono<Object> dedupeResponse = interfaceAdapter.callExternalService(header, earningMemReq,
					apiRequest.getInterfaceName(), true);
			Mono<ResponseWrapper> resWrapper = adapterUtil.generateRespWrapper(dedupeResponse,
					apiRequest.getInterfaceName(), header, true);
			return resWrapper;
		} catch (Exception e) {
			logger.error("exception at earningMemberUpdate: {}", e);
			respBody.setResponseObj(EXCEPTION_MSG);
			CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
			ResponseWrapper resWrapper = new ResponseWrapper();
			resWrapper.setApiResponse(response);
			return Mono.just(resWrapper);
		}

	}

	public Mono<ResponseWrapper> customerGeoUpdate(UpdateCustomerGeoLocationRequest apiRequest, Header header) {
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();
		try {

			UpdateCustomerGeoLocationRequestFields earningMemReq = apiRequest.getRequestObj();

			header.setInterfaceId(CUSTOMER_GRO_UPDATE_INTERFACEID);

			Mono<Object> dedupeResponse = interfaceAdapter.callExternalService(header, earningMemReq,
					apiRequest.getInterfaceName(), true);
			Mono<ResponseWrapper> resWrapper = adapterUtil.generateRespWrapper(dedupeResponse,
					apiRequest.getInterfaceName(), header, true);
			return resWrapper;
		} catch (Exception e) {
			logger.error("exception at customerGeoUpdate: {}", e);
			respBody.setResponseObj(EXCEPTION_MSG);
			CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
			ResponseWrapper resWrapper = new ResponseWrapper();
			resWrapper.setApiResponse(response);
			return Mono.just(resWrapper);
		}
	}

	public Mono<ResponseWrapper> kendraGeoUpdate(UpdateKendraGeoLocationRequest apiRequest, Header header) {
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();
		try {

			UpdateKendraGeoLocationRequestFields earningMemReq = apiRequest.getRequestObj();

			header.setInterfaceId(KENDRA_GRO_UPDATE_INTERFACEID);

			Mono<Object> dedupeResponse = interfaceAdapter.callExternalService(header, earningMemReq,
					apiRequest.getInterfaceName(), true);
			Mono<ResponseWrapper> resWrapper = adapterUtil.generateRespWrapper(dedupeResponse,
					apiRequest.getInterfaceName(), header, true);
			return resWrapper;
		} catch (Exception e) {
			logger.error("exception at kendraGeoUpdate: {}", e);
			respBody.setResponseObj(EXCEPTION_MSG);
			CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
			ResponseWrapper resWrapper = new ResponseWrapper();
			resWrapper.setApiResponse(response);
			return Mono.just(resWrapper);
		}
	}

	public Mono<ResponseWrapper> updateIncomeAssessment(UpdateIncomeAssessmentRequest apiRequest, Header header) {
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();
		try {

			UpdateIncomeAssessmentRequestFields earningMemReq = apiRequest.getRequestObj();

			header.setInterfaceId(UPDATE_INCOME_ASSESSMENT_INTERFACEID);

			Mono<Object> dedupeResponse = interfaceAdapter.callExternalService(header, earningMemReq,
					apiRequest.getInterfaceName(), true);
			Mono<ResponseWrapper> resWrapper = adapterUtil.generateRespWrapper(dedupeResponse,
					apiRequest.getInterfaceName(), header, true);

			return resWrapper;
		} catch (Exception e) {
			logger.error("exception at updateIncomeAssessment: {} ", e);
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

	
	
	
	public Mono<ResponseWrapper> callDisbursementStatusApi(String loanRefId, Header header) {
		logger.debug("Printing disbursement status apiRequest: {}", loanRefId);

		JSONObject requestObject = new JSONObject();
		requestObject.put("loanRefID", loanRefId);

		JSONObject finalobject = new JSONObject();
		finalobject.put("apiRequest", requestObject);

		logger.debug("Sending final request with this Object {}", finalobject);
		try {
			header.setInterfaceId(FETCH_DISBURSEMENT_STATUS_INTERFACEDID);

			logger.debug("Printing disbursement status after added header: {}", header);

			Mono<Object> disburseStatus = interfaceAdapter.callExternalService(header, finalobject,
					FETCH_DISBURSEMENT_STATUS_INTERFACEDID, true);

			return adapterUtil.generateRespWrapper(disburseStatus, FETCH_DISBURSEMENT_STATUS_INTERFACEDID, header, true)
					.flatMap(responseMono -> {
						return Mono.just(responseMono);
					});
		} catch (Exception e) {
			logger.error("Exception at disbursement Status LoanApi: {}", e);
			Response response = new Response();
			ResponseHeader respHeader = new ResponseHeader();
			ResponseBody respBody = new ResponseBody();
			respBody.setResponseObj(EXCEPTION_MSG);
			CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
			ResponseWrapper resWrapper = new ResponseWrapper();
			resWrapper.setApiResponse(response);
		}
		return null;
	}
	
	
	/*
	 * public Mono<ResponseWrapper> checkstatusapimethod(String loanRefId, Header
	 * header) { logger.debug("Printing disbursement status apiRequest: {}",
	 * loanRefId); JSONObject requestObject = new JSONObject(); return null; }
	 */

}
