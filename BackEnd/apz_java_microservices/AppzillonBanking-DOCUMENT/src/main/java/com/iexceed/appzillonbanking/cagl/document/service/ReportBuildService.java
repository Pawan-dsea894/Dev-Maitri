package com.iexceed.appzillonbanking.cagl.document.service;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cagl.document.domain.ab.ApplicationMasterHis;
import com.iexceed.appzillonbanking.cagl.document.domain.ab.ApplicationWorkflowHis;
import com.iexceed.appzillonbanking.cagl.document.domain.ab.SanctionRepaymentSchedule;
import com.iexceed.appzillonbanking.cagl.document.domain.ab.ServerDate;
import com.iexceed.appzillonbanking.cagl.document.domain.ab.TbAsmiUser;
import com.iexceed.appzillonbanking.cagl.document.domain.ab.TbUaobApiAuditLogsHis;
import com.iexceed.appzillonbanking.cagl.document.domain.ab.TbUaobCbResponseHis;
import com.iexceed.appzillonbanking.cagl.document.domain.ab.TbUaobIncomeAssessmentHis;
import com.iexceed.appzillonbanking.cagl.document.domain.apz.User;
import com.iexceed.appzillonbanking.cagl.document.payload.AddressDtls;
import com.iexceed.appzillonbanking.cagl.document.payload.ApplicationDtls;
import com.iexceed.appzillonbanking.cagl.document.payload.ApplyLoanRequestHisFields;
import com.iexceed.appzillonbanking.cagl.document.payload.ChargeAndBreakupDetails;
import com.iexceed.appzillonbanking.cagl.document.payload.CustomerDtls;
import com.iexceed.appzillonbanking.cagl.document.payload.Earnings;
import com.iexceed.appzillonbanking.cagl.document.payload.FetchAppRequest;
import com.iexceed.appzillonbanking.cagl.document.payload.FetchAppRequestFields;
import com.iexceed.appzillonbanking.cagl.document.payload.InsuranceDetails;
import com.iexceed.appzillonbanking.cagl.document.payload.LoanDtls;
import com.iexceed.appzillonbanking.cagl.document.repository.ab.ApplicationMasterHisRepository;
import com.iexceed.appzillonbanking.cagl.document.repository.ab.ApplicationWorkflowHisRepository;
import com.iexceed.appzillonbanking.cagl.document.repository.ab.SanctionRepaymentScheduleRepository;
import com.iexceed.appzillonbanking.cagl.document.repository.ab.T24ServerDateRepository;
import com.iexceed.appzillonbanking.cagl.document.repository.ab.TbAsmiUserRepo;
import com.iexceed.appzillonbanking.cagl.document.repository.ab.TbOfficeDataRepository;
import com.iexceed.appzillonbanking.cagl.document.repository.ab.TbUaobApiAuditLogsHisRepository;
import com.iexceed.appzillonbanking.cagl.document.repository.ab.TbUaobCbResponseHisRepository;
import com.iexceed.appzillonbanking.cagl.document.repository.ab.TbUaobIncomeAssessmentHisRepository;
import com.iexceed.appzillonbanking.cagl.document.repository.apz.UserRepository;
//import com.iexceed.appzillonbanking.cagl.document.domain.ab.TbUaobApiAuditLogsHis;
//import com.iexceed.appzillonbanking.cagl.document.repository.ab.TbUaobApiAuditLogsHisRepository;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;

import io.micrometer.common.util.StringUtils;

@Service
public class ReportBuildService {

	private static final Logger logger = LogManager.getLogger(ReportBuildService.class);

	private static boolean birtEngineIntialized = false;
	private static ReportEngine reportEngine = null;
	private static EngineConfig engineConfig = null;
	
	private static final Pattern BODY_PATTERN = Pattern.compile("(?i)<body[^>]*>([^<]*(?:<(?!/body>)[^<]*)*)</body>");
	
	private static final Pattern STYLE_PATTERN = Pattern.compile("(?i)<style\\b[^>]*>([^<]*(?:<(?!/style>)[^<]*)*)</style>");


	@Autowired
	TbOfficeDataRepository tbOfficeDataRepository;

