package com.iexceed.appzillonbanking.cagl.document.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.EngineConfig;
import org.eclipse.birt.report.engine.api.IReportRunnable;
import org.eclipse.birt.report.engine.api.IRunAndRenderTask;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.eclipse.birt.report.engine.api.ReportEngine;
import org.eclipse.core.internal.registry.RegistryProviderFactory;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cagl.document.constants.CommonConstants;
import com.iexceed.appzillonbanking.cagl.document.domain.ab.ApplicationMasterHis;
import com.iexceed.appzillonbanking.cagl.document.domain.ab.ApplicationWorkflowHis;
import com.iexceed.appzillonbanking.cagl.document.domain.ab.SanctionRepaymentSchedule;
import com.iexceed.appzillonbanking.cagl.document.domain.ab.ServerDate;
import com.iexceed.appzillonbanking.cagl.document.domain.ab.TbAsmiUser;
import com.iexceed.appzillonbanking.cagl.document.domain.ab.TbUacoInsuranceDtlsHis;
import com.iexceed.appzillonbanking.cagl.document.domain.ab.TbUalnLoanDtlsHis;
import com.iexceed.appzillonbanking.cagl.document.domain.ab.TbUaobAddressDetailsHis;
import com.iexceed.appzillonbanking.cagl.document.domain.ab.TbUaobCbResponseHis;
import com.iexceed.appzillonbanking.cagl.document.domain.ab.TbUaobCustDtlsHis;
import com.iexceed.appzillonbanking.cagl.document.domain.ab.TbUaobIncomeAssessmentHis;
import com.iexceed.appzillonbanking.cagl.document.domain.ab.TbUaobNomineeDetailsHis;
import com.iexceed.appzillonbanking.cagl.document.domain.ab.TbUaobOccupationDtlsHis;
import com.iexceed.appzillonbanking.cagl.document.domain.ab.WorkflowDefinition;
import com.iexceed.appzillonbanking.cagl.document.domain.apz.User;
import com.iexceed.appzillonbanking.cagl.document.payload.AddressDtls;
import com.iexceed.appzillonbanking.cagl.document.payload.ApplicationDtls;
import com.iexceed.appzillonbanking.cagl.document.payload.ApplyLoanRequestHisFields;
import com.iexceed.appzillonbanking.cagl.document.payload.BankDetails;
import com.iexceed.appzillonbanking.cagl.document.payload.ChargeAndBreakupDetails;
import com.iexceed.appzillonbanking.cagl.document.payload.CustomerDtls;
import com.iexceed.appzillonbanking.cagl.document.payload.Earnings;
import com.iexceed.appzillonbanking.cagl.document.payload.FetchAppRequest;
import com.iexceed.appzillonbanking.cagl.document.payload.FetchAppRequestFields;
import com.iexceed.appzillonbanking.cagl.document.payload.Income;
import com.iexceed.appzillonbanking.cagl.document.payload.InsuranceDetails;
import com.iexceed.appzillonbanking.cagl.document.payload.KYCDetails;
import com.iexceed.appzillonbanking.cagl.document.payload.LoanDtls;
import com.iexceed.appzillonbanking.cagl.document.payload.NomineeDtls;
import com.iexceed.appzillonbanking.cagl.document.payload.PrecloserLoanResponse;
import com.iexceed.appzillonbanking.cagl.document.payload.PrecloserResponse;
import com.iexceed.appzillonbanking.cagl.document.payload.SanctionLoanScheduleRequest;
import com.iexceed.appzillonbanking.cagl.document.payload.SanctionLoanScheduleRequestFields;
import com.iexceed.appzillonbanking.cagl.document.repository.ab.ApplicationMasterHisRepository;
import com.iexceed.appzillonbanking.cagl.document.repository.ab.ApplicationWorkflowHisRepository;
import com.iexceed.appzillonbanking.cagl.document.repository.ab.SanctionRepaymentScheduleRepository;
import com.iexceed.appzillonbanking.cagl.document.repository.ab.T24ServerDateRepository;
import com.iexceed.appzillonbanking.cagl.document.repository.ab.TbAsmiUserRepo;
import com.iexceed.appzillonbanking.cagl.document.repository.ab.TbOfficeDataRepository;
import com.iexceed.appzillonbanking.cagl.document.repository.ab.TbUacoInsuranceDtlsHisRepo;
import com.iexceed.appzillonbanking.cagl.document.repository.ab.TbUalLoanDtlsHisRepository;
import com.iexceed.appzillonbanking.cagl.document.repository.ab.TbUaobAddressDetailsHisRepository;
import com.iexceed.appzillonbanking.cagl.document.repository.ab.TbUaobApiAuditLogsHisRepository;
import com.iexceed.appzillonbanking.cagl.document.repository.ab.TbUaobCbResponseHisRepository;
import com.iexceed.appzillonbanking.cagl.document.repository.ab.TbUaobCustomerDtlsHisRepo;
import com.iexceed.appzillonbanking.cagl.document.repository.ab.TbUaobIncomeAssessmentHisRepository;
import com.iexceed.appzillonbanking.cagl.document.repository.ab.TbUaobNomineeDtlsHisRepository;
import com.iexceed.appzillonbanking.cagl.document.repository.ab.TbUaobOccupationDtlsHisRepo;
import com.iexceed.appzillonbanking.cagl.document.repository.ab.WorkflowDefinitionHisRepository;
import com.iexceed.appzillonbanking.cagl.document.repository.apz.UserRepository;
import com.iexceed.appzillonbanking.cagl.document.domain.ab.TbUaobApiAuditLogsHis;
import com.iexceed.appzillonbanking.core.payload.*;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.core.utils.CommonUtilsCBS;
import com.iexceed.appzillonbanking.interfaceAdapter.service.InterfaceAdapter;
import com.iexceed.appzillonbanking.interfaceAdapter.utils.AdapterUtil;

