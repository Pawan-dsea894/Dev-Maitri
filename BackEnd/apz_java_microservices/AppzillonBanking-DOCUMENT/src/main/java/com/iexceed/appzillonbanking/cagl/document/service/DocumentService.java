package com.iexceed.appzillonbanking.cagl.document.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import com.iexceed.appzillonbanking.cagl.document.domain.ab.*;
import com.iexceed.appzillonbanking.cagl.document.payload.*;
import com.iexceed.appzillonbanking.cagl.document.repository.ab.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cagl.document.constants.CommonConstants;
import com.iexceed.appzillonbanking.cagl.document.core.repository.apz.UserRoleRepository;
import com.iexceed.appzillonbanking.cagl.document.repository.ab.SanctionRepaymentScheduleRepository;
import com.iexceed.appzillonbanking.cagl.document.repository.apz.UserRepository;
import com.iexceed.appzillonbanking.core.payload.Header;
import com.iexceed.appzillonbanking.core.utils.CommonUtilsCBS;
import com.iexceed.appzillonbanking.interfaceAdapter.service.InterfaceAdapter;
import com.iexceed.appzillonbanking.interfaceAdapter.utils.AdapterUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import reactor.core.publisher.Mono;

@Service
public class DocumentService {

	private static final Logger logger = LogManager.getLogger(DocumentService.class);

	@Autowired
	private ApplicationMasterHisRepository applicationMasterHisRepository;

	@Autowired
	private TbUalLoanDtlsHisRepository loanDtlsHisRepo;


	@Autowired
	private TbUaobAddressDetailsHisRepository addressRepository;

	@Autowired
	private TbUaobCustomerDtlsHisRepo custDtlsHisRepo;

	@Autowired
	private TbUaobOccupationDtlsHisRepo tbUaobOccpationDtlHisRepo;

	@Autowired
	private TbUacoInsuranceDtlsHisRepo tbUacoInsuranceDtlsHisRepo;

	@Autowired
	private TbUaobNomineeDtlsHisRepository nomineeDtlsHisRepository;

	@Autowired
	private InterfaceAdapter interfaceAdapter;

	@Autowired
	private AdapterUtil adapterUtil;

	@Autowired
	private TbUaobCbResponseHisRepository cbResHisRepository;

	@Autowired
	private ApplicationWorkflowHisRepository applnWfHisRepository;

	@Autowired
	private WorkflowDefinitionHisRepository wfDefnHisRepoLn;

	@Autowired
	private TbUaobApiAuditLogsHisRepository tbUaobApiAuditLogsHisRepository;


	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserRoleRepository userRoleRepository;


	@Lazy // Prevents circular dependency
	@Autowired
	private ReportBuildService reportBuildService;

	@Autowired
	private SanctionRepaymentScheduleRepository sanctionRepaymentScheduleRepository;

	@PersistenceContext
	private final EntityManager entityManager;


