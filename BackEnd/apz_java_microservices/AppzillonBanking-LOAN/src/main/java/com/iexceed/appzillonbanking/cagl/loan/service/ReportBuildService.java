package com.iexceed.appzillonbanking.cagl.loan.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.sql.DataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
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
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.ApplicationWorkflow;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.SanctionRepaymentSchedule;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.ServerDate;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbAsmiUser;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbOfficeData;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUaobCbResponse;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUaobIncomeAssessment;
import com.iexceed.appzillonbanking.cagl.loan.domain.apz.User;
import com.iexceed.appzillonbanking.cagl.loan.payload.AddressDtls;
import com.iexceed.appzillonbanking.cagl.loan.payload.ApplicationDtls;
import com.iexceed.appzillonbanking.cagl.loan.payload.ApplyLoanRequestFields;
import com.iexceed.appzillonbanking.cagl.loan.payload.ChargeAndBreakupDetails;
import com.iexceed.appzillonbanking.cagl.loan.payload.CustomerDtls;
import com.iexceed.appzillonbanking.cagl.loan.payload.Earnings;
import com.iexceed.appzillonbanking.cagl.loan.payload.FetchAppRequest;
import com.iexceed.appzillonbanking.cagl.loan.payload.FetchAppRequestFields;
import com.iexceed.appzillonbanking.cagl.loan.payload.InsuranceDetails;
import com.iexceed.appzillonbanking.cagl.loan.payload.LoanDtls;
import com.iexceed.appzillonbanking.cagl.loan.payload.SanctionLoanScheduleRequest;
import com.iexceed.appzillonbanking.cagl.loan.payload.SanctionLoanScheduleRequestFields;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.ApplicationMasterRepository;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.ApplicationWorkflowRepository;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.SanctionRepaymentScheduleRepository;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.T24ServerDateRepository;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.TbAsmiUserRepo;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.TbOfficeDataRepository;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.TbUaobCbResponseRepository;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.TbUaobIncomeAssessmentRepository;
import com.iexceed.appzillonbanking.cagl.loan.repository.apz.UserRepository;
import com.iexceed.appzillonbanking.core.domain.ab.TbUaobApiAuditLogs;
import com.iexceed.appzillonbanking.core.payload.Header;
import com.iexceed.appzillonbanking.core.repository.ab.TbUaobApiAuditLogsRepository;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.interfaceAdapter.service.InterfaceAdapter;
import io.micrometer.common.util.StringUtils;
import reactor.core.publisher.Mono;

@Service
public class ReportBuildService {

	private static boolean birtEngineIntialized = false;
	private static ReportEngine reportEngine = null;
	private static EngineConfig engineConfig = null;

	private static final Logger logger = LogManager.getLogger(ReportBuildService.class);

	private static final String LOAN_SCHEDULE_PROJECTOR = "GetSanctionLoanSchedule";

	@Autowired
	DataSource dataSource;

	@Autowired
	LoanService loanService;

	@Autowired
	UserRepository userRepo;

	@Autowired
	TbUaobApiAuditLogsRepository auditApiRepo;

	@Autowired
	private ApplicationMasterRepository applicationMasterRepo;

	@Autowired
	private TbOfficeDataRepository tbOfficeDataRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private TbUaobIncomeAssessmentRepository tbUaobIncomeAssessmentRepository;

	@Autowired
	private TbUaobCbResponseRepository cbResRepository;

	@Autowired
	private InterfaceAdapter interfaceAdapter;

	@Autowired
	private ApplicationWorkflowRepository applicationWorkflowRepository;

	@Autowired
	private TbAsmiUserRepo tbAsmiUserRepo;

	@Autowired
	private SanctionRepaymentScheduleRepository sanctionRepaymentScheduleRepository;

	@Autowired
	private T24ServerDateRepository dateRepository;

	private static void intializeBirtEngine() {
		try {
			logger.debug("Initializing Birt Report Engine");
			if (!birtEngineIntialized) {
				engineConfig = new EngineConfig();
				engineConfig.setLogConfig(null,Level.FINEST);
				RegistryProviderFactory.releaseDefault();
				Platform.startup(engineConfig);
				reportEngine = new ReportEngine(engineConfig);
				birtEngineIntialized = true;
				logger.debug("Font Path is "+ System.getProperty("org.eclipse.birt.report.engine.font.config"));
			}
			logger.debug("Birt Report Engine Initialized Successfully");
		} catch (Exception e) {
			logger.error(e);
		}
	}
	private HashMap<String, String> getSanctionReportParameters(String applicationId) {
		HashMap<String, String> sanctionReportParams = new HashMap<String, String>();
		List<String> keyList = Arrays.asList(new String[] {"bmId", "loanAmt", "pdtName", "purpose", "bmName", "pfGstAmt",
				"sanctionedDate", "interestRate", "memInsurance", "amountApproved", "tenure",
				"frequency", "spouseInsurance", "installmentAmt", "memberName", "memberId", "kendraId" });
		List<ApplicationMaster> applicationMasterList = applicationMasterRepo.findByApplicationId(applicationId);
		try {
			if (applicationMasterList != null && applicationMasterList.size() > 0) {
				ApplicationMaster appMaster = applicationMasterList.get(0);

				FetchAppRequest appReq = new FetchAppRequest();
				appReq.setAppId(appMaster.getAppId());
				FetchAppRequestFields fields = new FetchAppRequestFields();
				fields.setApplicationId(applicationId);
				fields.setVersionNum(Integer.parseInt(appMaster.getVersionNum()));
				appReq.setRequestObj(fields);

				logger.debug("========fetching data from database=====");
				ApplyLoanRequestFields loanFields = loanService.getCustomerData(applicationMasterList, appReq);
				logger.debug("========Outside Data fetch=====");
				if (loanFields != null && loanFields.getApplicationdtls() != null
						&& loanFields.getApplicationdtls().size() > 0) {
					ApplicationDtls appDtl = loanFields.getApplicationdtls().get(0);
					CustomerDtls customerDtl = appDtl.getCustomerDetails();
					if (customerDtl != null) {
						LoanDtls loanDtl = customerDtl.getLoanDtls();
						String loanAmt = "Rs." + loanDtl.getCaglAmt();
						sanctionReportParams.put("loanAmt",loanAmt );
						sanctionReportParams.put("pdtName", loanDtl.getProduct());
						HashMap<String, String> purposeObj = (HashMap<String, String>) loanDtl.getPurpose();
						sanctionReportParams.put("purpose", purposeObj.getOrDefault("purposeDesc", "NA"));
						User userObj = getBranchManagerInfo("BM", appMaster.getBranchCode());
						if (userObj != null) {
						    sanctionReportParams.put("bmId", userObj.getUserId());
						}
						try {
							Optional<TbUaobCbResponse> cbRes = cbResRepository
									.findByAppIdAndApplicationIdOrderByVersionNumDesc(appReq.getAppId(), applicationId);
							logger.debug("Printing cbRes 2nd:" + cbRes);
							if (cbRes.isPresent()) {
								logger.debug("cbRes is present" + cbRes.get());
								TbUaobCbResponse cbResponse = cbRes.get();
								String payload = cbResponse.getResPayload();
								JSONObject paylonJsonObj = new JSONObject(payload);
								if (paylonJsonObj.has("roi")) {
									String roi = paylonJsonObj.getString("roi");
									logger.debug("Printing roi:" + roi);
									String interestRate = roi + "%";
									sanctionReportParams.put("interestRate", interestRate);
								}
							}
						} catch (Exception e) {
							sanctionReportParams.put("interestRate", "19.75");
							logger.error("error in findByAppIdAndApplicationIdOrderByVersionNumDesc:" + e);
						}
//						sanctionReportParams.put("interestRate", "");
						sanctionReportParams.put("tenure", loanDtl.getTerm());
						HashMap<String, String> freqObj = (HashMap<String, String>) loanDtl.getRepayFrequency();
						sanctionReportParams.put("frequency", freqObj.getOrDefault("idDesc", "WEEKLY"));
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
							memInsurance = "NO";
						}
						sanctionReportParams.put("memInsurance", memInsurance);
						sanctionReportParams.put("spouseInsurance", spouseIns);
						String installment = "";
						List<SanctionRepaymentSchedule> repaymentSchedules = sanctionRepaymentScheduleRepository
								.findByApplicationId(applicationId);
						logger.debug("Printing repaymentSchedules:" + repaymentSchedules);

						if (repaymentSchedules.size() > 1) {
							installment = repaymentSchedules.get(1).getInstallment();
							String installmentAmt = "Rs. " + installment;
							sanctionReportParams.put("installmentAmt", installmentAmt);
						} else {
							logger.debug("Less than 2 repayment schedules found for applicationId: " + applicationId);
						}
						if (loanDtl.getChargeAndBreakupDtls() != null) {
							
							Double processFee = Double.parseDouble(loanDtl.getChargeAndBreakupDtls().getLoanProcessingFee());
							Double gstAmt = Double.parseDouble(loanDtl.getChargeAndBreakupDtls().getGST());
							sanctionReportParams.put("pfGstAmt", "" + (processFee + gstAmt));
						}
					}
				}
				sanctionReportParams.put("memberName", appMaster.getCustomerName());
				sanctionReportParams.put("memberId", appMaster.getCustomerId());
				sanctionReportParams.put("kendraId", appMaster.getKendraId());
				sanctionReportParams.put("branchCode", appMaster.getBranchCode());
				User userObj = getBranchManagerInfo("BM", appMaster.getBranchCode());
				String gkId = "", bmName = "";
				try {
					Optional<ApplicationWorkflow> opApplnWfRec = applicationWorkflowRepository
							.findCreatedByUsingApplicationIdAndApplicationStatus(applicationId);
					logger.debug("ApplicationWorkflow rec to get CreatedBy:" + opApplnWfRec);
					if (opApplnWfRec.isPresent()) {
						logger.debug("ApplicationWorkflow rec Present");
						ApplicationWorkflow applicationWorkflowRec = opApplnWfRec.get();
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
				} catch (Exception e) {
					logger.error("Error occured while fetching BM info"+e);
				}

				if (!"".equals(gkId)) {
					sanctionReportParams.put("bmName", bmName);
				} else {
					sanctionReportParams.put("bmName", "NA");
				}
				if (gkId != null) {
					sanctionReportParams.put("bmId", gkId);
				} else {
					sanctionReportParams.put("bmId", "NA");
				}

				getParamsFromDisbursementResponse(sanctionReportParams, applicationId, appMaster.getCustomerId());
			}
		} catch (Exception exp) {
			logger.error("Error occurred while fetching params :: " + exp);
		}
		for (String key : keyList) {
			if (!(sanctionReportParams.containsKey(key) && StringUtils.isNotBlank(sanctionReportParams.get(key)))) {
				sanctionReportParams.put(key, "");
			}
		}
		// akshay.shahane Code::START
//		callLoanScheduleProjector(sanctionReportParams, applicationMasterList.get(0));
		// akshay.shahane Code::END
		logger.debug("Printing final sanctionReportParams request : " + sanctionReportParams);

		return sanctionReportParams;
	}

	private void callLoanScheduleProjector(HashMap<String, String> sanctionReportParams, ApplicationMaster appMaster) {
		Header header = new Header();
		header.setInterfaceId(LOAN_SCHEDULE_PROJECTOR);
		SanctionLoanScheduleRequest sanctionReportRequest = new SanctionLoanScheduleRequest();
		SanctionLoanScheduleRequestFields sanctionLoanScheduleRequestFields = new SanctionLoanScheduleRequestFields();
		sanctionLoanScheduleRequestFields.setCustomerId(appMaster.getCustomerId());
		sanctionLoanScheduleRequestFields.setInterestRate(sanctionReportParams.getOrDefault("interestRate", ""));
		sanctionLoanScheduleRequestFields.setLoanAmount(sanctionReportParams.getOrDefault("amountApproved", ""));
		sanctionLoanScheduleRequestFields.setLoanFrequency(sanctionReportParams.getOrDefault("frequency", ""));
		sanctionLoanScheduleRequestFields.setProductID(appMaster.getProductCode());
		sanctionLoanScheduleRequestFields.setTenure(sanctionReportParams.getOrDefault("tenure", ""));
		sanctionReportRequest.setRequestObj(sanctionLoanScheduleRequestFields);
		logger.debug("Printing final GetSanctionLoanSchedule request : " + sanctionReportRequest);

		Mono<Object> externalServiceResponse = interfaceAdapter.callExternalService(header, sanctionReportRequest,
				LOAN_SCHEDULE_PROJECTOR, true);
		externalServiceResponse
				.doOnNext(response -> logger.debug("Received response from external service earnMem: {}", response))
				.subscribe();
	}