import io.micrometer.common.util.StringUtils;
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
	UserRepository userRepo;

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
	private TbUaobIncomeAssessmentHisRepository tbUaobIncomeAssessmentRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TbAsmiUserRepo tbAsmiUserRepo;

//	@Autowired
//	private UserRoleRepository userRoleRepository;

	@Lazy // Prevents circular dependency
	@Autowired
	private ReportBuildService reportBuildService;

	@Autowired
	private SanctionRepaymentScheduleRepository sanctionRepaymentScheduleRepository;

	@Autowired
	private TbOfficeDataRepository tbOfficeDataRepository;

	@Autowired
	private T24ServerDateRepository dateRepository;

	@Autowired
	TbUaobApiAuditLogsHisRepository auditApiRepo;
	

	@Autowired
	DataSource dataSource;

	private static boolean birtEngineIntialized = false;
	private static ReportEngine reportEngine = null;
	private static EngineConfig engineConfig = null;

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

		List<ApplicationMasterHis> applicationMasterHisList = applicationMasterHisRepository
				.findByApplicationId(applicationId);
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
			byte[] fileObjBytes = reportBuildService.generateAndDownloadSanctionReport(applicationId, "mergeAll");
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
						custDtls.setKycDtls(new ObjectMapper().readValue(customerDetailsHis.get().getKycDetails(),
								KYCDetails.class));
						custDtls.setBankDtls(new ObjectMapper().readValue(customerDetailsHis.get().getBankDtls(),
								BankDetails.class));
					} catch (Exception e) {
						logger.error("exception while fetching kycdetails or bank details:", e);
					}
					appDtls.setCustomerDetails(custDtls);
				}

				// Optional<ApplicationWorkflow> workflow = applnWfRepository
				// .findTopByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumDesc(request.getAppId(),
				// applicationId, request.getRequestObj().getVersionNum());

				Optional<ApplicationWorkflowHis> workflow = applnWfHisRepository
						.findlatestWorkflowDetails(applicationId);

				logger.debug("Printing workflow records from DB:" + workflow);

				if (workflow.isPresent()) {
					ApplicationWorkflowHis applnWf = workflow.get();
					List<WorkflowDefinition> wfDefnLis = wfDefnHisRepoLn
							.findByFromStageId(applnWf.getNextWorkFlowStage());
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

	// Modified By Prem
	public byte[] generateAndDownloadDBKitReport(String applicationId) {
		try {
			HashMap<String, String> dbKitReportParams = getDBKitParameters(applicationId);
			String branchid = dbKitReportParams.get("branchCode");
			String branchLan = tbOfficeDataRepository.findBranchLan(branchid);
			logger.debug("Priting branchLan: " + branchLan);
			String DBKitReport = "DBKitReport";
			String format = "html";
			if (!StringUtils.isBlank(branchLan)) {
				branchLan = branchLan.toLowerCase();
				if (!branchLan.isBlank()) {
					if (branchLan.equalsIgnoreCase("kan") || branchLan.equalsIgnoreCase("mar")
							|| branchLan.equalsIgnoreCase("tam") || branchLan.equalsIgnoreCase("tel")
							|| branchLan.equalsIgnoreCase("hin") || branchLan.equalsIgnoreCase("mal")
							|| branchLan.equalsIgnoreCase("odi") || branchLan.equalsIgnoreCase("guj")
							|| branchLan.equalsIgnoreCase("ori") || branchLan.equalsIgnoreCase("ben")
							|| branchLan.equalsIgnoreCase("wb")) {

						if (branchLan.equalsIgnoreCase("ori") || branchLan.equalsIgnoreCase("odi")) {
							branchLan = "odi";
						}
						if (branchLan.equalsIgnoreCase("wb") || branchLan.equalsIgnoreCase("ben")) {
							branchLan = "ben";
						}

						DBKitReport = DBKitReport + "_" + branchLan;
						format = "html";
					}
				}
			}
			logger.debug("generateAndDownloadDBKitReport : {}", dbKitReportParams);
			File dbKitReport = generateBirReport(applicationId, DBKitReport, dbKitReportParams, format);
			boolean isOtherThanEnglish = DBKitReport.contains("_");
			logger.debug("report language is not english");
			try {
				byte[] dbKitReporthtmlBytes = Files.readAllBytes(dbKitReport.toPath());
				try {
					String dbKitReportbase64Html = java.util.Base64.getEncoder().encodeToString(dbKitReporthtmlBytes);
					String dbKitReporthtmlContent = new String(
							java.util.Base64.getDecoder().decode(dbKitReportbase64Html), "UTF-8");
					dbKitReporthtmlContent = dbKitReporthtmlContent.replaceAll("file:/tmp",
							"https://www.creditaccessgrameen.in/wp-content/themes/creditaccessgrameen/assets//images/logo.png?");
					// String mergedContent =mergeReports(dbKitReporthtmlContent,"",");

					// String mergedContent =
					// sanctionReporthtmlContent+"<br/><br/>"+factSheethtmlContent+"<br/><br/>"+loanApplicationhtmlContent;
					// logger.debug("Final String "+ mergedContent);

					File megeFile = new File("mergerDBKIT.html");
					Files.write(Paths.get("mergerDBKIT.html"), dbKitReporthtmlContent.getBytes());

					logger.debug("file Merger Successfully");
					return Files.readAllBytes(megeFile.toPath());

//								 String loanApplicationFormbase64Html = java.util.Base64.getEncoder().encodeToString(loanApplicationFormhtmlBytes,"UTF-8");
//								 String loanApplicationFormhtmlContent = new String(java.util.Base64.getDecoder().decode(base64Html), "UTF-8");

					/*
					 * ConverterProperties convertPro = new ConverterProperties(); FontProvider
					 * fontProvider = new FontProvider(); convertPro.setCharset("UTF-8"); //
					 * fontProvider.addStandardPdfFonts(); String reportTemplateFile =
					 * CommonUtils.getExternalProperties("reportsTemplateDir");
					 *
					 * logger.debug("report Path is"+reportTemplateFile ); try { if
					 * (DBKitReport.contains("_kan")){
					 * fontProvider.addFont(reportTemplateFile+"NotoSansKannada-Regular.ttf");
					 *
					 * logger.debug("font Path is"+reportTemplateFile+"NotoSansKannada-Regular.ttf"
					 * ); logger.debug("Got Kannada Font");
					 *
					 * } else if (DBKitReport.contains("_mar") ){
					 * fontProvider.addFont(reportTemplateFile+"NotoSansDevanagari-Regular.ttf"); }
					 * } catch (Exception e) { logger.error("Error in setting font:" + e); }
					 *
					 * // fontProvider.addSystemFonts(); fontProvider.addStandardPdfFonts();
					 * fontProvider.addSystemFonts(); File dbKitReporthtmlContentpdf = new
					 * File("report1.pdf"); File dbKitCustomerAssessmentFormhtmlContentpdf = new
					 * File("report2.pdf"); convertPro.setFontProvider(fontProvider);
					 * HtmlConverter.convertToPdf(dbKitReporthtmlContent, new
					 * FileOutputStream(dbKitReporthtmlContentpdf), convertPro);
					 *
					 * List<File> mergedPdfFiles = new ArrayList<>();
					 * mergedPdfFiles.add(dbKitReporthtmlContentpdf);
					 * mergedPdfFiles.add(dbKitCustomerAssessmentFormhtmlContentpdf); return
					 * mergePdfFiles(mergedPdfFiles);
					 */

				} catch (IOException e) {
					//e.printStackTrace();
				}
				return dbKitReporthtmlBytes;
			} catch (Exception e) {
				//e.printStackTrace();
				return null;
			}

		} catch (Exception e) {
			logger.error("Error occurred while merging pdf file {} ", e);
			return null;
		}

	}

	private HashMap<String, String> getDBKitParameters(String applicationId) {
		HashMap<String, String> sanctionReportParams = new HashMap<String, String>();
		List<String> keyList = Arrays.asList(new String[] { "ewi", "insurancePremium", "processingFee", "bankName",
				"ifscCode", "bankAccNo", "apr", "pdtName", "bmId", "kmName", "kmId", "spousekycType", "spousekycId",
				"kycType", "kycId", "phoneno", "memberName", "memberId", "kendraId", "bmName", "branchName",
				"kendraName", "date", "id", "spouceOrFatherName", "fullAddress", "approvedAmtInRupees", "interestRate",
				"approvedAmtInWord", "purpose", "gkId", "borrowerName", "coBorrowerName", "dob", "gender",
				"spouseGender", "spouseDOB", "nomineeName", "nomineeDOB", "nomineeGender", "nomineeRelationShip",
				"appointeeName", "appointeeDOB", "appointeeGender", "appointeeRelationShip", "paymentFreq", "term",
				"type_of_roof_concrete", "type_of_roof_asbestos_tin", "type_of_roof_teracote",
				"type_of_roof_terracotta", "house_ownership_rented", "house_ownership_owned",
				"basic_amenities_electricity", "basic_amenities_water_pipeline_connection", "basic_amenities_toilet",
				"basic_amenities_sewage_connection", "basic_amenities_lpg_connection", "basic_amenities_none",
				"other_assets_land", "other_assets_livestock_(cattle)", "other_assets_2-wheeler",
				"other_assets_4-wheeler", "other_assets_tv", "other_assets_ridge_(refrigerator)",
				"other_assets_smartphone", "other_assets_none" });
		List<ApplicationMasterHis> applicationMasterList = applicationMasterHisRepository
				.findByApplicationId(applicationId);
		try {
			if (applicationMasterList != null && applicationMasterList.size() > 0) {
				ApplicationMasterHis appMaster = applicationMasterList.get(0);
				FetchAppRequest appReq = new FetchAppRequest();
				appReq.setAppId(appMaster.getAppId());
				FetchAppRequestFields fields = new FetchAppRequestFields();
				fields.setApplicationId(applicationId);
				fields.setVersionNum(Integer.parseInt(appMaster.getVersionNum()));
				appReq.setRequestObj(fields);
				ApplyLoanRequestHisFields loanFields = getCustomerData(applicationMasterList, appReq);
				if (loanFields != null && loanFields.getApplicationdtls() != null
						&& loanFields.getApplicationdtls().size() > 0) {
					ApplicationDtls appDtl = loanFields.getApplicationdtls().get(0);
					CustomerDtls customerDtl = appDtl.getCustomerDetails();
					if (customerDtl != null) {
						LoanDtls loanDtl = customerDtl.getLoanDtls();
						List<Object> activeLoanDtls = loanDtl.getActiveLoanDtls();
						if (!activeLoanDtls.isEmpty()) {
							Object activeLoanDetailsObject = loanDtl.getActiveLoanDtls().get(0);
							JSONObject activeLoanDetailsJsonObject = new JSONObject(
									new Gson().toJson(activeLoanDetailsObject));
							if (activeLoanDetailsJsonObject.has("loanId")) {
								Object loanId = activeLoanDetailsJsonObject.get("loanId");
								if (loanId != null) {
									sanctionReportParams.put("id", loanId.toString());
								}
							}
							if (activeLoanDetailsJsonObject.has("freq")) {
								Object freq = activeLoanDetailsJsonObject.get("freq");
								if (freq != null) {
									sanctionReportParams.put("freq", freq.toString());
								}
							}
						} else {
							logger.debug("Empty ActiveLoanDtls" + activeLoanDtls);
						}
						ChargeAndBreakupDetails chargeAndBreakupDtls = loanDtl.getChargeAndBreakupDtls();
						String aprxLoanCharges = chargeAndBreakupDtls.getAprxLoanCharges();
						sanctionReportParams.put("processingFee", aprxLoanCharges);

						InsuranceDetails insurDtls = loanDtl.getInsurDtls();
						String insurCharges = insurDtls.getInsurCharges();
						sanctionReportParams.put("insurancePremium", insurCharges);

//							String installment = "";
//							Optional<SanctionRepaymentSchedule> opSanctRepaySchedule = sanctionRepaymentScheduleRepository.findTopByApplicationIdOrderByCreateTsDesc(applicationId);
//							logger.debug("Printing opSanctRepaySchedule:"+opSanctRepaySchedule);
//							if(opSanctRepaySchedule.isPresent()) {
//								SanctionRepaymentSchedule sanctionRepaymentSchedule = opSanctRepaySchedule.get();
//								logger.debug("sanctionRepaymentSchedule is present :"+sanctionRepaymentSchedule);
//								installment = sanctionRepaymentSchedule.getInstallment();
//								sanctionReportParams.put("ewi", installment);
//							}else {
//								sanctionReportParams.put("ewi", "0");
//							}

						String installment = "";
						List<SanctionRepaymentSchedule> repaymentSchedules = sanctionRepaymentScheduleRepository
								.findByApplicationId(applicationId);

						if (repaymentSchedules.size() > 1) {
							installment = repaymentSchedules.get(1).getInstallment();
							sanctionReportParams.put("ewi", installment);
						} else {
							sanctionReportParams.put("ewi", "0.0");
							logger.debug("Less than 2 repayment schedules found for applicationId: " + applicationId);
						}
						sanctionReportParams.put("bankAccNo", customerDtl.getBankDtls().getBankAccNo());
						sanctionReportParams.put("ifscCode", customerDtl.getBankDtls().getBankIfscCode());
						sanctionReportParams.put("bankName", customerDtl.getBankDtls().getBankName());
						sanctionReportParams.put("id", appMaster.getApplicationId());
						sanctionReportParams.put("pdtName", loanDtl.getProduct());

						AddressDtls addressDtls = customerDtl.getAddressDtls().get(0);
						String fullAddress = addressDtls.getAddLine1() + addressDtls.getAddLine2() + ", "
								+ addressDtls.getPincode();
						sanctionReportParams.put("phoneno", customerDtl.getMobileNum());
						sanctionReportParams.put("borrowerName", customerDtl.getCustomerName());
						int number = Integer.parseInt(customerDtl.getKycDtls().getDob());
						String dateStr = String.format("%08d", number);
						String year = dateStr.substring(0, 4);
						String month = dateStr.substring(4, 6);
						String day = dateStr.substring(6, 8);

						LocalDate date = LocalDate.parse(year + "-" + month + "-" + day,
								DateTimeFormatter.ofPattern("yyyy-MM-dd"));
						logger.debug("date before dd-MM-yyyy:" + date);
						String formattedDateVal = date.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
						logger.debug("Printing final DOB:" + formattedDateVal);

						String dt = formattedDateVal.toString();
						logger.debug("Printing dt:" + dt);

						sanctionReportParams.put("dob", dt);
						sanctionReportParams.put("kycType", customerDtl.getKycDtls().getPrimaryType());
						sanctionReportParams.put("kycId", customerDtl.getKycDtls().getPrimaryId());
						sanctionReportParams.put("nomineeName",
								customerDtl.getLoanDtls().getNomineeDtls().getNomineeName());
						sanctionReportParams.put("nomineeRelationShip",
								customerDtl.getLoanDtls().getNomineeDtls().getRelationWithMember());
						sanctionReportParams.put("fullAddress", fullAddress);
//							sanctionReportParams.put("paymentFreq", loanDtl.getTerm());
						String kmId = appMaster.getKmId();
						sanctionReportParams.put("kmId", kmId);
						Optional<User> opuserRecords = userRepository.findByUserId(kmId);
						String userName = "";
						if (opuserRecords.isPresent()) {
							User userRecords = opuserRecords.get();
							userName = userRecords.getUserName();
						}
						sanctionReportParams.put("kmName", userName);

						if (customerDtl.getEarnings() != null && customerDtl.getEarnings().size() > 0) {
							Earnings earning = customerDtl.getEarnings().get(0);
							sanctionReportParams.put("spouceOrFatherName", earning.getName());
							sanctionReportParams.put("spousekycType", earning.getLegaldocName());
							sanctionReportParams.put("spousekycId", earning.getLegaldocId());
							sanctionReportParams.put("coBorrowerName", earning.getName());
							int num = Integer.parseInt(earning.getDob());
							String datestr = String.format("%08d", num);
							String yy = datestr.substring(0, 4);
							String mm = datestr.substring(4, 6);
							String dd = datestr.substring(6, 8);
							LocalDate dat = LocalDate.parse(yy + "-" + mm + "-" + dd,
									DateTimeFormatter.ofPattern("yyyy-MM-dd"));
							logger.debug("date before dd-MM-yyyy:" + dat);
							String formattedDate = dat.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
							logger.debug("Printing formattedDate DOB for spouse:" + formattedDate);
							String dte = formattedDate.toString();
							logger.debug("Printing dob for spouse:" + dte);
							sanctionReportParams.put("spouseDOB", dte);

							if (("FATHER".equalsIgnoreCase(earning.getMemRelation())
									|| ("SPOUSE".equalsIgnoreCase(earning.getMemRelation())))) {
								sanctionReportParams.put("spouseGender", "Male");
							} else {
								sanctionReportParams.put("spouseGender", "NA");
							}
						} else {
							sanctionReportParams.put("coBorrowerName", "");
							sanctionReportParams.put("spouseDOB", "");
							sanctionReportParams.put("spouseGender", "");
						}
						String addInfo = appMaster.getAddInfo();
						JSONObject addInfoObj = new JSONObject(addInfo);
						String branchName = "";
						if (addInfoObj.has("branchName")) {
							branchName = addInfoObj.getString("branchName");
							logger.debug("branchName:" + branchName);
						}

						if ("INCOMEASSESSMENT".equalsIgnoreCase(appMaster.getApplicationType())) {
							Optional<TbUaobIncomeAssessmentHis> opIncomeAssessmentRecords = tbUaobIncomeAssessmentRepository
									.findByApplicationId(applicationId);
							if (opIncomeAssessmentRecords.isPresent()) {
								TbUaobIncomeAssessmentHis tbUaobIncomeAssessment = opIncomeAssessmentRecords.get();
								String payload = tbUaobIncomeAssessment.getPayload();
								JSONObject paylonJsonObj = new JSONObject(payload);
								if (paylonJsonObj.has("houseHoldDetails")) {
									JSONObject houseHoldDetailsJSON = paylonJsonObj.getJSONObject("houseHoldDetails");
									JSONArray questionArray = houseHoldDetailsJSON.getJSONArray("questions");
									int questLengthArr = questionArray.length();
									for (int i = 0; i < questLengthArr; i++) {
										JSONObject eachQuest = questionArray.getJSONObject(i);
										String titleVal = "";
										if (eachQuest.has("title")) {
											titleVal = eachQuest.getString("title");
										}
										JSONArray optionJsonArray = new JSONArray();
										JSONArray valueJsonArray = new JSONArray();

										logger.debug("Chechbox attributes set START");
										try {
											setCheckboxValues(titleVal, optionJsonArray, valueJsonArray, eachQuest,
													sanctionReportParams);
										} catch (Exception e) {

										}
										logger.debug("Chechbox attributes set END");
									}
								}
							}
						}
						sanctionReportParams.put("kendraName", appMaster.getKendraName());
						sanctionReportParams.put("branchName", branchName);
						sanctionReportParams.put("branchCode", appMaster.getBranchCode());
						sanctionReportParams.put("gkId", appMaster.getKmId());
						sanctionReportParams.put("loanAmt", loanDtl.getCaglAmt());
						sanctionReportParams.put("pdtName", loanDtl.getProduct());
						HashMap<String, String> purposeObj = (HashMap<String, String>) loanDtl.getPurpose();
						sanctionReportParams.put("purpose", purposeObj.getOrDefault("purposeDesc", "NA"));
						Optional<TbUaobCbResponseHis> cbRes = cbResHisRepository
								.findByAppIdAndApplicationIdOrderByVersionNumDesc(appReq.getAppId(), applicationId);
						if (cbRes.isPresent()) {
							logger.debug("cbRes present:");
							TbUaobCbResponseHis cbResponse = cbRes.get();
							String payload = cbResponse.getResPayload();
							JSONObject paylonJsonObj = new JSONObject(payload);
							String APRMaster = appMaster.getAddInfo();
							logger.debug("Printing AddInfo val:" + APRMaster);
							JSONObject aprJsonObj = new JSONObject(APRMaster);
							logger.debug("Printing aprJsonObj val:" + aprJsonObj);
							if (aprJsonObj.has("APR") && !aprJsonObj.isEmpty()) {
								logger.debug("aprJsonObj has APR and aprJsonObj is not empty");
								String aprVal = aprJsonObj.getString("APR");
								if (!aprVal.isEmpty()) {
									logger.debug("aprVal is not empty");
									String apr = "" + aprJsonObj.getString("APR") + "%";
									sanctionReportParams.put("apr", apr);
								} else {
									logger.debug("aprVal is empty");
									if (paylonJsonObj.has("eir")) {
										String APR = paylonJsonObj.getString("eir");
										logger.debug("Printing APR from paylonJsonObj:" + APR);
										sanctionReportParams.put("apr", "" + APR + "%");
									}
								}
							} else {
								logger.debug("aprJsonObj does not has APR aprJsonObj is empty");
								if (paylonJsonObj.has("eir")) {
									String APR = paylonJsonObj.getString("eir");
									logger.debug("Printing APR from paylonJsonObj:" + APR);
									sanctionReportParams.put("apr", "" + APR);
								}
							}
						}
						sanctionReportParams.put("tenure", loanDtl.getTerm());
//							String term = "";
//							if (loanDtl.getTerm() != null) {
//								term = loanDtl.getTerm().replaceAll("[^\\d]", "");
//							}
//							sanctionReportParams.put("term", term);

						HashMap<String, String> freqObj = (HashMap<String, String>) loanDtl.getRepayFrequency();
						sanctionReportParams.put("paymentFreq", freqObj.getOrDefault("idDesc", "WEEKLY"));

						String termVal = loanDtl.getTerm() == null ? "" : loanDtl.getTerm().replaceAll("[^0-9]", " ");
//							sanctionReportParams.put("tenure", termVal);
						String frequency = freqObj.getOrDefault("idDesc", "WEEKLY");
						if ("WEEKLY".equalsIgnoreCase(frequency)) {
							String instType = loanDtl.getTerm();
							String mod = instType.replace("W", "");
							sanctionReportParams.put("term", mod);
						} else if ("BIWEEKLY".equalsIgnoreCase(frequency) || "BI-WEEKLY".equalsIgnoreCase(frequency)) {
							String instType = loanDtl.getTerm();
							String mod = instType.replace("W", "");
							int epi = Integer.parseInt(mod);
							Integer paymentFreq = epi / 2;
							sanctionReportParams.put("term", paymentFreq.toString());
						} else if ("FOURWEEKLY".equalsIgnoreCase(frequency)
								|| "FOUR-WEEKLY".equalsIgnoreCase(frequency)) {
							String instType = loanDtl.getTerm();
							String mod = instType.replace("W", "");
							int epi = Integer.parseInt(mod);
							Integer paymentFreq = epi / 4;
							sanctionReportParams.put("term", paymentFreq.toString());
						}

						String memInsurance = loanDtl.getInsurDtls().getMember();
						String spouseIns = loanDtl.getInsurDtls().getSpouse();
						if (StringUtils.isNotBlank(spouseIns) && spouseIns.contains("Y")) {
							spouseIns = "YES";
						} else {
							spouseIns = "NO";
						}
						if (StringUtils.isNotBlank(memInsurance) && memInsurance.contains("Y")) {
							memInsurance = "YES";
						} else {
							spouseIns = "NO";
						}
						sanctionReportParams.put("gender", "Female");
						sanctionReportParams.put("spouseInsurance", spouseIns);
						if (loanDtl.getChargeAndBreakupDtls() != null) {
							Integer processFee = convertStringToInt(
									loanDtl.getChargeAndBreakupDtls().getLoanProcessingFee());
							Integer gstAmt = convertStringToInt(loanDtl.getChargeAndBreakupDtls().getGST());
							sanctionReportParams.put("pfGstAmt", "" + (processFee + gstAmt));
						}
					}
				}
				sanctionReportParams.put("memberName", appMaster.getCustomerName());
				sanctionReportParams.put("memberId", appMaster.getCustomerId());
				sanctionReportParams.put("kendraId", appMaster.getKendraId());
				User userObj = getBranchManagerInfo("BM", appMaster.getBranchCode());
				String gkId = "", bmName = "";
				Optional<ApplicationWorkflowHis> opApplnWfRec = applnWfHisRepository
						.findCreatedByUsingApplicationIdAndApplicationStatus(applicationId);
				logger.debug("ApplicationWorkflow rec to get CreatedBy:" + opApplnWfRec);
				if (opApplnWfRec.isPresent()) {
					logger.debug("ApplicationWorkflow rec Present");
					ApplicationWorkflowHis applicationWorkflowRec = opApplnWfRec.get();
					String createdBy = applicationWorkflowRec.getCreatedBy();
					logger.debug("createdBy is:" + createdBy);
					Optional<TbAsmiUser> opAsmiUserRec = tbAsmiUserRepo.findBmNameAndGkId(createdBy);
					logger.debug("opAsmiUserRec is:" + opAsmiUserRec);
					if (opAsmiUserRec.isPresent()) {
						logger.debug("opAsmiUserRec is present");
						TbAsmiUser tbAsmiUserRec = opAsmiUserRec.get();
						bmName = tbAsmiUserRec.getUserName();
						gkId = tbAsmiUserRec.getUserId();
					}
				}
				sanctionReportParams.put("bmName", bmName);
				sanctionReportParams.put("bmId", gkId);
				getParamsFromDisbursementResponse(sanctionReportParams, applicationId, appMaster.getCustomerId());
				sanctionReportParams.put("approvedAmtInRupees", appMaster.getAmount().toString());

				BigDecimal amount = appMaster.getAmount();
				String a = appMaster.getAmount().toString();
				BigDecimal rounded = amount.setScale(0, RoundingMode.HALF_UP);
				long longValue = rounded.longValue();
				String approvedAmtInWords = NumberToWordsConverter.convert(longValue);
				sanctionReportParams.put("approvedAmtInWord", approvedAmtInWords);
				Optional<ServerDate> serverDate = dateRepository.findById(1); // Assuming the ID of the single row is 1
				String date = serverDate.map(ServerDate::getCurrentDate).orElse(null);
				int number = Integer.parseInt(date);
				String dateStr = String.format("%08d", number);
				String year = dateStr.substring(0, 4);
				String month = dateStr.substring(4, 6);
				String day = dateStr.substring(6, 8);
				LocalDate date_server = LocalDate.parse(year + "-" + month + "-" + day,
						DateTimeFormatter.ofPattern("yyyy-MM-dd"));
				logger.debug("date before dd-MM-yyyy:" + date);
				String formattedDateVal = date_server.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
				logger.debug("Printing serverDate:" + formattedDateVal);
				String dt = formattedDateVal.toString();
				logger.debug("Printing datet:" + dt);
				sanctionReportParams.put("date", dt);
				logger.debug("Printing ROI value from getDBkitReportParameters:");
				Optional<TbUaobCbResponseHis> cbRes = cbResHisRepository
						.findByAppIdAndApplicationIdOrderByVersionNumDesc(appReq.getAppId(), applicationId);
				logger.debug("Printing cbRes 2nd:" + cbRes);
				if (cbRes.isPresent()) {
					logger.debug("cbRes is present" + cbRes.get());
					TbUaobCbResponseHis cbResponse = cbRes.get();
					String payload = cbResponse.getResPayload();
					JSONObject paylonJsonObj = new JSONObject(payload);
					if (paylonJsonObj.has("roi")) {
						String roi = paylonJsonObj.getString("roi");
						sanctionReportParams.put("interestRate", "" + roi);
						logger.debug("Printing InterestRate value from getDBkitReportParameters:" + roi);
					}
				}
			}
		} catch (Exception exp) {
			logger.error("Error occurred while fetching params :: " + exp);
		}
		for (String key : keyList) {
			if (!(sanctionReportParams.containsKey(key) && StringUtils.isNotEmpty(sanctionReportParams.get(key)))) {
				sanctionReportParams.put(key, "_");
			}
		}
		return sanctionReportParams;
	}

	private HashMap<String, String> getParamsFromDisbursementResponse(HashMap<String, String> parameters,
			String applicationId, String custDtlId) {
		try {
			if (parameters != null) {
				Gson gson = new Gson();

				List<TbUaobApiAuditLogsHis> auditApiList = auditApiRepo.findByApplicationIdAndCustDtlId(applicationId,
						custDtlId);
				if (auditApiList != null && auditApiList.size() > 0) {
					TbUaobApiAuditLogsHis auditResponse = auditApiList.get(0);
					JSONObject jsonObj = new JSONObject(auditResponse.getResponsePayload());
					if (jsonObj.has("body") && jsonObj.isNull("body") == false) {
						HashMap<String, Object> responseBody = gson.fromJson(jsonObj.getJSONObject("body").toString(),
								HashMap.class);
						responseBody.keySet().forEach(key -> {
							if (!"processingFee".equals(key)) {
								parameters.put(key, responseBody.get(key).toString());
							}
						});
					}
					HashMap<String, Object> responseHeader = gson.fromJson(jsonObj.getJSONObject("header").toString(),
							HashMap.class);
					responseHeader.keySet().forEach(key -> {
						parameters.put(key, responseHeader.get(key).toString());
					});
					String sanctionedDate = parameters.getOrDefault("dateTime", "");
					if (StringUtils.isNotBlank(sanctionedDate)) {
						sanctionedDate = sanctionedDate.substring(0, 2) + "/" + sanctionedDate.substring(2, 4) + "/"
								+ sanctionedDate.substring(4, 6);
						parameters.put("sanctionedDate", sanctionedDate);
					}
				}
			}
		} catch (Exception exp) {
			// logger.error("Error occurred while merging pdf file {} ", exp);
		}
		return parameters;
	}

	private Integer convertStringToInt(String value) {
		try {
			return Integer.parseInt(value.split("\\.")[0]);
		} catch (Exception exp) {
			logger.info("Error occurred while converting String to int");
		}
		return 0;
	}

	private User getBranchManagerInfo(String role, String branchCode) {
		List<User> userList = userRepo.findByAddInfo1AndAddInfo2(role, branchCode);
		if (userList != null && userList.size() > 0) {
			return userList.get(0);
		}
		return null;
	}

	private void setCheckboxValues(String titleVal, JSONArray optionJsonArray, JSONArray valueJsonArray,
			JSONObject eachQuest, HashMap<String, String> sanctionReportParams) {

		String loweCaseTitle = titleVal.toLowerCase();
		logger.debug("Printing lowerCaseTitle:" + loweCaseTitle);
		String finalTitle = loweCaseTitle.replace(" ", "_");
		logger.debug("Printing finalTitle:" + finalTitle);

		if (eachQuest.has("options")) {
			optionJsonArray = eachQuest.getJSONArray("options");
		}
		if (eachQuest.has("value")) {
			valueJsonArray = eachQuest.getJSONArray("value");
		}
		Set<String> optionsSet = new HashSet<>();
		for (int i = 0; i < optionJsonArray.length(); i++) {
			optionsSet.add(optionJsonArray.getString(i));
		}
		String tempVal = "";
		for (int i = 0; i < valueJsonArray.length(); i++) {
			String value = valueJsonArray.getString(i);
			tempVal = value;
			String replaceVal = value.toLowerCase().replace(" ", "_");
			if (optionsSet.contains(value)) {
				sanctionReportParams.put(finalTitle + "_" + replaceVal, "*");
			}
			for (String element : optionsSet) {
				if (!element.equalsIgnoreCase(value)) {
					sanctionReportParams.put(finalTitle + "_" + element.toLowerCase().replace(" ", "_"), "_");
				}
			}
		}
	}

	@SuppressWarnings("unchecked")
	private File generateBirReport(String applicationId, String reportType, HashMap<String, String> params,
			String format) {
		String reportOutputFilePath = CommonUtils.getExternalProperties("reportsOutputDir");
		String reportTemplateFile = CommonUtils.getExternalProperties("reportsTemplateDir");
		String reportName = reportType;
		Path outputFilePath = Paths.get(reportOutputFilePath, applicationId, reportName + "." + format);
		File finalReportFile = null;

//			Check if directory existOrNot then create document based on needs
		if (Files.exists(outputFilePath)) {
			finalReportFile = outputFilePath.toFile();
		}
		try {
			Files.createDirectories(Paths.get(reportOutputFilePath, applicationId));
		} catch (IOException e) {
			logger.error(e);
		}
		System.setProperty("org.eclipse.birt.report.engine.font.config",
				CommonUtils.getExternalProperties("reportsTemplateDir") + "fontConfig.xml");
		String fontConfigPath = System.getProperty("org.eclipse.birt.report.engine.font.config");
		logger.debug("Font Path is " + fontConfigPath);
		intializeBirtEngine();
		IRunAndRenderTask runAndRender = null;
		try {
			IReportRunnable reportRunnable = reportEngine
					.openReportDesign(reportTemplateFile + reportName + ".rptdesign");
			runAndRender = reportEngine.createRunAndRenderTask(reportRunnable);

			runAndRender.setParameterValues(params);
			runAndRender.getAppContext().put("Locale", new Locale("kn", "IN"));

			RenderOption options = new RenderOption();

			options.setOutputFormat(format);
			options.setOutputFileName(outputFilePath.toString());

			runAndRender.setRenderOption(options);
			runAndRender.getAppContext().put("OdaJDBCDriverPassInConnection", dataSource.getConnection());
			runAndRender.run();

			File finalGeneratedReportFile = new File(outputFilePath.toString());
			logger.debug("output file path" + finalGeneratedReportFile.getPath());
			finalReportFile = finalGeneratedReportFile;

		} catch (Exception exp) {
			//exp.printStackTrace();
		}
		return finalReportFile;
	}

	private static void intializeBirtEngine() {
		try {
			logger.debug("Initializing Birt Report Engine");
			if (!birtEngineIntialized) {
				engineConfig = new EngineConfig();
				engineConfig.setLogConfig(null, Level.FINEST);
				RegistryProviderFactory.releaseDefault();
				Platform.startup(engineConfig);
				reportEngine = new ReportEngine(engineConfig);
				birtEngineIntialized = true;
				logger.debug("Font Path is " + System.getProperty("org.eclipse.birt.report.engine.font.config"));
			}
			logger.debug("Birt Report Engine Initialized Successfully");
		} catch (Exception e) {
			logger.error(e);
		}
	}

	// ==METHOD TO CALL Schedule==
	public Mono<byte[]> callandGenerateKFSScheudule(String applicationId, String stage) {
		logger.debug("=====Insertion for Sanction Letter Repay=====");
		Header header = new Header();
		String roi = "", loanAmt = "";
		header.setInterfaceId(LOAN_SCHEDULE_PROJECTOR);
		LocalDateTime currentDate = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String dateWithTime = currentDate.format(formatter);
		logger.debug("Printing date:" + dateWithTime);

		List<ApplicationMasterHis> applicationMasterList = applicationMasterHisRepository
				.findByApplicationId(applicationId);
		logger.debug("applicationMasterList for Report:" + applicationMasterList);

		ApplicationMasterHis appMaster = applicationMasterList.get(0);

		FetchAppRequest appReq = new FetchAppRequest();
		appReq.setAppId(appMaster.getAppId());
		FetchAppRequestFields fields = new FetchAppRequestFields();
		fields.setApplicationId(applicationId);
		fields.setVersionNum(Integer.parseInt(appMaster.getVersionNum()));
		appReq.setRequestObj(fields);
		ApplyLoanRequestHisFields loanFields = getCustomerData(applicationMasterList, appReq);
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
			byte[] fileObjBytes = reportBuildService.generateAndDownloadSanctionReport(applicationId, "mergeAll");
			logger.warn("Sanction report downloaded successfully for applicationId: {}", applicationId);
			sanctionRepaymentScheduleRepository.deleteByApplicationId(applicationId);
			logger.debug("Deleted repayment schedules post-report generation for applicationId: {}", applicationId);
			return Mono.just(fileObjBytes);
		}).onErrorResume(ex -> {
			logger.error("Error occurred while calling external service for applicationId: {}", applicationId, ex);
			return Mono.error(new RuntimeException("External service failed for applicationId: " + applicationId, ex));
		});
	}

	@SuppressWarnings({ "unchecked" })
	public ApplyLoanRequestHisFields getCustomerData(List<ApplicationMasterHis> applicationMasterList,
			FetchAppRequest request) {
		logger.debug("Printing applicationMasterList" + applicationMasterList);
		logger.debug("Printing FetchAppRequest" + request);
		ApplyLoanRequestHisFields loanFields = new ApplyLoanRequestHisFields();
		List<ApplicationDtls> appDtlsList = new ArrayList<>();
		Gson gson = new Gson();
		List<AddressDtls> addressList = new ArrayList<>();
		try {
			for (ApplicationMasterHis appMaster : applicationMasterList) {
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
				Optional<TbUaobCustDtlsHis> customerDetails = custDtlsHisRepo.findByApplicationId(applicationId);
				logger.debug("Printing customerDetails records from DB:" + customerDetails);
				if (customerDetails.isPresent()) {
					logger.debug("customerDetails is present");
					CustomerDtls custDtls = new CustomerDtls();
					JSONObject kycDetailsJson = new JSONObject(customerDetails.get().getKycDetails());
					if (kycDetailsJson.has("mobileNum")) {
						custDtls.setMobileNum(kycDetailsJson.get("mobileNum").toString());
					}
					custDtls.setCustomerName(customerDetails.get().getCustomerName());
					custDtls.setCustomerId(customerDetails.get().getCustomerId());
					List<TbUaobAddressDetailsHis> addressDetailsList = addressRepository
							.findByApplicationId(applicationId);
					logger.debug("Printing addressDetailsList records from DB:" + addressDetailsList);
					for (TbUaobAddressDetailsHis addDtl : addressDetailsList) {
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
								new ObjectMapper().readValue(customerDetails.get().getKycDetails(), KYCDetails.class));
						custDtls.setBankDtls(
								new ObjectMapper().readValue(customerDetails.get().getBankDtls(), BankDetails.class));
					} catch (Exception e) {
						logger.error("exception while fetching kycdetails or bank details:", e);
					}
					appDtls.setCustomerDetails(custDtls);
				}

				Optional<ApplicationWorkflowHis> workflow = applnWfHisRepository
						.findTopByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumDesc(request.getAppId(),
								applicationId, request.getRequestObj().getVersionNum());
				logger.debug("Printing workflow records from DB:" + workflow);

				if (workflow.isPresent()) {
					ApplicationWorkflowHis applnWf = workflow.get();
					List<WorkflowDefinition> wfDefnLis = wfDefnHisRepoLn
							.findByFromStageId(applnWf.getNextWorkFlowStage());
//						loanFields.setApplnWfDefinitionList(wfDefnLis);
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
