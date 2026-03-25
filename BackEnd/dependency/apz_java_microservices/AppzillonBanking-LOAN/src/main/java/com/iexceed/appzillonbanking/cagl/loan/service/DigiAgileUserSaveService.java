
package com.iexceed.appzillonbanking.cagl.loan.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.AuditTrailEntity;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.MisReport;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbOfficeData;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUaobCbResponse;
import com.iexceed.appzillonbanking.cagl.loan.domain.apz.DigiAgileUser;
import com.iexceed.appzillonbanking.cagl.loan.payload.*;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.*;
import com.iexceed.appzillonbanking.cagl.loan.repository.apz.DigiAgileUserRepository;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class DigiAgileUserSaveService {

	private static final Logger logger = LogManager.getLogger(DigiAgileUserSaveService.class);

	@Autowired
	private DigiAgileUserRepository digiAgileUserRepository;

	@Autowired
	@Lazy
	LoanService loanService;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TbOfficeDataRepository tbOfficeDataRepository;

	@Autowired
	TbUaobCbResponseRepository tbUaobCbResponseRepository;

	@Autowired
	AuditTrailRepo auditTrailRepo;

	@Autowired
	MisReportRepository misReportRepository;

	@Transactional
	public Response saveDigiAgileUserInfo(DigiAgileUserDtls digiAgileUserDtls) {
		logger.debug("Printing DigiAgile applyLoan:{} ", digiAgileUserDtls);
		Response response;
		try {
			DigiAgileUser digiAgileUser = new DigiAgileUser();
			digiAgileUser.setDigiagile_applicationId(digiAgileUserDtls.getApplicationId());
			digiAgileUser.setApplicationId("");
			digiAgileUser.setCustomerId(digiAgileUserDtls.getCustomerId());
			digiAgileUser.setPayload(new ObjectMapper().writeValueAsString(digiAgileUserDtls));
			digiAgileUser.setCreatedDate(LocalDateTime.now());
			digiAgileUser.setStatus("PENDING");
			digiAgileUser.setRemarks("INSERTION SUCCESS");
			digiAgileUser.setResponse("");
			logger.debug("Printing DigiAgile digiAgileUser:{} ", digiAgileUser);
			DigiAgileUser digiUser = digiAgileUserRepository.save(digiAgileUser);
			logger.debug("Printing DigiAgile digiUser:{} ", digiUser);
			Response maitriResponse = saveIntoMaitriApplication(digiAgileUserDtls);
			logger.debug("Printing for DigiAgile details response :" + maitriResponse);
			Optional<DigiAgileUser> updatedUser = digiAgileUserRepository
					.findById(digiUser.getDigiagile_applicationId());
			logger.debug("Printing for DigiAgile updatedUser :" + updatedUser);
			if (updatedUser.isPresent()) {
				digiUser = updatedUser.get();
			}
			logger.debug("Printing DIGIAGILE user info: " + digiUser);
			if (maitriResponse != null && maitriResponse.getResponseHeader() != null) {
				if ("0".equals(maitriResponse.getResponseHeader().getResponseCode())) {
					String responseObjStr = maitriResponse.getResponseBody().getResponseObj();
					JSONObject json = new JSONObject(responseObjStr);
					String applicationId = json.getString("applicationId");
					digiUser.setApplicationId(applicationId);
					logger.debug("Application Id : " + applicationId);
				} else {
					digiUser.setRemarks("APPLICATION CREATION FAILED");
					digiUser.setResponse(String.valueOf(maitriResponse));
					logger.debug("Error response message:" + maitriResponse.getResponseHeader().getResponseMessage());
				}
				logger.debug("Printing for DigiAgile digiUser :" + digiUser);
				digiAgileUserRepository.save(digiUser);
			}
			logger.debug("Printing for DigiAgile maitriResponse :" + maitriResponse);
			response = maitriResponse;
		} catch (Exception e) {
			logger.error("Exception at saveDigiAgileUserInfo", e);
			ResponseHeader respHeader = new ResponseHeader();
			ResponseBody respBody = new ResponseBody();
			respBody.setResponseObj("Something went wrong, Please try again!!");
			CommonUtils.generateHeaderForFailure(respHeader, "Exception occurred");
			response = new Response();
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
		}
		return response;
	}

	@Transactional
	public Response saveIntoMaitriApplication(DigiAgileUserDtls digiAgileUserDtls) {
		logger.debug("Start : saveIntoMaitriApplication Request : {}", digiAgileUserDtls);
		try {
			Random random = new Random();
			int randomNum = random.nextInt(90000) + 10000;
			String refId = digiAgileUserDtls.getBranchId() + randomNum;
			ApplicationDtls appDtls = new ApplicationDtls();
			appDtls.setApplicationId("");
			appDtls.setRefId(refId);
			appDtls.setVersionNo("");
			appDtls.setCreatedBy(digiAgileUserDtls.getKmId());
			appDtls.setApplicationDate(LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy")));
			appDtls.setStatus("INITIATE");
			appDtls.setKmId(digiAgileUserDtls.getKmId());
			appDtls.setKendraName(digiAgileUserDtls.getKendraName());
			appDtls.setKendraId(digiAgileUserDtls.getKendraId());
			appDtls.setBranchId(digiAgileUserDtls.getBranchId());
			appDtls.setGroupId(digiAgileUserDtls.getGroupId());
			appDtls.setProductGroupCode("KM");
			appDtls.setCreateTs(new Timestamp(System.currentTimeMillis()));
			appDtls.setModifyTs(new Timestamp(System.currentTimeMillis()));
			appDtls.setUpdatedBy(digiAgileUserDtls.getKmName());
			appDtls.setCustomerId(digiAgileUserDtls.getCustomerId());
			appDtls.setCustomerName(digiAgileUserDtls.getCustomerName());
			appDtls.setAmount(digiAgileUserDtls.getLoanAmount());
			appDtls.setAppVersion("DIGILOAN");
			appDtls.setActiveLoanCount("0");
			appDtls.setOutstandingPrincipal("0");
			appDtls.setOutstandingInterest("0");
			appDtls.setLeader("B");
			appDtls.setKendrafrequency(digiAgileUserDtls.getKendrafrequency());
			appDtls.setMeetingday(digiAgileUserDtls.getMeetingday());
			Map<String, Object> repayFreq = (Map<String, Object>) digiAgileUserDtls.getLoanDtls().getRepayFrequency();
			String idDesc = (String) repayFreq.get("idDesc");
			appDtls.setLoanfrequency(idDesc);
			appDtls.setRemarks("DIGIAGILE");
			appDtls.setCurrentScreenId("FROMDIGIAGILE");
			CustomerDtls customerDtls = new CustomerDtls();
			customerDtls.setCustomerId(digiAgileUserDtls.getCustomerId());
			customerDtls.setCustomerName(digiAgileUserDtls.getCustomerName());
			customerDtls.setKycDtls(digiAgileUserDtls.getKycDtls());
			List<Income> incomeList = digiAgileUserDtls.getIncome();
			if (incomeList != null && !incomeList.isEmpty()) {
				Income income = incomeList.get(0);
				income.setCustomerId(digiAgileUserDtls.getCustomerId());
			}
			customerDtls.setIncome(incomeList);
			customerDtls.setEarnings(digiAgileUserDtls.getEarnings());
			customerDtls.setBankDtls(digiAgileUserDtls.getBankDtls());
			customerDtls.setAddressDtls(digiAgileUserDtls.getAddressDtls());
			LoanDtls loanDtls = digiAgileUserDtls.getLoanDtls();
			BigDecimal interest = new BigDecimal(digiAgileUserDtls.getCbDetails().getRoi());
			loanDtls.setInterestRate(interest);
			String term = digiAgileUserDtls.getTerm();
			if (!term.contains("W")) {
				term = term + "W";
			}
			loanDtls.setTerm(term);
			customerDtls.setLoanDtls(loanDtls);
			customerDtls.setMobileNum(digiAgileUserDtls.getMobileNum());
			DigiAgileUserDtls digiUser = new DigiAgileUserDtls();
			digiUser.setApplicationId(digiAgileUserDtls.getApplicationId());
			digiUser.setCustomerId(digiAgileUserDtls.getCustomerId());
			digiUser.setKendraId(digiAgileUserDtls.getKendraId());
			digiUser.setBranchId(digiAgileUserDtls.getBranchId());
			digiUser.setKmId(digiAgileUserDtls.getKmId());
			digiUser.setCustomerName(digiAgileUserDtls.getCustomerName());
			digiUser.setMobileNum(digiAgileUserDtls.getMobileNum());
			digiUser.setKmName(digiAgileUserDtls.getKmName());
			digiUser.setGroupId(digiAgileUserDtls.getGroupId());
			digiUser.setKendraName(digiAgileUserDtls.getKendraName());
			digiUser.setKendrafrequency(digiAgileUserDtls.getKendrafrequency());
			digiUser.setMeetingday(digiAgileUserDtls.getMeetingday());
			digiUser.setLoanAmount(digiAgileUserDtls.getLoanAmount());
			digiUser.setKycDtls(digiAgileUserDtls.getKycDtls());
			digiUser.setIncome(digiAgileUserDtls.getIncome());
			digiUser.setEarnings(digiAgileUserDtls.getEarnings());
			digiUser.setBankDtls(digiAgileUserDtls.getBankDtls());
			digiUser.setAddressDtls(digiAgileUserDtls.getAddressDtls());
			digiUser.setLoanDtls(digiAgileUserDtls.getLoanDtls());
			digiUser.setCbDetails(digiAgileUserDtls.getCbDetails());
			CBDetails cbDetails = new CBDetails();
			cbDetails.setCustomer_id(digiAgileUserDtls.getCbDetails().getCustomer_id());
			cbDetails.setLoan_ID(digiAgileUserDtls.getCbDetails().getLoan_ID());
			cbDetails.setApplied_loan_code(digiAgileUserDtls.getCbDetails().getApplied_loan_code());
			cbDetails.setRequest_Date(digiAgileUserDtls.getCbDetails().getRequest_Date());
			cbDetails.setOTS_Flag(digiAgileUserDtls.getCbDetails().getOTS_Flag());
			cbDetails.setEligible_emi(digiAgileUserDtls.getCbDetails().getEligible_emi());
			cbDetails.setDerived_Attribute_1(digiAgileUserDtls.getCbDetails().getDerived_Attribute_1());
			cbDetails.setDerived_Attribute_2(digiAgileUserDtls.getCbDetails().getDerived_Attribute_2());
			cbDetails.setDerived_Attribute_3(digiAgileUserDtls.getCbDetails().getDerived_Attribute_3());
			cbDetails.setDerived_Attribute_4(digiAgileUserDtls.getCbDetails().getDerived_Attribute_4());
			cbDetails.setDerived_Attribute_5(digiAgileUserDtls.getCbDetails().getDerived_Attribute_5());
			cbDetails.setDerived_Attribute_6(digiAgileUserDtls.getCbDetails().getDerived_Attribute_6());
			cbDetails.setFinal_Decision(digiAgileUserDtls.getCbDetails().getFinal_Decision());
			BigDecimal approvedLoanAmount = new BigDecimal(digiAgileUserDtls.getCbDetails().getApproved_Loan_Amount());
			if (digiAgileUserDtls.getLoanAmount().compareTo(approvedLoanAmount) == 0) {
				cbDetails.setApproved_Loan_Amount(digiAgileUserDtls.getCbDetails().getApproved_Loan_Amount());
			} else {
				throw new Exception("Enter same loan amount and approved loan amount");
			}
			cbDetails.setDeviation_Category(digiAgileUserDtls.getCbDetails().getDeviation_Category());
			cbDetails.setNQA_Flag(digiAgileUserDtls.getCbDetails().getNQA_Flag());
			cbDetails.setPermissable_Income(digiAgileUserDtls.getCbDetails().getPermissable_Income());
			cbDetails.setFlow_response(digiAgileUserDtls.getCbDetails().getFlow_response());
			cbDetails.setRejection_reason(digiAgileUserDtls.getCbDetails().getRejection_reason());
			cbDetails.setIRIS_message(digiAgileUserDtls.getCbDetails().getIRIS_message());
			cbDetails.setRoi(digiAgileUserDtls.getCbDetails().getRoi());
			cbDetails.setEir(digiAgileUserDtls.getCbDetails().getEir());
			cbDetails.setInsurance_Charge_Member(digiAgileUserDtls.getCbDetails().getInsurance_Charge_Member());
			cbDetails.setInsurance_Charge_Spouse(digiAgileUserDtls.getCbDetails().getInsurance_Charge_Spouse());
			cbDetails.setProcessing_fees_without_GST(digiAgileUserDtls.getCbDetails().getProcessing_fees_without_GST());
			cbDetails.setGST(digiAgileUserDtls.getCbDetails().getGST());
			cbDetails.setInterest_Fee(digiAgileUserDtls.getCbDetails().getInterest_Fee());
			cbDetails.setUpfront_Fee(digiAgileUserDtls.getCbDetails().getUpfront_Fee());
			cbDetails.setApproved_loan_emi(digiAgileUserDtls.getCbDetails().getApproved_loan_emi());
			cbDetails.setFinal_foir_obligation(digiAgileUserDtls.getCbDetails().getFinal_foir_obligation());
			cbDetails.setFinal_foir(digiAgileUserDtls.getCbDetails().getFinal_foir());
			appDtls.setCustomerDetails(customerDtls);
			appDtls.setCbDetails(cbDetails);
			appDtls.setDigiAgileUserDtls(digiUser);
			Optional<TbOfficeData> officeData = tbOfficeDataRepository
					.findOfficeByBranchId(digiAgileUserDtls.getBranchId());
			if (officeData.isPresent()) {
				Map<String, Object> addInfo = objectMapper.convertValue(officeData.get(),
						new TypeReference<Map<String, Object>>() {
						});
				appDtls.setAddInfo(addInfo);
			}
			ApplyLoanRequestFields requestObj = new ApplyLoanRequestFields();
			List<ApplicationDtls> applicationList = new ArrayList<>();
			applicationList.add(appDtls);
			requestObj.setApplicationdtls(applicationList);
			requestObj.setUserRole("KM");
			requestObj.setUserName(digiAgileUserDtls.getKmName());
			requestObj.setAppVersion("DIGILOAN");
			requestObj.setRemarks("The DigiAgile Loan submitted by " + digiAgileUserDtls.getKmId() + " (KM)");
			requestObj.setUserId(digiAgileUserDtls.getKmId());
			ApplyLoanRequest request = new ApplyLoanRequest();
			request.setAppId("APZCBO");
			request.setInterfaceName("CreateLoanApplication");
			request.setUserId(digiAgileUserDtls.getKmId());
			request.setRequestObj(requestObj);
			ApplyLoanRequestWrapper requestWrapper = new ApplyLoanRequestWrapper();
			requestWrapper.setApiRequest(request);
			logger.debug("Maitri API Request from DigiAgile request : {}", requestWrapper);
			Response loanResponse = loanService.applyLoan(requestWrapper.getApiRequest());
			logger.debug("Loan Service Response : {}", loanResponse);
			return loanResponse;
		} catch (Exception e) {
			logger.error("Exception at saveIntoMaitriApplication", e);
			ResponseHeader respHeader = new ResponseHeader();
			ResponseBody respBody = new ResponseBody();
			respBody.setResponseObj("Something went wrong, Please try again!!" + " Exception: " + e);
			CommonUtils.generateHeaderForFailure(respHeader, "Exception occurred");
			Response response = new Response();
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
			return response;
		}
	}

	public String tbResponse(ApplyLoanRequest applyLoanRequest, String applicationId, String versionNo) {
		logger.debug("Printing Digi Agile tbResponse applyLoanRequest : {}", applyLoanRequest);
		logger.debug("Printing Digi Agile tbResponse applyLoanRequest : {}", applicationId);
		logger.debug("Printing Digi Agile tbResponseversionNo : {}", versionNo);
		try {
			TbUaobCbResponse tbUaobCbResponse = new TbUaobCbResponse();
			String id = CommonUtils.generateRandomNumStr();
			tbUaobCbResponse.setCbDtlId(id);
			tbUaobCbResponse.setAppId("APZCBO");
			tbUaobCbResponse.setApplicationId(applicationId);
			tbUaobCbResponse.setVersionNum("0");
			tbUaobCbResponse.setCustDtlId(applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getCustomerId());
			DigiAgileCBRequest digiAgileCBRequest = new DigiAgileCBRequest();
			digiAgileCBRequest.setAppId("APZCBO");
			digiAgileCBRequest.setInterfaceName("CreateLoanApplication");
			digiAgileCBRequest.setUserId(applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getKmId());
			digiAgileCBRequest.setLoanAmount(
					String.valueOf(applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getAmount()));
			digiAgileCBRequest.setLoanProductType(applyLoanRequest.getRequestObj().getApplicationdtls().get(0)
					.getCustomerDetails().getLoanDtls().getProductId());
			digiAgileCBRequest.setApplicantInsurance(applyLoanRequest.getRequestObj().getApplicationdtls().get(0)
					.getCustomerDetails().getLoanDtls().getInsurDtls().getMember());
			digiAgileCBRequest.setSpouseInsurance(applyLoanRequest.getRequestObj().getApplicationdtls().get(0)
					.getCustomerDetails().getLoanDtls().getInsurDtls().getSpouse());
			digiAgileCBRequest.setApplicantInsuranceAmt(applyLoanRequest.getRequestObj().getApplicationdtls().get(0)
					.getCustomerDetails().getLoanDtls().getInsurDtls().getApplicant_insurance_amt());
			digiAgileCBRequest.setSpouseInsuranceAmt(applyLoanRequest.getRequestObj().getApplicationdtls().get(0)
					.getCustomerDetails().getLoanDtls().getInsurDtls().getSpouse_insurance_amt());
			digiAgileCBRequest.setApplicationId(applicationId);
			digiAgileCBRequest.setVersionNo(versionNo);
			digiAgileCBRequest
					.setCustDtlId(applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getCustomerId());
			tbUaobCbResponse.setReqPayload(new ObjectMapper().writeValueAsString(digiAgileCBRequest));
			CBDetails cbDetails = new CBDetails();
			cbDetails.setCustomer_id(
					applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getCbDetails().getCustomer_id());
			cbDetails.setLoan_ID(
					applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getCbDetails().getLoan_ID());
			cbDetails.setApplied_loan_code(
					applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getCbDetails().getApplied_loan_code());
			cbDetails.setRequest_Date(
					applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getCbDetails().getRequest_Date());
			cbDetails.setOTS_Flag(
					applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getCbDetails().getOTS_Flag());
			cbDetails.setEligible_emi(
					applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getCbDetails().getEligible_emi());
			cbDetails.setDerived_Attribute_1(applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getCbDetails()
					.getDerived_Attribute_1());
			cbDetails.setDerived_Attribute_2(applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getCbDetails()
					.getDerived_Attribute_2());
			cbDetails.setDerived_Attribute_3(applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getCbDetails()
					.getDerived_Attribute_3());
			cbDetails.setDerived_Attribute_4(applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getCbDetails()
					.getDerived_Attribute_4());
			cbDetails.setDerived_Attribute_5(applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getCbDetails()
					.getDerived_Attribute_5());
			cbDetails.setDerived_Attribute_6(applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getCbDetails()
					.getDerived_Attribute_6());
			cbDetails.setFinal_Decision(
					applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getCbDetails().getFinal_Decision());
			cbDetails.setApproved_Loan_Amount(applyLoanRequest.getRequestObj().getApplicationdtls().get(0)
					.getCbDetails().getApproved_Loan_Amount());
			cbDetails.setDeviation_Category(applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getCbDetails()
					.getDeviation_Category());
			cbDetails.setNQA_Flag(
					applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getCbDetails().getNQA_Flag());
			cbDetails.setPermissable_Income(applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getCbDetails()
					.getPermissable_Income());
			cbDetails.setFlow_response(
					applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getCbDetails().getFlow_response());
			cbDetails.setRejection_reason(
					applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getCbDetails().getRejection_reason());
			cbDetails.setIRIS_message(
					applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getCbDetails().getIRIS_message());
			cbDetails.setRoi(applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getCbDetails().getRoi());
			cbDetails.setEir(applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getCbDetails().getEir());
			cbDetails.setInsurance_Charge_Member(applyLoanRequest.getRequestObj().getApplicationdtls().get(0)
					.getCbDetails().getInsurance_Charge_Member());
			cbDetails.setInsurance_Charge_Spouse(applyLoanRequest.getRequestObj().getApplicationdtls().get(0)
					.getCbDetails().getInsurance_Charge_Spouse());
			cbDetails.setProcessing_fees_without_GST(applyLoanRequest.getRequestObj().getApplicationdtls().get(0)
					.getCbDetails().getProcessing_fees_without_GST());
			cbDetails.setGST(applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getCbDetails().getGST());
			cbDetails.setInterest_Fee(
					applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getCbDetails().getInterest_Fee());
			cbDetails.setUpfront_Fee(
					applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getCbDetails().getUpfront_Fee());
			cbDetails.setApproved_loan_emi(
					applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getCbDetails().getApproved_loan_emi());
			cbDetails.setFinal_foir_obligation(applyLoanRequest.getRequestObj().getApplicationdtls().get(0)
					.getCbDetails().getFinal_foir_obligation());
			cbDetails.setFinal_foir(
					applyLoanRequest.getRequestObj().getApplicationdtls().get(0).getCbDetails().getFinal_foir());
			tbUaobCbResponse.setResPayload(new ObjectMapper().writeValueAsString(cbDetails));
			tbUaobCbResponse.setStatus("SUCCESS");
			tbUaobCbResponse.setCbCheckstatus("SUCCESS");
			tbUaobCbResponse.setReqTs(new Timestamp(System.currentTimeMillis()));
			tbUaobCbResponse.setResTs(new Timestamp(System.currentTimeMillis()));
			tbUaobCbResponse.setRetryCount(0);
			logger.debug("Printing Digi Agile tbUaobCbResponse : {}", tbUaobCbResponse);
			TbUaobCbResponse savedResponse = tbUaobCbResponseRepository.save(tbUaobCbResponse);
			logger.debug("Printing Digi Agile savedResponse : {}", savedResponse);
			if (savedResponse.getCbDtlId() != null) {
				return "SUCCESS";
			} else {
				return "FAILED";
			}
		} catch (Exception e) {
			logger.debug("Failed to save TB response: " + e.getMessage());
			return "FAILED";
		}
	}

	public Response workFlow(ApplyLoanRequest apiRequest, String applicationId, String versionNo) {
		logger.debug("Printing Digi Agile workflow apiRequest : {}", apiRequest);
		logger.debug("Printing Digi Agile workflow applicationId : {}", applicationId);
		logger.debug("Printing Digi Agile workflow versionNo : {}", versionNo);
		PopulateapplnWFRequest populateapplnWFRequest = new PopulateapplnWFRequest();
		PopulateapplnWFRequestFields populateapplnWFRequestFields = new PopulateapplnWFRequestFields();
		ApplicationList applicationList = new ApplicationList();
		applicationList.setApplicationId(applicationId);
		applicationList.setVersionNum(versionNo);
		List<ApplicationList> applicationListsNew = new ArrayList<>();
		applicationListsNew.add(applicationList);
		populateapplnWFRequestFields.setApplicationDetailList(applicationListsNew);
		WorkFlowDetails workFlowDetails = new WorkFlowDetails();
		workFlowDetails.setSeqNo(0);
		workFlowDetails.setNextStageId("BMQUEUE");
		workFlowDetails.setNextRole("BM");
		workFlowDetails.setNextWorkflowStatus("SANCTIONINPROGRESS");
		populateapplnWFRequestFields.setWorkflow(workFlowDetails);
		populateapplnWFRequestFields.setCreatedBy(apiRequest.getRequestObj().getApplicationdtls().get(0).getKmId());
		populateapplnWFRequestFields.setApplicationId(applicationId);
		populateapplnWFRequestFields.setApplicationStatus("");
		populateapplnWFRequestFields.setAppId("APZCBO");
		populateapplnWFRequestFields.setVersionNum(versionNo);
		// populateapplnWFRequestFields.setCbApproveManual(apiRequest.get);
		populateapplnWFRequestFields.setUserRole(apiRequest.getRequestObj().getUserRole());
		populateapplnWFRequestFields.setUserName(apiRequest.getRequestObj().getUserName());
		populateapplnWFRequestFields.setAppVersion("DIGILOAN");
		populateapplnWFRequestFields.setRemarks("DIGIAGILESUCEESS");
		// populateapplnWFRequestFields.setApplicationStatus("SANCTIONINPROGRESS");
		populateapplnWFRequest.setRequestObj(populateapplnWFRequestFields);
		logger.debug("Printing Digi Agile populateapplnWFRequest ::   " + populateapplnWFRequest);
		Response response = loanService.populateApplnWorkFlow(populateapplnWFRequest);
		logger.debug("Printing Digi Agile workflow response ::   " + response);
		loanService.updateStatusInMaster(populateapplnWFRequest);
		return response;
	}

	void updateMisReport(ApplyLoanRequest apiRequest, String applicationId) {
		AuditTrailEntity existingAuditTrail = auditTrailRepo.findApplicationId(applicationId);
		AuditTrailEntity newAuditTrail = new AuditTrailEntity();
		DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		String timestampString = currentTimestamp.toString();
		logger.debug("Existing Audit Trail for DigiAgile Loan : {}", existingAuditTrail);
		newAuditTrail.setAppId(existingAuditTrail.getAppId());
		newAuditTrail.setApplicationId(existingAuditTrail.getApplicationId());
		newAuditTrail.setLoanAmount(existingAuditTrail.getLoanAmount());
		newAuditTrail.setMobileNumber(existingAuditTrail.getMobileNumber());
		newAuditTrail.setPurpose(existingAuditTrail.getPurpose());
		newAuditTrail.setBranchId(existingAuditTrail.getBranchId());
		newAuditTrail.setProductId(existingAuditTrail.getProductId());
		newAuditTrail.setCustomerId(existingAuditTrail.getCustomerId());
		newAuditTrail.setKendraName(existingAuditTrail.getKendraName());
		newAuditTrail.setSpouse(existingAuditTrail.getSpouse());
		newAuditTrail.setCustomerName(existingAuditTrail.getCustomerName());
		newAuditTrail.setPayload(existingAuditTrail.getPayload());
		newAuditTrail.setRepaymentFrequency(existingAuditTrail.getRepaymentFrequency());
		newAuditTrail.setAddInfo1(existingAuditTrail.getAddInfo1());
		newAuditTrail.setAddInfo3(existingAuditTrail.getAddInfo3());
		newAuditTrail.setAddInfo4(existingAuditTrail.getAddInfo4());
		newAuditTrail.setCreateTs(timestampString);
		newAuditTrail.setCreateDate(LocalDate.now().format(dateFormatter));
		newAuditTrail.setUserName(apiRequest.getRequestObj().getUserName());
		newAuditTrail.setKendraId(existingAuditTrail.getKendraId());
		newAuditTrail.setStageid("5");
		newAuditTrail.setUserId(existingAuditTrail.getUserId());
		newAuditTrail.setUserRole(existingAuditTrail.getUserRole());
		newAuditTrail.setAddInfo2("The Loan CB saved from Digiagile");
		newAuditTrail.setAppVersion(existingAuditTrail.getAppVersion());
		newAuditTrail.setUserName(existingAuditTrail.getUserName());
		logger.debug("Updated Audit Trail for DigiAgile Loan : {}", newAuditTrail);
		auditTrailRepo.save(newAuditTrail);
		Optional<MisReport> optionalExisting = misReportRepository.findByApplicationId(applicationId);
		logger.debug("Existing MisReport for DigiAgile Loan : {}", optionalExisting);
		MisReport existingReport = optionalExisting.get();
		existingReport.setUpdateDate(timestampString);
		existingReport.setModifyBy(apiRequest.getUserId());
		existingReport.setRemarks(newAuditTrail.getAddInfo2());
		existingReport.setApplicationStatus("CB PASS");
		existingReport.setStageID("5");
		logger.debug("Saved updated/Edited MIS Report for DigiLoan: {}", existingReport);
		misReportRepository.save(existingReport);
	}
}