	private Integer convertStringToInt(String value) {
		try {
			return Integer.parseInt(value.split("\\.")[0]);
		} catch (Exception exp) {
			logger.info("Error occurred while converting String to int");
		}
		return 0;
	}

	private HashMap<String, String> getParamsFromDisbursementResponse(HashMap<String, String> parameters,
			String applicationId, String custDtlId) {	
		try {
			if (parameters != null) {
				Gson gson = new Gson();
				List<TbUaobApiAuditLogs> auditApiList = auditApiRepo.findByApplicationIdAndCustDtlId(applicationId,
						custDtlId);
				if (auditApiList != null && auditApiList.size() > 0) {
					TbUaobApiAuditLogs auditResponse = auditApiList.get(0);
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
			//logger.error("Error occurred while merging pdf file {} ", exp);
		}
		return parameters;
	}

	private User getBranchManagerInfo(String role, String branchCode) {
		List<User> userList = userRepo.findByAddInfo1AndAddInfo2(role, branchCode);
		if (userList != null && userList.size() > 0) {
			return userList.get(0);
		}
		return null;
	}

	private HashMap<String, String> getGkFactReportParameters(String applicationId) {
		HashMap<String, String> sanctionReportParams = new HashMap<String, String>();
		List<String> keyList = Arrays.asList(new String[] { "kendraName", "kendraId", "totalInstallment", "apr",
				"preclosedAmt", "bmId", "amountApproved", "memberName", "memberId", "id", "pdtName", "sancAmtRupees",
				"disbursementStage", "paymentFreq", "term", "interestRate", "referenceBenchMark", "benchMarkRate",
				"spread", "finalRate", "respectivePeriodB", "respectivePeriodS", "impactEPI", "impactNoOfEPI",
				"insuranceAmt", "overAllFeeChargeAmt", "tenure", "installmentAmt", "repaymentDate", "loanCharge",
				"processingFee", "approvedAmtInRupees", "sanctId" ,"insuranceChargeText" });
		List<ApplicationMaster> applicationMasterList = applicationMasterRepo.findByApplicationId(applicationId);
		logger.debug("Printing applicationMasterList:" + applicationMasterList);
		sanctionReportParams.put("insuranceChargeText", "One Time");
		try {
			if (applicationMasterList != null && applicationMasterList.size() > 0) {
				ApplicationMaster appMaster = applicationMasterList.get(0);
				BigDecimal amount = appMaster.getAmount();
				String loanAmount = amount.toString();
				String elTotalInstallment="";
				int lnamount = convertStringToInt(loanAmount);
				FetchAppRequest appReq = new FetchAppRequest();
				appReq.setAppId(appMaster.getAppId());
				FetchAppRequestFields fields = new FetchAppRequestFields();
				fields.setApplicationId(applicationId);
				fields.setVersionNum(Integer.parseInt(appMaster.getVersionNum()));
				appReq.setRequestObj(fields);
				logger.debug("=====getGkFactReportParameters Calling getCustomerData to fetch DB Details========");
				ApplyLoanRequestFields loanFields = loanService.getCustomerData(applicationMasterList, appReq);
				logger.debug("=====Outside the getCustomerData of getGkFactReportParameters========");
				logger.debug("Printing loanFields:" + loanFields);
				if (loanFields != null && loanFields.getApplicationdtls() != null
						&& loanFields.getApplicationdtls().size() > 0) {
					ApplicationDtls appDtl = loanFields.getApplicationdtls().get(0);
					CustomerDtls customerDtl = appDtl.getCustomerDetails();
					sanctionReportParams.put("kendraName", appMaster.getKendraName());
					sanctionReportParams.put("kendraId", appMaster.getKendraId());
					logger.debug("Printing Customer Details before null check{}", customerDtl);
					if (customerDtl != null) {
						logger.debug("Printing Customer Details{}", customerDtl);
						LoanDtls loanDtl = customerDtl.getLoanDtls();
						String product = loanDtl.getProduct();
						sanctionReportParams.put("loanAmt", loanDtl.getCaglAmt());
						sanctionReportParams.put("pdtName", loanDtl.getProduct());
						HashMap<String, String> purposeObj = (HashMap<String, String>) loanDtl.getPurpose();
						sanctionReportParams.put("purpose", purposeObj.getOrDefault("purposeDesc", "NA"));
						sanctionReportParams.put("intRate", "" + loanDtl.getInterestRate());
						String term = loanDtl.getTerm();
						logger.debug("Original term: " + term + ", Product: " + product + ", Loan Amount: " + lnamount);
						if (("GL.EMERG.LOAN".equalsIgnoreCase(product) || "GL.EMERGENCY.LN".equalsIgnoreCase(product))
								&& lnamount == 2000) {
							logger.debug("EL Scenario Loan Amount 2000");
							sanctionReportParams.put("term", "5 Months");
						} else if (("GL.EMERG.LOAN".equalsIgnoreCase(product)
								|| "GL.EMERGENCY.LN".equalsIgnoreCase(product)) && lnamount == 1000) {
							logger.debug("EL Scenario Loan Amount 1000");
							sanctionReportParams.put("term", "3 Months");
						} else {
							String tenureInWeeks = null;
							if (term != null) {
								if (term.contains("11W")) {
									tenureInWeeks = "3 Months";
								} else if (term.contains("24W")) {
									tenureInWeeks = "6 Months";
								} else if (term.contains("52W")) {
									tenureInWeeks = "12 Months";
								} else if (term.contains("104W")) {
									tenureInWeeks = "24 Months";
								} else if (term.contains("156W")) {
									tenureInWeeks = "36 Months";
								}
							}
							logger.debug("Mapped tenure: " + tenureInWeeks);
							if (tenureInWeeks != null) {
								sanctionReportParams.put("term", tenureInWeeks);
								logger.debug("Final term set in map: " + sanctionReportParams.get("term"));
							} else {
								logger.warn("tenureInWeeks is null, not setting term!");
							}
						}
						String installment = "";						
						List<SanctionRepaymentSchedule> repaymentSchedules = sanctionRepaymentScheduleRepository
								.findByApplicationId(applicationId);
						logger.debug("Printing repaymentSchedules: " + repaymentSchedules);
						if (repaymentSchedules.size() > 1) {
							installment = repaymentSchedules.get(1).getInstallment();
							String installmentAmt = "Rs. " + installment;
							sanctionReportParams.put("installmentAmt", installmentAmt);
						} else {
							logger.debug("Less than 2 repayment schedules found for applicationId: " + applicationId);
						}
						HashMap<String, String> freqObj = (HashMap<String, String>) loanDtl.getRepayFrequency();
						sanctionReportParams.put("paymentFreq", freqObj.getOrDefault("idDesc", "WEEKLY"));
						sanctionReportParams.put("frequency", freqObj.getOrDefault("idDesc", "WEEKLY"));
						String termVal = loanDtl.getTerm() == null ? "" : loanDtl.getTerm().replaceAll("[^0-9]", " ");
						String frequency = freqObj.getOrDefault("idDesc", "WEEKLY");
						if ((("GL.EMERG.LOAN").equalsIgnoreCase(product) || "GL.EMERGENCY.LN".equalsIgnoreCase(product))
								&& lnamount == 1000 && "WEEKLY".equalsIgnoreCase(frequency)) {
							logger.debug("EL Scenario EPI 1000 & WEEKLY");
							sanctionReportParams.put("tenure", "10");
						} else if ((("GL.EMERG.LOAN").equalsIgnoreCase(product)
								|| "GL.EMERGENCY.LN".equalsIgnoreCase(product)) && lnamount == 1000
								&& ("BIWEEKLY".equalsIgnoreCase(frequency)
										|| "BI-WEEKLY".equalsIgnoreCase(frequency))) {
							logger.debug("EL Scenario EPI 1000 & BIWEEKLY");
							sanctionReportParams.put("tenure", "5");
						} else if ((("GL.EMERG.LOAN").equalsIgnoreCase(product)
								|| "GL.EMERGENCY.LN".equalsIgnoreCase(product)) && lnamount == 2000
								&& "WEEKLY".equalsIgnoreCase(frequency)) {
							logger.debug("EL Scenario EPI 2000 & WEEKLY");
							sanctionReportParams.put("tenure", "20");

						} else if ((("GL.EMERG.LOAN").equalsIgnoreCase(product)
								|| "GL.EMERGENCY.LN".equalsIgnoreCase(product)) && lnamount == 2000
								&& ("BIWEEKLY".equalsIgnoreCase(frequency)
										|| "BI-WEEKLY".equalsIgnoreCase(frequency))) {
							logger.debug("EL Scenario EPI 2000 & BIWEEKLY");
							sanctionReportParams.put("tenure", "10");
						} else {
							if ("WEEKLY".equalsIgnoreCase(frequency)) {
								String instType = loanDtl.getTerm();
								String mod = instType.replace("W", "");
								sanctionReportParams.put("tenure", mod);
							} else if ("BIWEEKLY".equalsIgnoreCase(frequency)
									|| "BI-WEEKLY".equalsIgnoreCase(frequency)) {
								String instType = loanDtl.getTerm();
								String mod = instType.replace("W", "");
								int epi = Integer.parseInt(mod);
								Integer paymentFreq = epi / 2;
								sanctionReportParams.put("tenure", paymentFreq.toString());
							} else if ("FOURWEEKLY".equalsIgnoreCase(frequency)
									|| "FOUR-WEEKLY".equalsIgnoreCase(frequency)) {
								String instType = loanDtl.getTerm();
								String mod = instType.replace("W", "");
								int epi = Integer.parseInt(mod);
								Integer paymentFreq = epi / 4;
								sanctionReportParams.put("tenure", paymentFreq.toString());
							}
						}
						int overAllfeeChargeAmt = convertStringToInt(
								sanctionReportParams.getOrDefault("insuranceAmt", "0"))
								+ convertStringToInt(sanctionReportParams.getOrDefault("processingFee", "0"));
						// EL Scenario Loan Amount 1000 for 6th pt
						Double processFee = Double
								.parseDouble(loanDtl.getChargeAndBreakupDtls().getLoanProcessingFee());
						Double gstAmt = Double.parseDouble(loanDtl.getChargeAndBreakupDtls().getGST());
						Double totalProcessingFee = processFee + gstAmt;
						String formattedProcessingFee = String.format("%.2f", totalProcessingFee);
						logger.debug("formattedProcessingFee:" + formattedProcessingFee);
						sanctionReportParams.put("processingFee", formattedProcessingFee);
						sanctionReportParams.put("loanCharge", loanDtl.getChargeAndBreakupDtls().getAprxLoanCharges());

						Integer insuranceAmt = 0;
						try {
							logger.debug("Going to get insuranceAmt");
//							insuranceAmt=Integer.parseInt(loanDtl.getInsurDtls().getApplicant_insurance_amt())+Integer.parseInt(loanDtl.getInsurDtls().getSpouse_insurance_amt());
							String applicantInsuranceAmtStr = loanDtl.getInsurDtls().getApplicant_insurance_amt();
							String spouseInsuranceAmtStr = loanDtl.getInsurDtls().getSpouse_insurance_amt();
							if (applicantInsuranceAmtStr != null && !applicantInsuranceAmtStr.isEmpty()) {
								try {
									insuranceAmt += Integer.parseInt(applicantInsuranceAmtStr);
									logger.debug("Printing insuranceAmt:" + insuranceAmt);
								} catch (NumberFormatException e) {
									logger.error(
											"Invalid applicant insurance amount format: " + applicantInsuranceAmtStr,
											e);
								}
							}
							if (spouseInsuranceAmtStr != null && !spouseInsuranceAmtStr.isEmpty()) {
								try {
									insuranceAmt += Integer.parseInt(spouseInsuranceAmtStr);
								} catch (NumberFormatException e) {
									logger.error("Invalid spouse insurance amount format: " + spouseInsuranceAmtStr, e);
								}
							}
						} catch (Exception e) {
							logger.debug("insuranceAmt convertion fails:" + e);
						}

						String formattedInsuranceAmt = String.format("%d", insuranceAmt);
						sanctionReportParams.put("insuranceAmt", formattedInsuranceAmt);
						logger.debug("insuranceAmt set");
						Double overAllFeeChargeAmt = 0.0;
						if (("GL.EMERG.LOAN").equalsIgnoreCase(product)
								|| ("GL.EMERGENCY.LN").equalsIgnoreCase(product) && lnamount == 1000) {
							logger.debug("EL Scenario Loan Amount 1000 for 6th pt");
							overAllFeeChargeAmt = processFee + gstAmt + insuranceAmt;
							logger.debug("Printing overAllFeeChargeAmt:" + overAllFeeChargeAmt);
							sanctionReportParams.put("overAllFeeChargeAmt", "Rs." + overAllFeeChargeAmt);
							// overAllfeeChargeAmt=6;
						} else if (("GL.EMERG.LOAN").equalsIgnoreCase(product)
								|| ("GL.EMERGENCY.LN").equalsIgnoreCase(product) && lnamount == 2000) {
							logger.debug("EL Scenario Loan Amount 2000 for 6th pt");
							overAllFeeChargeAmt = processFee + gstAmt + insuranceAmt;
							logger.debug("Printing overAllFeeChargeAmt1:" + overAllFeeChargeAmt);
							sanctionReportParams.put("overAllFeeChargeAmt", "Rs." + overAllFeeChargeAmt);
							// overAllfeeChargeAmt=12;
						} else {
							// Amit Changes
							String formattedOverAllFeeChargeAmt = String.format("Rs %d", overAllfeeChargeAmt);
							logger.debug("Printing formattedOverAllFeeChargeAmt:" + formattedOverAllFeeChargeAmt);
							sanctionReportParams.put("overAllFeeChargeAmt", "" + formattedOverAllFeeChargeAmt);
						}
	
						String elProduct = loanDtl.getProduct();
						if (("GL.EMERG.LOAN").equalsIgnoreCase(elProduct)
								|| ("GL.EMERGENCY.LN").equalsIgnoreCase(elProduct)) {
							// Amit Adding New Param for Text change
							sanctionReportParams.put("insuranceChargeText", "NA");
							String elFrequency = freqObj.getOrDefault("idDesc", "WEEKLY");
							String elInterestFee = loanDtl.getChargeAndBreakupDtls().getInterest_Fee();
							if (elInterestFee != null && !elInterestFee.isEmpty()) {
								elTotalInstallment = elInterestFee;
							} else {
								if (lnamount == 1000 && ("WEEKLY").equalsIgnoreCase(elFrequency)) {
									elTotalInstallment = "22.44";
								} else if (lnamount == 1000 && ("BIWEEKLY").equalsIgnoreCase(elFrequency)) {
									elTotalInstallment = "27.62";
								} else if (lnamount == 2000 && ("WEEKLY").equalsIgnoreCase(elFrequency)) {
									elTotalInstallment = "79.40";
								} else if (lnamount == 2000 && ("BIWEEKLY").equalsIgnoreCase(elFrequency)) {
									elTotalInstallment = "89.75";
								}
							}
						}
						 
						double totalInstallment = 0;
						if (("GL.EMERG.LOAN").equalsIgnoreCase(product)
								|| ("GL.EMERGENCY.LN").equalsIgnoreCase(product)) {
							if (lnamount == 1000) {
								if ("WEEKLY".equalsIgnoreCase(frequency)) {
									logger.debug("EL Scenario Loan Amount 1000 & WEEKLY");
									sanctionReportParams.put("totalInstallment", "Rs. 22");
									totalInstallment = 22;
								} else if ("BIWEEKLY".equalsIgnoreCase(frequency)) {
									logger.debug("EL Scenario Loan Amount 1000 & BIWEEKLY");
									sanctionReportParams.put("totalInstallment", "Rs. 27");
									totalInstallment = 27;
								} else {
									logger.debug("EL Scenario Loan Amount 1000 Default Calculation");
									sanctionReportParams.put("totalInstallment", "Rs. 6");
									totalInstallment = 6;
								}
							} else if (lnamount == 2000) {
								if ("WEEKLY".equalsIgnoreCase(frequency)) {
									logger.debug("EL Scenario Loan Amount 2000 & WEEKLY");
									sanctionReportParams.put("totalInstallment", "Rs. 78");
									totalInstallment = 78;
								} else if ("BIWEEKLY".equalsIgnoreCase(frequency)) {
									logger.debug("EL Scenario Loan Amount 2000 & BIWEEKLY");
									sanctionReportParams.put("totalInstallment", "Rs. 88");
									totalInstallment = 88;
								} else {
									logger.debug("EL Scenario Loan Amount 2000 Default Calculation");
									sanctionReportParams.put("totalInstallment", "Rs. 12");
									totalInstallment = 12;
								}
							} else {
								// Fetch repayment schedule if loan amount doesn't match predefined values
								List<SanctionRepaymentSchedule> repaymentSchedul = sanctionRepaymentScheduleRepository
										.findByApplicationId(applicationId);
								double totalInstallmentDouble = 0.0;
								for (SanctionRepaymentSchedule schedule : repaymentSchedul) {
									String installmentStr = schedule.getInterest();
									if (installmentStr != null && !installmentStr.isEmpty()) {
										try {
											totalInstallmentDouble += Double.parseDouble(installmentStr);
											logger.debug("Printing totalInstallmentDouble:" + totalInstallmentDouble);
										} catch (NumberFormatException e) {
											logger.error("Invalid installment value: " + installmentStr, e);
										}
									}
								}
								totalInstallment = (int) Math.round(totalInstallmentDouble);
								String formattedInstallment = String.format("Rs. %d", totalInstallment);
								logger.debug("formattedInstallment: " + formattedInstallment);
								sanctionReportParams.put("totalInstallment", formattedInstallment);
							}

							// Calculate net amount
							// double netAmount = lnamount - totalInstallment - overAllfeeChargeAmt;
							// logger.debug("Final Calculation: " + lnamount + " - " + totalInstallment + "
							// - "
							// + overAllfeeChargeAmt + " = " + netAmount);
							Double netAmount = lnamount - overAllFeeChargeAmt;
							logger.debug("Printing netAmount:" + netAmount);
							sanctionReportParams.put("preclosedAmt", "Rs. " + netAmount);
							
							// need to chnage for point 8 (1+5)			
							double  elInst = Double.parseDouble(elTotalInstallment);
							double preclosedAmt = lnamount + elInst;
							logger.debug("preclosedAmt:" + preclosedAmt);
							sanctionReportParams.put("approvedAmtInRupees", "Rs. " + preclosedAmt);
						} else {
							Integer preclosedAmt = lnamount + overAllfeeChargeAmt;
							logger.debug("Printing preclosedAmt:" + preclosedAmt);
						
							String formattedPreclosedAmt = String.format("Rs. %d", preclosedAmt);
							logger.debug("Printing formattedPreclosedAmt:" + formattedPreclosedAmt);
							sanctionReportParams.put("preclosedAmt", formattedPreclosedAmt);
						}
						sanctionReportParams.put("spouseInsurance", loanDtl.getSpouseInsurance());

						/*
						 * Double processFee =
						 * Double.parseDouble(loanDtl.getChargeAndBreakupDtls().getLoanProcessingFee());
						 * logger.debug("processFee:"+processFee); Double gstAmt =
						 * Double.parseDouble(loanDtl.getChargeAndBreakupDtls().getGST());
						 * logger.debug("gstAmt:"+gstAmt); Double totalProcessingFee = processFee +
						 * gstAmt; logger.debug("totalProcessingFee:"+totalProcessingFee); String
						 * formattedProcessingFee = String.format("%.2f", totalProcessingFee);
						 * logger.debug("formattedProcessingFee:"+formattedProcessingFee);
						 * 
						 * sanctionReportParams.put("processingFee", formattedProcessingFee);
						 * sanctionReportParams.put("loanCharge",
						 * loanDtl.getChargeAndBreakupDtls().getAprxLoanCharges());
						 * 
						 * Integer insuranceAmt = 0; try { logger.debug("Going to get insuranceAmt"); //
						 * insuranceAmt=Integer.parseInt(loanDtl.getInsurDtls().
						 * getApplicant_insurance_amt())+Integer.parseInt(loanDtl.getInsurDtls().
						 * getSpouse_insurance_amt()); String applicantInsuranceAmtStr =
						 * loanDtl.getInsurDtls().getApplicant_insurance_amt(); String
						 * spouseInsuranceAmtStr = loanDtl.getInsurDtls().getSpouse_insurance_amt(); if
						 * (applicantInsuranceAmtStr != null && !applicantInsuranceAmtStr.isEmpty()) {
						 * try { insuranceAmt += Integer.parseInt(applicantInsuranceAmtStr);
						 * logger.debug("Printing insuranceAmt:"+insuranceAmt); } catch
						 * (NumberFormatException e) { logger.error(
						 * "Invalid applicant insurance amount format: " + applicantInsuranceAmtStr, e);
						 * } } if (spouseInsuranceAmtStr != null && !spouseInsuranceAmtStr.isEmpty()) {
						 * try { insuranceAmt += Integer.parseInt(spouseInsuranceAmtStr); } catch
						 * (NumberFormatException e) {
						 * logger.error("Invalid spouse insurance amount format: " +
						 * spouseInsuranceAmtStr, e); } } } catch (Exception e) {
						 * logger.debug("insuranceAmt convertion fails:" + e); }
						 * 
						 * 
						 * String formattedInsuranceAmt = String.format("%d", insuranceAmt);
						 * sanctionReportParams.put("insuranceAmt", formattedInsuranceAmt);
						 * logger.debug("insuranceAmt set");
						 * 
						 * logger.debug("Printing sanctionReportParams:"+sanctionReportParams);
						 * 
						 * 
						 * 
						 */

						sanctionReportParams.put("id", appMaster.getApplicationId());
						if (("GL.EMERG.LOAN").equalsIgnoreCase(product)
								|| ("GL.EMERGENCY.LN").equalsIgnoreCase(product) && lnamount == 1000) {
							logger.debug("EL Scenario Loan Amount 1000 for 6th A");
							String processingFee = String.valueOf(processFee + gstAmt);
							logger.debug("Printing processingFee:" + processingFee);
							sanctionReportParams.put("processingFee", "Rs." + processingFee);
						} else if (("GL.EMERG.LOAN").equalsIgnoreCase(product)
								|| ("GL.EMERGENCY.LN").equalsIgnoreCase(product) && lnamount == 2000) {
							logger.debug("EL Scenario Loan Amount 2000 for 6th A");
							String processingFee = String.valueOf(processFee + gstAmt);
							logger.debug("Printing processingFee1:" + processingFee);
							sanctionReportParams.put("processingFee", "Rs." + processingFee);
						} else {
							try {
								// Temp Code Amit
								try {
									overAllfeeChargeAmt = convertStringToInt(
											sanctionReportParams.getOrDefault("insuranceAmt", "0"))
											+ convertStringToInt(
													sanctionReportParams.getOrDefault("processingFee", "0"));
									String formattedOverAllFeeChargeAmt = String.format("Rs %d", overAllfeeChargeAmt);
									logger.debug(
											"Printing formattedOverAllFeeChargeAmt:" + formattedOverAllFeeChargeAmt);
									sanctionReportParams.put("overAllFeeChargeAmt", "" + formattedOverAllFeeChargeAmt);

									double netAmount = lnamount - totalInstallment - overAllfeeChargeAmt;
									logger.debug("Printing netAmount:" + netAmount);
									logger.debug("Final Calculation: " + lnamount + " - " + totalInstallment + " - "
											+ overAllfeeChargeAmt + " = " + netAmount);
									sanctionReportParams.put("preclosedAmt", "Rs. " + netAmount);

								} catch (Exception e) {
								}
								// Temp code ends here
								formattedInsuranceAmt = "Rs. " + sanctionReportParams.get("insuranceAmt");
								sanctionReportParams.put("insuranceAmt", formattedInsuranceAmt);
								formattedProcessingFee = "Rs. " + sanctionReportParams.get("processingFee");
								sanctionReportParams.put("processingFee", formattedProcessingFee);
							} catch (Exception e) {
								logger.error(
										"Error occured while concatenating insuranceAmt with Rs or processingFee with Rs.");
							}
						}
						logger.debug("Printing sanctionReportParams:" + sanctionReportParams);
					}
				}
				logger.debug("Fetching cbResRepository to get roi");
				Optional<TbUaobCbResponse> cbRes = cbResRepository
						.findByAppIdAndApplicationIdOrderByVersionNumDesc(appReq.getAppId(), applicationId);
				logger.debug("Printing cbRes:" + cbRes);
				String interestFeeStr = null;
				if (cbRes.isPresent()) {
					logger.debug("cbRes present:");
					TbUaobCbResponse cbResponse = cbRes.get();
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
					if (paylonJsonObj.has("roi")) {
						String roi = paylonJsonObj.getString("roi");

						sanctionReportParams.put("interestRate", "" + roi + "%");
						logger.debug("Printing InterestRate value for getGkFactReportParameters:" + roi);

					}
				}

				logger.debug("Going to get sanctRepoId");
				String sanctRepoId = "";
				Optional<SanctionRepaymentSchedule> opSanctRepaySchedule = sanctionRepaymentScheduleRepository
						.findTopByApplicationIdOrderByCreateTsDesc(applicationId);
				logger.debug("Printing opSanctRepaySchedule:" + opSanctRepaySchedule);
				if (opSanctRepaySchedule.isPresent()) {
					SanctionRepaymentSchedule sanctionRepaymentSchedule = opSanctRepaySchedule.get();
					logger.debug("sanctionRepaymentSchedule is present :" + sanctionRepaymentSchedule);
					sanctRepoId = sanctionRepaymentSchedule.getSanctRepoId();
				}
				ApplicationDtls appDtl = loanFields.getApplicationdtls().get(0);
				logger.debug("printing appDtl :" + appDtl);
				CustomerDtls customerDtl = appDtl.getCustomerDetails();
				LoanDtls loanDtl = customerDtl.getLoanDtls();
				//String interestFee = loanDtl.getChargeAndBreakupDtls().getInterest_Fee();
				
				HashMap<String, String> freqObj = (HashMap<String, String>) loanDtl.getRepayFrequency();
				sanctionReportParams.put("paymentFreq", freqObj.getOrDefault("idDesc", "WEEKLY"));
				sanctionReportParams.put("frequency", freqObj.getOrDefault("idDesc", "WEEKLY"));
//				sanctionReportParams.put("tenure", termVal);
			
				String frequency = freqObj.getOrDefault("idDesc", "WEEKLY");
				String product = loanDtl.getProduct();
				//logger.debug("Printing  interestFee:" + interestFee);

				/*
				 * if (("GL.EMERG.LOAN").equalsIgnoreCase(product) ||
				 * ("GL.EMERGENCY.LN").equalsIgnoreCase(product) && lnamount == 1000 &&
				 * ("WEEKLY").equalsIgnoreCase(frequency)) {
				 * logger.debug("EL Scenario Loan Amount 1000 & WEEKLY"); if (interestFee !=
				 * null && !interestFee.isEmpty()) {
				 * sanctionReportParams.put("totalInstallment", "Rs." + interestFee); } else {
				 * sanctionReportParams.put("totalInstallment", "Rs. 22.44"); } } else if
				 * (("GL.EMERG.LOAN").equalsIgnoreCase(product) ||
				 * ("GL.EMERGENCY.LN").equalsIgnoreCase(product) && lnamount == 1000 &&
				 * ("BIWEEKLY").equalsIgnoreCase(frequency)) {
				 * logger.debug("EL Scenario Loan Amount 1000 & BIWEEKLY"); if (interestFee !=
				 * null && !interestFee.isEmpty()) {
				 * sanctionReportParams.put("totalInstallment", "Rs." + interestFee); } else {
				 * sanctionReportParams.put("totalInstallment", "Rs. 27.62"); } } else if
				 * (("GL.EMERG.LOAN").equalsIgnoreCase(product) ||
				 * ("GL.EMERGENCY.LN").equalsIgnoreCase(product) && lnamount == 2000 &&
				 * ("WEEKLY").equalsIgnoreCase(frequency)) {
				 * logger.debug("EL Scenario Loan Amount 1000 & WEEKLY"); if (interestFee !=
				 * null && !interestFee.isEmpty()) {
				 * sanctionReportParams.put("totalInstallment", "Rs." + interestFee); } else {
				 * sanctionReportParams.put("totalInstallment", "Rs. 79.40"); } } else if
				 * (("GL.EMERG.LOAN").equalsIgnoreCase(product) ||
				 * ("GL.EMERGENCY.LN").equalsIgnoreCase(product) && lnamount == 2000 &&
				 * ("BIWEEKLY").equalsIgnoreCase(frequency)) {
				 * logger.debug("EL Scenario Loan Amount 2000 & BIWEEKLY"); if (interestFee !=
				 * null && !interestFee.isEmpty()) {
				 * sanctionReportParams.put("totalInstallment", "Rs." + interestFee); } else {
				 * sanctionReportParams.put("totalInstallment", "Rs. 89.75"); } }
				 */
			
				if (("GL.EMERG.LOAN").equalsIgnoreCase(product) || ("GL.EMERGENCY.LN").equalsIgnoreCase(product)
						) {					
						sanctionReportParams.put("totalInstallment", "Rs. "+elTotalInstallment);		
				}else {
					List<SanctionRepaymentSchedule> repaymentSchedules = sanctionRepaymentScheduleRepository
							.findByApplicationId(applicationId);
					logger.debug("Printing  repaymentSchedules:" + repaymentSchedules);
					double totalInstallment = 0.0;
					String format = null;
					for (SanctionRepaymentSchedule schedule : repaymentSchedules) {
						String installmentStr = schedule.getInterest();
						if (installmentStr != null && !installmentStr.isEmpty()) {
							try {
								double installmentValue = Double.parseDouble(installmentStr);
								totalInstallment += installmentValue;
								format = String.format("Rs. %.2f", totalInstallment);
							} catch (NumberFormatException e) {
								System.err.println("Invalid installment value: " + installmentStr);
							}
						}
					}
					sanctionReportParams.put("totalInstallment", format);
					BigDecimal amout = appMaster.getAmount();
					String lonAmount = amout.toString();
					int lnAmt = convertStringToInt(lonAmount);
					Double totAmtToBorrower = totalInstallment + lnAmt;
					String formattedApprovedAmt = "Rs. " + String.format("%.2f", totAmtToBorrower);
					sanctionReportParams.put("approvedAmtInRupees", formattedApprovedAmt);
				}

				if (!sanctRepoId.isEmpty()) {
					sanctionReportParams.put("sanctId", sanctRepoId);
				} else {
					logger.debug("sanctRepoId:" + sanctRepoId);
					sanctionReportParams.put("sanctId", "-");
				}
				sanctionReportParams.put("memberName", appMaster.getCustomerName());
				sanctionReportParams.put("memberId", appMaster.getCustomerId());
				sanctionReportParams.put("kendraId", appMaster.getKendraId());
				int intValueExact = amount.toBigInteger().intValueExact();
				String amountApproved = "Rs. " + intValueExact;
				sanctionReportParams.put("amountApproved", amountApproved);
				sanctionReportParams.put("disbursementStage", "100 % upfront");
				User userObj = getBranchManagerInfo("BM", appMaster.getBranchCode());
				if (userObj != null) {
				    sanctionReportParams.put("bmId", userObj.getUserId());
				}
				sanctionReportParams.put("branchCode", appMaster.getBranchCode());

				getParamsFromDisbursementResponse(sanctionReportParams, applicationId, appMaster.getCustomerId());
				logger.debug("all parameter set complete:" + sanctionReportParams);
			}
		} catch (Exception exp) {
			logger.error("Error occurred while fetching params :: " + exp);
		}
		for (String key : keyList) {
			if (!(sanctionReportParams.containsKey(key) && StringUtils.isNotBlank(sanctionReportParams.get(key)))) {
				sanctionReportParams.put(key, "");
			}
		}
		logger.debug("Final Value of insuranceChargeText" +sanctionReportParams.get("insuranceChargeText"));
		logger.debug("Printing final sanctionReportParams:" + sanctionReportParams);
		return sanctionReportParams;
	}
		
	public byte[] generateAndDownloadSanctionReport(String applicationId) {
		try {
			HashMap<String, String> sanctionReportParams = getSanctionReportParameters(applicationId);
			HashMap<String, String> loanApplicationParams = getLoanApplicationParams(applicationId);
			String branchCode = sanctionReportParams.get("branchCode");
			String branchLan = tbOfficeDataRepository.findBranchLan(branchCode);
			logger.debug("Printing branchLan:" + branchLan);
			String SanctionReport = "SanctionReport";
			String LoanApplicationForm = "LoanApplicationForm";
			String GKFactReport = "GKFactReport";
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
						// this is temp code need to remove later
						if (branchLan.equalsIgnoreCase("ori") || branchLan.equalsIgnoreCase("odi")) {
							branchLan = "odi";
						}
						// this is temp code need to remove later
						if (branchLan.equalsIgnoreCase("wb") || branchLan.equalsIgnoreCase("ben")) {
							branchLan = "ben";
						}
						SanctionReport = SanctionReport + "_" + branchLan;
						LoanApplicationForm = LoanApplicationForm + "_" + branchLan;
						GKFactReport = GKFactReport + "_" + branchLan;
						format = "html";
						logger.debug("format is:" + format);
					}
				}
			}
			logger.debug("sanctionReportParams : {}", sanctionReportParams);
			HashMap<String, String> factReportParams = getGkFactReportParameters(applicationId);
			logger.debug("factReportParams : {}", factReportParams);
			if (factReportParams.containsKey("pdtName") && (factReportParams.containsValue("GL.EMERG.LOAN")
					|| factReportParams.containsValue("GL.EMERGENCY.LN"))) {
				logger.debug("merging reports for EL");
				File factSheet = generateBirReport(applicationId, GKFactReport, factReportParams, format);
				logger.debug("report language is not english");
				if (factSheet != null && factSheet.exists()) {
					try {
						byte[] factSheethtmlBytes = Files.readAllBytes(factSheet.toPath());
						try {
							String factSheetbase64Html = java.util.Base64.getEncoder()
									.encodeToString(factSheethtmlBytes);
							String factSheethtmlContent = new String(
									java.util.Base64.getDecoder().decode(factSheetbase64Html), "UTF-8");
							factSheethtmlContent = factSheethtmlContent.replace(";\">Repayment Schedule",
									";page-break-before: always;\">Repayment Schedule");
							String mergeForEL = mergeReportsForEL(factSheethtmlContent);
							File megeFile = new File("merger.html");
							Files.write(Paths.get("merger.html"), mergeForEL.getBytes());
							logger.debug("file Merger for RL");
							return Files.readAllBytes(megeFile.toPath());
						} catch (IOException e) {
							// e.printStackTrace();
							logger.error("Exception occurred", e);
						}
						return null;
					} catch (Exception e) {
						// e.printStackTrace();
						logger.error("Exception occurred", e);
						return null;
					}
				} else {
					return null;
				}
			} else {
				File loanApplicationForm=generateBirReport(applicationId, LoanApplicationForm, loanApplicationParams,format);
				File sanctionReport = generateBirReport(applicationId, SanctionReport, sanctionReportParams, format);
				File factSheet = generateBirReport(applicationId, GKFactReport, factReportParams, format);
				boolean isOtherThanEnglish = SanctionReport.contains("_");
				logger.debug("report language is not english");
				if (factSheet != null && factSheet.exists()) {
					try {
						byte[] loanApplicationHtmlBytes = Files.readAllBytes(loanApplicationForm.toPath());
						byte[] sanctionReporthtmlBytes = Files.readAllBytes(sanctionReport.toPath());
						byte[] factSheethtmlBytes = Files.readAllBytes(factSheet.toPath());
						try {
							//Loan Application
							String loanApplicationbase64Html = java.util.Base64.getEncoder()
									.encodeToString(loanApplicationHtmlBytes);
							String loanApplicationhtmlContent = new String(
									java.util.Base64.getDecoder().decode(loanApplicationbase64Html), "UTF-8");
													
							
							//Sanction Report
							String sanctionReportbase64Html = java.util.Base64.getEncoder()
									.encodeToString(sanctionReporthtmlBytes);
							String sanctionReporthtmlContent = new String(
									java.util.Base64.getDecoder().decode(sanctionReportbase64Html), "UTF-8");
							logger.debug("sanctionReporthtmlContent" + sanctionReporthtmlContent);
							
							
							//KFS Report
							String factSheetbase64Html = java.util.Base64.getEncoder()
									.encodeToString(factSheethtmlBytes);

							String factSheethtmlContent = new String(
									java.util.Base64.getDecoder().decode(factSheetbase64Html), "UTF-8");
							 logger.debug("before replacement :" + factSheethtmlContent); // Adding a line
							// break
							factSheethtmlContent = factSheethtmlContent.replace(";\">Repayment Schedule",
									";page-break-before: always;\">Repayment Schedule");
							// logger.debug("after replacement :" + factSheethtmlContent);

							// logger.debug("factSheethtmlContent"+ factSheethtmlContent);

							/*
							 * ConverterProperties convertPro = new ConverterProperties(); FontProvider
							 * fontProvider = new FontProvider(); convertPro.setCharset("UTF-8"); String
							 * reportTemplateFile = CommonUtils.getExternalProperties("reportsTemplateDir");
							 * logger.debug("report path is "+ reportTemplateFile); logger.debug(
							 * SanctionReport +":"+GKFactReport); try { if (SanctionReport.contains("_kan")
							 * || GKFactReport.contains("_kan")) { logger.debug("font path is "+
							 * reportTemplateFile+"Tunga.ttf");
							 * fontProvider.addFont(reportTemplateFile+"Tunga.ttf");
							 * logger.debug("Got Kannada Font"); } else if (SanctionReport.contains("_mar")
							 * || GKFactReport.contains("_mar")) {
							 * fontProvider.addFont(reportTemplateFile+"NotoSansDevanagari-Regular.ttf"); }
							 * } catch (Exception e) { logger.error("Error in setting font:" + e); }
							 * 
							 * //fontProvider.addSystemFonts(); fontProvider.addStandardPdfFonts();
							 * logger.debug("added standardfont"+ fontProvider.addStandardPdfFonts());
							 * fontProvider.addSystemFonts(); logger.debug("added systemfont"+
							 * fontProvider.addSystemFonts()); File sanctionReportpdfFile = new
							 * File("report1.pdf"); File factSheetpdfFile = new File("report2.pdf"); File
							 * loanApplnpdfFile=new File("report3.pdf");
							 * convertPro.setFontProvider(fontProvider);
							 * HtmlConverter.convertToPdf(sanctionReporthtmlContent, new
							 * FileOutputStream(sanctionReportpdfFile), convertPro);
							 * HtmlConverter.convertToPdf(factSheethtmlContent, new
							 * FileOutputStream(factSheetpdfFile), convertPro);
							 * HtmlConverter.convertToPdf(loanApplicationhtmlContent, new
							 * FileOutputStream(loanApplnpdfFile), convertPro);
							 * 
							 */

							// String report1Content = "<div class='report1'>" + sanctionReporthtmlContent +
							// "</div>";
							// String report2Content = "<div class='report2'>" + loanApplicationhtmlContent
							// + "</div>";
							// String report3Content = "<div class='report2'>" + factSheethtmlContent +
							// "</div>";
							// String mergedContent = report1Content + "<br/><br/>" + report2Content
							// +"<br/><br/>"+report3Content ;

							String mergedContent = mergeReports(loanApplicationhtmlContent,sanctionReporthtmlContent,factSheethtmlContent);
							// String mergedContent =
							//		 loanApplicationhtmlContent+"<br/><br/>"+sanctionReporthtmlContent+"<br/><br/>"+factSheethtmlContent;
							 logger.debug("Final String "+ mergedContent);

							File megeFile = new File("merger.html");
							Files.write(Paths.get("merger.html"), mergedContent.getBytes());
							logger.debug("file Merger Successfully");
							return Files.readAllBytes(megeFile.toPath());
						} catch (IOException e) {
							// e.printStackTrace();
							logger.error("Exception occurred", e);
						}
						return null;
					} catch (Exception e) {
						// e.printStackTrace();
						logger.error("Exception occurred", e);
						return null;
					}
				} else {
					return null;
				}
			}
		} catch (Exception e) {
			logger.error("Error occurred while merging pdf file {} ", e);
			return null;
		}
	}
	
	
	private static String mergeReports(String report1Html,String report2Html,String report3Html) {
	    // Extract body content from each report
	   
		 report1Html = report1Html.replaceAll("class=\"", "class=\"report1-");
		 report2Html = report2Html.replaceAll("class=\"", "class=\"report2-");
		 report3Html = report3Html.replaceAll("class=\"", "class=\"report3-");
		
		String body1 = extractBodyContent(report1Html);
	    String body2 = extractBodyContent(report2Html).replaceAll("file:/tmp","https://www.creditaccessgrameen.in/wp-content/themes/creditaccessgrameen/assets//images/logo.png?");
	    String body3 = extractBodyContent(report3Html).replaceAll("file:/tmp","https://www.creditaccessgrameen.in/wp-content/themes/creditaccessgrameen/assets//images/logo.png?");;
	    // Extract styles from each report
	    String styles1 = extractStyles(report1Html,"report1-");
	    String styles2 = extractStyles(report2Html,"report2-");
	    String styles3 = extractStyles(report3Html,"report3-");

	    
	    // Combine styles, ensuring no duplicates
	    String combinedStyles = combineStyles(styles1,styles2,styles3);

	    // Construct the new HTML document
	    StringBuilder mergedHtml = new StringBuilder();
	    mergedHtml.append("<html>");
	    mergedHtml.append("<head><meta charset='UTF-8'><title>Sanction Report</title>");
	    mergedHtml.append(combinedStyles); // Add combined styles
	    mergedHtml.append("</head>");
	    mergedHtml.append("<body>");
	    mergedHtml.append(body1);
	    mergedHtml.append("<br/>"); // Optional: Add some space between reports
	    mergedHtml.append("<div style='page-break-after: always;'></div>");
	    mergedHtml.append(body2);
	    mergedHtml.append("<br/>");
	    mergedHtml.append("<div style='page-break-after: always;'></div>");
	    mergedHtml.append(body3);
	    mergedHtml.append("</body>");
	    mergedHtml.append("</html>");

	    return mergedHtml.toString();
	}
	private static String mergeReportsForEL(String report1Html) {
	    // Extract body content from each report
		logger.debug("inside mergeReportsForEL:");
		 report1Html = report1Html.replaceAll("class=\"", "class=\"report1-");
	
		String body1 = extractBodyContent(report1Html);
		// Extract styles from each report
	    String styles1 = extractStyles(report1Html,"report1-");
	
	    // Combine styles, ensuring no duplicates
	    String combinedStyles = combineStylesForEL(styles1);

	    // Construct the new HTML document
	    StringBuilder mergedHtml = new StringBuilder();
	    mergedHtml.append("<html>");
	    mergedHtml.append("<head><meta charset='UTF-8'><title>Sanction Report</title>");
	    mergedHtml.append(combinedStyles); // Add combined styles
	    mergedHtml.append("</head>");
	    mergedHtml.append("<body>");
	    mergedHtml.append(body1);
	    mergedHtml.append("<br/><br/>"); // Optional: Add some space between reports
	  //  mergedHtml.append("<div style='page-break-after: always;'></div>");
	    mergedHtml.append("<br/><br/>");
	   // mergedHtml.append("<div style='page-break-after: always;'></div>");
	    mergedHtml.append("</body>");
	    mergedHtml.append("</html>");

	    return mergedHtml.toString();
	}
	private static String combineStylesForEL(String styles1) {
	    // Here you may want to handle conflicts between styles if needed
	    // For simplicity, this just combines them
	    return "<style>" + styles1 +"</style>";
	}
	
	private static String extractBodyContent(String html) {
	    // Use regex or a library like Jsoup to extract the body content
	    // This is a simple regex example; consider using Jsoup for complex HTML
	    Pattern pattern = Pattern.compile("<body.*?>(.*?)</body>", Pattern.DOTALL);
	    Matcher matcher = pattern.matcher(html);
	    if (matcher.find()) {
	        return matcher.group(1); // Return the content inside the body tags
	    }
	    return ""; // Return empty if no body found
	}

	private static String extractStyles(String html, String reportName) {
	    // Use regex or a library like Jsoup to extract the style content
	    Pattern pattern = Pattern.compile("<style.*?>(.*?)</style>", Pattern.DOTALL);
	    Matcher matcher = pattern.matcher(html);
	    StringBuilder styles = new StringBuilder();
	    while (matcher.find()) {
	        styles.append(matcher.group(1)).append("\n"); // Append each style block
	    }
	    return styles.toString().replaceAll(".style", "."+reportName+"style");
	}

	private static String combineStyles(String styles1,String styles2,String styles3) {
	    // Here you may want to handle conflicts between styles if needed
	    // For simplicity, this just combines them
	    return "<style>" + styles1+"\n"+styles2+"\n"+styles3 +"</style>";
	}

	// Modified By Prem
	public byte[] generateAndDownloadDBKitReport(String applicationId) {
		try {
			HashMap<String, String> dbKitReportParams = getDBKitParameters(applicationId);
			String branchid = dbKitReportParams.get("branchCode");
			String branchLan = tbOfficeDataRepository.findBranchLan(branchid);
			logger.debug("Priting branchLan: " +branchLan);
			String DBKitReport = "DBKitReport";
			String format = "html";
			if (!StringUtils.isBlank(branchLan)) {
				branchLan = branchLan.toLowerCase();
				if (!branchLan.isBlank()) {		 
					if (branchLan.equalsIgnoreCase("kan") || branchLan.equalsIgnoreCase("mar")
							|| branchLan.equalsIgnoreCase("tam") || branchLan.equalsIgnoreCase("tel")
							|| branchLan.equalsIgnoreCase("hin") || branchLan.equalsIgnoreCase("mal")
							|| branchLan.equalsIgnoreCase("odi") || branchLan.equalsIgnoreCase("guj") 
							|| branchLan.equalsIgnoreCase("ori")
							|| branchLan.equalsIgnoreCase("ben") || branchLan.equalsIgnoreCase("wb")) {
						
						if(branchLan.equalsIgnoreCase("ori")|| branchLan.equalsIgnoreCase("odi")){
							branchLan = "odi";
						}
						if(branchLan.equalsIgnoreCase("wb") || branchLan.equalsIgnoreCase("ben")) {
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
				// Sonar Issue fixed
				if (dbKitReport == null || !dbKitReport.exists()) {
				    logger.error("DB Kit report file is null or does not exist. applicationId: {}, reportName: {}", applicationId, DBKitReport);
				}
					try {
						byte[] dbKitReporthtmlBytes = Files.readAllBytes(dbKitReport.toPath());
						try {
							String dbKitReportbase64Html = java.util.Base64.getEncoder().encodeToString(dbKitReporthtmlBytes);
							String dbKitReporthtmlContent = new String(java.util.Base64.getDecoder().decode(dbKitReportbase64Html), "UTF-8");
							dbKitReporthtmlContent = dbKitReporthtmlContent.replaceAll("file:/tmp","https://www.creditaccessgrameen.in/wp-content/themes/creditaccessgrameen/assets//images/logo.png?");
							// String mergedContent =mergeReports(dbKitReporthtmlContent,"",");
							    
								//String mergedContent = sanctionReporthtmlContent+"<br/><br/>"+factSheethtmlContent+"<br/><br/>"+loanApplicationhtmlContent;
								//logger.debug("Final String "+ mergedContent);
								
								File megeFile=new File("mergerDBKIT.html");
								Files.write(Paths.get("mergerDBKIT.html"), dbKitReporthtmlContent.getBytes());
								
								logger.debug("file Merger Successfully");
								return Files.readAllBytes(megeFile.toPath());
								
							
//							 String loanApplicationFormbase64Html = java.util.Base64.getEncoder().encodeToString(loanApplicationFormhtmlBytes,"UTF-8");
//							 String loanApplicationFormhtmlContent = new String(java.util.Base64.getDecoder().decode(base64Html), "UTF-8");

							/*
							ConverterProperties convertPro = new ConverterProperties();
							FontProvider fontProvider = new FontProvider();
							convertPro.setCharset("UTF-8");
							// fontProvider.addStandardPdfFonts();
							String reportTemplateFile = CommonUtils.getExternalProperties("reportsTemplateDir");
							
							logger.debug("report Path is"+reportTemplateFile );
							try {
								if (DBKitReport.contains("_kan")){
									fontProvider.addFont(reportTemplateFile+"NotoSansKannada-Regular.ttf");
									
									logger.debug("font Path is"+reportTemplateFile+"NotoSansKannada-Regular.ttf" );
									logger.debug("Got Kannada Font");

								} else if (DBKitReport.contains("_mar") ){
									fontProvider.addFont(reportTemplateFile+"NotoSansDevanagari-Regular.ttf");
								}
							} catch (Exception e) {
								logger.error("Error in setting font:" + e);
							}

							// fontProvider.addSystemFonts();
							fontProvider.addStandardPdfFonts();
			                fontProvider.addSystemFonts();
							File dbKitReporthtmlContentpdf = new File("report1.pdf");
							File dbKitCustomerAssessmentFormhtmlContentpdf = new File("report2.pdf");
							convertPro.setFontProvider(fontProvider);
							HtmlConverter.convertToPdf(dbKitReporthtmlContent, new FileOutputStream(dbKitReporthtmlContentpdf), convertPro);

							List<File> mergedPdfFiles = new ArrayList<>();
							mergedPdfFiles.add(dbKitReporthtmlContentpdf);
							mergedPdfFiles.add(dbKitCustomerAssessmentFormhtmlContentpdf);
							return mergePdfFiles(mergedPdfFiles);
							*/
							
							
						} catch (IOException e) {
							//e.printStackTrace();
							logger.error("Exception occurred", e);
						}
						return dbKitReporthtmlBytes;
					} catch (Exception e) {
						//e.printStackTrace();
						logger.error("Exception occurred", e);
						return null;
					}

		} catch (Exception e) {
			logger.error("Error occurred while merging pdf file {} ", e);
			return null;
		}

	}

	private HashMap<String, String> getLoanApplicationParams(String applicationId) {
		HashMap<String, String> loanApplicationParams = new HashMap<String, String>();
		List<String> keyList = Arrays.asList(new String[] { "memberId", "memberName", "phoneno", "branchName",
				"kendraName", "kendraId", "date", "productName", "Lnpurpose", "loanAmount", "paymentFreq",
				"memInsurance", "spouseInsurance", "kmName", "kmId", "bmName", "bmId" });
		List<ApplicationMaster> applicationMasterList = applicationMasterRepo.findByApplicationId(applicationId);
		try {
			logger.debug("Loan Application starts for the application:" + applicationId);
			if (applicationMasterList != null && applicationMasterList.size() > 0) {
				ApplicationMaster appMaster = applicationMasterList.get(0);
				FetchAppRequest appReq = new FetchAppRequest();
				appReq.setAppId(appMaster.getAppId());
				FetchAppRequestFields fields = new FetchAppRequestFields();
				fields.setApplicationId(applicationId);
				fields.setVersionNum(Integer.parseInt(appMaster.getVersionNum()));
				appReq.setRequestObj(fields);
				ApplyLoanRequestFields loanFields = loanService.getCustomerData(applicationMasterList, appReq);
				if (loanFields != null && loanFields.getApplicationdtls() != null
						&& loanFields.getApplicationdtls().size() > 0) {
					ApplicationDtls appDtl = loanFields.getApplicationdtls().get(0);
					CustomerDtls customerDtl = appDtl.getCustomerDetails();

					if (customerDtl != null) {

						LoanDtls loanDtl = customerDtl.getLoanDtls();

						loanApplicationParams.put("memberId", appMaster.getCustomerId());
						loanApplicationParams.put("memberName", appMaster.getCustomerName());
						loanApplicationParams.put("phoneno", customerDtl.getMobileNum());
						String addInfo = appMaster.getAddInfo();
						JSONObject addInfoObj = new JSONObject(addInfo);
						String branchName = "";
						if (addInfoObj.has("branchName")) {
							branchName = addInfoObj.getString("branchName");
						}
						loanApplicationParams.put("branchName", branchName);
						loanApplicationParams.put("kendraName", appMaster.getKendraName());
						loanApplicationParams.put("kendraId", appMaster.getKendraId());
						Optional<ServerDate> serverDate = dateRepository.findById(1);
						// copied from DBkit, Assuming the ID of the row is 1
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
						logger.debug("Printing date:" + dt);
						loanApplicationParams.put("date", dt);
						loanApplicationParams.put("productName",loanDtl.getProduct());
						Object purpose = loanDtl.getPurpose();
						JSONObject jsonObj = new JSONObject(new Gson().toJson(purpose));
						if (jsonObj.has("purposeDesc")) {
							Object obj = jsonObj.get("purposeDesc");

							loanApplicationParams.put("Lnpurpose", obj.toString());
						}
						String loanAmount = "Rs."+appMaster.getAmount().toString();
						loanApplicationParams.put("loanAmount", loanAmount);
						HashMap<String, String> freqObj = (HashMap<String, String>) loanDtl.getRepayFrequency();
						loanApplicationParams.put("paymentFreq", freqObj.getOrDefault("idDesc", "WEEKLY"));
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
						loanApplicationParams.put("memInsurance", memInsurance);
						loanApplicationParams.put("spouseInsurance", spouseIns);
						Optional<User> opuserRecords = userRepository.findByUserId(appMaster.getKmId().toString());
						String userName = "";
						if (opuserRecords.isPresent()) {
							User userRecords = opuserRecords.get();
							userName = userRecords.getUserName();
						}
						loanApplicationParams.put("kmName", userName);
						loanApplicationParams.put("kmId", appMaster.getKmId());
						String gkId = "", bmName = "";
						Optional<ApplicationWorkflow> opApplnWfRec = applicationWorkflowRepository
								.findCreatedByUsingApplicationIdAndApplicationStatus(applicationId);
						logger.debug("ApplicationWorkflow for loanAppliction to get CreatedBy:" + opApplnWfRec);
						if (opApplnWfRec.isPresent()) {
							ApplicationWorkflow applicationWorkflowRec = opApplnWfRec.get();
							String createdBy = applicationWorkflowRec.getCreatedBy();
							Optional<TbAsmiUser> opAsmiUserRec = tbAsmiUserRepo.findBmNameAndGkId(createdBy);
							if (opAsmiUserRec.isPresent()) {
								TbAsmiUser tbAsmiUserRec = opAsmiUserRec.get();
								bmName = tbAsmiUserRec.getUserName();
								gkId = tbAsmiUserRec.getUserId();
							}
						}
						loanApplicationParams.put("bmName", bmName);
						
						if (!"".equals(gkId)) {
						loanApplicationParams.put("bmId", gkId);
						}else {
							loanApplicationParams.put("bmId", "");
						}		
					}
				}
			}
		} catch (Exception e) {
			logger.error("Error occurred while fetching params :: " + e);
		}
		for (String key : keyList) {
			if (!(loanApplicationParams.containsKey(key) && StringUtils.isNotEmpty(loanApplicationParams.get(key)))) {
				loanApplicationParams.put(key, "");
			}
		}
		return loanApplicationParams;
	}

	private HashMap<String, String> getDBKitParameters(String applicationId) {
		HashMap<String, String> sanctionReportParams = new HashMap<String, String>();
		List<String> keyList = Arrays.asList(new String[] {"ewi","insurancePremium","processingFee","bankName","ifscCode","bankAccNo","apr","pdtName", "bmId", "kmName", "kmId", "spousekycType", "spousekycId",
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
		List<ApplicationMaster> applicationMasterList = applicationMasterRepo.findByApplicationId(applicationId);
		try {
			if (applicationMasterList != null && applicationMasterList.size() > 0) {
				ApplicationMaster appMaster = applicationMasterList.get(0);
				FetchAppRequest appReq = new FetchAppRequest();
				appReq.setAppId(appMaster.getAppId());
				FetchAppRequestFields fields = new FetchAppRequestFields();
				fields.setApplicationId(applicationId);
				fields.setVersionNum(Integer.parseInt(appMaster.getVersionNum()));
				appReq.setRequestObj(fields);
				ApplyLoanRequestFields loanFields = loanService.getCustomerData(applicationMasterList, appReq);
				if (loanFields != null && loanFields.getApplicationdtls() != null
						&& loanFields.getApplicationdtls().size() > 0) {
					ApplicationDtls appDtl = loanFields.getApplicationdtls().get(0);
					CustomerDtls customerDtl = appDtl.getCustomerDetails();
					if (customerDtl != null) {
						LoanDtls loanDtl = customerDtl.getLoanDtls();
						List<Object> activeLoanDtls = loanDtl.getActiveLoanDtls();
						if(!activeLoanDtls.isEmpty()) {
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
						}else {
							logger.debug("Empty ActiveLoanDtls"+activeLoanDtls);
						}
						ChargeAndBreakupDetails chargeAndBreakupDtls = loanDtl.getChargeAndBreakupDtls();
						String aprxLoanCharges = chargeAndBreakupDtls.getAprxLoanCharges();
						sanctionReportParams.put("processingFee", aprxLoanCharges);
						
						InsuranceDetails insurDtls = loanDtl.getInsurDtls();
						String insurCharges = insurDtls.getInsurCharges();
						sanctionReportParams.put("insurancePremium", insurCharges);
						
//						String installment = "";
//						Optional<SanctionRepaymentSchedule> opSanctRepaySchedule = sanctionRepaymentScheduleRepository.findTopByApplicationIdOrderByCreateTsDesc(applicationId);
//						logger.debug("Printing opSanctRepaySchedule:"+opSanctRepaySchedule);
//						if(opSanctRepaySchedule.isPresent()) {
//							SanctionRepaymentSchedule sanctionRepaymentSchedule = opSanctRepaySchedule.get();
//							logger.debug("sanctionRepaymentSchedule is present :"+sanctionRepaymentSchedule);
//							installment = sanctionRepaymentSchedule.getInstallment();
//							sanctionReportParams.put("ewi", installment);
//						}else {
//							sanctionReportParams.put("ewi", "0");
//						}
						
						
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
						sanctionReportParams.put("bankAccNo",customerDtl.getBankDtls().getBankAccNo());	
						sanctionReportParams.put("ifscCode",customerDtl.getBankDtls().getBankIfscCode());	
						sanctionReportParams.put("bankName",customerDtl.getBankDtls().getBankName() );
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
//						sanctionReportParams.put("paymentFreq", loanDtl.getTerm());
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
							sanctionReportParams.put("spouseDOB",dte );
							
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
							Optional<TbUaobIncomeAssessment> opIncomeAssessmentRecords = tbUaobIncomeAssessmentRepository
									.findByApplicationId(applicationId);
							if (opIncomeAssessmentRecords.isPresent()) {
								TbUaobIncomeAssessment tbUaobIncomeAssessment = opIncomeAssessmentRecords.get();
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
						Optional<TbUaobCbResponse> cbRes = cbResRepository
								.findByAppIdAndApplicationIdOrderByVersionNumDesc(appReq.getAppId(), applicationId);
						if (cbRes.isPresent()) {
							logger.debug("cbRes present:");
							TbUaobCbResponse cbResponse = cbRes.get();
							String payload = cbResponse.getResPayload();
							JSONObject paylonJsonObj = new JSONObject(payload);
							String APRMaster = appMaster.getAddInfo();
							logger.debug("Printing AddInfo val:"+APRMaster);
							JSONObject aprJsonObj = new JSONObject(APRMaster);
							logger.debug("Printing aprJsonObj val:"+aprJsonObj);
							if (aprJsonObj.has("APR") && !aprJsonObj.isEmpty()) {
								logger.debug("aprJsonObj has APR and aprJsonObj is not empty");
								String aprVal = aprJsonObj.getString("APR");
								if(!aprVal.isEmpty()){
									logger.debug("aprVal is not empty");
									String apr = "" + aprJsonObj.getString("APR") + "%";
									sanctionReportParams.put("apr", apr);
								} else {
									logger.debug("aprVal is empty");
									if (paylonJsonObj.has("eir")) {
										String APR = paylonJsonObj.getString("eir");
										logger.debug("Printing APR from paylonJsonObj:"+APR);
										sanctionReportParams.put("apr", "" + APR + "%");
									}
								}
							} else {
								logger.debug("aprJsonObj does not has APR aprJsonObj is empty");
								if (paylonJsonObj.has("eir")) {
									String APR = paylonJsonObj.getString("eir");
									logger.debug("Printing APR from paylonJsonObj:"+APR);
									sanctionReportParams.put("apr", "" + APR);
								}
							}
						}
						sanctionReportParams.put("tenure", loanDtl.getTerm());
//						String term = "";
//						if (loanDtl.getTerm() != null) {
//							term = loanDtl.getTerm().replaceAll("[^\\d]", "");
//						}
//						sanctionReportParams.put("term", term);
						
		
						HashMap<String, String> freqObj = (HashMap<String, String>) loanDtl.getRepayFrequency();
						sanctionReportParams.put("paymentFreq", freqObj.getOrDefault("idDesc", "WEEKLY"));
						
						String termVal = loanDtl.getTerm() == null ? "" : loanDtl.getTerm().replaceAll("[^0-9]", " ");
//						sanctionReportParams.put("tenure", termVal);
						String frequency = freqObj.getOrDefault("idDesc", "WEEKLY");
						if ("WEEKLY".equalsIgnoreCase(frequency)) {
							String instType = loanDtl.getTerm();
							String mod=instType.replace("W", "");
							sanctionReportParams.put("term", mod);
						} else if ("BIWEEKLY".equalsIgnoreCase(frequency) || "BI-WEEKLY".equalsIgnoreCase(frequency) ) {
							String instType = loanDtl.getTerm();
							String mod=instType.replace("W", "");
							int epi = Integer.parseInt(mod);
							Integer paymentFreq = epi / 2;
							sanctionReportParams.put("term", paymentFreq.toString());
						} else if ("FOURWEEKLY".equalsIgnoreCase(frequency) || "FOUR-WEEKLY".equalsIgnoreCase(frequency)) {
							String instType = loanDtl.getTerm();
							String mod=instType.replace("W", "");
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
					Optional<ApplicationWorkflow> opApplnWfRec = applicationWorkflowRepository
							.findCreatedByUsingApplicationIdAndApplicationStatus(applicationId);
					logger.debug("ApplicationWorkflow rec to get CreatedBy:" + opApplnWfRec);
					if (opApplnWfRec.isPresent()) {
						logger.debug("ApplicationWorkflow rec Present");
						ApplicationWorkflow applicationWorkflowRec = opApplnWfRec.get();
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
				Optional<TbUaobCbResponse> cbRes = cbResRepository
						.findByAppIdAndApplicationIdOrderByVersionNumDesc(appReq.getAppId(), applicationId);
				logger.debug("Printing cbRes 2nd:" + cbRes);
				if (cbRes.isPresent()) {
					logger.debug("cbRes is present" + cbRes.get());
					TbUaobCbResponse cbResponse = cbRes.get();
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

	public byte[] mergePdfFiles(List<File> pdfFiles) throws IOException {
		PDFMergerUtility pdfMerger = new PDFMergerUtility();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		for (File pdf : pdfFiles) {
			pdfMerger.addSource(pdf);
		}
		pdfMerger.setDestinationStream(outputStream);
		pdfMerger.mergeDocuments(null);
		return outputStream.toByteArray();
	}

	@SuppressWarnings("unchecked")
	private File generateBirReport(String applicationId, String reportType, HashMap<String, String> params,
			String format) {
		String reportOutputFilePath = CommonUtils.getExternalProperties("reportsOutputDir");
		String reportTemplateFile = CommonUtils.getExternalProperties("reportsTemplateDir");
		String reportName = reportType;
		Path outputFilePath = Paths.get(reportOutputFilePath, applicationId, reportName + "." + format);
		File finalReportFile = null;

//		Check if directory existOrNot then create document based on needs
		if (Files.exists(outputFilePath)) {
			finalReportFile = outputFilePath.toFile();
		}
		try {
			Files.createDirectories(Paths.get(reportOutputFilePath, applicationId));
		} catch (IOException e) {
			logger.error(e);
		}
		System.setProperty("org.eclipse.birt.report.engine.font.config", CommonUtils.getExternalProperties("reportsTemplateDir")+"fontConfig.xml");
		String fontConfigPath = System.getProperty("org.eclipse.birt.report.engine.font.config");
		logger.debug("Font Path is "+ fontConfigPath);
		intializeBirtEngine();
		IRunAndRenderTask runAndRender = null;
		try {
			IReportRunnable reportRunnable = reportEngine
					.openReportDesign(reportTemplateFile + reportName + ".rptdesign");
			runAndRender = reportEngine.createRunAndRenderTask(reportRunnable);

			runAndRender.setParameterValues(params);
			 runAndRender.getAppContext().put("Locale", new Locale("kn","IN"));

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
			exp.printStackTrace();
		}
		return finalReportFile;
	}

	public HashMap<String, String> getParametersOfSummaryMonthlyReport() {

		HashMap<String, String> parameters = new HashMap<String, String>();
		return parameters;

	}
	
	
//	public static void main(String args[]) throws URISyntaxException
//	{
//		
//		String reportTemplateFile = "/app/reports/";
//		
//		String SanctionReport= "SanctionReport_kan";
//		String loanApplicationForm_kan="LoanApplicationForm_kan";
//		String GKFactReport = "GKFactReport_kan";
//		
//		 logger.debug( SanctionReport +":"+GKFactReport);
//			
//				if (SanctionReport.contains("_kan") || GKFactReport.contains("_kan")|| loanApplicationForm_kan.contains("_kan"))
//						{
//						logger.debug("font path is "+ reportTemplateFile+"NotoSansKannada-Regular.ttf");
//						
//						logger.debug("Got Kannada Font");
//				} else if (SanctionReport.contains("_mar") || GKFactReport.contains("_mar")||loanApplicationForm_kan.contains("_mar")) {
//					
//				}
//				else 
//				{
//					
//				}
//			}
	
	
	
	// Generate passsBook code start
	
	public byte[] generateAndDownloadPassBookReport(String applicationId) {
		try {
			HashMap<String, String> sanctionReportParams = getSanctionReportParameters(applicationId);
			//HashMap<String, String> loanApplicationParams = getLoanApplicationParams(applicationId);
			String branchCode = sanctionReportParams.get("branchCode");
			String branchLan = tbOfficeDataRepository.findBranchLan(branchCode);
			logger.debug("Printing branchLan:" + branchLan);
			//String branchLan = "";
			String SanctionReport = "SanctionReport";
			String LoanApplicationForm="LoanApplicationForm";
			String GKFactReport = "GKFactReport";
			
			String format = "html";
			if (!StringUtils.isBlank(branchLan)) {
				branchLan = branchLan.toLowerCase();
				if (!branchLan.isBlank()) {
					if (branchLan.equalsIgnoreCase("kan") || branchLan.equalsIgnoreCase("mar")
							|| branchLan.equalsIgnoreCase("tam") || branchLan.equalsIgnoreCase("tel")
							|| branchLan.equalsIgnoreCase("hin") || branchLan.equalsIgnoreCase("mal")
							|| branchLan.equalsIgnoreCase("odi") || branchLan.equalsIgnoreCase("guj") || branchLan.equalsIgnoreCase("ori")
							|| branchLan.equalsIgnoreCase("ben") || branchLan.equalsIgnoreCase("wb")) {
						// this is temp code need to remove later 
						
						if(branchLan.equalsIgnoreCase("ori")|| branchLan.equalsIgnoreCase("odi")){
							branchLan = "odi";
						}
						// this is temp code need to remove later 
						if(branchLan.equalsIgnoreCase("wb") || branchLan.equalsIgnoreCase("ben")) {
							branchLan = "ben";
						}
						SanctionReport = SanctionReport + "_" + branchLan;
						GKFactReport = GKFactReport + "_" + branchLan;
						LoanApplicationForm = LoanApplicationForm + "_" + branchLan;
						format = "html";
						logger.debug("format is:" + format);
					}
				}
			}

			logger.debug("sanctionReportParams : {}", sanctionReportParams);

			//File sanctionReport = generateBirReport(applicationId, SanctionReport, sanctionReportParams, format);
			//File loanApplicationForm=generateBirReport(applicationId, LoanApplicationForm, loanApplicationParams,format);
			
			HashMap<String, String> factReportParams = getGkFactReportParameters(applicationId);
			logger.debug("factReportParams : {}", factReportParams);
			if (factReportParams.containsKey("pdtName") && (factReportParams.containsValue("GL.EMERG.LOAN")
					|| factReportParams.containsValue("GL.EMERGENCY.LN"))) {
				logger.debug("merging reports for EL");
				File factSheet = generateBirReport(applicationId, GKFactReport, factReportParams, format);
				logger.debug("report language is not english");
				if (factSheet != null && factSheet.exists()) {
					try {
						byte[] factSheethtmlBytes = Files.readAllBytes(factSheet.toPath());
						try {
							String factSheetbase64Html = java.util.Base64.getEncoder()
									.encodeToString(factSheethtmlBytes);
							String factSheethtmlContent = new String(
									java.util.Base64.getDecoder().decode(factSheetbase64Html), "UTF-8");
							String mergeForEL = mergeReportsForEL(factSheethtmlContent);
							File megeFile = new File("merger.html");
							Files.write(Paths.get("merger.html"), mergeForEL.getBytes());
							logger.debug("file Merger for RL");
							return Files.readAllBytes(megeFile.toPath());
						} catch (IOException e) {
							//e.printStackTrace();
							logger.error("Exception occurred", e);
						}
						return null;
					} catch (Exception e) {
						//e.printStackTrace();
						logger.error("Exception occurred", e);
						return null;
					}
				} else {
					return null;
				}
			} else {
				File factSheet = generateBirReport(applicationId, GKFactReport, factReportParams, format);

				boolean isOtherThanEnglish = SanctionReport.contains("_");
				// if (isOtherThanEnglish) {
				logger.debug("report language is not english");
				if (factSheet != null && factSheet.exists()) {
					try {
						//byte[] sanctionReporthtmlBytes = Files.readAllBytes(sanctionReport.toPath());
						byte[] factSheethtmlBytes = Files.readAllBytes(factSheet.toPath());
						//byte[] loanApplicationHtmlBytes = Files.readAllBytes(loanApplicationForm.toPath());
						try {
//							String sanctionReportbase64Html = java.util.Base64.getEncoder()
//									.encodeToString(sanctionReporthtmlBytes);
//							String sanctionReporthtmlContent = new String(
//									java.util.Base64.getDecoder().decode(sanctionReportbase64Html), "UTF-8");
//							logger.debug("sanctionReporthtmlContent" + sanctionReporthtmlContent);
							String factSheetbase64Html = java.util.Base64.getEncoder()
									.encodeToString(factSheethtmlBytes);
							String factSheethtmlContent = new String(
									java.util.Base64.getDecoder().decode(factSheetbase64Html), "UTF-8");
							// logger.debug("factSheethtmlContent"+ factSheethtmlContent);
//							String loanApplicationbase64Html = java.util.Base64.getEncoder()
//									.encodeToString(loanApplicationHtmlBytes);
//							String loanApplicationhtmlContent = new String(
//									java.util.Base64.getDecoder().decode(loanApplicationbase64Html), "UTF-8");

							/*
							 * ConverterProperties convertPro = new ConverterProperties(); FontProvider
							 * fontProvider = new FontProvider(); convertPro.setCharset("UTF-8"); String
							 * reportTemplateFile = CommonUtils.getExternalProperties("reportsTemplateDir");
							 * logger.debug("report path is "+ reportTemplateFile); logger.debug(
							 * SanctionReport +":"+GKFactReport); try { if (SanctionReport.contains("_kan")
							 * || GKFactReport.contains("_kan")) { logger.debug("font path is "+
							 * reportTemplateFile+"Tunga.ttf");
							 * fontProvider.addFont(reportTemplateFile+"Tunga.ttf");
							 * logger.debug("Got Kannada Font"); } else if (SanctionReport.contains("_mar")
							 * || GKFactReport.contains("_mar")) {
							 * fontProvider.addFont(reportTemplateFile+"NotoSansDevanagari-Regular.ttf"); }
							 * } catch (Exception e) { logger.error("Error in setting font:" + e); }
							 * 
							 * //fontProvider.addSystemFonts(); fontProvider.addStandardPdfFonts();
							 * logger.debug("added standardfont"+ fontProvider.addStandardPdfFonts());
							 * fontProvider.addSystemFonts(); logger.debug("added systemfont"+
							 * fontProvider.addSystemFonts()); File sanctionReportpdfFile = new
							 * File("report1.pdf"); File factSheetpdfFile = new File("report2.pdf"); File
							 * loanApplnpdfFile=new File("report3.pdf");
							 * convertPro.setFontProvider(fontProvider);
							 * HtmlConverter.convertToPdf(sanctionReporthtmlContent, new
							 * FileOutputStream(sanctionReportpdfFile), convertPro);
							 * HtmlConverter.convertToPdf(factSheethtmlContent, new
							 * FileOutputStream(factSheetpdfFile), convertPro);
							 * HtmlConverter.convertToPdf(loanApplicationhtmlContent, new
							 * FileOutputStream(loanApplnpdfFile), convertPro);
							 * 
							 */

							// String report1Content = "<div class='report1'>" + sanctionReporthtmlContent +
							// "</div>";
							// String report2Content = "<div class='report2'>" + loanApplicationhtmlContent
							// + "</div>";
							// String report3Content = "<div class='report2'>" + factSheethtmlContent +
							// "</div>";
							// String mergedContent = report1Content + "<br/><br/>" + report2Content
							// +"<br/><br/>"+report3Content ;

							String mergedContent = mergeReportsForEL(factSheethtmlContent);
							// String mergedContent =
							// sanctionReporthtmlContent+"<br/><br/>"+factSheethtmlContent+"<br/><br/>"+loanApplicationhtmlContent;
							// logger.debug("Final String "+ mergedContent);

							File megeFile = new File("merger.html");
							Files.write(Paths.get("merger.html"), mergedContent.getBytes());

							logger.debug("file Merger Successfully");
							return Files.readAllBytes(megeFile.toPath());
							/*
							 * List<File> mergedPdfFiles = new ArrayList<>();
							 * mergedPdfFiles.add(sanctionReportpdfFile);
							 * mergedPdfFiles.add(loanApplnpdfFile); mergedPdfFiles.add(factSheetpdfFile);
							 * return mergePdfFiles(mergedPdfFiles);
							 */
						} catch (IOException e) {
							//e.printStackTrace();
							logger.error("Exception occurred", e);
						}
						return null;
					} catch (Exception e) {
						//e.printStackTrace();
						logger.error("Exception occurred", e);
						return null;
					}
				} else {
					return null;
				}
			}
		//	} 
		//	else {
			//	logger.debug("report language is english");
			//	logger.debug("Merging pdf");
			//	List<File> mergedPdfFiles = new ArrayList<>();
			//	mergedPdfFiles.add(sanctionReport);
			//	mergedPdfFiles.add(loanApplicationForm);
			//	mergedPdfFiles.add(factSheet);
			//	return mergePdfFiles(mergedPdfFiles);
			//}

		} catch (Exception e) {
			logger.error("Error occurred while merging pdf file {} ", e);
			return null;
		}
	}
	
	
	public byte[] generateAndDownloadDPassBookReport(String memberId, String memberName, String spouseName,
			String kendraName, String loanAmount, String productName, String moratorium, String roi, String loanPurpose,
			String disbursementDate, String termofLoan, String processingFee, String insuranceforMemeber,
			String insuranceSpouse, String phone, String fetchLoanIdfromAuditable, String type, int scheduleCount) {
		logger.debug("Generate passbook report loan id " + fetchLoanIdfromAuditable);
		try {
			HashMap<String, String> sanctionReportParams = new HashMap<String, String>();
			List<String> keyList = Arrays.asList(new String[] { "memberId", "kendraName", "pdtName", "loanPurpose",
					"memberName", "loanAccountNum", "moratorium", "disbursementDate", "spouseName", "loanAmt", "roi",
					"termOfLoan", "processingFee", "insuranceforMemeber", "insuranceSpouse", "phone", });
			sanctionReportParams.put("memberId", memberId);
			sanctionReportParams.put("kendraName", kendraName);
			sanctionReportParams.put("loanAmount", loanAmount);
			sanctionReportParams.put("productName", productName);
			sanctionReportParams.put("pdtName", productName);
			sanctionReportParams.put("loanPurpose", loanPurpose);
			sanctionReportParams.put("disbursementDate", disbursementDate);
			sanctionReportParams.put("roi", roi);
			sanctionReportParams.put("processingFee", processingFee);
			sanctionReportParams.put("phone", phone);
			sanctionReportParams.put("memberName", memberName);
			sanctionReportParams.put("moratorium", moratorium);
			sanctionReportParams.put("spouseName", spouseName);
			sanctionReportParams.put("loanAmt", loanAmount);
			sanctionReportParams.put("termOfLoan", termofLoan);
			sanctionReportParams.put("insuranceforMemeber", insuranceforMemeber);
			sanctionReportParams.put("insuranceSpouse", insuranceSpouse);
			sanctionReportParams.put("loanAccountNum", fetchLoanIdfromAuditable);
			sanctionReportParams.put("sanctId", fetchLoanIdfromAuditable);

			for (String key : keyList) {
				if (!(sanctionReportParams.containsKey(key) && StringUtils.isNotBlank(sanctionReportParams.get(key)))) {
					sanctionReportParams.put(key, "");
				}
			}
			logger.debug("sanctionReportParams " + sanctionReportParams);
			String GKFactReport = "GeneratePassBookReport";
			String GKPassBookReport2 = "GeneratePassBookReportMore104";
			logger.debug("type " + type);
			String format = "pdf";
			if ("view".equalsIgnoreCase(type)) {
				format = "html";
			}
			File passbookreportReport = generateBirReportforpassbook(GKFactReport, sanctionReportParams, format);
			File passbookreportReport2 = null;
			if (scheduleCount > 104) {
				passbookreportReport2 = generateBirReportforpassbook(GKPassBookReport2, sanctionReportParams, format);
			}
			logger.debug("dbKitReport " + passbookreportReport);
			if (passbookreportReport == null || !passbookreportReport.exists()) {
				logger.error("Report generation failed or report file does not exist for applicationId: {}");
				return null;
			}
			logger.debug("Reading report bytes from file: {}", passbookreportReport.getAbsolutePath());
			// Handling for HTML type of report.
			if (format.equalsIgnoreCase("html")) {
				if (passbookreportReport2 != null) {
					byte[] report1Bytes = Files.readAllBytes(passbookreportReport.toPath());
					byte[] report2Bytes = Files.readAllBytes(passbookreportReport2.toPath());
					ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
					outputStream.write(report1Bytes);
					outputStream.write(report2Bytes);
					return outputStream.toByteArray();
				} else {
					return Files.readAllBytes(passbookreportReport.toPath());
				}
			} else if (format.equalsIgnoreCase("pdf")) {
				if (passbookreportReport2 != null) {
					PDFMergerUtility merger = new PDFMergerUtility();
					ByteArrayOutputStream mergedOutput = new ByteArrayOutputStream();
					merger.addSource(passbookreportReport);
					merger.addSource(passbookreportReport2);
					merger.setDestinationStream(mergedOutput);
					merger.mergeDocuments(null);
					logger.debug("Merged report created.");
					return mergedOutput.toByteArray();
				} else {
					return Files.readAllBytes(passbookreportReport.toPath());
				}
			}
			return Files.readAllBytes(passbookreportReport.toPath());
		} catch (IOException e) {
			logger.error("IO error during passbook generation", e);
		} catch (Exception e) {
			logger.error("Unexpected error during passbook generation", e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	private File generateBirReportforpassbook(String reportType, HashMap<String, String> sanctionReportParams,
			String format) {
		String reportOutputFilePath = CommonUtils.getExternalProperties("reportsOutputDir");
		String reportTemplateFile = CommonUtils.getExternalProperties("reportsTemplateDir");
		String reportName = reportType;
		Path outputFilePath = Paths.get(reportOutputFilePath, reportName + "." + format);
		File finalReportFile = null;
		logger.debug("file path is" + outputFilePath);
		try {
			Files.createDirectories(Paths.get(reportOutputFilePath));
		} catch (IOException e) {
			logger.error("Error creating directories for report output: ", e);
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
			runAndRender.setParameterValues(sanctionReportParams);
			runAndRender.getAppContext().put("Locale", Locale.ENGLISH);
			RenderOption options = new RenderOption();
			options.setOutputFormat(format);
			options.setOutputFileName(outputFilePath.toString());
			runAndRender.setRenderOption(options);
			runAndRender.getAppContext().put("OdaJDBCDriverPassInConnection", dataSource.getConnection());
			runAndRender.run();
			finalReportFile = new File(outputFilePath.toString());
			logger.debug("Output file path: {}", finalReportFile.getPath());
		} catch (Exception exp) {
			logger.error("Error during report generation: ", exp);
			logger.debug("file path error is", exp);
		}
		logger.debug("finalReportFile {}", finalReportFile);
		return finalReportFile;
	}

}