	@Autowired
	private TbUaobIncomeAssessmentHisRepository tbUaobIncomeAssessmentRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	TbUaobApiAuditLogsHisRepository auditApiRepo;

	@Autowired
	DataSource dataSource;

	@Autowired
	private T24ServerDateRepository dateRepository;

	@Autowired
	ApplicationMasterHisRepository applicationMasterRepo;
	@Autowired
	DocumentService1 documentService1;
	@Autowired
	UserRepository userRepo;
	@Autowired
	TbUaobCbResponseHisRepository cbResRepository;

	@Autowired
	SanctionRepaymentScheduleRepository sanctionRepaymentScheduleRepository;

	@Autowired
	ApplicationWorkflowHisRepository applicationWorkflowRepository;
	@Autowired
	private TbAsmiUserRepo tbAsmiUserRepo;

	// ==============generateAndDownloadSanctionReport===========
	public byte[] generateAndDownloadSanctionReport(String applicationId, String reportType) {
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

			logger.debug("came to check the Report Type: " + reportType);
			if (reportType.equalsIgnoreCase("mergeAll")) {
				/**
				 * Existing code Logic
				 */
				// Change to disable Loan Application 29Sep-2025-Satish
//					File loanApplicationForm = generateBirReport(applicationId, LoanApplicationForm, loanApplicationParams,
//							format);
				File sanctionReport = generateBirReport(applicationId, SanctionReport, sanctionReportParams, format);
				File factSheet = generateBirReport(applicationId, GKFactReport, factReportParams, format);

				boolean isOtherThanEnglish = SanctionReport.contains("_");
				logger.debug("report language is not english");

				if (factSheet != null && factSheet.exists()) {
					try {
						// Change to disable Loan Application 29Sep-2025-Satish
//							byte[] loanApplicationHtmlBytes = Files.readAllBytes(loanApplicationForm.toPath());
						byte[] sanctionReporthtmlBytes = Files.readAllBytes(sanctionReport.toPath());
						byte[] factSheethtmlBytes = Files.readAllBytes(factSheet.toPath());
						try {

							// Change to disable Loan Application 29Sep-2025-Satish
							// Loan Application
//								String loanApplicationbase64Html = java.util.Base64.getEncoder()
//										.encodeToString(loanApplicationHtmlBytes);
//								String loanApplicationhtmlContent = new String(
//										java.util.Base64.getDecoder().decode(loanApplicationbase64Html), "UTF-8");

							// Sanction Report
							String sanctionReportbase64Html = java.util.Base64.getEncoder()
									.encodeToString(sanctionReporthtmlBytes);
							String sanctionReporthtmlContent = new String(
									java.util.Base64.getDecoder().decode(sanctionReportbase64Html), "UTF-8");
							logger.debug("sanctionReporthtmlContent" + sanctionReporthtmlContent);

							// KFS Report
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
							// Change to disable Loan Application 29Sep-2025-Satish
							// String mergedContent =
							// mergeReports(loanApplicationhtmlContent,sanctionReporthtmlContent,factSheethtmlContent);
							String mergedContent = mergeReports(sanctionReporthtmlContent, factSheethtmlContent);
							// String mergedContent =
							// loanApplicationhtmlContent+"<br/><br/>"+sanctionReporthtmlContent+"<br/><br/>"+factSheethtmlContent;
							logger.debug("Final String " + mergedContent);

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
			logger.debug("Report Type is MergeAll ");

			/**
			 * @author Ankit.CAG
			 */
			File reportForm = null;
			if (reportType.equalsIgnoreCase("Loan")) {
				logger.debug("generateBirReport LoanApplicationForm");
				reportForm = generateBirReport(applicationId, LoanApplicationForm, loanApplicationParams, format);
				logger.debug("generateBirReport " + reportForm);
			}
			if (reportType.equalsIgnoreCase("sanction")) {
				logger.debug("generateBirReport SanctionReport");
				reportForm = generateBirReport(applicationId, SanctionReport, sanctionReportParams, format);
				logger.debug("generateBirReport " + reportForm);
			}
			if (reportType.equalsIgnoreCase("factReport")) {
				logger.debug("generateBirReport GKFactReport");
				reportForm = generateBirReport(applicationId, GKFactReport, factReportParams, format);
				logger.debug("generateBirReport " + reportForm);
			}

			try {
				byte[] reportFormHtmlBytes = Files.readAllBytes(reportForm.toPath());
				logger.debug("Files.readAllBytes reportFormHtmlBytes ");

				try {
					String reportFormHtml64Html = java.util.Base64.getEncoder().encodeToString(reportFormHtmlBytes);
					String reportFormHtmlContent = new String(
							java.util.Base64.getDecoder().decode(reportFormHtml64Html), "UTF-8");

					File reportFile = new File("merger.html");
					Files.write(Paths.get("merger.html"), reportFormHtmlContent.getBytes());
					logger.debug("file write Successfully");
					return Files.readAllBytes(reportFile.toPath());

				} catch (Exception e) {
					logger.error("Exception occurred", e);
				}
			} catch (Exception e) {
				logger.error("Exception occurred", e);
				return null;
			}
			// ================end===================

			return null;
		} catch (Exception e) {
			logger.error("Error occurred while merging pdf file {} ", e);
			return null;
		}
	}

	private User getBranchManagerInfo(String role, String branchCode) {
		List<User> userList = userRepo.findByAddInfo1AndAddInfo2(role, branchCode);
		if (userList != null && userList.size() > 0) {
			return userList.get(0);
		}
		return null;
	}

	// ===========getSanctionReportParameters======
	private HashMap<String, String> getSanctionReportParameters(String applicationId) {
		HashMap<String, String> sanctionReportParams = new HashMap<String, String>();
		List<String> keyList = Arrays.asList(new String[] { "bmId", "loanAmt", "pdtName", "purpose", "bmName",
				"pfGstAmt", "sanctionedDate", "interestRate", "memInsurance", "amountApproved", "tenure", "frequency",
				"spouseInsurance", "installmentAmt", "memberName", "memberId", "kendraId" });
		List<ApplicationMasterHis> applicationMasterList = applicationMasterRepo.findByApplicationId(applicationId);
		try {
			if (applicationMasterList != null && applicationMasterList.size() > 0) {
				ApplicationMasterHis appMaster = applicationMasterList.get(0);

				FetchAppRequest appReq = new FetchAppRequest();
				appReq.setAppId(appMaster.getAppId());
				FetchAppRequestFields fields = new FetchAppRequestFields();
				fields.setApplicationId(applicationId);
				fields.setVersionNum(Integer.parseInt(appMaster.getVersionNum()));
				appReq.setRequestObj(fields);

				logger.debug("========fetching data from database=====");
				ApplyLoanRequestHisFields loanFields = documentService1.getCustomerData(applicationMasterList, appReq);
				logger.debug("========Outside Data fetch=====");
				if (loanFields != null && loanFields.getApplicationdtls() != null
						&& loanFields.getApplicationdtls().size() > 0) {
					ApplicationDtls appDtl = loanFields.getApplicationdtls().get(0);
					CustomerDtls customerDtl = appDtl.getCustomerDetails();
					if (customerDtl != null) {
						LoanDtls loanDtl = customerDtl.getLoanDtls();
						String loanAmt = "Rs." + loanDtl.getCaglAmt();
						sanctionReportParams.put("loanAmt", loanAmt);
						sanctionReportParams.put("pdtName", loanDtl.getProduct());
						HashMap<String, String> purposeObj = (HashMap<String, String>) loanDtl.getPurpose();
						sanctionReportParams.put("purpose", purposeObj.getOrDefault("purposeDesc", "NA"));
						User userObj = getBranchManagerInfo("BM", appMaster.getBranchCode());
						if (userObj != null) {
							sanctionReportParams.put("bmId", userObj.getUserId());
						}

						try {
							Optional<TbUaobCbResponseHis> cbRes = cbResRepository
									.findByAppIdAndApplicationIdOrderByVersionNumDesc(appReq.getAppId(), applicationId);
							logger.debug("Printing cbRes 2nd:" + cbRes);
							if (cbRes.isPresent()) {
								logger.debug("cbRes is present" + cbRes.get());
								TbUaobCbResponseHis cbResponse = cbRes.get();
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

							Double processFee = Double
									.parseDouble(loanDtl.getChargeAndBreakupDtls().getLoanProcessingFee());
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
					Optional<ApplicationWorkflowHis> opApplnWfRec = applicationWorkflowRepository
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
				} catch (Exception e) {
					logger.error("Error occured while fetching BM info" + e);
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

	// ===========getLoanApplicationParams======
	private HashMap<String, String> getLoanApplicationParams(String applicationId) {
		HashMap<String, String> loanApplicationParams = new HashMap<String, String>();
		List<String> keyList = Arrays.asList(new String[] { "memberId", "memberName", "phoneno", "branchName",
				"kendraName", "kendraId", "date", "productName", "Lnpurpose", "loanAmount", "paymentFreq",
				"memInsurance", "spouseInsurance", "kmName", "kmId", "bmName", "bmId" });
		List<ApplicationMasterHis> applicationMasterList = applicationMasterRepo.findByApplicationId(applicationId);
		try {
			logger.debug("Loan Application starts for the application:" + applicationId);
			if (applicationMasterList != null && applicationMasterList.size() > 0) {
				ApplicationMasterHis appMaster = applicationMasterList.get(0);
				FetchAppRequest appReq = new FetchAppRequest();
				appReq.setAppId(appMaster.getAppId());
				FetchAppRequestFields fields = new FetchAppRequestFields();
				fields.setApplicationId(applicationId);
				fields.setVersionNum(Integer.parseInt(appMaster.getVersionNum()));
				appReq.setRequestObj(fields);
				ApplyLoanRequestHisFields loanFields = documentService1.getCustomerData(applicationMasterList, appReq);
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
						/*
						 * String date = serverDate.map(ServerDate::getCurrentDate).orElse(null); int
						 * number = Integer.parseInt(date); String dateStr = String.format("%08d",
						 * number); String year = dateStr.substring(0, 4); String month =
						 * dateStr.substring(4, 6); String day = dateStr.substring(6, 8); LocalDate
						 * date_server = LocalDate.parse(year + "-" + month + "-" + day,
						 * DateTimeFormatter.ofPattern("yyyy-MM-dd"));
						 * logger.debug("date before dd-MM-yyyy:" + date); String formattedDateVal =
						 * date_server.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
						 * logger.debug("Printing serverDate:" + formattedDateVal); String dt =
						 * formattedDateVal.toString(); logger.debug("Printing date:" + dt);
						 */
						// commented for date fix loanApplicationParams.put("date", dt);
						// Added to format application date string to dd-mm-yyyy format
						LocalDate date_server = LocalDate.parse(appMaster.getApplicationDate().toString(),
								DateTimeFormatter.ofPattern("yyyy-MM-dd"));
						logger.debug("date before dd-MM-yyyy:" + date_server);
						String formattedDateVal = date_server.format(DateTimeFormatter.ofPattern("dd-MM-yyyy"));
						logger.debug("Printing LoanApplicationDate:" + formattedDateVal);
						String dt = formattedDateVal.toString();
						logger.debug("Printing String converted LoanApplicationDate date:" + dt);
						// loanApplicationParams.put("date", appMaster.getApplicationDate().toString());
						// Code before the date format dd-mm-yyyy is done
						loanApplicationParams.put("date", dt);
						loanApplicationParams.put("productName", loanDtl.getProduct());
						Object purpose = loanDtl.getPurpose();
						JSONObject jsonObj = new JSONObject(new Gson().toJson(purpose));
						if (jsonObj.has("purposeDesc")) {
							Object obj = jsonObj.get("purposeDesc");

							loanApplicationParams.put("Lnpurpose", obj.toString());
						}
						String loanAmount = "Rs." + appMaster.getAmount().toString();
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
							memInsurance = "NO";
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
						Optional<ApplicationWorkflowHis> opApplnWfRec = applicationWorkflowRepository
								.findCreatedByUsingApplicationIdAndApplicationStatus(applicationId);
						logger.debug("ApplicationWorkflow for loanAppliction to get CreatedBy:" + opApplnWfRec);
						if (opApplnWfRec.isPresent()) {
							ApplicationWorkflowHis applicationWorkflowRec = opApplnWfRec.get();
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
						} else {
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

	// ============convertStringToInt==============
	private Integer convertStringToInt(String value) {
		try {
			return Integer.parseInt(value.split("\\.")[0]);
		} catch (Exception exp) {
			logger.info("Error occurred while converting String to int");
		}
		return 0;
	}

	// ==============getGkFactReportParameters===============

	private HashMap<String, String> getGkFactReportParameters(String applicationId) {
		HashMap<String, String> sanctionReportParams = new HashMap<String, String>();
		List<String> keyList = Arrays.asList(new String[] { "kendraName", "kendraId", "totalInstallment", "apr",
				"preclosedAmt", "bmId", "amountApproved", "memberName", "memberId", "id", "pdtName", "sancAmtRupees",
				"disbursementStage", "paymentFreq", "term", "interestRate", "referenceBenchMark", "benchMarkRate",
				"spread", "finalRate", "respectivePeriodB", "respectivePeriodS", "impactEPI", "impactNoOfEPI",
				"insuranceAmt", "overAllFeeChargeAmt", "tenure", "installmentAmt", "repaymentDate", "loanCharge",
				"processingFee", "approvedAmtInRupees", "sanctId", "insuranceChargeText" });
		List<ApplicationMasterHis> applicationMasterList = applicationMasterRepo.findByApplicationId(applicationId);
		logger.debug("Printing applicationMasterList:" + applicationMasterList);
		sanctionReportParams.put("insuranceChargeText", "One Time");
		try {
			if (applicationMasterList != null && applicationMasterList.size() > 0) {
				ApplicationMasterHis appMaster = applicationMasterList.get(0);
				BigDecimal amount = appMaster.getAmount();
				String loanAmount = amount.toString();
				String elTotalInstallment = "";
				int lnamount = convertStringToInt(loanAmount);
				FetchAppRequest appReq = new FetchAppRequest();
				appReq.setAppId(appMaster.getAppId());
				FetchAppRequestFields fields = new FetchAppRequestFields();
				fields.setApplicationId(applicationId);
				fields.setVersionNum(Integer.parseInt(appMaster.getVersionNum()));
				appReq.setRequestObj(fields);
				logger.debug("=====getGkFactReportParameters Calling getCustomerData to fetch DB Details========");
				ApplyLoanRequestHisFields loanFields = documentService1.getCustomerData(applicationMasterList, appReq);
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
							double elInst = Double.parseDouble(elTotalInstallment);
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
				Optional<TbUaobCbResponseHis> cbRes = cbResRepository
						.findByAppIdAndApplicationIdOrderByVersionNumDesc(appReq.getAppId(), applicationId);
				logger.debug("Printing cbRes:" + cbRes);
				String interestFeeStr = null;
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
				// String interestFee = loanDtl.getChargeAndBreakupDtls().getInterest_Fee();

				HashMap<String, String> freqObj = (HashMap<String, String>) loanDtl.getRepayFrequency();
				sanctionReportParams.put("paymentFreq", freqObj.getOrDefault("idDesc", "WEEKLY"));
				sanctionReportParams.put("frequency", freqObj.getOrDefault("idDesc", "WEEKLY"));
//				sanctionReportParams.put("tenure", termVal);

				String frequency = freqObj.getOrDefault("idDesc", "WEEKLY");
				String product = loanDtl.getProduct();
				// logger.debug("Printing interestFee:" + interestFee);

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

				if (("GL.EMERG.LOAN").equalsIgnoreCase(product) || ("GL.EMERGENCY.LN").equalsIgnoreCase(product)) {
					sanctionReportParams.put("totalInstallment", "Rs. " + elTotalInstallment);
				} else {
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
		logger.debug("Final Value of insuranceChargeText" + sanctionReportParams.get("insuranceChargeText"));
		logger.debug("Printing final sanctionReportParams:" + sanctionReportParams);
		return sanctionReportParams;
	}

	// =========intializeBirtEngine===============
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

	// =================generateBirReport=============

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
			exp.printStackTrace();
		}
		return finalReportFile;
	}

	// ================extractBodyContent====================
	private static String extractBodyContent(String html) {
		// Use regex or a library like Jsoup to extract the body content
		// This is a simple regex example; consider using Jsoup for complex HTML
		//Pattern pattern = Pattern.compile("<body.*?>(.*?)</body>", Pattern.DOTALL);	
		//Matcher matcher = pattern.matcher(html);
		 Matcher matcher = BODY_PATTERN.matcher(html);
		if (matcher.find()) {
			return matcher.group(1); // Return the content inside the body tags
		}
		return ""; // Return empty if no body found
	}

	// ===========extractStyles=========
	private static String extractStyles(String html, String reportName) {
		// Use regex or a library like Jsoup to extract the style content
	   //Pattern pattern = Pattern.compile("<style.*?>(.*?)</style>", Pattern.DOTALL);
		//Matcher matcher = pattern.matcher(html);
		Matcher matcher = STYLE_PATTERN.matcher(html);		
		StringBuilder styles = new StringBuilder();
		while (matcher.find()) {
			styles.append(matcher.group(1)).append("\n"); // Append each style block
		}
		return styles.toString().replaceAll(".style", "." + reportName + "style");
	}

	// ==============combineStyles=========
	// Change to disable loan Application-29-Sep-25-Satish
	// private static String combineStyles(String styles1,String styles2,String
	// styles3)
	private static String combineStyles(String styles1, String styles2) {
		// Here you may want to handle conflicts between styles if needed
		// For simplicity, this just combines them

		// Change to disable loan Application-29-Sep-25-Satish
		// return "<style>" + styles1+"\n"+styles2+"\n"+styles3 +"</style>";
		return "<style>" + styles1 + "\n" + styles2 + "</style>";
	}

	// ============combineStylesForEL=======
	private static String combineStylesForEL(String styles1) {
		// Here you may want to handle conflicts between styles if needed
		// For simplicity, this just combines them
		return "<style>" + styles1 + "</style>";
	}

	// ============mergeReportsForEL================
	private static String mergeReportsForEL(String report1Html) {
		// Extract body content from each report
		logger.debug("inside mergeReportsForEL:");
		report1Html = report1Html.replaceAll("class=\"", "class=\"report1-");

		String body1 = extractBodyContent(report1Html);
		// Extract styles from each report
		String styles1 = extractStyles(report1Html, "report1-");

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
		// mergedHtml.append("<div style='page-break-after: always;'></div>");
		mergedHtml.append("<br/><br/>");
		// mergedHtml.append("<div style='page-break-after: always;'></div>");
		mergedHtml.append("</body>");
		mergedHtml.append("</html>");

		return mergedHtml.toString();
	}

	// ================mergeReports=======================
	// Change to disable Loan Application-29-Sep-25-Satish
	// private static String mergeReports(String report1Html,String
	// report2Html,String report3Html)
	private static String mergeReports(String report1Html, String report2Html) {
		// Extract body content from each report

		report1Html = report1Html.replaceAll("class=\"", "class=\"report1-");
		report2Html = report2Html.replaceAll("class=\"", "class=\"report2-");
		// Change to disable Loan Application-29-Sep-25-Satish
		// report3Html = report3Html.replaceAll("class=\"", "class=\"report3-");

		String body1 = extractBodyContent(report1Html);
		String body2 = extractBodyContent(report2Html).replaceAll("file:/tmp",
				"https://www.creditaccessgrameen.in/wp-content/themes/creditaccessgrameen/assets//images/logo.png?");
		// Change to disable Loan Application-29-Sep-25-Satish
		// String body3 =
		// extractBodyContent(report3Html).replaceAll("file:/tmp","https://www.creditaccessgrameen.in/wp-content/themes/creditaccessgrameen/assets//images/logo.png?");;
		// Extract styles from each report
		String styles1 = extractStyles(report1Html, "report1-");
		String styles2 = extractStyles(report2Html, "report2-");
		// Change to disable Loan Application-29-Sep-25-Satish
		// String styles3 = extractStyles(report3Html,"report3-");

		// Combine styles, ensuring no duplicates
		// Change to disable Loan Application-29-Sep-25-Satish
		// String combinedStyles = combineStyles(styles1,styles2,styles3);
		String combinedStyles = combineStyles(styles1, styles2);

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
		// Change to disable Loan Application-29-Sep-25-Satish
		/*
		 * mergedHtml.append("<div style='page-break-after: always;'></div>");
		 * mergedHtml.append(body3);
		 */
		mergedHtml.append("</body>");
		mergedHtml.append("</html>");

		return mergedHtml.toString();
	}

	// ============getParamsFromDisbursementResponse===========================
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

	// =====================================fetchDBKitReport===========================
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

			/**
			 * @author CAG @Ankit Changes: disbursement:Date field initialized
			 */
			logger.debug("Start Audit His auditLog: {}");
			TbUaobApiAuditLogsHis applicationauditLog = auditApiRepo.findApplicationId(applicationId);
			logger.debug("Audit His auditLog: {}", applicationauditLog.toString());
			if (applicationauditLog != null && applicationauditLog.getResponsePayload() != null) {
				try {
					ObjectMapper objectMapper = new ObjectMapper();
					JsonNode rootNode = objectMapper.readTree(applicationauditLog.getResponsePayload());
					logger.debug("Neft passbook rootNode: {}", rootNode);
					JsonNode linkedActivitiesNode = rootNode.get("linkedActivities");
					logger.debug("Neft passbook linkedActivitiesNode: {}", linkedActivitiesNode);
					if (linkedActivitiesNode != null && !linkedActivitiesNode.isNull()) {
						JsonNode firstlinkedActivitiesNode = linkedActivitiesNode.get(0);
						logger.debug("Neft passbook firstlinkedActivitiesNode: {}", firstlinkedActivitiesNode);
						JsonNode bodyNode = firstlinkedActivitiesNode.get("body");
						if (bodyNode != null && bodyNode.has("effectiveDate")) {
							String effectiveDate = bodyNode.get("effectiveDate").asText();
							
							logger.debug("effectiveDate: {}", effectiveDate);
							DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
							DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
							LocalDate date = LocalDate.parse(effectiveDate, inputFormatter);
							dbKitReportParams.put("date", date.format(outputFormatter));
						
						} else {
							logger.warn("effectiveDate not found in activity body");
							dbKitReportParams.put("date", "-");
						}
					} else {
						logger.warn("Header node not found in response JSON");
					}
				} catch (Exception e) {
					logger.error("Error parsing loanID from audit log", e);
					dbKitReportParams.put("date", "-");
				}
			}

			logger.debug("generateAndDownloadDBKitReport : {}", dbKitReportParams);
			File dbKitReport = generateBirReport(applicationId, DBKitReport, dbKitReportParams, format);
			boolean isOtherThanEnglish = DBKitReport.contains("_");
			logger.debug("report language is not english");
			// Sonar Issue fixed
			if (dbKitReport == null || !dbKitReport.exists()) {
				logger.error("DB Kit report file is null or does not exist. applicationId: {}, reportName: {}",
						applicationId, DBKitReport);
			}
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

//							 String loanApplicationFormbase64Html = java.util.Base64.getEncoder().encodeToString(loanApplicationFormhtmlBytes,"UTF-8");
//							 String loanApplicationFormhtmlContent = new String(java.util.Base64.getDecoder().decode(base64Html), "UTF-8");

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
					// e.printStackTrace();
					logger.error("Exception occurred", e);
				}
				return dbKitReporthtmlBytes;
			} catch (Exception e) {
				// e.printStackTrace();
				logger.error("Exception occurred", e);
				return null;
			}

		} catch (Exception e) {
			logger.error("Error occurred while merging pdf file {} ", e);
			return null;
		}

	}

	// ===========getDBKitParameters================

	private HashMap<String, String> getDBKitParameters(String applicationId) {
		HashMap<String, String> sanctionReportParams = new HashMap<String, String>();
		List<String> keyList = Arrays.asList(new String[] { "ewi", "insurancePremium", "processingFee", "bankName",
				"bankBranchName", "ifscCode", "bankAccNo", "apr", "pdtName", "bmId", "kmName", "kmId", "spousekycType",
				"spousekycId", "kycType", "kycId", "phoneno", "memberName", "memberId", "kendraId", "bmName",
				"branchName", "kendraName", "date", "id", "spouceOrFatherName", "fullAddress", "approvedAmtInRupees",
				"interestRate", "approvedAmtInWord", "purpose", "gkId", "borrowerName", "coBorrowerName", "dob",
				"gender", "spouseGender", "spouseDOB", "nomineeName", "nomineeDOB", "nomineeGender",
				"nomineeRelationShip", "appointeeName", "appointeeDOB", "appointeeGender", "appointeeRelationShip",
				"paymentFreq", "term", "type_of_roof_concrete", "type_of_roof_asbestos_tin", "type_of_roof_teracote",
				"type_of_roof_terracotta", "house_ownership_rented", "house_ownership_owned",
				"basic_amenities_electricity", "basic_amenities_water_pipeline_connection", "basic_amenities_toilet",
				"basic_amenities_sewage_connection", "basic_amenities_lpg_connection", "basic_amenities_none",
				"other_assets_land", "other_assets_livestock_(cattle)", "other_assets_2-wheeler",
				"other_assets_4-wheeler", "other_assets_tv", "other_assets_ridge_(refrigerator)",
				"other_assets_smartphone", "other_assets_none" });
		List<ApplicationMasterHis> applicationMasterList = applicationMasterRepo.findByApplicationId(applicationId);
		try {
			if (applicationMasterList != null && applicationMasterList.size() > 0) {
				ApplicationMasterHis appMaster = applicationMasterList.get(0);
				FetchAppRequest appReq = new FetchAppRequest();
				appReq.setAppId(appMaster.getAppId());
				FetchAppRequestFields fields = new FetchAppRequestFields();
				fields.setApplicationId(applicationId);
				fields.setVersionNum(Integer.parseInt(appMaster.getVersionNum()));
				appReq.setRequestObj(fields);
				ApplyLoanRequestHisFields loanFields = documentService1.getCustomerData(applicationMasterList, appReq);
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
						// String installment = "";
						// List<SanctionRepaymentSchedule> repaymentSchedules =
						// sanctionRepaymentScheduleRepository
						// .findByApplicationId(applicationId);

						if (loanDtl.getInstallmentDetails() != null) {
							sanctionReportParams.put("ewi", loanDtl.getInstallmentDetails());
						} else {
							sanctionReportParams.put("ewi", "0.0");
						}
						/*
						 * if (repaymentSchedules.size() > 1) { installment =
						 * repaymentSchedules.get(1).getInstallment(); sanctionReportParams.put("ewi",
						 * installment); } else { sanctionReportParams.put("ewi", "0.0");
						 * logger.debug("Less than 2 repayment schedules found for applicationId: " +
						 * applicationId); }
						 */
						sanctionReportParams.put("bankAccNo", customerDtl.getBankDtls().getBankAccNo());
						sanctionReportParams.put("bankBranchName", customerDtl.getBankDtls().getBankBranchName());
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
						// Modified to fetch depname from KYC details for spouseorfather in
						// Report-Satish 07-Nov-25
						// As per Satish team add like SetDepname and made changed to getDepname.25Nov
						// Handled the null value for Depname
						// sanctionReportParams.put("depname", customerDtl.getKycDtls().SetDepname());
						String depName = customerDtl.getKycDtls().getDepname();
						if (depName == null || depName.trim().isEmpty()) {
							depName = "-";
						}
						sanctionReportParams.put("depname", depName);
						// sanctionReportParams.put("depname", customerDtl.getKycDtls().getDepname());
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
						Optional<TbUaobCbResponseHis> cbRes = cbResRepository
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
							memInsurance = "NO";
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
				Optional<ApplicationWorkflowHis> opApplnWfRec = applicationWorkflowRepository
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
				Optional<TbUaobCbResponseHis> cbRes = cbResRepository
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

	// ================setCheckboxValues================

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

}
