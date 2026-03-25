package com.iexceed.appzillonbanking.cbs.service;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
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
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
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
import com.iexceed.appzillonbanking.cbs.repository.ab.TbUacoInsuranceDtlsRepository;
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
//import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
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
	
	//@Autowired
  //  private CircuitBreakerRegistry circuitBreakerRegistry; 
  
	public static final String EXCEPTION_MSG = "Something went wrong, Please try again!!";
	public static final String INVALIDAPP_MSG = "Invalid application details, Please try again!!";
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
	
	@Autowired
	private TbUacoInsuranceDtlsRepository tbUacoInsuranceDtlsRepository;
	
	public Mono<ResponseWrapper> callPreClosureLoanApi(PreClosureLoanRequest apiRequest, Header header) {
		logger.debug("Printing PreClosure Loan apiRequest:" + apiRequest);
		try {
			header.setInterfaceId(PRE_CLOSER_LOAN_INTERFACEID);
			String applicationId = apiRequest.getRequestObj().getReferenceId();
			String reqAmount = apiRequest.getRequestObj().getAmount();
			String reqProduct =apiRequest.getRequestObj().getLoanProduct();
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
			String LoanDtlsProduct = "";
			String applicationStatus = "";
			String addInfo = "";
			String ApplcustomerId = "";
			String cbApprovedinsuranceChargeMember = "";
			String cbApprovedinsuranceChargeSpouse = "";
			String termWithoutW = "";
			String spouseInsuranceAmt = "";
			String spouse = "";
			String applicantInsuranceAmt = "";
			String applicantInsurance = "";
			String spouseInsurance = "";
			String appliedTenure = "";
			String member = "";
			String product_code = "";
			String loanAmount = "";
			Boolean validApp = true;
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
					ApplcustomerId =appMaster.getCustomerId();
					applicationStatus = appMaster.getApplicationStatus();
                   // it required addInfo for fetch isApprovedCRT details for charge&BreakUpDetails
					 addInfo = appMaster.getAddInfo();				
				}
				// Before Starting Disbursment checking if amount mismatch in the 2 table.	
				
				List<TbUalnLoanDtls> tbUalnLoanDtlsList = tbualLoanDtlRepo.findAllByApplicationId(applicationId);
				logger.debug("Printing tbUalnLoanDtlsList:" + tbUalnLoanDtlsList);
				if(tbUalnLoanDtlsList != null && tbUalnLoanDtlsList.size() > 0) {
					TbUalnLoanDtls TbUalnLoan = tbUalnLoanDtlsList.get(0);
					LoanDtlsAmount = TbUalnLoan.getLoanAmount();
					String payload = TbUalnLoan.getPayload();
					ObjectMapper objectMapper = new ObjectMapper();
					JsonNode rootNode = objectMapper.readTree(payload);
					LoanDtlsProduct = rootNode.path("productId").asText();			
					String term = rootNode.path("term").asText();	
				    termWithoutW = term.replaceAll("\\D", "");	
				    logger.debug("Printing termWithoutW:" + termWithoutW);	
					logger.debug("Printing LoanDtlsProduct:" + LoanDtlsProduct);				
				    JsonNode insurDtlsNode = rootNode.path("insurDtls");
				    if (!insurDtlsNode.isMissingNode() && !insurDtlsNode.isNull()) {
				        spouseInsuranceAmt = insurDtlsNode.path("spouse_insurance_amt").asText();
				       applicantInsuranceAmt = insurDtlsNode.path("applicant_insurance_amt").asText();			        
				        spouse = insurDtlsNode.path("Spouse").asText(); 
				        member = insurDtlsNode.path("member").asText(); 				        
				    }		  
				}
				
				logger.error("Going to compare both Amount");
				logger.error("applicationMasterAmount is "+ applicationMasterAmount );
				logger.error("LoanDtlsAmount is "+ LoanDtlsAmount );
				try {
					logger.debug("Printing applicationMasterAmount:" + applicationMasterAmount);
					logger.debug("Printing LoanDtlsAmount:" + LoanDtlsAmount);
					List<String> invalidStatuses = Arrays.asList("REJECTED", "DISBURSED", "CANCELLED");
                     // if req amt and master amt mismatch validation
					if (!areEqualAmounts(reqAmount, applicationMasterAmount) 
					        || !areEqualStrings(ApplcustomerId, customerId) || !areEqualStrings(LoanDtlsProduct, reqProduct)) {
						logger.error("Invalid Application. Stop the disbursment");
						logger.debug("Invalid Application. Stop the disbursment");
						validApp=false;
					}	
					if (!(applicationMasterAmount.compareTo(new BigDecimal(LoanDtlsAmount)) == 0)
							|| invalidStatuses.contains(applicationStatus) || !validApp) {
						logger.error("There is mismatch. Stop the disbursment");
						logger.error("exception at callPreCloserLoanApi: {}");
						Response response = new Response();
						ResponseHeader respHeader = new ResponseHeader();
						ResponseBody respBody = new ResponseBody();
						respBody.setResponseObj(INVALIDAPP_MSG);
						CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
						response.setResponseBody(respBody);
						response.setResponseHeader(respHeader);
						ResponseWrapper resWrapper = new ResponseWrapper();
						resWrapper.setApiResponse(response);
						return Mono.just(resWrapper);
					} 
					else {	
						logger.error("amount is same proceeding");
					}
				} catch (Exception e) {
				}				
				try {
					// Adding one more condition if master application amount and approved amount
					// from cb mismatch
					TbUaobCbResponse cbRes = cbResRepository.findByAppIdAndApplicationId(applicationId);
					logger.debug("Printing cbRes " + cbRes);
					if (cbRes != null) {
						String respPayload = cbRes.getResPayload();
						logger.debug("respPayload " + respPayload);
						JSONObject responPayloadObj = new JSONObject(respPayload);
						logger.debug("responPayloadObj " + responPayloadObj);
						String cbApprovedAmount = responPayloadObj.get("Approved_Loan_Amount") + "";
						cbApprovedinsuranceChargeMember = responPayloadObj.get("Insurance_Charge_Member") + "";
						cbApprovedinsuranceChargeSpouse = responPayloadObj.get("Insurance_Charge_Spouse") + "";
						
						logger.debug("CB Approved Amount is " + cbApprovedAmount);
						logger.debug("CB Approved applicationMasterAmount is " + applicationMasterAmount);
						
						// new validation for Insurance_Charge_Member ,term,spouse and products
						String insuranceChargeSpouse = responPayloadObj.get("Insurance_Charge_Spouse") + "";
						String insuranceChargeMember = responPayloadObj.get("Insurance_Charge_Member") + "";

						String requestPayload = cbRes.getReqPayload();
						JSONObject requestPayloadObj = new JSONObject(requestPayload);
						JSONObject requestObj = requestPayloadObj.getJSONObject("requestObj");
						logger.debug("Printing requestObj " + requestObj);
						applicantInsurance = requestObj.optString("applicantInsurance", "");
						product_code = requestObj.optString("product_code", "");
						loanAmount = requestObj.optString("loanAmount", "");
						spouseInsurance = requestObj.optString("spouseInsurance", "");					
						String appliedTenureRaw = requestObj.optString("appliedTenure", "");
						appliedTenure = appliedTenureRaw.replaceAll("\\D", "");
						
						logger.debug("CB Details -> applicantInsurance: {}, product_code: {}, appliedTenure: {}, loanAmount: {}, "
										+ "spouseInsurance: {}, spouse: {}, termWithoutW: {}, LoanDtlsProduct: {}, "
										+ "insuranceChargeSpouse: {}, insuranceChargeMember: {}, applicationMasterAmount: {}, spouseInsuranceAmt: {}",
								applicantInsurance, product_code, appliedTenure, loanAmount, spouseInsurance, spouse,
								termWithoutW, LoanDtlsProduct, insuranceChargeSpouse, insuranceChargeMember,
								applicationMasterAmount, spouseInsuranceAmt);

						logger.debug("areEqualTenure is " + areEqualTenure(appliedTenure, termWithoutW));
						logger.debug("areEqualStrings is " + areEqualStrings(product_code, LoanDtlsProduct));
						logger.debug("isValidYN CBmem is  " + isValidYN(applicantInsurance));
						logger.debug("isValidYN Loanmem is  " + isValidYN(member));
						logger.debug("areEqualAmounts member ins is "+ areEqualAmountsEmptyZero(insuranceChargeMember, applicantInsuranceAmt));
						logger.debug("areEqualAmounts spouse ins is "+ areEqualAmountsEmptyZero(insuranceChargeSpouse, spouseInsuranceAmt));
						logger.debug("areEqualAmounts loan amt is " + areEqualAmounts(loanAmount, applicationMasterAmount));
						logger.debug("isValidYN spouse is " + isValidYN(spouseInsurance));
						logger.debug("isValidYN CBspouse is " + isValidYN(spouse));

						try {
							if (!areEqualTenure(appliedTenure, termWithoutW)
									|| !areEqualStrings(product_code, LoanDtlsProduct)
									|| isValidYN(applicantInsurance) != isValidYN(member)
									|| isValidYN(spouseInsurance) != isValidYN(spouse)
									|| !areEqualAmountsEmptyZero(insuranceChargeMember, applicantInsuranceAmt)
									|| !areEqualAmountsEmptyZero(insuranceChargeSpouse, spouseInsuranceAmt)
									|| !areEqualAmounts(loanAmount, applicationMasterAmount)
									|| !areEqualStrings(product_code, reqProduct)) {

								logger.error("Mismatch found in spouse/insurance/tenure/member validation");
								logger.error("There is a mismatch. Stop the disbursment");
								Response response = new Response();
								ResponseHeader respHeader = new ResponseHeader();
								ResponseBody respBody = new ResponseBody();
								respBody.setResponseObj(INVALIDAPP_MSG);
								CommonUtils.generateHeaderForFailure(respHeader, INVALIDAPP_MSG);
								response.setResponseBody(respBody);
								response.setResponseHeader(respHeader);
								ResponseWrapper resWrapper = new ResponseWrapper();
								resWrapper.setApiResponse(response);
								return Mono.just(resWrapper);
							}
						} catch (Exception e) {
							logger.error("Error while validating insurance/spouse/member/term details", e);
						}
						// end
						if (!(applicationMasterAmount.compareTo(new BigDecimal(cbApprovedAmount)) == 0)) {
							TbUaobCbResponse cbResdata = cbResRepository.findByAppIdAndApplicationId(applicationId);
							logger.debug("Printing cbResdata " + cbResdata);
							if (cbResdata != null) {
								String resObj = cbResdata.getResPayload();
								logger.debug("Printing resObj " + resObj);
								JSONObject cbresponPayloadObj = new JSONObject(resObj);
								logger.error("Printing  cbresponPayloadObj", cbresponPayloadObj);
								logger.error("There is mismatch. Stop the disbursment");
								if (addInfo != null) {
									ObjectMapper objectMapper = new ObjectMapper();
									JsonNode rootNode = objectMapper.readTree(addInfo);
									logger.debug("Printing rootNode " + rootNode);
									JsonNode isApprovedCRTNode = rootNode.path("isApprovedCRT");
									if (!isApprovedCRTNode.isMissingNode() && !isApprovedCRTNode.asBoolean()) {
										updateChargeAndBreakUpAndInsurancedetails(apiRequest, cbresponPayloadObj);
									} else {
										logger.error("isApprovedCRT found True");
									}
								}
							}
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
					}
				} catch (Exception e) {
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
			if (!cbApprovedinsuranceChargeSpouse
					.equalsIgnoreCase(apiRequest.getRequestObj().getInsuranceChargeSpouse())) {
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
           // checking before application is available is there or not based on status 
			List<TbUaobApiAuditLogs> tbUaobApiAuditLogs = auditLogsRepo.findByApplicationId(applicationId);
			logger.debug("Printing tbUaobApiAuditLogs:" + tbUaobApiAuditLogs);
			List<TbUaobApiAuditLogs> tbUaobApifailedApplication = auditLogsRepo
					.findByApplicationIdAndStatusFailure(applicationId);
			logger.debug("Printing tbUaobApifailedApplication:" + tbUaobApifailedApplication);
				
			if ((tbUaobApiAuditLogs == null || tbUaobApiAuditLogs.isEmpty())|| (tbUaobApifailedApplication != null && !tbUaobApifailedApplication.isEmpty())) {

				Mono<Object> dedupeResponse = interfaceAdapter.callExternalService(header, apiRequest.getRequestObj(),
						PRE_CLOSER_LOAN_INTERFACEID, true);

				Mono<ResponseWrapper> monoResWrapper = adapterUtil.generateRespWrapper(dedupeResponse,
						PRE_CLOSER_LOAN_INTERFACEID, header, true);

				return monoResWrapper.flatMap(responseMono -> {					
					logger.debug("Printing responseMono:" + responseMono);
					tbAudtiLog.setResTs(new Timestamp(System.currentTimeMillis()));
					String resObj = responseMono.getApiResponse().getResponseBody().getResponseObj();
					logger.debug("Printing resObj by T24:" + resObj);
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
								logger.debug("Printing appMaster : {}", appMaster);
								appMasterRepo.save(appMaster);
								// Audit Trail changes
								AuditTrailEntity existingAudit  = auditTrailRepo.findApplicationId(applicationId);
								logger.debug("Exiting AuditTrailEntity data for disbursement: {}", existingAudit);
								if (existingAudit != null) {
									AuditTrailEntity newAudit = new AuditTrailEntity(); 
									newAudit.setAppId(existingAudit.getAppId());
									newAudit.setApplicationId(existingAudit.getApplicationId());
									newAudit.setBranchId(existingAudit.getBranchId());
									newAudit.setAddInfo1(existingAudit.getAddInfo1());
									newAudit.setAddInfo2(apiRequest.getRequestObj().getRemarks());
									newAudit.setAddInfo3(existingAudit.getAddInfo3());
									newAudit.setAddInfo4(existingAudit.getAddInfo4());
									newAudit.setCustomerId(existingAudit.getCustomerId());
									newAudit.setCustomerName(existingAudit.getCustomerName());
									newAudit.setKendraName(existingAudit.getKendraName());
									newAudit.setKendraId(existingAudit.getKendraId());
									newAudit.setLoanAmount(existingAudit.getLoanAmount());
									newAudit.setMobileNumber(existingAudit.getMobileNumber());
									newAudit.setPayload(existingAudit.getPayload());
									newAudit.setProductId(existingAudit.getProductId());
									newAudit.setCreateDate(existingAudit.getCreateDate());
									newAudit.setUserName(apiRequest.getRequestObj().getUserName());
									Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
									String timestampString = currentTimestamp.toString();
									newAudit.setCreateTs(timestampString);
									newAudit.setPurpose(existingAudit.getPurpose());
									newAudit.setRepaymentFrequency(existingAudit.getRepaymentFrequency());
									newAudit.setSpouse(existingAudit.getSpouse());
									newAudit.setAppVersion(apiRequest.getRequestObj().getAppVersion());
									newAudit.setStageid("17");
									newAudit.setUserId(apiRequest.getRequestObj().getUserId());
									newAudit.setUserRole(apiRequest.getRequestObj().getUserRole());
									logger.debug("Before Saving AuditTrailEntity for DISBURSED entry: {}", newAudit);
									auditTrailRepo.save(newAudit);
									// Mis Report changes
									Optional<MisReport> optionalExisting = misReportRepository.findByApplicationId(appMaster.getApplicationId());
									logger.debug("Fetched Exiting MisReport data for update DISBURSED details: {}", optionalExisting);
									if (optionalExisting.isPresent()) {
										MisReport misReport = optionalExisting.get();
										misReport.setUpdateDate(timestampString);
										misReport.setModifyBy(apiRequest.getRequestObj().getUserId());
										misReport.setStageID("17");
										misReport.setRemarks(existingAudit.getAddInfo2());
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
								Optional<MisReport> optionalExisting = misReportRepository.findByApplicationId(appMaster.getApplicationId());
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
			
							// start for Error message							
							if(resObj != null && !resObj.trim().isEmpty()) {
								logger.debug("Printing resObj error message: {}", resObj);
								JSONObject jsonObject = new JSONObject(resObj);
								logger.debug("Printing JSONOBject for error message: {}", jsonObj);	
								try {
									if (jsonObject.has("header") && jsonObject.has("error")) {
									    JSONObject headerforId = jsonObject.getJSONObject("header");
									    JSONObject error = jsonObject.getJSONObject("error");
									    if (headerforId.has("id") && headerforId.getString("id").startsWith("LO")) {
									        if (error.has("errorDetails")) {
									            JSONArray errorDetails = error.getJSONArray("errorDetails");
									            logger.debug("Printing errorDetails: {}", errorDetails);
									            if (errorDetails.length() > 0) {
									                JSONObject firstError = errorDetails.getJSONObject(0);
  									                    if (firstError.has("message")) {
									                    String message = firstError.getString("message");
									                    if("Already one loan application is in processing stage for the customernew".equalsIgnoreCase(message.trim())) {                    	
															
															return callDisbursementStatusApi(apiRequest, header).flatMap(resWrapper -> {
																logger.debug("Fetching disbursement status API with apiRequest : {}", apiRequest);	
																tbAudtiLog.setResTs(new Timestamp(System.currentTimeMillis()));
																String disburseResPayload = resWrapper.getApiResponse().getResponseBody()
																		.getResponseObj();
																boolean isvalidresponsepayload=false;
																logger.debug("Fetching disbursementStatus for check loan status error : {}", disburseResPayload);													
																try {
																	if (disburseResPayload != null) {
																		JSONObject jsonObject1 = new JSONObject(disburseResPayload);
																		logger.debug("Printing jsonObject for check laon status : {}", jsonObject);	
																		try {
																		    if (jsonObject1.has("error")) {
																		        JSONObject errorObjforNoRecord = jsonObject1.getJSONObject("error");
																		        if (errorObjforNoRecord.has("message")) {
																		            String errorMsg1 = errorObjforNoRecord.getString("message");
																		            if ("No record Found".equalsIgnoreCase(errorMsg1.trim())) {
																		                logger.info("Disbursement response message: No record Found");
																		                tbAudtiLog.setResTs(new Timestamp(System.currentTimeMillis()));
																		                tbAudtiLog.setResponsePayload(disburseResPayload);
																						tbAudtiLog.setApiStatus("FAILED");
																						tbAudtiLog.setStatus("FAILED");
																						Optional<MisReport> optionalExisting = misReportRepository.findByApplicationId(apiRequest.getRequestObj().getReferenceId());
																						logger.debug("Existing MisReport details for updating: {}", optionalExisting);
																						if (optionalExisting.isPresent()) {
																							MisReport misReport = optionalExisting.get();
																							Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
																							String timestampString = currentTimestamp.toString();
																							misReport.setUpdateDate(timestampString);
																							misReport.setRemarks("T24 disbursementStatus rejected");
																							logger.debug("MIS Report T24 Reject flow for disbursement Status :15---: {}", misReport);
																							misReportRepository.save(misReport);
																						} else {
																							logger.warn("No MIS Report found for applicationId: {}",
																									apiRequest.getRequestObj().getReferenceId());
																						}
																		                    
																		            }
																		        }
																		    }
																		} catch (Exception e) {
																		    logger.error("Exception while processing disburse response", e);
																		}
																		JSONObject resultObj = null;
																		if (jsonObject.has("body")&& jsonObject.getJSONArray("body").length() > 0) {
																			JSONObject bodyObj = jsonObject.getJSONArray("body")
																					.getJSONObject(0);
																			if (bodyObj.has("result") && bodyObj.getJSONArray("result").length() > 0) {
																				resultObj = bodyObj.getJSONArray("result").getJSONObject(0);									
																				if (resultObj.has("loanStatus") && "DISBURSED"
																						.equalsIgnoreCase(resultObj.optString("loanStatus"))) {
																					List<ApplicationMaster> appMasterListnew = appMasterRepo.findAllByApplicationId(applicationId);
																					logger.debug("Printing appMasterList for disbursed:" + appMasterListnew);
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
																					AuditTrailEntity existingAudit = auditTrailRepo.findApplicationId(applicationId);
																					logger.debug("Exiting AuditTrailEntity details for update disbursed status : {}", existingAudit);
																					if (existingAudit != null) {
																						AuditTrailEntity newTrailEntity = new AuditTrailEntity();
																						newTrailEntity.setAppId(existingAudit.getAppId());
																						newTrailEntity.setApplicationId(existingAudit.getApplicationId());
																						newTrailEntity.setBranchId(existingAudit.getBranchId());
																						newTrailEntity.setAddInfo1(existingAudit.getAddInfo1());
																						newTrailEntity.setAddInfo2(apiRequest.getRequestObj().getRemarks()+"~withDisburesestatusAPI~DISBURSED");
																						newTrailEntity.setAddInfo3(existingAudit.getAddInfo3());
																						newTrailEntity.setAddInfo4(existingAudit.getAddInfo4());
																						newTrailEntity.setCustomerId(existingAudit.getCustomerId());
																						newTrailEntity.setCustomerName(existingAudit.getCustomerName());
																						newTrailEntity.setKendraName(existingAudit.getKendraName());
																						newTrailEntity.setKendraId(existingAudit.getKendraId());
																						newTrailEntity.setLoanAmount(existingAudit.getLoanAmount());
																						newTrailEntity.setMobileNumber(existingAudit.getMobileNumber());
																						newTrailEntity.setPayload(existingAudit.getPayload());
																						newTrailEntity.setProductId(existingAudit.getProductId());
																						newTrailEntity.setCreateDate(existingAudit.getCreateDate());
																						newTrailEntity.setUserName(apiRequest.getRequestObj().getUserName());
																						Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
																						String timestampString = currentTimestamp.toString();
																						newTrailEntity.setCreateTs(timestampString);
																						newTrailEntity.setPurpose(existingAudit.getPurpose());
																						newTrailEntity.setRepaymentFrequency(existingAudit.getRepaymentFrequency());
																						newTrailEntity.setSpouse(existingAudit.getSpouse());
																						newTrailEntity.setAppVersion(apiRequest.getRequestObj().getAppVersion());
																						newTrailEntity.setStageid("17");
																						newTrailEntity.setUserId(apiRequest.getRequestObj().getUserId());
																						newTrailEntity.setUserRole(apiRequest.getRequestObj().getUserRole());
																						logger.debug("Saving AuditTrailEntity for dibuserment status : {}", newTrailEntity);
																						auditTrailRepo.save(newTrailEntity);
																						// Mis Report changes
																						Optional<MisReport> optionalExisting = misReportRepository
																								.findByApplicationId(applicationId);
																						logger.debug("Exiting MisReport for Updating disbursed status data: {}", optionalExisting);
																						if (optionalExisting.isPresent()) {
																							MisReport misReport = optionalExisting.get();
																							misReport.setUpdateDate(timestampString);
																							misReport.setModifyBy(apiRequest.getRequestObj().getUserId());
																							misReport.setStageID("17");
																							misReport.setRemarks(existingAudit.getAddInfo2());
																							misReport.setApplicationStatus("DISBURSED");
																							logger.debug("Updating MIS Report for stageId (Disbursed) :17: {}", misReport);
																							misReportRepository.save(misReport);
																						} else {
																							logger.warn("No MIS Report record found for applicationId for disbursed loan: {}",
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
																					AuditTrailEntity existingAudit = auditTrailRepo.findApplicationId(applicationId);
																					logger.debug("Exiting AuditTrailEntity for disbursement for loan created: {}", existingAudit);
																					if (existingAudit != null) {
																						AuditTrailEntity newAudit = new AuditTrailEntity(); 
																						newAudit.setAppId(existingAudit.getAppId());
																						newAudit.setApplicationId(existingAudit.getApplicationId());
																						newAudit.setBranchId(existingAudit.getBranchId());
																						newAudit.setAddInfo1(existingAudit.getAddInfo1());
																						newAudit.setAddInfo2(apiRequest.getRequestObj().getRemarks()+"~withDisburesestatusAPI~LOANCREATED");
																						newAudit.setAddInfo3(existingAudit.getAddInfo3());
																						newAudit.setAddInfo4(existingAudit.getAddInfo4());
																						newAudit.setCustomerId(existingAudit.getCustomerId());
																						newAudit.setCustomerName(existingAudit.getCustomerName());
																						newAudit.setKendraName(existingAudit.getKendraName());
																						newAudit.setKendraId(existingAudit.getKendraId());
																						newAudit.setLoanAmount(existingAudit.getLoanAmount());
																						newAudit.setMobileNumber(existingAudit.getMobileNumber());
																						newAudit.setPayload(existingAudit.getPayload());
																						newAudit.setProductId(existingAudit.getProductId());
																						newAudit.setCreateDate(existingAudit.getCreateDate());
																						newAudit.setUserName(apiRequest.getRequestObj().getUserName());
																						Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
																						String timestampString = currentTimestamp.toString();
																						newAudit.setCreateTs(timestampString);
																						newAudit.setPurpose(existingAudit.getPurpose());
																						newAudit.setRepaymentFrequency(existingAudit.getRepaymentFrequency());
																						newAudit.setSpouse(existingAudit.getSpouse());
																						newAudit.setAppVersion(apiRequest.getRequestObj().getAppVersion());
																						newAudit.setStageid("16");
																						newAudit.setUserId(apiRequest.getRequestObj().getUserId());
																						newAudit.setUserRole(apiRequest.getRequestObj().getUserRole());
																						auditTrailRepo.save(newAudit);
																						// Mis Report changes
																						Optional<MisReport> optionalExisting = misReportRepository.findByApplicationId(applicationId);
																						logger.debug("Exiting MisReport for Updating loan created data: {}", optionalExisting);
																						if (optionalExisting.isPresent()) {
																							MisReport misReport = optionalExisting.get();
																							misReport.setUpdateDate(timestampString);
																							misReport.setModifyBy(apiRequest.getRequestObj().getUserId());
																							misReport.setRemarks(existingAudit.getAddInfo2());
																							logger.debug("Updating MIS Report for stageId :16 (Loan Created): {}", misReport);
																							misReportRepository.save(misReport);
																						} else {
																							logger.warn("No MIS Report record found for applicationId for loan created: {}",
																									applicationId);
																						}
																					} 				
																				}
																				
																				else {
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
																			///here need to add refId is there and result is not there
																			else if(bodyObj.has("loanRefId") && !bodyObj.has("result")) {				
																				String loanRefId = bodyObj.getString("loanRefId");
																				logger.debug("Fetched loanRefId: {}", loanRefId);
																				tbAudtiLog.setResponsePayload(disburseResPayload);
																				tbAudtiLog.setApiStatus("FAILED");
																				tbAudtiLog.setStatus("INPROGRESS");	
																				AuditTrailEntity existingAudit = auditTrailRepo.findApplicationId(applicationId);
																				logger.debug("Exiting AuditTrailEntity for disbursement for Loan application is in pipeline: {}", existingAudit);
																				if (existingAudit != null) {
																					AuditTrailEntity newAudit = new AuditTrailEntity(); 
																					newAudit.setAppId(existingAudit.getAppId());
																					newAudit.setApplicationId(existingAudit.getApplicationId());
																					newAudit.setBranchId(existingAudit.getBranchId());
																					newAudit.setAddInfo1(existingAudit.getAddInfo1());
																					newAudit.setAddInfo2(apiRequest.getRequestObj().getRemarks()+"~withDisburesestatusAPI~INPROGRESS");
																					newAudit.setAddInfo3(existingAudit.getAddInfo3());
																					newAudit.setAddInfo4(existingAudit.getAddInfo4());
																					newAudit.setCustomerId(existingAudit.getCustomerId());
																					newAudit.setCustomerName(existingAudit.getCustomerName());
																					newAudit.setKendraName(existingAudit.getKendraName());
																					newAudit.setKendraId(existingAudit.getKendraId());
																					newAudit.setLoanAmount(existingAudit.getLoanAmount());
																					newAudit.setMobileNumber(existingAudit.getMobileNumber());
																					newAudit.setPayload(existingAudit.getPayload());
																					newAudit.setProductId(existingAudit.getProductId());
																					newAudit.setCreateDate(existingAudit.getCreateDate());
																					newAudit.setUserName(apiRequest.getRequestObj().getUserName());
																					Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
																					String timestampString = currentTimestamp.toString();
																					newAudit.setCreateTs(timestampString);
																					newAudit.setPurpose(existingAudit.getPurpose());
																					newAudit.setRepaymentFrequency(existingAudit.getRepaymentFrequency());
																					newAudit.setSpouse(existingAudit.getSpouse());
																					newAudit.setAppVersion(apiRequest.getRequestObj().getAppVersion());
																					newAudit.setStageid("16");
																					newAudit.setUserId(apiRequest.getRequestObj().getUserId());
																					newAudit.setUserRole(apiRequest.getRequestObj().getUserRole());
																					auditTrailRepo.save(newAudit);
																					// Mis Report changes
																					Optional<MisReport> optionalExisting = misReportRepository
																							.findByApplicationId(applicationId);
																					logger.debug("Exiting MisReport for Updating  Loan application is in pipeline: {}", optionalExisting);
																					if (optionalExisting.isPresent()) {
																						MisReport misReport = optionalExisting.get();
																						misReport.setUpdateDate(timestampString);
																						misReport.setModifyBy(apiRequest.getRequestObj().getUserId());
																						misReport.setRemarks(existingAudit.getAddInfo2());
																						logger.debug("Updating MIS Report for stageId :16(InPipeLine Application): {}", misReport);
																						misReportRepository.save(misReport);
																					} else {
																						logger.warn("No MIS Report record found for applicationId for pipeline Application: {}",
																								applicationId);
																					}
																				} 	
																			else {
																				logger.error("loanRefId not found in response body");
																			}	
																		}								
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
									                    }else {
									                    	tbAudtiLog.setResponsePayload(resObj);													
															tbAudtiLog.setApiStatus("FAILED");
															tbAudtiLog.setStatus("FAILURE");	
									                    }
									                }
									            }
									        }
									    }
									}
									
								}catch(Exception e) {
									//e.printStackTrace();
								}
							}
							if (resObj != null && resObj.trim().startsWith("{")) {
								JSONObject json = new JSONObject(resObj);
								if (json.has("error")) {
									Object errorObj = json.opt("error");
									if(errorObj instanceof String) {
										 String errorMsg = (String) errorObj;
										 if("3166-A timeout error occurred whilst performing an SSL socket operation"
								                    .equalsIgnoreCase(errorMsg)
								                    || "SOCKET error whilst invoking a web service".equalsIgnoreCase(errorMsg)) {
											    tbAudtiLog.setResTs(new Timestamp(System.currentTimeMillis()));
												tbAudtiLog.setResponsePayload(resObj);
												tbAudtiLog.setStatus("INPROGRESS");
												tbAudtiLog.setApiStatus("FAILED");
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
												
												
												//start
											/*
											 * return callDisbursementStatusApi(apiRequest, header).flatMap(resWrapper
											 * -> {
											 * logger.debug("Fetching disbursement status API with apiRequest : {}",
											 * apiRequest); tbAudtiLog.setResTs(new
											 * Timestamp(System.currentTimeMillis())); String disburseResPayload =
											 * resWrapper.getApiResponse().getResponseBody() .getResponseObj(); boolean
											 * isvalidresponsepayload=false;
											 * logger.debug("Fetching disbursementStatus for check loan status : {}",
											 * disburseResPayload); try { if (disburseResPayload != null) { JSONObject
											 * jsonObject = new JSONObject(disburseResPayload);
											 * logger.debug("Printing jsonObject for check laon status : {}",
											 * jsonObject); try { if (jsonObject.has("header")) { JSONObject headerObj =
											 * jsonObject.getJSONObject("header"); if (headerObj.has("id")) { String
											 * headerId = headerObj.getString("id"); if (headerId.startsWith("LO") &&
											 * jsonObject.has("error")) { JSONObject errorObjError =
											 * jsonObject.getJSONObject("error"); if (errorObjError.has("errorDetails"))
											 * { JSONArray errorDetails = errorObjError.getJSONArray("errorDetails"); if
											 * (errorDetails.length() > 0) { JSONObject firstError =
											 * errorDetails.getJSONObject(0); if (firstError.has("message")) { String
											 * message = firstError.getString("message").trim(); if
											 * ("Already one loan application is in processing stage for the customer"
											 * .equalsIgnoreCase(message)) { logger.debug("Matched message: {}",
											 * message); logger.debug("Header ID: {}", headerId);
											 * 
											 * tbAudtiLog.setResTs(new Timestamp(System.currentTimeMillis()));
											 * tbAudtiLog.setResponsePayload(disburseResPayload);
											 * tbAudtiLog.setApiStatus("FAILED"); tbAudtiLog.setStatus("INPROGRESS");
											 * 
											 * AuditTrailEntity auditTrailEntity =
											 * auditTrailRepo.findApplicationId(applicationId); logger.
											 * debug("Exiting AuditTrailEntity for disbursement for loan created: {}",
											 * auditTrailEntity); if (auditTrailEntity != null) {
											 * auditTrailEntity.setAppId(auditTrailEntity.getAppId());
											 * auditTrailEntity.setApplicationId(auditTrailEntity.getApplicationId());
											 * auditTrailEntity.setBranchId(auditTrailEntity.getBranchId());
											 * auditTrailEntity.setAddInfo1(auditTrailEntity.getAddInfo1());
											 * auditTrailEntity.setAddInfo2(apiRequest.getRequestObj().getRemarks()+
											 * "~withDisburesestatusAPI~INPROGRESS");
											 * auditTrailEntity.setAddInfo3(auditTrailEntity.getAddInfo3());
											 * auditTrailEntity.setAddInfo4(auditTrailEntity.getAddInfo4());
											 * auditTrailEntity.setCustomerId(auditTrailEntity.getCustomerId());
											 * auditTrailEntity.setCustomerName(auditTrailEntity.getCustomerName());
											 * auditTrailEntity.setKendraName(auditTrailEntity.getKendraName());
											 * auditTrailEntity.setLoanAmount(auditTrailEntity.getLoanAmount());
											 * auditTrailEntity.setMobileNumber(auditTrailEntity.getMobileNumber());
											 * auditTrailEntity.setPayload(auditTrailEntity.getPayload());
											 * auditTrailEntity.setProductId(auditTrailEntity.getProductId());
											 * auditTrailEntity.setCreateDate(auditTrailEntity.getCreateDate());
											 * auditTrailEntity.setUserName(apiRequest.getRequestObj().getUserName());
											 * Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
											 * String timestampString = currentTimestamp.toString();
											 * auditTrailEntity.setCreateTs(timestampString);
											 * auditTrailEntity.setPurpose(auditTrailEntity.getPurpose());
											 * auditTrailEntity.setRepaymentFrequency(auditTrailEntity.
											 * getRepaymentFrequency());
											 * auditTrailEntity.setSpouse(auditTrailEntity.getSpouse());
											 * auditTrailEntity.setAppVersion(apiRequest.getRequestObj().getAppVersion()
											 * ); auditTrailEntity.setStageid("16");
											 * auditTrailEntity.setUserId(apiRequest.getRequestObj().getUserId());
											 * auditTrailEntity.setUserRole(apiRequest.getRequestObj().getUserRole());
											 * auditTrailRepo.save(auditTrailEntity); // Mis Report changes
											 * Optional<MisReport> optionalExisting = misReportRepository
											 * .findByApplicationId(applicationId);
											 * logger.debug("Exiting MisReport for Updating loan created data: {}",
											 * optionalExisting); if (optionalExisting.isPresent()) { MisReport
											 * misReport = optionalExisting.get();
											 * misReport.setUpdateDate(timestampString);
											 * misReport.setModifyBy(apiRequest.getRequestObj().getUserId());
											 * misReport.setRemarks(auditTrailEntity.getAddInfo2()); logger.
											 * debug("Updating MIS Report for stageId :16 (for customer message): {}",
											 * misReport); misReportRepository.save(misReport); } else { logger.
											 * warn("No MIS Report record found for applicationId for loan created: {}",
											 * applicationId); } } else {
											 * logger.warn("No MIS Report found for applicationId: {}",
											 * apiRequest.getRequestObj().getReferenceId()); } } } } } } } } }
											 * catch(Exception e) { e.printStackTrace(); } try { if
											 * (jsonObject.has("error")) { JSONObject errorObjforNoRecord =
											 * jsonObject.getJSONObject("error"); if
											 * (errorObjforNoRecord.has("message")) { String errorMsg1 =
											 * errorObjforNoRecord.getString("message"); if
											 * ("No record Found".equalsIgnoreCase(errorMsg1.trim())) {
											 * logger.info("Disbursement response message: No record Found");
											 * tbAudtiLog.setResTs(new Timestamp(System.currentTimeMillis()));
											 * tbAudtiLog.setResponsePayload(disburseResPayload);
											 * tbAudtiLog.setApiStatus("FAILED"); tbAudtiLog.setStatus("FAILED");
											 * Optional<MisReport> optionalExisting = misReportRepository
											 * .findByApplicationId(apiRequest.getRequestObj().getReferenceId());
											 * logger.debug("Existing MisReport details for updating: {}",
											 * optionalExisting); if (optionalExisting.isPresent()) { MisReport
											 * misReport = optionalExisting.get(); Timestamp currentTimestamp = new
											 * Timestamp(System.currentTimeMillis()); String timestampString =
											 * currentTimestamp.toString(); misReport.setUpdateDate(timestampString);
											 * misReport.setRemarks("T24 disbursementStatus rejected"); logger.
											 * debug("MIS Report T24 Reject flow for disbursement Status :15---: {}",
											 * misReport); misReportRepository.save(misReport); } else {
											 * logger.warn("No MIS Report found for applicationId: {}",
											 * apiRequest.getRequestObj().getReferenceId()); }
											 * 
											 * } } } } catch (Exception e) {
											 * logger.error("Exception while processing disburse response", e); }
											 * JSONObject resultObj = null; if (jsonObject.has("body")&&
											 * jsonObject.getJSONArray("body").length() > 0) { JSONObject bodyObj =
											 * jsonObject.getJSONArray("body").getJSONObject(0); if
											 * (bodyObj.has("result") && bodyObj.getJSONArray("result").length() > 0) {
											 * resultObj = bodyObj.getJSONArray("result").getJSONObject(0); if
											 * (resultObj.has("loanStatus") && "DISBURSED"
											 * .equalsIgnoreCase(resultObj.optString("loanStatus"))) {
											 * List<ApplicationMaster> appMasterListnew =
											 * appMasterRepo.findAllByApplicationId(applicationId);
											 * logger.debug("Printing appMasterList for disbursed:" + appMasterListnew);
											 * ApplicationMaster appMasternew = null; if (appMasterListnew != null &&
											 * appMasterListnew.size() > 0) { appMasternew = appMasterList.get(0);
											 * appMasternew.setApplicationStatus("DISBURSED");
											 * appMasterRepo.save(appMasternew); }
											 * tbAudtiLog.setResponsePayload(disburseResPayload);
											 * tbAudtiLog.setApiStatus("SUCCESS"); tbAudtiLog.setStatus("SUCCESS"); //
											 * Audit trail changes AuditTrailEntity auditTrailEntity =
											 * auditTrailRepo.findApplicationId(applicationId); logger.
											 * debug("Exiting AuditTrailEntity details for update disbursed status : {}"
											 * , auditTrailEntity); if (auditTrailEntity != null) {
											 * auditTrailEntity.setAppId(auditTrailEntity.getAppId());
											 * auditTrailEntity.setApplicationId(auditTrailEntity.getApplicationId());
											 * auditTrailEntity.setBranchId(auditTrailEntity.getBranchId());
											 * auditTrailEntity.setAddInfo1(auditTrailEntity.getAddInfo1());
											 * auditTrailEntity.setAddInfo2(apiRequest.getRequestObj().getRemarks()+
											 * "~withDisburesestatusAPI~DISBURSED");
											 * auditTrailEntity.setAddInfo3(auditTrailEntity.getAddInfo3());
											 * auditTrailEntity.setAddInfo4(auditTrailEntity.getAddInfo4());
											 * auditTrailEntity.setCustomerId(auditTrailEntity.getCustomerId());
											 * auditTrailEntity.setCustomerName(auditTrailEntity.getCustomerName());
											 * auditTrailEntity.setKendraName(auditTrailEntity.getKendraName());
											 * auditTrailEntity.setLoanAmount(auditTrailEntity.getLoanAmount());
											 * auditTrailEntity.setMobileNumber(auditTrailEntity.getMobileNumber());
											 * auditTrailEntity.setPayload(auditTrailEntity.getPayload());
											 * auditTrailEntity.setProductId(auditTrailEntity.getProductId());
											 * auditTrailEntity.setCreateDate(auditTrailEntity.getCreateDate());
											 * auditTrailEntity.setUserName(apiRequest.getRequestObj().getUserName());
											 * Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
											 * String timestampString = currentTimestamp.toString();
											 * auditTrailEntity.setCreateTs(timestampString);
											 * auditTrailEntity.setPurpose(auditTrailEntity.getPurpose());
											 * auditTrailEntity.setRepaymentFrequency(auditTrailEntity.
											 * getRepaymentFrequency());
											 * auditTrailEntity.setSpouse(auditTrailEntity.getSpouse());
											 * auditTrailEntity.setAppVersion(apiRequest.getRequestObj().getAppVersion()
											 * ); auditTrailEntity.setStageid("17");
											 * auditTrailEntity.setUserId(apiRequest.getRequestObj().getUserId());
											 * auditTrailEntity.setUserRole(apiRequest.getRequestObj().getUserRole());
											 * logger.debug("Saving AuditTrailEntity for dibuserment status : {}",
											 * auditTrailEntity); auditTrailRepo.save(auditTrailEntity); // Mis Report
											 * changes Optional<MisReport> optionalExisting = misReportRepository
											 * .findByApplicationId(applicationId);
											 * logger.debug("Exiting MisReport for Updating disbursed status data: {}",
											 * optionalExisting); if (optionalExisting.isPresent()) { MisReport
											 * misReport = optionalExisting.get();
											 * misReport.setUpdateDate(timestampString);
											 * misReport.setModifyBy(apiRequest.getRequestObj().getUserId());
											 * misReport.setStageID("17");
											 * misReport.setRemarks(auditTrailEntity.getAddInfo2());
											 * misReport.setApplicationStatus("DISBURSED");
											 * logger.debug("Updating MIS Report for stageId (Disbursed) :17: {}",
											 * misReport); misReportRepository.save(misReport); } else { logger.
											 * warn("No MIS Report record found for applicationId for disbursed loan: {}"
											 * , applicationId); } } } else if (resultObj != null &&
											 * resultObj.has("loanStatus") && "LOAN.CREATED"
											 * .equalsIgnoreCase(resultObj.optString("loanStatus"))) {
											 * logger.debug("Entering inside loan created:");
											 * tbAudtiLog.setResponsePayload(disburseResPayload);
											 * tbAudtiLog.setApiStatus("FAILED"); tbAudtiLog.setStatus("INPROGRESS"); //
											 * need to check Audit trail and Mis Report AuditTrailEntity
											 * auditTrailEntity = auditTrailRepo.findApplicationId(applicationId);
											 * logger.
											 * debug("Exiting AuditTrailEntity for disbursement for loan created: {}",
											 * auditTrailEntity); if (auditTrailEntity != null) {
											 * auditTrailEntity.setAppId(auditTrailEntity.getAppId());
											 * auditTrailEntity.setApplicationId(auditTrailEntity.getApplicationId());
											 * auditTrailEntity.setBranchId(auditTrailEntity.getBranchId());
											 * auditTrailEntity.setAddInfo1(auditTrailEntity.getAddInfo1());
											 * auditTrailEntity.setAddInfo2(apiRequest.getRequestObj().getRemarks()+
											 * "~withDisburesestatusAPI~LOANCREATED");
											 * auditTrailEntity.setAddInfo3(auditTrailEntity.getAddInfo3());
											 * auditTrailEntity.setAddInfo4(auditTrailEntity.getAddInfo4());
											 * auditTrailEntity.setCustomerId(auditTrailEntity.getCustomerId());
											 * auditTrailEntity.setCustomerName(auditTrailEntity.getCustomerName());
											 * auditTrailEntity.setKendraName(auditTrailEntity.getKendraName());
											 * auditTrailEntity.setLoanAmount(auditTrailEntity.getLoanAmount());
											 * auditTrailEntity.setMobileNumber(auditTrailEntity.getMobileNumber());
											 * auditTrailEntity.setPayload(auditTrailEntity.getPayload());
											 * auditTrailEntity.setProductId(auditTrailEntity.getProductId());
											 * auditTrailEntity.setCreateDate(auditTrailEntity.getCreateDate());
											 * auditTrailEntity.setUserName(apiRequest.getRequestObj().getUserName());
											 * Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
											 * String timestampString = currentTimestamp.toString();
											 * auditTrailEntity.setCreateTs(timestampString);
											 * auditTrailEntity.setPurpose(auditTrailEntity.getPurpose());
											 * auditTrailEntity.setRepaymentFrequency(auditTrailEntity.
											 * getRepaymentFrequency());
											 * auditTrailEntity.setSpouse(auditTrailEntity.getSpouse());
											 * auditTrailEntity.setAppVersion(apiRequest.getRequestObj().getAppVersion()
											 * ); auditTrailEntity.setStageid("16");
											 * auditTrailEntity.setUserId(apiRequest.getRequestObj().getUserId());
											 * auditTrailEntity.setUserRole(apiRequest.getRequestObj().getUserRole());
											 * auditTrailRepo.save(auditTrailEntity); // Mis Report changes
											 * Optional<MisReport> optionalExisting = misReportRepository
											 * .findByApplicationId(applicationId);
											 * logger.debug("Exiting MisReport for Updating loan created data: {}",
											 * optionalExisting); if (optionalExisting.isPresent()) { MisReport
											 * misReport = optionalExisting.get();
											 * misReport.setUpdateDate(timestampString);
											 * misReport.setModifyBy(apiRequest.getRequestObj().getUserId());
											 * misReport.setRemarks(auditTrailEntity.getAddInfo2());
											 * logger.debug("Updating MIS Report for stageId :16 (Loan Created): {}",
											 * misReport); misReportRepository.save(misReport); } else { logger.
											 * warn("No MIS Report record found for applicationId for loan created: {}",
											 * applicationId); } } } else { ApplicationMaster apMaster = null; if
											 * (appMasterList != null && appMasterList.size() > 0) { apMaster =
											 * appMasterList.get(0); Optional<MisReport> optionalExisting =
											 * misReportRepository .findByApplicationId(apMaster.getApplicationId());
											 * logger.debug("Existing MisReport details fetch : {}", optionalExisting);
											 * if (optionalExisting.isPresent()) { MisReport misReport =
											 * optionalExisting.get(); Timestamp currentTimestamp = new
											 * Timestamp(System.currentTimeMillis()); String timestampString =
											 * currentTimestamp.toString(); misReport.setUpdateDate(timestampString);
											 * misReport.setRemarks("T24 disbursement rejected~withDisburesestatusAPI");
											 * logger.debug("MIS Report T24 Reject flow :15---: {}", misReport);
											 * misReportRepository.save(misReport); } else {
											 * logger.warn("No MIS Report found for applicationId: {}",
											 * apMaster.getApplicationId()); } }
											 * tbAudtiLog.setResponsePayload(disburseResPayload);
											 * tbAudtiLog.setApiStatus("FAILED"); tbAudtiLog.setStatus("FAILURE"); } }
											 * ///here need to add refId is there and result is not there else
											 * if(bodyObj.has("loanRefId") && !bodyObj.has("result")) { String loanRefId
											 * = bodyObj.getString("loanRefId"); logger.debug("Fetched loanRefId: {}",
											 * loanRefId); tbAudtiLog.setResponsePayload(disburseResPayload);
											 * tbAudtiLog.setApiStatus("FAILED"); tbAudtiLog.setStatus("INPROGRESS");
											 * AuditTrailEntity auditTrailEntity =
											 * auditTrailRepo.findApplicationId(applicationId); logger.
											 * debug("Exiting AuditTrailEntity for disbursement for Loan application is in pipeline: {}"
											 * , auditTrailEntity); if (auditTrailEntity != null) {
											 * auditTrailEntity.setAppId(auditTrailEntity.getAppId());
											 * auditTrailEntity.setApplicationId(auditTrailEntity.getApplicationId());
											 * auditTrailEntity.setBranchId(auditTrailEntity.getBranchId());
											 * auditTrailEntity.setAddInfo1(auditTrailEntity.getAddInfo1());
											 * auditTrailEntity.setAddInfo2(apiRequest.getRequestObj().getRemarks()+
											 * "~withDisburesestatusAPI~INPROGRESS");
											 * auditTrailEntity.setAddInfo3(auditTrailEntity.getAddInfo3());
											 * auditTrailEntity.setAddInfo4(auditTrailEntity.getAddInfo4());
											 * auditTrailEntity.setCustomerId(auditTrailEntity.getCustomerId());
											 * auditTrailEntity.setCustomerName(auditTrailEntity.getCustomerName());
											 * auditTrailEntity.setKendraName(auditTrailEntity.getKendraName());
											 * auditTrailEntity.setLoanAmount(auditTrailEntity.getLoanAmount());
											 * auditTrailEntity.setMobileNumber(auditTrailEntity.getMobileNumber());
											 * auditTrailEntity.setPayload(auditTrailEntity.getPayload());
											 * auditTrailEntity.setProductId(auditTrailEntity.getProductId());
											 * auditTrailEntity.setCreateDate(auditTrailEntity.getCreateDate());
											 * auditTrailEntity.setUserName(apiRequest.getRequestObj().getUserName());
											 * Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
											 * String timestampString = currentTimestamp.toString();
											 * auditTrailEntity.setCreateTs(timestampString);
											 * auditTrailEntity.setPurpose(auditTrailEntity.getPurpose());
											 * auditTrailEntity.setRepaymentFrequency(auditTrailEntity.
											 * getRepaymentFrequency());
											 * auditTrailEntity.setSpouse(auditTrailEntity.getSpouse());
											 * auditTrailEntity.setAppVersion(apiRequest.getRequestObj().getAppVersion()
											 * ); auditTrailEntity.setStageid("16");
											 * auditTrailEntity.setUserId(apiRequest.getRequestObj().getUserId());
											 * auditTrailEntity.setUserRole(apiRequest.getRequestObj().getUserRole());
											 * auditTrailRepo.save(auditTrailEntity); // Mis Report changes
											 * Optional<MisReport> optionalExisting = misReportRepository
											 * .findByApplicationId(applicationId); logger.
											 * debug("Exiting MisReport for Updating  Loan application is in pipeline: {}"
											 * , optionalExisting); if (optionalExisting.isPresent()) { MisReport
											 * misReport = optionalExisting.get();
											 * misReport.setUpdateDate(timestampString);
											 * misReport.setModifyBy(apiRequest.getRequestObj().getUserId());
											 * misReport.setRemarks(auditTrailEntity.getAddInfo2()); logger.
											 * debug("Updating MIS Report for stageId :16(InPipeLine Application): {}",
											 * misReport); misReportRepository.save(misReport); } else { logger.
											 * warn("No MIS Report record found for applicationId for pipeline Application: {}"
											 * , applicationId); } } else {
											 * logger.error("loanRefId not found in response body"); } } } } else {
											 * logger.error("Invalid JSON response: {}", disburseResPayload); } } catch
											 * (Exception e) { e.printStackTrace();
											 * logger.error("Exception while parsing disbursement response", e); }
											 * if(!isvalidresponsepayload) {
											 * tbAudtiLog.setResponsePayload(disburseResPayload);
											 * tbAudtiLog.setApiStatus("FAILED"); tbAudtiLog.setStatus("FAILURE");
											 * ApplicationMaster applMaster = null; if (appMasterList != null &&
											 * appMasterList.size() > 0) { applMaster = appMasterList.get(0);
											 * Optional<MisReport> optionalExisting = misReportRepository
											 * .findByApplicationId(applMaster.getApplicationId()); logger.
											 * debug("Printing misreport details for T24 disbursement invalid response  : {}"
											 * , optionalExisting); if (optionalExisting.isPresent()) { MisReport
											 * misReport = optionalExisting.get(); Timestamp currentTimestamp = new
											 * Timestamp(System.currentTimeMillis()); String timestampString =
											 * currentTimestamp.toString(); misReport.setUpdateDate(timestampString);
											 * misReport.
											 * setRemarks("T24 disbursement invalid response~withDisburesestatusAPI");
											 * misReportRepository.save(misReport); } else {
											 * logger.warn("No MIS Report found for applicationId: {}",
											 * applMaster.getApplicationId()); } } } auditLogsRepo.save(tbAudtiLog);
											 * return Mono.just(resWrapper); });
											 */
												
												// commented for error message
										 }	else {
											    tbAudtiLog.setResponsePayload(resObj);
												tbAudtiLog.setApiStatus("FAILED");
												tbAudtiLog.setStatus("FAILURE");
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
														misReport.setRemarks("T24 disbursement Status rejected");
														logger.debug("MIS Report T24 disbursement Status Reject flow :15---: {}", misReport);
														misReportRepository.save(misReport);
													} else {
														logger.warn("No MIS Report found for applicationId for disbursement Status Rejected: {}",
																appMaster.getApplicationId());
													}
												}   
												auditLogsRepo.save(tbAudtiLog);				 
										 }
									}
								}
							} 
	                    }						
						// end
					} catch (Exception exp) {
						logger.error("exception Occurred while extracting Status from PreCloserLoanApi: {}", exp);
					}
					logger.debug("Printing tbAudtiLog : {}", tbAudtiLog);
					auditLogsRepo.save(tbAudtiLog);
					logger.debug("Printing for TimeOut Error response : {}", responseMono);
					return Mono.just(responseMono);
				});
			} else {
				List<TbUaobApiAuditLogs> auditLogsforStatusInProgress = auditLogsRepo
						.findByApplicationIdAndApiStatusInProgress(applicationId);
				logger.debug("Printing disbursement status apiRequest for inProgress: {}", auditLogsforStatusInProgress);	
				if (auditLogsforStatusInProgress != null && !auditLogsforStatusInProgress.isEmpty()) {
					return callDisbursementStatusApi(apiRequest, header).flatMap(resWrapper -> {
						logger.debug("Printing InProgress apiRequest : {}", apiRequest);
						tbAudtiLog.setResTs(new Timestamp(System.currentTimeMillis()));
						String disburseResPayload = resWrapper.getApiResponse().getResponseBody()
								.getResponseObj();
						logger.debug("Printing Inprgress disburseResPayload details : {}", disburseResPayload);
						boolean isvalidresponsepayload=false;													
						try {
							if (disburseResPayload != null && !disburseResPayload.trim().isEmpty()) {
								JSONObject jsonObject = new JSONObject(disburseResPayload);
								logger.debug("Printing jsonObject for Inprogress response: {}", jsonObject);
								try {
									if (jsonObject.has("header")) {
									    JSONObject headerObj = jsonObject.getJSONObject("header");
									    if (headerObj.has("id")) {
									        String headerId = headerObj.getString("id");
									        if (headerId.startsWith("LO") && jsonObject.has("error")) {
									            JSONObject errorObjError = jsonObject.getJSONObject("error");
									            if (errorObjError.has("errorDetails")) {
									                JSONArray errorDetails = errorObjError.getJSONArray("errorDetails");
									                if (errorDetails.length() > 0) {
									                    JSONObject firstError = errorDetails.getJSONObject(0);
									                    if (firstError.has("message")) {
									                        String message = firstError.getString("message").trim();
									                        if ("Already one loan application is in processing stage for the customer".equalsIgnoreCase(message)) {
									                            logger.debug("Matched message: {}", message);
									                            logger.debug("Header ID: {}", headerId);

									                            tbAudtiLog.setResTs(new Timestamp(System.currentTimeMillis()));
									                            tbAudtiLog.setResponsePayload(disburseResPayload);
									                            tbAudtiLog.setApiStatus("FAILED");
									                            tbAudtiLog.setStatus("INPROGRESS");
									                            
									                            AuditTrailEntity exitingEntity = auditTrailRepo.findApplicationId(applicationId);
																logger.debug("Exiting AuditTrailEntity for disbursement for loan created: {}", exitingEntity);
																if (exitingEntity != null) {
																	AuditTrailEntity newAudit = new AuditTrailEntity();
																	newAudit.setAppId(exitingEntity.getAppId());
																	newAudit.setApplicationId(exitingEntity.getApplicationId());
																	newAudit.setBranchId(exitingEntity.getBranchId());
																	newAudit.setAddInfo1(exitingEntity.getAddInfo1());
																	newAudit.setAddInfo2(apiRequest.getRequestObj().getRemarks()+"~withDisburesestatusAPI~INPROGRESS");
																	newAudit.setAddInfo3(exitingEntity.getAddInfo3());
																	newAudit.setAddInfo4(exitingEntity.getAddInfo4());
																	newAudit.setCustomerId(exitingEntity.getCustomerId());
																	newAudit.setCustomerName(exitingEntity.getCustomerName());
																	newAudit.setKendraName(exitingEntity.getKendraName());
																	newAudit.setKendraId(exitingEntity.getKendraId());																	
																	newAudit.setLoanAmount(exitingEntity.getLoanAmount());
																	newAudit.setMobileNumber(exitingEntity.getMobileNumber());
																	newAudit.setPayload(exitingEntity.getPayload());
																	newAudit.setProductId(exitingEntity.getProductId());
																	newAudit.setCreateDate(exitingEntity.getCreateDate());
																	newAudit.setUserName(apiRequest.getRequestObj().getUserName());
																	Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
																	String timestampString = currentTimestamp.toString();
																	newAudit.setCreateTs(timestampString);
																	newAudit.setPurpose(exitingEntity.getPurpose());
																	newAudit.setRepaymentFrequency(exitingEntity.getRepaymentFrequency());
																	newAudit.setSpouse(exitingEntity.getSpouse());
																	newAudit.setAppVersion(apiRequest.getRequestObj().getAppVersion());
																	newAudit.setStageid("16");
																	newAudit.setUserId(apiRequest.getRequestObj().getUserId());
																	newAudit.setUserRole(apiRequest.getRequestObj().getUserRole());
																	auditTrailRepo.save(newAudit);
																	// Mis Report changes
																	Optional<MisReport> optionalExisting = misReportRepository
																			.findByApplicationId(applicationId);
																	logger.debug("Exiting MisReport for Updating loan created data: {}", optionalExisting);
																	if (optionalExisting.isPresent()) {
																		MisReport misReport = optionalExisting.get();
																		misReport.setUpdateDate(timestampString);
																		misReport.setModifyBy(apiRequest.getRequestObj().getUserId());
																		misReport.setRemarks(exitingEntity.getAddInfo2());
																		logger.debug("Updating MIS Report for stageId :16 (INPROGRESS for customer message): {}", misReport);
																		misReportRepository.save(misReport);
																	} else {
																		logger.warn("No MIS Report record found for applicationId for loan created: {}",
																				applicationId);
																	}
																}  else {
									                                logger.warn("No MIS Report found for applicationId: {}",
									                                        apiRequest.getRequestObj().getReferenceId());
									                            }
									                        }
									                    }
									                }
									            }
									        }
									    }
									}
                  			}catch(Exception e) {
								//e.printStackTrace();	
								}
								
								try {
								    if (jsonObject.has("error")) {
								        JSONObject errorObjforNoRecord = jsonObject.getJSONObject("error");
								        logger.debug("Printing errorObjforNoRecord for inprogress response: {}", errorObjforNoRecord);	
								        if (errorObjforNoRecord.has("message")) {
								            String errorMsg1 = errorObjforNoRecord.getString("message");
								            if ("No record Found".equalsIgnoreCase(errorMsg1.trim())) {
								                logger.info("Disbursement response message for inprogress application: No record Found");
								                tbAudtiLog.setResTs(new Timestamp(System.currentTimeMillis()));
								                tbAudtiLog.setResponsePayload(disburseResPayload);
												tbAudtiLog.setApiStatus("FAILED");
												tbAudtiLog.setStatus("FAILED");
												Optional<MisReport> optionalExisting = misReportRepository
														.findByApplicationId(apiRequest.getRequestObj().getReferenceId());
												logger.debug("Existing MisReport details for updating/Inprogress: {}", optionalExisting);
												if (optionalExisting.isPresent()) {
													MisReport misReport = optionalExisting.get();
													Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
													String timestampString = currentTimestamp.toString();
													misReport.setUpdateDate(timestampString);
													misReport.setRemarks("T24 disbursement Status rejected");
													logger.debug("MIS Report T24 Reject flow for disbursement Status for inprogesss application :15---: {}", misReport);
													misReportRepository.save(misReport);
												} else {
													logger.warn("No MIS Report found for applicationId: {}",
															apiRequest.getRequestObj().getReferenceId());
												}               
								            }
								        }
								    }
								} catch (Exception e) {
								    logger.error("Exception while processing disburse response", e);
								}
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
											    logger.warn("No ApplicationMaster found for applicationId for Inprogress: {}", applicationId);
											}
											tbAudtiLog.setResponsePayload(disburseResPayload);																		
											tbAudtiLog.setApiStatus("SUCCESS");
											tbAudtiLog.setStatus("SUCCESS");
											// Audit trail changes
											AuditTrailEntity exitingEntity = auditTrailRepo.findApplicationId(applicationId);
											logger.debug("Exiting AuditTrailEntity for RetryCasewithDisburesestatusAPI : {}", exitingEntity);
											if (exitingEntity != null) {
												AuditTrailEntity newAuditEntity =  new AuditTrailEntity();
												newAuditEntity.setAppId(exitingEntity.getAppId());
												newAuditEntity.setApplicationId(exitingEntity.getApplicationId());
												newAuditEntity.setBranchId(exitingEntity.getBranchId());
												newAuditEntity.setAddInfo1(exitingEntity.getAddInfo1());
												newAuditEntity.setAddInfo2(apiRequest.getRequestObj().getRemarks()+"~DisburesestatusAPI~DISBURSED");
												newAuditEntity.setAddInfo3(exitingEntity.getAddInfo3());
												newAuditEntity.setAddInfo4(exitingEntity.getAddInfo4());
												newAuditEntity.setCustomerId(exitingEntity.getCustomerId());
												newAuditEntity.setCustomerName(exitingEntity.getCustomerName());
												newAuditEntity.setKendraName(exitingEntity.getKendraName());
												newAuditEntity.setKendraId(exitingEntity.getKendraId());
												newAuditEntity.setLoanAmount(exitingEntity.getLoanAmount());
												newAuditEntity.setMobileNumber(exitingEntity.getMobileNumber());
												newAuditEntity.setPayload(exitingEntity.getPayload());
												newAuditEntity.setProductId(exitingEntity.getProductId());
												newAuditEntity.setCreateDate(exitingEntity.getCreateDate());
												newAuditEntity.setUserName(apiRequest.getRequestObj().getUserName());
												Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
												String timestampString = currentTimestamp.toString();
												newAuditEntity.setCreateTs(timestampString);
												newAuditEntity.setPurpose(exitingEntity.getPurpose());
												newAuditEntity.setRepaymentFrequency(exitingEntity.getRepaymentFrequency());
												newAuditEntity.setSpouse(exitingEntity.getSpouse());
												newAuditEntity.setAppVersion(apiRequest.getRequestObj().getAppVersion());
												newAuditEntity.setStageid("17");
												newAuditEntity.setUserId(apiRequest.getRequestObj().getUserId());
												newAuditEntity.setUserRole(apiRequest.getRequestObj().getUserRole());
												logger.debug("Saving AuditTrailEntity for RetryCasewithDisburesestatusAPI: {}", newAuditEntity);
												auditTrailRepo.save(newAuditEntity);
												// Mis Report changes
												Optional<MisReport> optionalExisting = misReportRepository
														.findByApplicationId(applicationId);
												logger.debug("Exiting MisReport for Updating RetryCasewithDisburesestatusAPI: {}", optionalExisting);
												if (optionalExisting.isPresent()) {
													MisReport misReport = optionalExisting.get();
													misReport.setUpdateDate(timestampString);
													misReport.setModifyBy(apiRequest.getRequestObj().getUserId());
													misReport.setStageID("17");
													misReport.setRemarks(exitingEntity.getAddInfo2());
													misReport.setApplicationStatus("DISBURSED");
													logger.debug("Updating MIS Report for stageId for status is inprogress:17: {}", misReport);
													misReportRepository.save(misReport);
												} else {
													logger.warn("No MIS Report record found for applicationId for inprogress: {}",
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
											AuditTrailEntity exitingEntity = auditTrailRepo.findApplicationId(applicationId);
											logger.debug("Exiting AuditTrailEntity for withDisburesestatusAPI for loan created for status is in Inprogress: {}", exitingEntity);
											if (exitingEntity != null) {
												AuditTrailEntity newAuditrailEntity = new AuditTrailEntity();
												newAuditrailEntity.setAppId(exitingEntity.getAppId());
												newAuditrailEntity.setApplicationId(exitingEntity.getApplicationId());
												newAuditrailEntity.setBranchId(exitingEntity.getBranchId());
												newAuditrailEntity.setAddInfo1(exitingEntity.getAddInfo1());
												newAuditrailEntity.setAddInfo2(apiRequest.getRequestObj().getRemarks()+"~DisburesestatusAPI~LOANCREATED");
												newAuditrailEntity.setAddInfo3(exitingEntity.getAddInfo3());
												newAuditrailEntity.setAddInfo4(exitingEntity.getAddInfo4());
												newAuditrailEntity.setCustomerId(exitingEntity.getCustomerId());
												newAuditrailEntity.setCustomerName(exitingEntity.getCustomerName());
												newAuditrailEntity.setKendraName(exitingEntity.getKendraName());
												newAuditrailEntity.setKendraId(exitingEntity.getKendraId());											
												newAuditrailEntity.setLoanAmount(exitingEntity.getLoanAmount());
												newAuditrailEntity.setMobileNumber(exitingEntity.getMobileNumber());
												newAuditrailEntity.setPayload(exitingEntity.getPayload());
												newAuditrailEntity.setProductId(exitingEntity.getProductId());
												newAuditrailEntity.setCreateDate(exitingEntity.getCreateDate());
												newAuditrailEntity.setUserName(apiRequest.getRequestObj().getUserName());
												Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
												String timestampString = currentTimestamp.toString();
												newAuditrailEntity.setCreateTs(timestampString);
												newAuditrailEntity.setPurpose(exitingEntity.getPurpose());
												newAuditrailEntity.setRepaymentFrequency(exitingEntity.getRepaymentFrequency());
												newAuditrailEntity.setSpouse(exitingEntity.getSpouse());
												newAuditrailEntity.setAppVersion(apiRequest.getRequestObj().getAppVersion());
												newAuditrailEntity.setStageid("16");
												newAuditrailEntity.setUserId(apiRequest.getRequestObj().getUserId());
												newAuditrailEntity.setUserRole(apiRequest.getRequestObj().getUserRole());
												auditTrailRepo.save(newAuditrailEntity);
												// Mis Report changes
												Optional<MisReport> optionalExisting = misReportRepository
														.findByApplicationId(applicationId);
												logger.debug("Exiting MisReport for Updating withDisburese Status data: {}", optionalExisting);
												if (optionalExisting.isPresent()) {
													MisReport misReport = optionalExisting.get();
													misReport.setUpdateDate(timestampString);
													misReport.setModifyBy(apiRequest.getRequestObj().getUserId());
													misReport.setRemarks(exitingEntity.getAddInfo2());
													logger.debug("Updating MIS Report for stageId :16(Loan Created inprogress): {}", misReport);
													misReportRepository.save(misReport);
												} else {
													logger.warn("No MIS Report record found for applicationId for loan created inprogress: {}",
															applicationId);
												}
											} 				
										}		
										else {
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
													logger.debug("MIS Report T24 Reject flow :15(InProgress cases)---: {}", misReport);
													misReportRepository.save(misReport);
												} else {
													logger.warn("No MIS Report found for applicationId for inprogress cases: {}",
															apMaster.getApplicationId());
												}
											}
											tbAudtiLog.setResponsePayload(disburseResPayload);													
											tbAudtiLog.setApiStatus("FAILED");
											tbAudtiLog.setStatus("FAILURE");
										}
									}		
									///here need to add refId is there and result is not there							
									else if(bodyObj.has("loanRefId") && !bodyObj.has("result")) {				
										String loanRefId = bodyObj.getString("loanRefId");
										logger.debug("Fetched loanRefId for inprogress: {}", loanRefId);
										tbAudtiLog.setResponsePayload(disburseResPayload);
										tbAudtiLog.setApiStatus("FAILED");
										tbAudtiLog.setStatus("INPROGRESS");	
										AuditTrailEntity exitingEntity = auditTrailRepo.findApplicationId(applicationId);
										logger.debug("Exiting AuditTrailEntity for disbursement for Loan application is in pipeline with status inprogress: {}", exitingEntity);
										if (exitingEntity != null) {
											AuditTrailEntity newTrailEntity = new AuditTrailEntity();
											newTrailEntity.setAppId(exitingEntity.getAppId());
											newTrailEntity.setApplicationId(exitingEntity.getApplicationId());
											newTrailEntity.setBranchId(exitingEntity.getBranchId());
											newTrailEntity.setAddInfo1(exitingEntity.getAddInfo1());
											newTrailEntity.setAddInfo2(apiRequest.getRequestObj().getRemarks()+"~DisburesestatusAPI~INPROGRESS");
											newTrailEntity.setAddInfo3(exitingEntity.getAddInfo3());
											newTrailEntity.setAddInfo4(exitingEntity.getAddInfo4());
											newTrailEntity.setCustomerId(exitingEntity.getCustomerId());
											newTrailEntity.setCustomerName(exitingEntity.getCustomerName());
											newTrailEntity.setKendraName(exitingEntity.getKendraName());
											newTrailEntity.setKendraId(exitingEntity.getKendraId());
											newTrailEntity.setLoanAmount(exitingEntity.getLoanAmount());
											newTrailEntity.setMobileNumber(exitingEntity.getMobileNumber());
											newTrailEntity.setPayload(exitingEntity.getPayload());
											newTrailEntity.setProductId(exitingEntity.getProductId());
											newTrailEntity.setCreateDate(exitingEntity.getCreateDate());
											newTrailEntity.setUserName(apiRequest.getRequestObj().getUserName());
											Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
											String timestampString = currentTimestamp.toString();
											newTrailEntity.setCreateTs(timestampString);
											newTrailEntity.setPurpose(exitingEntity.getPurpose());
											newTrailEntity.setRepaymentFrequency(exitingEntity.getRepaymentFrequency());
											newTrailEntity.setSpouse(exitingEntity.getSpouse());
											newTrailEntity.setAppVersion(apiRequest.getRequestObj().getAppVersion());
											newTrailEntity.setStageid("16");
											newTrailEntity.setUserId(apiRequest.getRequestObj().getUserId());
											newTrailEntity.setUserRole(apiRequest.getRequestObj().getUserRole());
											auditTrailRepo.save(newTrailEntity);
											// Mis Report changes
											Optional<MisReport> optionalExisting = misReportRepository
													.findByApplicationId(applicationId);
											logger.debug("Exiting MisReport for Updating  Loan application is in pipeline for status is Inprogress: {}", optionalExisting);
											if (optionalExisting.isPresent()) {
												MisReport misReport = optionalExisting.get();
												misReport.setUpdateDate(timestampString);
												misReport.setModifyBy(apiRequest.getRequestObj().getUserId());
												misReport.setRemarks(exitingEntity.getAddInfo2());
												logger.debug("Updating MIS Report for stageId :15(for status is InProgress): {}", misReport);
												misReportRepository.save(misReport);
											} else {
												logger.warn("No MIS Report record found for applicationId for status is InProgress: {}",
														applicationId);
											}
										} 	

									else {
										logger.error("loanRefId not found in response body");
									}	
								}
								} 
						} else {
								logger.error("Invalid JSON response: {}", disburseResPayload);	
						}
						} catch (Exception e) {
							//e.printStackTrace();
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
	

	// logic for update for charge&BreakUPDetails 
	private void updateChargeAndBreakUpAndInsurancedetails(PreClosureLoanRequest apiRequest, JSONObject cbResJson)
			throws JsonMappingException, JsonProcessingException {

		String applicationId = apiRequest.getRequestObj().getApplicationId();
		String memberInsStr = String.valueOf(cbResJson.getInt("Insurance_Charge_Member"));
		String spouseInsStr = String.valueOf(cbResJson.getInt("Insurance_Charge_Spouse"));

		logger.debug("Fetching memberInsStr :" + memberInsStr);
		logger.debug("Fetching spouseInsStr :" + spouseInsStr);

		int memberInsAmt = 0;
		int spouseInsAmt = 0;

		try {
			memberInsAmt = memberInsStr != null && !memberInsStr.isEmpty() ? Integer.parseInt(memberInsStr) : 0;
			spouseInsAmt = spouseInsStr != null && !spouseInsStr.isEmpty() ? Integer.parseInt(spouseInsStr) : 0;
		} catch (NumberFormatException e) {
			logger.error("Exception occurred", e);
		}
		logger.debug("Fetching memberInsAmt :" + memberInsAmt);
		logger.debug("Fetching spouseInsAmt :" + spouseInsAmt);

		int insurAmt = 0;

		List<TbUalnLoanDtls> tbUalnLoanDtlsList = tbualLoanDtlRepo.findAllByApplicationId(applicationId);
		logger.debug("Fetching tbUalnLoanDtlsList :" + tbUalnLoanDtlsList);

		if (!tbUalnLoanDtlsList.isEmpty()) {
			TbUalnLoanDtls loanDtl = tbUalnLoanDtlsList.get(0);
			String payload = loanDtl.getPayload();
			logger.debug("Fetching payload :" + payload);

			try {
				ObjectMapper objectMapper = new ObjectMapper();
				JsonNode rootNode = objectMapper.readTree(payload);
				JsonNode insurDtlsNode = rootNode.path("insurDtls");
				logger.debug("Fetching insurDtlsNode :" + insurDtlsNode);
				JsonNode chargeAndBreakupDtlsNode = rootNode.path("chargeAndBreakupDtls");
				logger.debug("Printing chargeAndBreakupDtlsNode :" + chargeAndBreakupDtlsNode);
				if (!insurDtlsNode.isMissingNode()) {
					String member = insurDtlsNode.path("member").asText();
					String spouse = insurDtlsNode.path("Spouse").asText();

					if ("Y".equalsIgnoreCase(member) && "Y".equalsIgnoreCase(spouse)) {
						insurAmt = memberInsAmt + spouseInsAmt;
						((ObjectNode) insurDtlsNode).put("applicant_insurance_amt", memberInsStr);
						((ObjectNode) insurDtlsNode).put("spouse_insurance_amt", spouseInsStr);
						//((ObjectNode) insurDtlsNode).put("reasonForNotOptingInsurance", "");
					} else if ("Y".equalsIgnoreCase(member)) {
						insurAmt = memberInsAmt;
						((ObjectNode) insurDtlsNode).put("applicant_insurance_amt", memberInsStr);
						((ObjectNode) insurDtlsNode).put("spouse_insurance_amt", "");
					} else if ("Y".equalsIgnoreCase(spouse)) {
						insurAmt = spouseInsAmt;
						((ObjectNode) insurDtlsNode).put("applicant_insurance_amt", "");
						((ObjectNode) insurDtlsNode).put("spouse_insurance_amt", spouseInsStr);
						//((ObjectNode) insurDtlsNode).put("reasonForNotOptingInsurance", "");
					} else {
						insurAmt = 0;
						((ObjectNode) insurDtlsNode).put("applicant_insurance_amt", "");
						((ObjectNode) insurDtlsNode).put("spouse_insurance_amt", "");
					}
				}

				double aprxloancharges = cbResJson.getDouble("GST")
						+ cbResJson.getDouble("Processing_fees_without_GST");
				logger.debug("Fetch aprxloancharges" + aprxloancharges);
				double aprxloanchargeswithins = aprxloancharges + insurAmt;
				logger.debug("Fetching aprxloanchargeswithins" + aprxloanchargeswithins);
				double aprxloanamount = (cbResJson.getDouble("Approved_Loan_Amount")
						- (cbResJson.getDouble("GST") + cbResJson.getDouble("Processing_fees_without_GST") + insurAmt));
				logger.debug("Fetch aprxloanamount" + aprxloanamount);

				DecimalFormat decimalFormat = new DecimalFormat("#.##");
				DecimalFormat intFormat = new DecimalFormat("0");

				((ObjectNode) chargeAndBreakupDtlsNode).put("loanAmt",
						intFormat.format(cbResJson.optInt("Approved_Loan_Amount", 0)));
				((ObjectNode) chargeAndBreakupDtlsNode).put("GST",
						decimalFormat.format(cbResJson.optDouble("GST", 0.0)));
				((ObjectNode) chargeAndBreakupDtlsNode).put("addInfo1", aprxloanchargeswithins);
				((ObjectNode) chargeAndBreakupDtlsNode).put("aprxLoanCharges", aprxloancharges);
				((ObjectNode) chargeAndBreakupDtlsNode).put("loanProcessingFee",
						cbResJson.optDouble("Processing_fees_without_GST", 0.0));
				((ObjectNode) chargeAndBreakupDtlsNode).put("aprxLoanAmt", aprxloanamount);

				String updatedPayload = objectMapper.writeValueAsString(rootNode);
				logger.debug("Printing updatedPayload: {}", updatedPayload);
				tbualLoanDtlRepo.updateValuesPostRetrigger(String.valueOf(cbResJson.getInt("Approved_Loan_Amount")),
						updatedPayload, applicationId);
				appMasterRepo.updateApplicationAmount(new BigDecimal(cbResJson.get("Approved_Loan_Amount").toString()),
						applicationId);

				String insurancejsonStringPost = objectMapper.writeValueAsString(insurDtlsNode);
				logger.debug("Printing insurancejsonStringPost: {}", insurancejsonStringPost);
				try {
					tbUacoInsuranceDtlsRepository.updateInsuranceValuesPostRetrigger(insurancejsonStringPost,
							applicationId);
				} catch (Exception e) {
					logger.error("exception occurred:", e);
				}

			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
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
       // validation for req amt and Appmaster amt validation 
	private boolean areEqualAmounts(Object val1, Object val2) {
	    try {
	        if (val1 == null || val2 == null) return false;
	        BigDecimal num1 = new BigDecimal(val1.toString().trim());
	        BigDecimal num2 = new BigDecimal(val2.toString().trim());
	        return num1.compareTo(num2) == 0;
	    } catch (NumberFormatException e) {
	        return false; 
	    }
	}
        // validation for req amt and Appmaster amt validation 
	private boolean areEqualStrings(Object val1, Object val2) {
	    if (val1 == null || val2 == null) return false;
	    return val1.toString().trim().equalsIgnoreCase(val2.toString().trim());
	}
	
	/*
	 * private boolean isValidYN(String value) { if (value == null) return false;
	 * String normalized = value.trim(); return normalized.equalsIgnoreCase("Y") ||
	 * normalized.equalsIgnoreCase("YES") || normalized.equalsIgnoreCase("N") ||
	 * normalized.equalsIgnoreCase("NO"); }
	 */
	
	private Boolean isValidYN(String value) {
	    if (value == null)
	        return null;

	    String normalized = value.trim().toUpperCase();

	    if (normalized.equals("Y") || normalized.equals("YES"))
	        return true;

	    if (normalized.equals("N") || normalized.equals("NO"))
	        return false;

	    return null;
	}


	
	private boolean areEqualTenure(String t1, String t2) {
	    if (t1 == null || t2 == null) return false;
	    return Integer.parseInt(t1.trim()) == Integer.parseInt(t2.trim());
	}
	
	
	private boolean areEqualAmountsEmptyZero(Object val1, Object val2) {
		try {
			BigDecimal num1 = normalizeAmount(val1);
			BigDecimal num2 = normalizeAmount(val2);
			return num1.compareTo(num2) == 0;
		} catch (Exception e) {
			return false;
		}
	}

	private BigDecimal normalizeAmount(Object val) {
		if (val == null) {
			return BigDecimal.ZERO;
		}
		String str = val.toString().trim();
		if (str.isEmpty()) {
			return BigDecimal.ZERO;
		}
		return new BigDecimal(str);
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

	
	
	// this is for DisbursementStatus API Service
	public Mono<ResponseWrapper> callDisbursementStatusApi(PreClosureLoanRequest apiRequest, Header header) {
		logger.debug("Printing disbursement status apiRequest: {}", apiRequest);
		logger.debug("Printing header: {}", header);
		try {
			header.setInterfaceId(FETCH_DISBURSEMENT_STATUS_INTERFACEDID);

			logger.debug("Printing disbursement status after added header: {}", header);

			Mono<Object> disburseStatus = interfaceAdapter.callExternalService(header, apiRequest.getRequestObj(),
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

}