	public DocumentService(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public static final String EXCEPTION_MSG = "Something went wrong, Please try again!!";
	public static final String EXCEPTION_OCCURED = "Exception occurred";
	public static final String CBCHECK_REPORT_INTERFACEID = "fetchCbReport";
	public static final String FETCH_LOAN_SCHEDULE_INTERFACEID = "FetchLoanSchedule";
	public static final String FETCH_SANCTION_LOAN_SCHEDULE_INTERFACEID = "GetSanctionLoanSchedule";

	private static final String LOAN_SCHEDULE_PROJECTOR = "GetSanctionLoanSchedule";

	private static final String GENERATEPASSBOOK = "GeneratePassbook";


	public Mono<byte[]> callandGenerateKFSScheuduleHis(String applicationId, String stage) {
		logger.debug("=====Insertion for Sanction Letter Repay=====");
		Header header = new Header();
		String roi = "", loanAmt = "";
		header.setInterfaceId(LOAN_SCHEDULE_PROJECTOR);
		LocalDateTime currentDate = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String dateWithTime = currentDate.format(formatter);
		logger.debug("Printing date:" + dateWithTime);

		List<ApplicationMasterHis> applicationMasterHisList = applicationMasterHisRepository.findByApplicationId(applicationId);
		logger.debug("applicationMasterList for Report:" + applicationMasterHisList);

		ApplicationMasterHis appMaster = applicationMasterHisList.get(0);

		FetchAppRequest appReq = new FetchAppRequest();
		appReq.setAppId(appMaster.getAppId());
		FetchAppRequestFields fields = new FetchAppRequestFields();
		fields.setApplicationId(applicationId);
		fields.setVersionNum(Integer.parseInt(appMaster.getVersionNum()));
		appReq.setRequestObj(fields);
		ApplyLoanRequestHisFields loanFields = getCustomerDataHis(applicationMasterHisList, appReq);
		logger.debug("Fetching loanFields :" + loanFields);
		ApplicationDtls appDtl = loanFields.getApplicationdtls().get(0);
		CustomerDtls customerDtl = appDtl.getCustomerDetails();
		LoanDtls loanDtl = customerDtl.getLoanDtls();
		logger.debug("Fetching loanDtl :" + loanDtl);
		HashMap<String, String> freqObj = (HashMap<String, String>) loanDtl.getRepayFrequency();
		logger.debug("Fetching freqObj :" + freqObj);
		String productFullCode = loanDtl.getProduct();

		logger.debug("proudct full name is " + productFullCode);

		if (appMaster != null) {
			loanAmt = appMaster.getAmount().toString();
		}
		Optional<TbUaobCbResponseHis> cbRes = cbResHisRepository.findTopByApplicationIdOrderByResTsDesc(applicationId);
		logger.debug("cbRes for Report:" + cbRes);
		if (cbRes.isPresent()) {
			TbUaobCbResponseHis cbResponse = cbRes.get();
			logger.debug("cbRes is present:" + cbResponse);
			String payload = cbResponse.getResPayload();
			JSONObject paylonJsonObj = new JSONObject(payload);
			if (paylonJsonObj.has("roi")) {
				roi = paylonJsonObj.getString("roi");
			}
		}
		if (appMaster.getProductCode().equals("EL")) {
			roi = "0";
		}

		logger.debug("roi val is:" + roi);
		CommonUtilsCBS commonUtilsCBS = new CommonUtilsCBS();
		String uniqueId = commonUtilsCBS.generateReferenceNumber(5);
		SanctionLoanScheduleRequest sanctionReportRequest = new SanctionLoanScheduleRequest();
		SanctionLoanScheduleRequestFields sanctionLoanScheduleRequestFields = new SanctionLoanScheduleRequestFields();
		sanctionLoanScheduleRequestFields.setCustomerId(appMaster.getCustomerId());
		sanctionLoanScheduleRequestFields.setInterestRate(roi);
		sanctionLoanScheduleRequestFields.setLoanAmount(loanAmt);
		sanctionLoanScheduleRequestFields.setLoanFrequency(freqObj.getOrDefault("idDesc", "WEEKLY"));
		sanctionLoanScheduleRequestFields.setProductID(productFullCode);
		String tenure = loanDtl.getTerm();

		int loanAmtForEL = 0;
		if (loanDtl.getChargeAndBreakupDtls() != null) {
			String loanAmtStr = loanDtl.getChargeAndBreakupDtls().getLoanAmt();
			if (loanAmtStr != null && loanAmtStr.matches("\\d+")) {
				loanAmtForEL = Integer.parseInt(loanAmtStr);
			}
		}
		String frequency = freqObj != null ? freqObj.get("idDesc") : null;
		if ("EL".equalsIgnoreCase(appMaster.getProductCode())) {
			if ("WEEKLY".equalsIgnoreCase(frequency) && loanAmtForEL == 1000) {
				tenure = "11";
			} else if ("WEEKLY".equalsIgnoreCase(frequency) && loanAmtForEL == 2000) {
				tenure = "21";
			} else if (("BI-WEEKLY".equalsIgnoreCase(frequency) || "BIWEEKLY".equalsIgnoreCase(frequency))
					&& loanAmtForEL == 1000) {
				tenure = "12";
			} else if (("BI-WEEKLY".equalsIgnoreCase(frequency) || "BIWEEKLY".equalsIgnoreCase(frequency))
					&& loanAmtForEL == 2000) {
				tenure = "22";
			}
		}
		logger.debug("Printing tenure without removing unit:" + tenure);
		String replacedTenure = "";
		if (tenure != null && !tenure.isEmpty()) {
			replacedTenure = tenure.replaceAll("[a-zA-Z]", "");
		}
		if (("BI-WEEKLY".equalsIgnoreCase(frequency) || "BIWEEKLY".equalsIgnoreCase(frequency))) {
			sanctionLoanScheduleRequestFields.setLoanFrequency("BI-WEEKLY");
		}
		logger.debug("Printing replacedTenure:" + replacedTenure);
		sanctionLoanScheduleRequestFields.setTenure(replacedTenure);
		sanctionReportRequest.setRequestObj(sanctionLoanScheduleRequestFields);

		logger.debug("Printing final GetSanctionLoanSchedule request : " + sanctionReportRequest);

		Mono<Object> externalServiceResponse = interfaceAdapter.callExternalService(header, sanctionReportRequest,
				LOAN_SCHEDULE_PROJECTOR, true);

		logger.debug("Printing externalServiceResponse : " + externalServiceResponse);

		return externalServiceResponse.flatMap(val -> {
			logger.debug("Received response from external service Report: {}" + val);

			try {
				JSONObject responseJsonObj = new JSONObject(new ObjectMapper().writeValueAsString(val));
				logger.debug("Received responseJsonObj: " + responseJsonObj);

				if (responseJsonObj.has("body")) {
					// Going to delete old application Data and going to insert new data
					logger.debug("Deleting old repayment schedules for applicationId: {}", applicationId);
					sanctionRepaymentScheduleRepository.deleteByApplicationId(applicationId);
					logger.debug("Delete done");

					JSONArray respBody = responseJsonObj.getJSONArray("body");
					List<SanctionRepaymentSchedule> repaymentSchedules = new ArrayList<>();
					String installmentValue = "";

					for (int i = 0; i < respBody.length(); i++) {
						JSONObject bodyIndivObj = respBody.getJSONObject(i);
						SanctionRepaymentSchedule schedule = new SanctionRepaymentSchedule();
						schedule.setOutstandingPrincipal(bodyIndivObj.optString("Outstanding", "0"));
						schedule.setPrincipal(bodyIndivObj.optString("Principal", "0"));
						schedule.setParameter(bodyIndivObj.optString("SL.NO", ""));
						schedule.setInterest(bodyIndivObj.optString("Interest", "0"));
						schedule.setInstallment(bodyIndivObj.optString("Total Due", "0"));

						// going to inside into loan table with value of installmentValue
						if ("2".equals(schedule.getParameter())) {
							installmentValue = schedule.getInstallment();
							logger.debug("Installment value found for parameter 2: {}", installmentValue);
						}
						schedule.setSanctRepoId(applicationId + uniqueId);
						schedule.setCreateTs(dateWithTime);
						schedule.setApplicationId(applicationId);
						schedule.setTypeOfStage(stage);
						repaymentSchedules.add(schedule);

					}

					if (!repaymentSchedules.isEmpty()) {
						sanctionRepaymentScheduleRepository.saveAll(repaymentSchedules);
						logger.debug("Inserted {} repayment schedules into DB.", repaymentSchedules.size());
					}

					if (!installmentValue.isEmpty()) {
						try {
							int updatedRows = loanDtlsHisRepo.updateInstallmentDetails(installmentValue, applicationId);
							logger.debug("Updated {} rows for installment details of applicationId: {}", updatedRows,
									applicationId);
						} catch (Exception e) {
							logger.error("Error updating installment details for applicationId: {}", applicationId, e);
						}
					}

				}
			} catch (JsonProcessingException e) {
				logger.error("Error processing JSON response for applicationId: {}", applicationId, e);
				return Mono.error(e);
			}
			byte[] fileObjBytes = reportBuildService.generateAndDownloadSanctionReport(applicationId);
			logger.warn("Sanction report downloaded successfully for applicationId: {}", applicationId);
			sanctionRepaymentScheduleRepository.deleteByApplicationId(applicationId);
			logger.debug("Deleted repayment schedules post-report generation for applicationId: {}", applicationId);
			return Mono.just(fileObjBytes);
		}).onErrorResume(ex -> {
			logger.error("Error occurred while calling external service for applicationId: {}", applicationId, ex);
			return Mono.error(new RuntimeException("External service failed for applicationId: " + applicationId, ex));
		});
	}


	public ApplyLoanRequestHisFields getCustomerDataHis(List<ApplicationMasterHis> applicationMasterHisList,
														FetchAppRequest request) {
		logger.debug("Printing applicationMasterList" + applicationMasterHisList);
		logger.debug("Printing FetchAppRequest" + request);
		ApplyLoanRequestHisFields loanFields = new ApplyLoanRequestHisFields();
		List<ApplicationDtls> appDtlsList = new ArrayList<>();
		Gson gson = new Gson();
		List<AddressDtls> addressList = new ArrayList<>();
		try {
			for (ApplicationMasterHis appMaster : applicationMasterHisList) {
				ApplicationDtls appDtls = new ApplicationDtls();
				appDtls.setApplicationId(appMaster.getApplicationId());
				appDtls.setVersionNo(String.valueOf(appMaster.getVersionNum()));
				appDtls.setApplicationDate(String.valueOf(appMaster.getApplicationDate()));
				appDtls.setStatus(appMaster.getApplicationStatus());
				appDtls.setKmId(appMaster.getKmId());
				appDtls.setKendraId(appMaster.getKendraId());
				appDtls.setKendraName(appMaster.getKendraName());
				appDtls.setBranchId(appMaster.getBranchCode());
				appDtls.setLeader(appMaster.getLeader());
				appDtls.setCreateTs(appMaster.getCreateTs());
				appDtls.setModifyTs(appMaster.getCreateTs());
				appDtls.setUpdatedBy(appMaster.getKmId());
				appDtls.setCustomerId(appMaster.getCustomerId());
				appDtls.setAmount(appMaster.getAmount());
				appDtls.setCustomerName(appMaster.getCustomerName());
				appDtls.setLoanMode(appMaster.getLoanMode());
				appDtls.setProductCode(appMaster.getProductCode());
				appDtls.setProductType(appMaster.getProductCode());
				appDtls.setProductGroupCode(appMaster.getProductGroupCode());
				appDtls.setRemarks(appMaster.getRemarks());
				appDtls.setCurrentScreenId(appMaster.getCurrentScreenId());
				appDtls.setCurrentStage(appMaster.getCurrentStage());
				appDtls.setKycType(appMaster.getKycType());
				appDtls.setApplicationType(appMaster.getApplicationType());
				appDtls.setAddInfo(appMaster.getAddInfo());
				appDtls.setCreatedBy(appMaster.getCreatedBy());
				String applicationId = appMaster.getApplicationId();
				Optional<TbUaobCustDtlsHis> customerDetailsHis = custDtlsHisRepo.findByApplicationId(applicationId);
				logger.debug("Printing customerDetails records from DB:" + customerDetailsHis);
				if (customerDetailsHis.isPresent()) {
					logger.debug("customerDetails is present");
					CustomerDtls custDtls = new CustomerDtls();
					JSONObject kycDetailsJson = new JSONObject(customerDetailsHis.get().getKycDetails());
					if (kycDetailsJson.has("mobileNum")) {
						custDtls.setMobileNum(kycDetailsJson.get("mobileNum").toString());
					}
					custDtls.setCustomerName(customerDetailsHis.get().getCustomerName());
					custDtls.setCustomerId(customerDetailsHis.get().getCustomerId());
					List<TbUaobAddressDetailsHis> addressDetailsHisList = addressRepository
							.findByApplicationId(applicationId);
					logger.debug("Printing addressDetailsList records from DB:" + addressDetailsHisList);
					for (TbUaobAddressDetailsHis addDtl : addressDetailsHisList) {
						AddressDtls addPayload = new AddressDtls();
						JSONObject addPayloadJson = new JSONObject(addDtl.getPayload());
						if (addPayloadJson.has("addLine1")) {
							addPayload.setAddLine1(addPayloadJson.getString("addLine1"));
						}
						if (addPayloadJson.has("addLine2")) {
							addPayload.setAddLine2(addPayloadJson.getString("addLine2"));
						}
						addPayload.setAddressType(addDtl.getAddressType());
						if (addPayloadJson.has("district")) {
							addPayload.setDistrict(addPayloadJson.getString("district"));
						}
						if (addPayloadJson.has("pincode")) {
							addPayload.setPincode(addPayloadJson.getString("pincode"));
						}
						if (addPayloadJson.has("state")) {
							addPayload.setState(addPayloadJson.getString("state"));
						}
						if (addPayloadJson.has("taluk")) {
							addPayload.setTaluk(addPayloadJson.getString("taluk"));
						}
						if (addPayloadJson.has("villageLocality")) {
							addPayload.setVillageLocality(addPayloadJson.getString("villageLocality"));
						}
						addressList.add(addPayload);
					}
					custDtls.setAddressDtls(addressList);
					List<Income> incomeList = new ArrayList<>();
					List<Earnings> earningsList = new ArrayList<>();
					Optional<TbUaobOccupationDtlsHis> occupDtlOpt = tbUaobOccpationDtlHisRepo
							.findByApplicationId(applicationId);
					logger.debug("Printing occupDtlOpt records from DB:" + occupDtlOpt);
					if (occupDtlOpt.isPresent()) {
						JSONArray incomeJsonArr = new JSONArray(occupDtlOpt.get().getIncomePayload());
						for (Object incomeJson : incomeJsonArr) {
							JSONObject incomeJsonObj = new JSONObject(incomeJson.toString());
							Income income = new Income();
							if (incomeJsonObj.has(CommonConstants.CUSTOMERID)) {
								income.setCustomerId(incomeJsonObj.getString(CommonConstants.CUSTOMERID));
							}
							if (incomeJsonObj.has("assesmentDt")) {
								income.setAssesmentDt(incomeJsonObj.getString("assesmentDt"));
							}
							if (incomeJsonObj.has("totExpense")) {
								income.setTotExpense(incomeJsonObj.getString("totExpense"));
							}
							if (incomeJsonObj.has("totIncome")) {
								income.setTotIncome(incomeJsonObj.getString("totIncome"));
							}
							incomeList.add(income);
						}
						JSONArray earningJsonArr = new JSONArray(occupDtlOpt.get().getEarningsPayload());
						for (Object earningJson : earningJsonArr) {
							JSONObject earningJsonObj = new JSONObject(earningJson.toString());
							Earnings earnings = new Earnings();
							if (earningJsonObj.has(CommonConstants.CUSTOMERID)) {
								earnings.setCustomerId(earningJsonObj.getString(CommonConstants.CUSTOMERID));
							}
							if (earningJsonObj.has("name")) {
								earnings.setName(earningJsonObj.getString("name"));
							}
							if (earningJsonObj.has("dob")) {
								earnings.setDob(earningJsonObj.getString("dob"));
							}
							if (earningJsonObj.has("memRelation")) {
								earnings.setMemRelation(earningJsonObj.getString("memRelation"));
							}
							if (earningJsonObj.has("legaldocName")) {
								earnings.setLegaldocName(earningJsonObj.getString("legaldocName"));
							}
							if (earningJsonObj.has("legaldocId")) {
								earnings.setLegaldocId(earningJsonObj.getString("legaldocId"));
							}
							earningsList.add(earnings);
						}
					}
					Optional<TbUalnLoanDtlsHis> loanDtlOptional = loanDtlsHisRepo.findByApplicationId(applicationId);
					logger.debug("Printing loanDtlOptional records from DB:" + loanDtlOptional);
					if (loanDtlOptional.isPresent()) {

						String loanAmountfromtable = loanDtlOptional.get().getLoanAmount();
						custDtls.setLoanAmount(loanAmountfromtable);

						LoanDtls loanDtls = gson.fromJson(loanDtlOptional.get().getPayload(), LoanDtls.class);

						JSONObject jsonPayload = new JSONObject(loanDtlOptional.get().getPayload());
						if (jsonPayload.has("disburseMode")) {
							try {
								loanDtls.setDisburseMode(new ObjectMapper()
										.readValue(jsonPayload.get("disburseMode").toString(), Object.class));
							} catch (Exception e) {
								logger.error("error while reading disburse mode", e);
							}
						}

						if (jsonPayload.has("cbAmt")) {
							loanDtls.setCbAmt(jsonPayload.getString("cbAmt"));
						}
						if (jsonPayload.has("insurPer")) {
							loanDtls.setInsurancePercentage(jsonPayload.getString("insurPer"));
						}
						if (jsonPayload.has("spouseIns")) {
							loanDtls.setSpouseInsurance(jsonPayload.getString("spouseIns"));
						}
						if (jsonPayload.has(CommonConstants.PRODID)
								&& jsonPayload.isNull(CommonConstants.PRODID) == false) {
							loanDtls.setProduct(jsonPayload.getString(CommonConstants.PRODID));
							loanDtls.setProductId(jsonPayload.getString(CommonConstants.PRODID));
						}
						if (jsonPayload.has("productType")) {
							loanDtls.setProductType(jsonPayload.getString("productType"));
						}
						if (jsonPayload.has("shortDesc")) {
							loanDtls.setShortDesc(jsonPayload.getString("shortDesc"));
						}
						if (jsonPayload.has(CommonConstants.LOANMODE)) {
							loanDtls.setLoanMode(jsonPayload.getString(CommonConstants.LOANMODE));
						}
						if (jsonPayload.has("purpose")) {
							try {
								loanDtls.setPurpose(new ObjectMapper().readValue(jsonPayload.get("purpose").toString(),
										Object.class));
							} catch (Exception e) {
								logger.error("error while reading repayFrequency", e);
							}
						}

						ChargeAndBreakupDetails chargeBreakupDtls = new ChargeAndBreakupDetails();
						if (jsonPayload.has("chargeAndBreakupDtls")) {
							chargeBreakupDtls = gson.fromJson(
									jsonPayload.getJSONObject("chargeAndBreakupDtls").toString(),
									ChargeAndBreakupDetails.class);
						}
						loanDtls.setCaglAmt(loanDtlOptional.get().getLoanAmount());
						loanDtls.setInterestRate(new BigDecimal(loanDtlOptional.get().getInterestRate()));
						loanDtls.setCustVintageInterestRate(loanDtlOptional.get().getInterestRate());
						loanDtls.setChargeAndBreakupDtls(chargeBreakupDtls);
						loanDtls.setInstallmentDetails(loanDtlOptional.get().getInstallmentDetails());
						try {

							loanDtls.setRepayFrequency(
									new ObjectMapper().readValue(loanDtlOptional.get().getFrequency(), Object.class));
							loanDtls.setActiveLoanDtls(new ObjectMapper()
									.readValue(loanDtlOptional.get().getActiveLoanDtls(), List.class));
						} catch (Exception e) {
							logger.error("error while reading repayFrequency", e);
						}
						loanDtls.setTerm(loanDtlOptional.get().getTerm());
						Optional<TbUacoInsuranceDtlsHis> insuranceDtls = tbUacoInsuranceDtlsHisRepo
								.findByApplicationId(applicationId);
						logger.debug("Printing insuranceDtls records from DB:" + insuranceDtls);
						if (insuranceDtls.isPresent()) {
							InsuranceDetails insDts = gson.fromJson(insuranceDtls.get().getPayload(),
									InsuranceDetails.class);
							loanDtls.setInsurDtls(insDts);
						}
						Optional<TbUaobNomineeDetailsHis> nomineeDtls = nomineeDtlsHisRepository
								.findByApplicationId(applicationId);
						logger.debug("Printing nomineeDtls records from DB:" + nomineeDtls);
						if (nomineeDtls.isPresent()) {
							NomineeDtls nomineePayload = new NomineeDtls();
							JSONObject nomineeJson = new JSONObject(nomineeDtls.get().getPayload());

							// nomineePayload.setNomineeName(nomineeJson.getString("nomineeName"));
							// nomineePayload.setRelationWithMember(nomineeJson.getString("relationWithMember"));
							// nomineePayload.setRelationWithMemberCode(nomineeJson.getString("relationWithMemberCode"));

							if (nomineeJson.has("nomineeName") && !nomineeJson.isNull("nomineeName")) {
								nomineePayload.setNomineeName(nomineeJson.getString("nomineeName"));
							}
							if (nomineeJson.has("relationWithMember") && !nomineeJson.isNull("relationWithMember")) {
								nomineePayload.setRelationWithMember(nomineeJson.getString("relationWithMember"));
							}
							if (nomineeJson.has("relationWithMemberCode")
									&& !nomineeJson.isNull("relationWithMemberCode")) {
								nomineePayload
										.setRelationWithMemberCode(nomineeJson.getString("relationWithMemberCode"));
							}
							loanDtls.setNomineeDtls(nomineePayload);
						}
						custDtls.setLoanDtls(loanDtls);
					}
					custDtls.setEarnings(earningsList);
					custDtls.setIncome(incomeList);
					try {
						custDtls.setKycDtls(
								new ObjectMapper().readValue(customerDetailsHis.get().getKycDetails(), KYCDetails.class));
						custDtls.setBankDtls(
								new ObjectMapper().readValue(customerDetailsHis.get().getBankDtls(), BankDetails.class));
					} catch (Exception e) {
						logger.error("exception while fetching kycdetails or bank details:", e);
					}
					appDtls.setCustomerDetails(custDtls);
				}

				// Optional<ApplicationWorkflow> workflow = applnWfRepository
				// .findTopByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumDesc(request.getAppId(),
				// applicationId, request.getRequestObj().getVersionNum());

				Optional<ApplicationWorkflowHis> workflow = applnWfHisRepository.findlatestWorkflowDetails(applicationId);

				logger.debug("Printing workflow records from DB:" + workflow);

				if (workflow.isPresent()) {
					ApplicationWorkflowHis applnWf = workflow.get();
					List<WorkflowDefinitionHis> wfDefnLis = wfDefnHisRepoLn.findByFromStageId(applnWf.getNextWorkFlowStage());
					loanFields.setApplnWfDefinitionHisList(wfDefnLis);
					logger.debug("Printing wfDefnLis records from DB:" + wfDefnLis);
				}

				// Optional<TbUaobCbResponse> cbRes = cbResRepository
				// .findByAppIdAndApplicationIdOrderByVersionNumDesc(request.getAppId(),
				// applicationId);

				TbUaobCbResponseHis cbRes = cbResHisRepository.findByAppIdAndApplicationId(applicationId);
				logger.debug("Printing cbRes records from DB:" + cbRes);

				PrecloserResponse precloserResponse = tbUaobApiAuditLogsHisRepository
						.findByApplicationidApiName(applicationId);
				logger.debug("Printing precloserResponse records from DB:" + precloserResponse);

				if (precloserResponse != null) {
					logger.debug(
							"precloserResponse -> requestPayload: {}, responsePayload: {}, apiReqTs: {}, apiResTs: {}",
							precloserResponse.getRequestPayload(), precloserResponse.getResponsePayload(),
							precloserResponse.getApiReqTs(), precloserResponse.getApiResTs());
					PrecloserLoanResponse precloserLoanResponse = new PrecloserLoanResponse();
					precloserLoanResponse.setResponsePayload(precloserResponse.getResponsePayload());
					precloserLoanResponse.setRequestPayload(precloserResponse.getRequestPayload());
					precloserLoanResponse.setApiReqTs(precloserResponse.getApiReqTs());
					precloserLoanResponse.setApiResTs(precloserResponse.getApiResTs());
					loanFields.setPrecloserLoanResponse(precloserLoanResponse);
				}
				if (cbRes != null) {
					loanFields.setCbResponseHis(cbRes);
				}
				appDtlsList.add(appDtls);
			}
			loanFields.setApplicationdtls(appDtlsList);
			logger.debug("Printing Final loanFields:" + loanFields);
		} catch (Exception e) {
			logger.error("===Error Occured while calling DB Calls====" + e);
		}
		return loanFields;
	}

}