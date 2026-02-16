package com.iexceed.appzillonbanking.cagl.loan.service;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import com.iexceed.appzillonbanking.cagl.loan.payload.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.codehaus.jettison.json.JSONException;
import com.fasterxml.jackson.core.type.TypeReference;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cagl.loan.constants.CommonConstants;
import com.iexceed.appzillonbanking.cagl.loan.core.domain.apz.UserRole;
import com.iexceed.appzillonbanking.cagl.loan.core.repository.apz.UserRoleRepository;
import com.iexceed.appzillonbanking.cagl.loan.core.utils.Products;
import com.iexceed.appzillonbanking.cagl.loan.core.utils.WidgetQueueStatus;
import com.iexceed.appzillonbanking.cagl.loan.core.utils.WorkflowStatus;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.ApplicationWorkflow;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.AuditTrailEntity;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.BusinessTarget;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.EmiProductList;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.MisReport;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.RoleAccessMap;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.RoleAccessMapId;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.SanctionRepaymentSchedule;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUacoInsuranceDtls;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUalnLoanDtls;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUaobAddressDetails;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUaobAuditLogs;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUaobCbResponse;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUaobCustDtls;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUaobNomineeDetails;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUaobOccupationDtls;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.WorkflowDefinition;
import com.iexceed.appzillonbanking.cagl.loan.domain.apz.User;
import com.iexceed.appzillonbanking.cagl.loan.domain.apz.UserId;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.ApplicationMasterRepository;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.ApplicationWorkflowRepository;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.AuditTrailRepo;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.BusinessTargetRepository;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.EmiProductListRepository;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.FetchStateRepository;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.MisReportRepository;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.RoleAccessMapRepository;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.SanctionRepaymentScheduleRepository;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.TOfficeDetailsRepo;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.TbAsmiUserRepo;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.TbLockCustomerRepository;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.TbUacoInsuranceDtlsRepository;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.TbUalLoanDtlsRepository;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.TbUaobAddressDetailsRepository;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.TbUaobAuditLogsRepository;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.TbUaobCbResponseRepository;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.TbUaobCustomerDtlsRepo;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.TbUaobNomineeDtlsRepository;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.TbUaobOccupationDtlsRepository;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.WorkflowDefinitionRepository;
import com.iexceed.appzillonbanking.cagl.loan.repository.apz.UserRepository;
import com.iexceed.appzillonbanking.core.payload.Header;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.core.utils.CommonUtilsCBS;
import com.iexceed.appzillonbanking.core.utils.FallbackUtils;
import com.iexceed.appzillonbanking.interfaceAdapter.service.InterfaceAdapter;
import com.iexceed.appzillonbanking.interfaceAdapter.utils.AdapterUtil;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.common.util.StringUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import net.sf.jasperreports.engine.util.ObjectUtils;
import reactor.core.publisher.Mono;

@Service
public class LoanService {

	private static final Logger logger = LogManager.getLogger(LoanService.class);

	@Autowired
	private ApplicationMasterRepository applicationMasterRepo;

	@Autowired
	private TbUalLoanDtlsRepository loanDtlsRepo;

	@Autowired
	private FetchStateRepository fetchStateRepository;

	@Autowired
	private TbUaobAddressDetailsRepository addressRepository;

	@Autowired
	private TbUaobCustomerDtlsRepo custDtlsRepo;

	@Autowired
	private TbUaobOccupationDtlsRepository tbUaobOccpationDtlRepo;

	@Autowired
	private TbUacoInsuranceDtlsRepository tbUacoInsuranceDtlsRepo;

	@Autowired
	private TbUaobNomineeDtlsRepository nomineeRepository;

	@Autowired
	private InterfaceAdapter interfaceAdapter;

	@Autowired
	private AdapterUtil adapterUtil;

	@Autowired
	private TbUaobCbResponseRepository cbResRepository;

	@Autowired
	private ApplicationWorkflowRepository applnWfRepository;

	@Autowired
	private WorkflowDefinitionRepository wfDefnRepoLn;

	@Autowired
	private RoleAccessMapRepository roleAccessMapRepository;

	@Autowired
	private MisReportRepository misReportRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private TbLockCustomerRepository customerRepository;

	@Lazy // Prevents circular dependency
	@Autowired
	private ReportBuildService reportBuildService;

	// @Autowired
	// private ApprovalMatrixMasterRepository approvalMatrixMasterRepository;

	@Autowired
	private SanctionRepaymentScheduleRepository sanctionRepaymentScheduleRepository;

	@Autowired
	private AuditTrailRepo auditTrailRepo;

	@Autowired
	private TOfficeDetailsRepo tbOfficeDataRepository;

	@Autowired
	private TbAsmiUserRepo tbAsmiUserRepo;

	@Autowired
	private TbUaobAuditLogsRepository tbUaobApiAuditLogsRepository;

	@Autowired
	private BusinessTargetRepository businessTargetRepository;
	
	@Autowired
	private EmiProductListRepository emiProductListRepository;

	@Value("${ab.common.numOfDaysRecords}")
	private String numOfDaysRecords;

	@Value("${ab.common.numOfRecordsInWidget}")
	private String numOfRecordsInWidget;

	@Value("${ab.common.accountSTP}")
	private String accountSTP;

	@Value("${ab.common.depositSTP}")
	private String depositSTP;

	@Value("${ab.common.cardSTP}")
	private String cardSTP;

	@Value("${ab.common.loanSTP}")
	private String loanSTP;

	@PersistenceContext
	private final EntityManager entityManager;

	CbRequest cbRequest;
	Header header;

	public LoanService(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public static final String EXCEPTION_MSG = "Something went wrong, Please try again!!";
	public static final String EXCEPTION_OCCURED = "Exception occurred";
	public static final String CBCHECK_REPORT_INTERFACEID = "fetchCbReport";
	public static final String FETCH_LOAN_SCHEDULE_INTERFACEID = "FetchLoanSchedule";
	public static final String FETCH_SANCTION_LOAN_SCHEDULE_INTERFACEID = "GetSanctionLoanSchedule";

	private static final String LOAN_SCHEDULE_PROJECTOR = "GetSanctionLoanSchedule";

	private static final String GENERATEPASSBOOK = "GeneratePassbook";

	public Response createApplication(CreateAppRequest apiRequest) {
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();

		ApplicationMaster appMaster = new ApplicationMaster();
		logger.debug("inside createApplication:{} ", apiRequest);

		try {
			appMaster.setAppId(apiRequest.getAppId());
			String loanType = "ML";
			String userId = apiRequest.getRequestObj().getCreatedBy();
			String branchId = apiRequest.getRequestObj().getBranchCode();
			String applicationId = CommonUtils.generateApplicationId(loanType, branchId, userId);
			// String applicationId = CommonUtils.generateRandomNumStr();
			appMaster.setApplicationId(applicationId);
			appMaster.setVersionNum("1");
			appMaster.setApplicationDate(apiRequest.getRequestObj().getApplicationDate());
			appMaster.setCreateTs(new Timestamp(System.currentTimeMillis()));
			appMaster.setCreatedBy(apiRequest.getRequestObj().getCreatedBy());
			appMaster.setApplicationType(apiRequest.getRequestObj().getApplicationType());
			appMaster.setKycType(apiRequest.getRequestObj().getKycType());
			appMaster.setCurrentStage(apiRequest.getRequestObj().getCurrentStage());
			appMaster.setCurrentScreenId(apiRequest.getRequestObj().getCurrentScrId());
			appMaster.setProductCode(apiRequest.getRequestObj().getProductCode());
			appMaster.setProductGroupCode(apiRequest.getRequestObj().getProductGrpCode());
			appMaster.setBranchCode(apiRequest.getRequestObj().getBranchCode());
			appMaster.setCbCheck(apiRequest.getRequestObj().getCbCheck());
			appMaster.setCustomerId(apiRequest.getRequestObj().getCustomerId());
			appMaster.setKendraId(apiRequest.getRequestObj().getKendraId());
			appMaster.setKmId(apiRequest.getRequestObj().getKmId());
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

	@Transactional
	public Response applyLoan(ApplyLoanRequest apiRequest) {
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();
		logger.debug("inside applyLoan:{} ", apiRequest);
		try {
			JSONObject json = saveApplicationDetails(apiRequest);
			if (json.has("messageCode")) {
				logger.debug("existing application:{}", json);
				respBody.setResponseObj(EXCEPTION_MSG);
				CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
				json.put("message",
						"A loan application already exists for this customer.Please verify the details and try again.");
			} else {
				String applicationId = (String) json.get(CommonConstants.APPLICATIONID);
				String custId = saveCustomerDetails(apiRequest, applicationId);
				saveOccupationDtls(apiRequest, applicationId, custId);
				saveInsuranceDtls(apiRequest, applicationId, custId);
				saveLoanDetails(apiRequest, applicationId, custId);
				saveAddressDetails(apiRequest, applicationId, custId);
				json.put("message", "Loan Application details saved successfully!!");
			}
			respBody.setResponseObj(json.toString());
			CommonUtils.generateHeaderForSuccess(respHeader);
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
		} catch (Exception e) {
			logger.error("exception at applyLoan: ", e);
			respBody.setResponseObj(EXCEPTION_MSG);
			CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
		}
		logger.debug("response from applyLoan:{} ", response);
		return response;
	}

	private void saveAddressDetails(ApplyLoanRequest apiRequest, String applicationIdRef, String custId) {
		logger.debug("inside saveAddressDetails:{} ", apiRequest);

		List<ApplicationDtls> applicationDtlsPayload = apiRequest.getRequestObj().getApplicationdtls();
		List<TbUaobAddressDetails> addDtlsDomainList = new ArrayList<>();

		for (ApplicationDtls appDtls : applicationDtlsPayload) {
			if (null != appDtls.getCustomerDetails() && null != appDtls.getCustomerDetails().getAddressDtls()) {
				List<AddressDtls> addDtlsList = appDtls.getCustomerDetails().getAddressDtls();
				if (CommonUtils.isNullOrEmpty(appDtls.getApplicationId())
						&& CommonUtils.isNullOrEmpty(appDtls.getVersionNo())) {
					for (AddressDtls addDtlsPayload : addDtlsList) {
						TbUaobAddressDetails addDtls = new TbUaobAddressDetails();
						BigDecimal applicationId = CommonUtils.generateRandomNum();
						addDtls.setAddressDtlId(String.valueOf(applicationId));
						addDtls.setAddressType(addDtlsPayload.getAddressType());
						addDtls.setAppId(apiRequest.getAppId());
						addDtls.setApplicationId(applicationIdRef);
						addDtls.setCustDtlId(custId);
						addDtls.setCustomerId(appDtls.getCustomerId());
						addDtls.setVersionNum("1");
						addDtls.setKendraId(appDtls.getKendraId());
						try {
							addDtls.setPayload(new ObjectMapper().writeValueAsString(addDtlsPayload));
						} catch (JsonProcessingException e) {
							logger.error("exception at saveAddressDetails: ", e);
						}
						addDtlsDomainList.add(addDtls);
					}
				} else {
					List<TbUaobAddressDetails> addressDtlList = addressRepository.findByApplicationId(applicationIdRef);
					String ver = addressDtlList.get(0).getVersionNum();
					String updatedVer = String.valueOf(Integer.parseInt(ver) + 1);
					List<String> applicationIds = new ArrayList<>();
					for (TbUaobAddressDetails tbUaobAdd : addressDtlList) {
						applicationIds.add(tbUaobAdd.getApplicationId());
					}
					addressRepository.deleteByApplicationIdIn(applicationIds);
					for (AddressDtls addDtlsPayload : addDtlsList) {
						TbUaobAddressDetails addDtls = new TbUaobAddressDetails();
						BigDecimal applicationId = CommonUtils.generateRandomNum();
						addDtls.setAddressDtlId(String.valueOf(applicationId));
						addDtls.setAddressType(addDtlsPayload.getAddressType());
						addDtls.setAppId(apiRequest.getAppId());
						addDtls.setApplicationId(applicationIdRef);
						addDtls.setCustDtlId(custId);
						addDtls.setCustomerId(appDtls.getCustomerId());
						addDtls.setVersionNum(updatedVer);
						addDtls.setKendraId(appDtls.getKendraId());
						try {
							addDtls.setPayload(new ObjectMapper().writeValueAsString(addDtlsPayload));
						} catch (JsonProcessingException e) {
							logger.error("exception at saveAddressDetails: ", e);
						}
						addDtlsDomainList.add(addDtls);
					}
				}
			}
		}
		if (!addDtlsDomainList.isEmpty()) {
			addressRepository.saveAll(addDtlsDomainList);
		}
		logger.debug("saved data to address details:{} ", addDtlsDomainList);
	}

	@Transactional
	private JSONObject saveApplicationDetails(ApplyLoanRequest apiRequest) {
		logger.debug("inside saveApplicationDetails:{} ", apiRequest);
		List<ApplicationMaster> appMasterList = new ArrayList<>();
		List<ApplicationDtls> applicationDtlsPayload = apiRequest.getRequestObj().getApplicationdtls();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		String applicationIdRef = null;
		JSONObject json = new JSONObject();
		for (ApplicationDtls appDtls : applicationDtlsPayload) {
			ApplicationMaster appMaster = new ApplicationMaster();
			if (!CommonUtils.isNullOrEmpty(appDtls.getApplicationId())) {
			    List<ApplicationMaster> appMasterDetails = applicationMasterRepo
			            .findApplicationBasedOnCustomerIdandApplication(
			                    appDtls.getCustomerId(), 
			                    appDtls.getApplicationId());
			    logger.debug("Printing customer and AppId details appMasterDetails:{} ", appMasterDetails);
			    if (appMasterDetails.isEmpty()) {
			        appDtls.setApplicationId("");
			        appDtls.setVersionNo("");
			    }
			}
			if (CommonUtils.isNullOrEmpty(appDtls.getApplicationId())
					&& CommonUtils.isNullOrEmpty(appDtls.getVersionNo())) {
				try {
					List<String> statuses = Arrays.asList("INITIATE", "PENDING");
					List<ApplicationMaster> appMasterDetails = applicationMasterRepo
							.findApplicationBasedOnCustomerId(appDtls.getCustomerId(), statuses);
					logger.debug("Printing appMasterDetails:{} ", appMasterDetails);
					int applicationCount = appMasterDetails.size();
					if (applicationCount != 0) {
						logger.debug("A loan application already exists for this customer.");
						json.put("messageCode", "409");
						json.put("message",
								"A loan application already exists for this customer. Please verify the details and try again.");
						return json;
					}
				} catch (Exception e) {
					//e.printStackTrace();
					logger.error("Exception occurred", e);
				}
				appMaster.setAppId(apiRequest.getAppId());
				String loanType = "ML";
				String userId = appDtls.getCreatedBy();
				String branchId = Optional.ofNullable(apiRequest).map(ApplyLoanRequest::getRequestObj)
						.map(ApplyLoanRequestFields::getApplicationdtls).filter(list -> !list.isEmpty())
						.map(list -> list.get(0)).map(ApplicationDtls::getBranchId).orElse(null);
				String applicationId = CommonUtils.generateApplicationId(loanType, branchId, userId);
				// BigDecimal applicationId = CommonUtils.generateRandomNum();
				appMaster.setApplicationId(String.valueOf(applicationId));
				appMaster.setVersionNum("1");
				appMaster.setApplicationDate(LocalDate.now());
				appMaster.setCreateTs(new Timestamp(System.currentTimeMillis()));
				appMaster.setCreatedBy(appDtls.getCreatedBy());
				appMaster.setApplicationStatus(appDtls.getStatus());
				appMaster.setApplicationType(appDtls.getApplicationType());
				appMaster.setKycType(appDtls.getKycType());
				appMaster.setCurrentStage(appDtls.getCurrentStage());
				appMaster.setCurrentScreenId(appDtls.getCurrentScreenId());
				appMaster.setProductGroupCode(appDtls.getProductGroupCode());
				appMaster.setBranchCode(appDtls.getBranchId());
				appMaster.setCbCheck(appDtls.getCbCheck());
				appMaster.setCustomerId(appDtls.getCustomerId());
				appMaster.setKendraId(appDtls.getKendraId());
				appMaster.setKmId(appDtls.getKmId());
				appMaster.setLeader(appDtls.getLeader());
				appMaster.setKendraName(appDtls.getKendraName());
				appMaster.setCustomerName(appDtls.getCustomerName());
				appMaster.setAmount(appDtls.getAmount());

				Gson gson = new Gson();
				if (appDtls.getAddInfo() != null) {
					String addInfoVal = gson.toJson(appDtls.getAddInfo());
					appMaster.setAddInfo(addInfoVal);
				}
				if (appDtls.getCustomerDetails() != null && appDtls.getCustomerDetails().getLoanDtls() != null) {
					LoanDtls loanDtlsPayload = appDtls.getCustomerDetails().getLoanDtls();
					appMaster.setLoanMode(loanDtlsPayload.getLoanMode());
					appMaster.setProductCode(loanDtlsPayload.getShortDesc());
				}
				appMasterList.add(appMaster);
				logger.debug("First Save Audit Trail Details: {}, applicationId: {}", apiRequest, applicationId);
				saveAuditTrialDetails(apiRequest, applicationId);

				applicationIdRef = String.valueOf(applicationId);
				json.put(CommonConstants.APPLICATIONID, applicationIdRef);
				json.put(CommonConstants.STATUS, appDtls.getStatus());
				json.put(CommonConstants.VERSION_NO, "1");

			} else {
				applicationIdRef = appDtls.getApplicationId();
				try {
					List<String> statuses = Arrays.asList("REJECTED", "DISBURSED", "CANCELLED");
					List<ApplicationMaster> appMasterDetails = applicationMasterRepo
							.findApplicationBasedOnApplicationId(appDtls.getApplicationId(), statuses);
					logger.debug("Printing appMasterDetails: {}", appMasterDetails);
					if (!appMasterDetails.isEmpty()) {
						logger.debug("Loan application {} has already been processed with status: {}",
								appDtls.getApplicationId(), appMasterDetails.get(0).getApplicationStatus());
						json.put("messageCode", 409);
						json.put("message",
								"The loan application has already been rejected, disbursed, or cancelled. Please verify the details and try again.");
						return json;
					}
				} catch (Exception e) {
					logger.error("Exception occurred while checking application status for ID {}",
							appDtls.getApplicationId(), e);
				}
				String loanMode = null;
				String prodCode = null;
				if (appDtls.getCustomerDetails() != null && appDtls.getCustomerDetails().getLoanDtls() != null) {
					LoanDtls loanDtlsPayload = appDtls.getCustomerDetails().getLoanDtls();
					loanMode = loanDtlsPayload.getLoanMode();
					prodCode = loanDtlsPayload.getProductType();
				}
				if (appDtls.getAddInfo() != null) {
					if (appDtls.getAddInfo() instanceof String) {
						JSONObject json2 = updateAppDetails(appDtls, appDtls.getApplicationId(), appDtls.getVersionNo(),
								loanMode, prodCode);
						String verNo = (String) json2.get(CommonConstants.VERSION_NO);
						json.put(CommonConstants.APPLICATIONID, applicationIdRef);
						json.put(CommonConstants.STATUS, appDtls.getStatus());
						json.put(CommonConstants.VERSION_NO, verNo);
						logger.debug("Updated1 appDtls Audit Trail Details: {} ", appDtls);
					} else {
						JSONObject json1 = updateAppDetailsforAddInfo(appDtls, appDtls.getApplicationId(),
								appDtls.getVersionNo(), loanMode, prodCode);
						String verNo = (String) json1.get(CommonConstants.VERSION_NO);
						json.put(CommonConstants.APPLICATIONID, applicationIdRef);
						json.put(CommonConstants.STATUS, appDtls.getStatus());
						json.put(CommonConstants.VERSION_NO, verNo);
						logger.debug("Updated2 appDtls for Audit Trail Details: {} ", appDtls);
					}
				} else {
					JSONObject json3 = updateAppDetails(appDtls, appDtls.getApplicationId(), appDtls.getVersionNo(),
							loanMode, prodCode);
					String verNo = (String) json3.get(CommonConstants.VERSION_NO);
					json.put(CommonConstants.APPLICATIONID, applicationIdRef);
					json.put(CommonConstants.STATUS, appDtls.getStatus());
					json.put(CommonConstants.VERSION_NO, verNo);
					logger.debug("Updated3 appDtls for Audit Trail Details:{} ", appDtls);
				}
				String exitingApplicationId = appDtls.getApplicationId();
				logger.debug("exitingApplicationId: {}, appDtls: {}", exitingApplicationId, appDtls);
				saveAuditTrialDetailsForUpdated(appDtls, exitingApplicationId, apiRequest.getRequestObj().getUserRole(),
						apiRequest.getRequestObj().getUserId(), apiRequest.getRequestObj().getRemarks(),
						apiRequest.getRequestObj().getUserName(), apiRequest.getRequestObj().getAppVersion());
			}
		}
		if (!appMasterList.isEmpty()) {
			applicationMasterRepo.saveAll(appMasterList);
		}
		return json;
	}

	@Transactional
	public JSONObject updateAppDetails(ApplicationDtls appDtls, String applicationId, String versionNum,
			String loanMode, String prodCode) {
		// DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		int ver = Integer.parseInt(versionNum) + 1;
		String verNo = String.valueOf(ver);

		String sql = "UPDATE TB_UACO_APPLICATION_MASTER SET LATEST_VERSION_NO = :versionNum, "
				+ "APPLICATION_TYPE = :applicationType, KYC_TYPE = :kycType, "
				+ "CURRENT_STAGE = :currentStage, CURRENT_SCREEN_ID = :currentScreenId, PRODUCT_CODE = :productCode, "
				+ "PRODUCT_GROUP_CODE = :productGroupCode, BRANCH_CODE = :branchId, CB_CHECK = :cbCheck, CUSTOMER_ID = :customerId, "
				+ "KENDRA_ID = :kendraId, KMID = :kmId, LEADER = :leader, LOANMODE = :loanMode, AMOUNT = :amount, REMARKS = :remarks "
				+ "WHERE APPLICATION_ID = :applicationId AND LATEST_VERSION_NO = :versionNo";

		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("versionNum", verNo);
		query.setParameter("applicationType", appDtls.getApplicationType());
		query.setParameter("kycType", appDtls.getKycType());
		query.setParameter("currentStage", appDtls.getCurrentStage());
		query.setParameter("currentScreenId", appDtls.getCurrentScreenId());
		query.setParameter("productCode", prodCode);
		query.setParameter("productGroupCode", appDtls.getProductGroupCode());
		query.setParameter("branchId", appDtls.getBranchId());
		query.setParameter("cbCheck", appDtls.getCbCheck());
		query.setParameter("customerId", appDtls.getCustomerId());
		query.setParameter("kendraId", appDtls.getKendraId());
		query.setParameter("kmId", appDtls.getKmId());
		query.setParameter("leader", appDtls.getLeader());
		query.setParameter("loanMode", loanMode);
		query.setParameter("applicationId", applicationId);
		query.setParameter("versionNo", versionNum);
		query.setParameter("amount", appDtls.getAmount());
		query.setParameter("remarks", appDtls.getRemarks());
		// query.setParameter("add_info", appDtls.getAddInfo());
		query.executeUpdate();
		JSONObject json = new JSONObject();
		json.put("versionNo", verNo);
		return json;
	}

	@Transactional
	public JSONObject updateAppDetailsforAddInfo(ApplicationDtls appDtls, String applicationId, String versionNum,
			String loanMode, String prodCode) {
		// DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
		int ver = Integer.parseInt(versionNum) + 1;
		String verNo = String.valueOf(ver);
		String sql = "UPDATE TB_UACO_APPLICATION_MASTER SET LATEST_VERSION_NO = :versionNum, "
				+ "APPLICATION_TYPE = :applicationType, KYC_TYPE = :kycType, "
				+ "CURRENT_STAGE = :currentStage, CURRENT_SCREEN_ID = :currentScreenId, PRODUCT_CODE = :productCode, "
				+ "PRODUCT_GROUP_CODE = :productGroupCode, BRANCH_CODE = :branchId, CB_CHECK = :cbCheck, CUSTOMER_ID = :customerId, "
				+ "KENDRA_ID = :kendraId, KMID = :kmId, LEADER = :leader, LOANMODE = :loanMode, ADD_INFO = :add_info, AMOUNT = :amount, REMARKS = :remarks "
				+ "WHERE APPLICATION_ID = :applicationId AND LATEST_VERSION_NO = :versionNo";

		Query query = entityManager.createNativeQuery(sql);
		query.setParameter("versionNum", verNo);
		query.setParameter("applicationType", appDtls.getApplicationType());
		query.setParameter("kycType", appDtls.getKycType());
		query.setParameter("currentStage", appDtls.getCurrentStage());
		query.setParameter("currentScreenId", appDtls.getCurrentScreenId());
		query.setParameter("productCode", prodCode);
		query.setParameter("productGroupCode", appDtls.getProductGroupCode());
		query.setParameter("branchId", appDtls.getBranchId());
		query.setParameter("cbCheck", appDtls.getCbCheck());
		query.setParameter("customerId", appDtls.getCustomerId());
		query.setParameter("kendraId", appDtls.getKendraId());
		query.setParameter("kmId", appDtls.getKmId());
		query.setParameter("leader", appDtls.getLeader());
		query.setParameter("loanMode", loanMode);
		query.setParameter("applicationId", applicationId);
		query.setParameter("versionNo", versionNum);
		query.setParameter("amount", appDtls.getAmount());
		query.setParameter("remarks", appDtls.getRemarks());
		Gson gson = new Gson();
		String addInfoVal = gson.toJson(appDtls.getAddInfo());
		query.setParameter("add_info", addInfoVal);
		query.executeUpdate();
		JSONObject json = new JSONObject();
		json.put("versionNo", verNo);
		return json;
	}

	private void saveOccupationDtls(ApplyLoanRequest apiRequest, String applicationIdRef, String custId) {

		List<ApplicationDtls> applicationPayload = apiRequest.getRequestObj().getApplicationdtls();
		List<TbUaobOccupationDtls> occupationDtlList = new ArrayList<>();

		logger.debug("inside saveOccupationDtls: {}", apiRequest);
		for (ApplicationDtls appDtls : applicationPayload) {

			if (appDtls.getCustomerDetails() != null && appDtls.getCustomerDetails().getIncome() != null
					&& appDtls.getCustomerDetails().getEarnings() != null) {
				if (CommonUtils.isNullOrEmpty(appDtls.getApplicationId())
						&& CommonUtils.isNullOrEmpty(appDtls.getVersionNo())) {
					List<Income> incomeDtls = appDtls.getCustomerDetails().getIncome();
					List<Earnings> earnings = appDtls.getCustomerDetails().getEarnings();
					ObjectMapper obj = new ObjectMapper();
					BigDecimal occptId = CommonUtils.generateRandomNum();
					TbUaobOccupationDtls occupationDtlDomain = new TbUaobOccupationDtls();
					occupationDtlDomain.setOccuPtDtlsId(String.valueOf(occptId));
					occupationDtlDomain.setCustDtlId(custId);
					occupationDtlDomain.setAppId(apiRequest.getAppId());
					occupationDtlDomain.setVersionNo("1");
					occupationDtlDomain.setApplicationId(applicationIdRef);
					try {
						occupationDtlDomain.setIncomePayload(obj.writeValueAsString(incomeDtls));
						occupationDtlDomain.setEarningsPayload(new ObjectMapper().writeValueAsString(earnings));
					} catch (JsonProcessingException e) {
						logger.error("exception at saveOccupationDtls: ", e);
					}
					occupationDtlList.add(occupationDtlDomain);
				} else {
					Optional<TbUaobOccupationDtls> occupDomain = tbUaobOccpationDtlRepo
							.findByApplicationId(appDtls.getApplicationId());
					if (occupDomain.isPresent()) {
						List<Income> incomeDtls = appDtls.getCustomerDetails().getIncome();
						List<Earnings> earnings = appDtls.getCustomerDetails().getEarnings();
						ObjectMapper obj = new ObjectMapper();
						String ver = String.valueOf(Integer.parseInt(appDtls.getVersionNo()) + 1);

						occupDomain.get().setVersionNo(ver);
						try {
							occupDomain.get().setIncomePayload(obj.writeValueAsString(incomeDtls));
							occupDomain.get().setEarningsPayload(new ObjectMapper().writeValueAsString(earnings));

						} catch (JsonProcessingException e) {
							logger.error("exception at saveOccupationDtls in else case: ", e);
						}
						tbUaobOccpationDtlRepo.save(occupDomain.get());
					}
				}
			}
		}

		if (!occupationDtlList.isEmpty()) {
			tbUaobOccpationDtlRepo.saveAll(occupationDtlList);
		}
		logger.debug("data saved to OccupationDtls: {}", occupationDtlList);

	}

	private void saveInsuranceDtls(ApplyLoanRequest apiRequest, String applicationIdRef, String custId) {
		List<ApplicationDtls> applicationPayload = apiRequest.getRequestObj().getApplicationdtls();
		List<TbUacoInsuranceDtls> insuranceDtlsList = new ArrayList<>();
		logger.debug("inside saveInsuranceDtlsDetails: {}", apiRequest);
		for (ApplicationDtls appDtls : applicationPayload) {
			BigDecimal applicationId = CommonUtils.generateRandomNum();
			if (appDtls.getCustomerDetails() != null && appDtls.getCustomerDetails().getLoanDtls() != null
					&& appDtls.getCustomerDetails().getLoanDtls().getInsurDtls() != null) {
				InsuranceDetails insuranceDtls = appDtls.getCustomerDetails().getLoanDtls().getInsurDtls();
				if (CommonUtils.isNullOrEmpty(appDtls.getApplicationId())
						&& CommonUtils.isNullOrEmpty(appDtls.getVersionNo())) {
					TbUacoInsuranceDtls insuranceDtlDomain = new TbUacoInsuranceDtls();
					insuranceDtlDomain.setInsuranceDtlId(String.valueOf(applicationId));
					insuranceDtlDomain.setCustDtlId(custId);
					insuranceDtlDomain.setAppId(apiRequest.getAppId());
					insuranceDtlDomain.setVersionNo("1");
					insuranceDtlDomain.setApplicationId(applicationIdRef);
					try {
						insuranceDtlDomain.setPayload(new ObjectMapper().writeValueAsString(insuranceDtls));
					} catch (JsonProcessingException e) {
						logger.error("exception at saveInsuranceDtlsDetails: ", e);
					}
					insuranceDtlsList.add(insuranceDtlDomain);
				} else {
					Optional<TbUacoInsuranceDtls> tbUacoInsOPt = tbUacoInsuranceDtlsRepo
							.findByApplicationId(applicationIdRef);
					if (tbUacoInsOPt.isPresent()) {
						String updatedVer = String.valueOf(Integer.parseInt(tbUacoInsOPt.get().getVersionNo()) + 1);
						tbUacoInsOPt.get().setVersionNo(updatedVer);
						try {
							tbUacoInsOPt.get().setPayload(new ObjectMapper().writeValueAsString(insuranceDtls));
						} catch (JsonProcessingException e) {
							logger.error("exception at saveInsuranceDtlsDetails in else case: ", e);
						}
						tbUacoInsuranceDtlsRepo.save(tbUacoInsOPt.get());
					}
				}
			}
		}
		if (!insuranceDtlsList.isEmpty()) {
			tbUacoInsuranceDtlsRepo.saveAll(insuranceDtlsList);
		}
		logger.debug("data saved to saveInsuranceDtlsDetails: {}", insuranceDtlsList);
	}

	private String saveCustomerDetails(ApplyLoanRequest apiRequest, String applicationIdRef) {
		logger.debug("inside saveCustomerDetails: {}", apiRequest);
		String custId = null;
		List<ApplicationDtls> applicationPayload = apiRequest.getRequestObj().getApplicationdtls();
		List<TbUaobCustDtls> custDtlsDomainList = new ArrayList<>();
		if (!applicationPayload.isEmpty()) {
			for (ApplicationDtls appDtls : applicationPayload) {
				if (appDtls.getCustomerDetails() != null) {
					CustomerDtls custDtlsPaylod = appDtls.getCustomerDetails();
					ChargeAndBreakupDetails loanCharges = custDtlsPaylod.getLoanDtls().getChargeAndBreakupDtls();
					if (CommonUtils.isNullOrEmpty(appDtls.getApplicationId())
							&& CommonUtils.isNullOrEmpty(appDtls.getVersionNo())) {
						BigDecimal applicationId = CommonUtils.generateRandomNum();
						TbUaobCustDtls custDtlsDomain = new TbUaobCustDtls();
						custDtlsDomain.setCustDtlId(String.valueOf(applicationId));
						custDtlsDomain.setAppId(apiRequest.getAppId());
						custDtlsDomain.setVersionNo("1");
						custDtlsDomain.setApplicationId(applicationIdRef);
						custDtlsDomain.setCustomerId(custDtlsPaylod.getCustomerId());
						custDtlsDomain.setCustomerName(custDtlsPaylod.getCustomerName());
						custDtlsDomain.setGroupId(appDtls.getGroupId());
						custDtlsDomain.setKendraId(appDtls.getKendraId());
						custDtlsDomain.setEligibleLoanAmt(loanCharges.getLoanAmt());
						try {
							custDtlsDomain
									.setKycDetails(new ObjectMapper().writeValueAsString(custDtlsPaylod.getKycDtls()));
							custDtlsDomain
									.setBankDtls(new ObjectMapper().writeValueAsString(custDtlsPaylod.getBankDtls()));
						} catch (JsonProcessingException e) {
							logger.error("error while saving kycdetls and bankdtls in customer table:", e);
						}
						custDtlsDomainList.add(custDtlsDomain);
						custId = String.valueOf(applicationId);
					} else {
						Optional<TbUaobCustDtls> custDtl = custDtlsRepo.findByApplicationId(appDtls.getApplicationId());
						if (custDtl.isPresent()) {
							String ver = String.valueOf(Integer.parseInt(appDtls.getVersionNo()) + 1);
							custDtl.get().setVersionNo(ver);
							custDtl.get().setCustomerId(custDtlsPaylod.getCustomerId());
							custDtl.get().setCustomerName(custDtlsPaylod.getCustomerName());
							custDtl.get().setGroupId(appDtls.getGroupId());
							custDtl.get().setKendraId(appDtls.getKendraId());
							custDtl.get().setEligibleLoanAmt(loanCharges == null ? custDtl.get().getEligibleLoanAmt()
									: loanCharges.getLoanAmt());
							try {
								custDtl.get().setKycDetails(
										new ObjectMapper().writeValueAsString(custDtlsPaylod.getKycDtls()));
								custDtl.get().setBankDtls(
										new ObjectMapper().writeValueAsString(custDtlsPaylod.getBankDtls()));
							} catch (JsonProcessingException e) {
								logger.error("error while saving kycdetls and bankdtls in customer table:", e);
							}
							custDtlsRepo.save(custDtl.get());
						}
						custId = appDtls.getApplicationId();
					}
				}

			}
			if (!custDtlsDomainList.isEmpty()) {
				custDtlsRepo.saveAll(custDtlsDomainList);
			}
			logger.debug("data saved to CustomerDetails: {}", custDtlsDomainList);
		}
		return custId;
	}

	private void saveLoanDetails(ApplyLoanRequest apiRequest, String applicationIdRef, String custId) {
		logger.debug("inside saveLoanDetails: {}", apiRequest);
		List<ApplicationDtls> applicationPayload = apiRequest.getRequestObj().getApplicationdtls();
		List<TbUalnLoanDtls> loanDtlsDomainList = new ArrayList<>();
		List<TbUaobNomineeDetails> nomineeDtlsList = new ArrayList<>();
		ObjectMapper mapper = new ObjectMapper();

		for (ApplicationDtls appDtls : applicationPayload) {
			if (appDtls.getCustomerDetails() != null && appDtls.getCustomerDetails().getLoanDtls() != null) {
				LoanDtls loanDtlsPayload = appDtls.getCustomerDetails().getLoanDtls();
				if (CommonUtils.isNullOrEmpty(appDtls.getApplicationId())
						&& CommonUtils.isNullOrEmpty(appDtls.getVersionNo())) {
					BigDecimal id = CommonUtils.generateRandomNum();
					TbUalnLoanDtls loanDtlsDomain = new TbUalnLoanDtls();
					TbUaobNomineeDetails nomineeDtls = new TbUaobNomineeDetails();
					loanDtlsDomain.setLoanDtlId(String.valueOf(id));
					loanDtlsDomain.setAppId(apiRequest.getAppId());
					loanDtlsDomain.setVersionNum("1");
					loanDtlsDomain.setApplicationId(applicationIdRef);
					loanDtlsDomain.setLoanAmount(loanDtlsPayload.getChargeAndBreakupDtls().getLoanAmt());
					loanDtlsDomain.setTerm(loanDtlsPayload.getTerm());
					try {
						loanDtlsDomain.setFrequency(mapper.writeValueAsString(loanDtlsPayload.getRepayFrequency()));
						loanDtlsDomain.setInterestRate(String.valueOf(loanDtlsPayload.getInterestRate()));
//						JSONObject payload = createPayload(loanDtlsPayload);
						loanDtlsDomain.setPayload(mapper.writeValueAsString(loanDtlsPayload));
						loanDtlsDomain
								.setActiveLoanDtls(mapper.writeValueAsString(loanDtlsPayload.getActiveLoanDtls()));
					} catch (Exception e) {
						logger.error("exception at saveLoanDetails: ", e);
					}
					// activeLoanDtls
					loanDtlsDomainList.add(loanDtlsDomain);
					nomineeDtls.setNomineeDtlsId(String.valueOf(id));
					nomineeDtls.setAppId(apiRequest.getAppId());
					nomineeDtls.setVersionNo("1");
					nomineeDtls.setApplicationId(applicationIdRef);
					nomineeDtls.setCustDtlId(custId);
					try {
						nomineeDtls.setPayload(mapper.writeValueAsString(loanDtlsPayload.getNomineeDtls()));
					} catch (Exception e) {
						logger.error("exception at saveNomineeDetails: ", e);
					}
					nomineeDtlsList.add(nomineeDtls);
				} else {
					Optional<TbUalnLoanDtls> loanOpt = loanDtlsRepo.findByApplicationId(applicationIdRef);
					if (loanOpt.isPresent()) {
						loanOpt.get()
								.setVersionNum(String.valueOf(Integer.parseInt(loanOpt.get().getVersionNum()) + 1));
						String loanAmt = loanDtlsPayload.getChargeAndBreakupDtls() == null
								? loanOpt.get().getLoanAmount()
								: loanDtlsPayload.getChargeAndBreakupDtls().getLoanAmt();
						loanOpt.get().setLoanAmount(loanAmt);
						loanOpt.get().setTerm(loanDtlsPayload.getTerm());
						try {
							loanOpt.get().setFrequency(mapper.writeValueAsString(loanDtlsPayload.getRepayFrequency()));
							loanOpt.get().setInterestRate(String.valueOf(loanDtlsPayload.getInterestRate()));
//							JSONObject payload = createPayload(loanDtlsPayload);
							if (loanDtlsPayload.getInsurDtls() != null
									&& loanDtlsPayload.getChargeAndBreakupDtls() != null) {
								loanOpt.get().setPayload(mapper.writeValueAsString(loanDtlsPayload));
							} else {
								loanOpt.get().setPayload(mapper.writeValueAsString(loanOpt.get().getPayload()));
							}

							String activeLoanDtlsJsonString = mapper
									.writeValueAsString(loanDtlsPayload.getActiveLoanDtls());
							loanOpt.get().setActiveLoanDtls(activeLoanDtlsJsonString);
						} catch (Exception e) {
							logger.error("exception at saveLoanDetails: ", e);
						}
						loanDtlsRepo.save(loanOpt.get());
					}
					Optional<TbUaobNomineeDetails> nomineeOpt = nomineeRepository.findByApplicationId(applicationIdRef);
					if (nomineeOpt.isPresent()) {
						nomineeOpt.get()
								.setVersionNo(String.valueOf(Integer.parseInt(nomineeOpt.get().getVersionNo()) + 1));
						try {
							nomineeOpt.get().setPayload(mapper.writeValueAsString(loanDtlsPayload.getNomineeDtls()));
						} catch (Exception e) {
							logger.error("exception at saveNomineeDetails: ", e);
						}
						nomineeRepository.save(nomineeOpt.get());
					}
				}
			}
		}
		if (!nomineeDtlsList.isEmpty()) {
			nomineeRepository.saveAll(nomineeDtlsList);
		}
		if (!loanDtlsDomainList.isEmpty()) {
			loanDtlsRepo.saveAll(loanDtlsDomainList);
		}
		logger.debug("data saved to loan details table: {}", loanDtlsDomainList);
	}

	private JSONObject createPayload(LoanDtls loanDtlsPayload) {
		JSONObject json = new JSONObject();
		json.put(CommonConstants.DISBMODE, loanDtlsPayload.getDisburseMode());
		json.put(CommonConstants.INSURPER, loanDtlsPayload.getInsurancePercentage());
		json.put(CommonConstants.SPOUSEINS, loanDtlsPayload.getSpouseInsurance());
		json.put(CommonConstants.PRODTYPE, loanDtlsPayload.getProductType());
		json.put(CommonConstants.PRODID, loanDtlsPayload.getProductId());
		json.put(CommonConstants.SHORTDESC, loanDtlsPayload.getShortDesc());
		json.put(CommonConstants.CBAMT, loanDtlsPayload.getCbAmt());
		json.put(CommonConstants.LOANMODE, loanDtlsPayload.getLoanMode());
		try {
			json.put(CommonConstants.CHARGES,
					new ObjectMapper().writeValueAsString(loanDtlsPayload.getChargeAndBreakupDtls()));
		} catch (Exception e) {
			logger.error("error while reading charge and breakupdetails", e);
		}
		json.put(CommonConstants.PURPOSE, loanDtlsPayload.getPurpose());

		return json;
	}

	@CircuitBreaker(name = "fallback", fallbackMethod = "cbCheckFallback")
	public Mono<Object> cbCheck(CbRequest cbRequest, Header header, String schedulerFlag) {
		this.cbRequest = cbRequest;
		this.header = header;
		logger.debug("calling cbCheck external service");
		try {
			List<String> statuses = Arrays.asList("REJECTED", "DISBURSED", "CANCELLED");
			List<ApplicationMaster> appMasterDetails = applicationMasterRepo
					.findApplicationBasedOnApplicationId(cbRequest.getRequestObj().getApplicationId(), statuses);
			logger.debug("Printing appMasterDetails: {}", appMasterDetails);
			if (!appMasterDetails.isEmpty()) {
				logger.debug("Loan application {} has already been processed with status: {}",
						cbRequest.getRequestObj().getApplicationId(), appMasterDetails.get(0).getApplicationStatus());
				Throwable error = new Throwable("Loan application has already been processed");
				return cbCheckFallback(cbRequest, header, schedulerFlag, error);
			}
		} catch (Exception e) {
			logger.error("Exception occurred while checking application status for ID {}",
					cbRequest.getRequestObj().getApplicationId(), e);
		}
		ResponseBody responseBody = new ResponseBody();
		Response response = new Response();
		ResponseWrapper responseWrapper = new ResponseWrapper();

		return Mono.fromCallable(() -> formRequestCbCheck(cbRequest))
				.flatMap(cbCheckRequestResponse -> cbCheckRequestResponse).map(val -> {
					ObjectMapper objMapper = new ObjectMapper();
					CbCheckRequest cbCheckReq = objMapper.convertValue(val, CbCheckRequest.class);
					cbCheckReq.getRequestObj().setApplicationId(cbRequest.getRequestObj().getApplicationId());
					cbCheckReq.getRequestObj().setVersionNo(cbRequest.getRequestObj().getVersionNo());
					cbCheckReq.getRequestObj().setCustDtlId(cbRequest.getRequestObj().getCustDtlId());

					// Address logic
					List<AddressPayload> addressList = cbCheckReq.getRequestObj().getAddress();
					List<AddressPayload> addressPayload = addressList.stream().peek(addr -> {
						if ("P".equalsIgnoreCase(addr.getAddrType())) {
							addr.setCity(addr.getDistrict());
						}
					}).collect(Collectors.toList());

					cbCheckReq.getRequestObj().setAddress(addressPayload);

					if ("true".equalsIgnoreCase(cbRequest.getRequestObj().getCrtFlag())) {
						cbCheckReq.getRequestObj().setCrtFlag("1");
						cbCheckReq.getRequestObj().setCrtApprovedamount(cbCheckReq.getRequestObj().getLoanAmount());
						cbCheckReq.getRequestObj().setCrtIdentifier("CRT Approved");
					} else {
						cbCheckReq.getRequestObj().setCrtFlag("0");
						cbCheckReq.getRequestObj().setCrtApprovedamount("0");
						cbCheckReq.getRequestObj().setCrtIdentifier("0");
					}

					return cbCheckReq;
				}).flatMap(cbCheckReq -> {
					Timestamp reqTs = new Timestamp(System.currentTimeMillis());
					Mono<Object> cbRes = this.interfaceAdapter.callExternalService(header, cbCheckReq,
							cbRequest.getInterfaceName(), true);
					return saveCbCheckData(cbRes, cbCheckReq, header, cbRequest, reqTs, schedulerFlag).flatMap(val2 -> {
						logger.debug("CB CHECK RESPONSE STR : {}", val2);
						responseBody.setResponseObj(new Gson().toJson(val2));
						response.setResponseBody(responseBody);
						responseWrapper.setApiResponse(response);
						return Mono.just(val2);
					});
				}).onErrorResume(ex -> {
					logger.error("Exception in cbCheck reactive chain", ex);
					return cbCheckFallback(cbRequest, header, schedulerFlag, ex);
				});
	}

	private Mono<Object> formRequestCbCheck(CbRequest cbRequest) {

		DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
		DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

		CbCheckRequest cbCheckReq = new CbCheckRequest();
		CbCheckRequestFields reqFields = new CbCheckRequestFields();
		List<AddressPayload> addressPayloadList = new ArrayList<>();
		List<DocumentPayload> docPayloadList = new ArrayList<>();

		List<ApplicationMaster> appMasterList = applicationMasterRepo
				.findByApplicationId(cbRequest.getRequestObj().getApplicationId());

		Optional<TbUaobCustDtls> custDtls = custDtlsRepo
				.findByApplicationId(cbRequest.getRequestObj().getApplicationId());
		Optional<TbUaobOccupationDtls> occupationData = tbUaobOccpationDtlRepo
				.findByApplicationId(cbRequest.getRequestObj().getApplicationId());
		List<TbUaobAddressDetails> addressData = addressRepository
				.findByApplicationId(cbRequest.getRequestObj().getApplicationId());
		Optional<TbUalnLoanDtls> loanData = loanDtlsRepo
				.findByApplicationId(cbRequest.getRequestObj().getApplicationId());

		if (!addressData.isEmpty()) {
			for (TbUaobAddressDetails addDomain : addressData) {

				AddressPayload addPayload = new AddressPayload();
				JSONObject payloadObj = new JSONObject(addDomain.getPayload());

				addPayload.setAddrType(addDomain.getAddressType());
				if (payloadObj.has(CommonConstants.ADDLINE1)) {
					String addLine1 = payloadObj.getString(CommonConstants.ADDLINE1);
					if (StringUtils.isNotBlank(addLine1)) {
						addLine1 = addLine1.replaceAll("[^a-zA-Z0-9]", " ");
					}
					addPayload.setAddrLine1(addLine1);
				}
				if (payloadObj.has(CommonConstants.VILLAGELOCALITY)) {
					addPayload.setCity(payloadObj.getString(CommonConstants.VILLAGELOCALITY));
				}
				if (payloadObj.has(CommonConstants.DISTRICT)) {
					addPayload.setDistrict(payloadObj.getString(CommonConstants.DISTRICT));
				}
				if (payloadObj.has(CommonConstants.STATE)) {
					addPayload.setState(payloadObj.getString(CommonConstants.STATE));
				}
				if (payloadObj.has(CommonConstants.PINCODE)) {
					addPayload.setPinCode(payloadObj.getString(CommonConstants.PINCODE));
				}
				addressPayloadList.add(addPayload);
			}

		}
		reqFields.setAddress(addressPayloadList);

		if (custDtls.isPresent()) {
			JSONObject kycJson = new JSONObject(custDtls.get().getKycDetails());
			if (kycJson.has("primaryType") && kycJson.getString("primaryType") != null) {
				String docTypeStr = kycJson.getString("primaryType");
				String docIdStr = kycJson.getString("primaryId");
				List<String> docTypList = Arrays.asList(docTypeStr.split(",")).stream()
						.filter(a -> StringUtils.isNotBlank(a)).collect(Collectors.toList());
				List<String> docIdList = Arrays.asList(docIdStr.split(",")).stream()
						.filter(a -> StringUtils.isNotBlank(a)).collect(Collectors.toList());
				for (int i = 0; i < docTypList.size(); i++) {
					DocumentPayload docPayload = new DocumentPayload();
					docPayload.setDocType(docTypList.get(i));
					docPayload.setDocId(docIdList.get(i).replaceAll("[^a-zA-Z0-9]", ""));
					docPayloadList.add(docPayload);
				}
			}
			if (kycJson.has("dob")) {
				LocalDate dob = LocalDate.parse(kycJson.getString("dob"), inputFormatter);
				String formattedDob = dob.format(outputFormatter);
				reqFields.setDob(formattedDob);
			}
			if (kycJson.has("mobileNum")) {
				reqFields.setPhone(kycJson.getString("mobileNum"));
			}
			reqFields.setCustName(custDtls.get().getCustomerName());
			reqFields.setEmail("");
			if (kycJson.has("stateBranch") && kycJson.isNull("stateBranch") == false) {
				reqFields.setStateBranch(kycJson.getString("stateBranch"));
			}
			if (kycJson.has("activationDate") && kycJson.isNull("activationDate") == false) {
				reqFields.setActivationDate(kycJson.getString("activationDate"));
			}
		}
		reqFields.setDocument(docPayloadList);
		reqFields.setSlNo("1");
		List<HouseholdMemberPayload> houseHoldList = new ArrayList<>();
		if (occupationData.isPresent()) {
			JSONArray earningPayloadArr = new JSONArray(occupationData.get().getEarningsPayload());
			JSONObject earningPayload = new JSONObject();
			if (earningPayloadArr != null && earningPayloadArr.length() > 0) {
				earningPayload = earningPayloadArr.getJSONObject(0);
			}
			if (earningPayload.has(CommonConstants.MEM_RELATION)) {
				reqFields.setDepType(earningPayload.getString(CommonConstants.MEM_RELATION));
			}
			if (earningPayload.has("name")) {
				reqFields.setDepName(earningPayload.getString("name"));
			}

			JSONArray incomePayloadArr = new JSONArray(occupationData.get().getIncomePayload());
			/*
			 * JSONObject incomePayload = incomePayloadArr.getJSONObject(0);
			 * 
			 * 
			 * if (incomePayload.has("totIncome")) {
			 * reqFields.setHhAnnualIncome(incomePayload.getString("totIncome"));
			 * 
			 * }
			 */

			String hhAnnualIncome = "0";
			if (incomePayloadArr.length() > 0) {
				JSONObject incomePayload = incomePayloadArr.getJSONObject(0);
				if (incomePayload.has("totIncome") && !incomePayload.isNull("totIncome")) {
					String totIncome = incomePayload.getString("totIncome");
					if (totIncome != null && !totIncome.trim().isEmpty()) {
						hhAnnualIncome = totIncome;
					}
				}
			}
			reqFields.setHhAnnualIncome(hhAnnualIncome);

			for (int i = 0; i < earningPayloadArr.length(); i++) {
				HouseholdMemberPayload memberPayload = new HouseholdMemberPayload();
				JSONObject earningJson = earningPayloadArr.getJSONObject(i);
				if (earningPayload.has(CommonConstants.MEM_RELATION)) {
					memberPayload.setRelationtype(earningJson.getString(CommonConstants.MEM_RELATION));
				}
				if (earningPayload.has("dob")) {
					LocalDate dob = LocalDate.parse(earningJson.getString("dob"), inputFormatter);
					String formattedDob = dob.format(outputFormatter);
					memberPayload.setDob(formattedDob);
				}
				if (earningPayload.has("name")) {
					memberPayload.setCustName(earningJson.getString("name"));
				}
				memberPayload.setGender("1");
				memberPayload.setEarningFlag("ABC");
				memberPayload.setApplicantType("H");
				if (custDtls.isPresent()) {
					JSONObject kycJson = new JSONObject(custDtls.get().getKycDetails());
					if (kycJson.has("mobileNum")) {
						memberPayload.setPhone(kycJson.getString("mobileNum"));
					}
				}
				memberPayload.setAddress(addressPayloadList);
				List<DocumentPayload> houseHoldDocList = new ArrayList<>();
				if (earningJson.has("legaldocName") && earningJson.getString("legaldocId") != null) {
					String docTypeStr = earningJson.getString("legaldocName");
					String docIdStr = earningJson.getString("legaldocId");
					List<String> docTypList = Arrays.asList(docTypeStr.split(",")).stream()
							.filter(a -> StringUtils.isNotBlank(a)).collect(Collectors.toList());
					List<String> docIdList = Arrays.asList(docIdStr.split(",")).stream()
							.filter(a -> StringUtils.isNotBlank(a)).collect(Collectors.toList());
					for (int hm = 0; hm < docTypList.size(); hm++) {
						DocumentPayload docPayload = new DocumentPayload();
						docPayload.setDocType(docTypList.get(hm));
						docPayload.setDocId(docIdList.get(hm).replaceAll("[^a-zA-Z0-9]", ""));
						houseHoldDocList.add(docPayload);
					}
				}
				memberPayload.setDocument(houseHoldDocList);
				houseHoldList.add(memberPayload);
			}

			reqFields.setHouseholdMember(houseHoldList);
		}
		reqFields.setCategoryID("1");
		// reqFields.setProductcode("CCR");
		reqFields.setDurationOfAgreement("12");
		reqFields.setBankProductId("01");
		reqFields.setGender("2");
		reqFields.setLoanType("2");
		reqFields.setAppId(cbRequest.getRequestObj().getApplicationId());
		reqFields.setLosIndex("LOS");
		reqFields.setLosIndicator("LOS");
		reqFields.setMaritalstatus("2");
		if (!appMasterList.isEmpty()) {
			reqFields.setKendra(appMasterList.get(0).getKendraId());
			reqFields.setGroupID(appMasterList.get(0).getKendraId());
			reqFields.setBranch(appMasterList.get(0).getBranchCode());
			reqFields.setCustId(appMasterList.get(0).getCustomerId());
			// Read amount from the master instead of Loan Table
			// reqFields.setLoanAmount(appMasterList.get(0).getAmount().toString());
		}

		reqFields.setProspectID(cbRequest.getRequestObj().getApplicationId());
		reqFields.setEnquiryType("A");
		reqFields.setLoanId(cbRequest.getRequestObj().getApplicationId());
		reqFields.setDigiAgilDFAFlag("MAITRI_LOAN");
		reqFields.setCustomerEnquiryFlag("Loan Application");
		reqFields.setEarningFlag("ABC");
		if (loanData.isPresent()) {
			JSONObject loanPayload = new JSONObject(loanData.get().getPayload());
			if (loanPayload.has("productId")) {
				reqFields.setLoanProductType(loanPayload.getString("productId"));
				reqFields.setLoanProductcode(loanPayload.getString("productId"));
				reqFields.setProductcode("CCR");
				reqFields.setProduct_code(loanPayload.getString("productId"));
			}
			reqFields.setAppliedTenure(loanData.get().getTerm());
			// commenting here instead of will read from master
			reqFields.setLoanAmount(loanData.get().getLoanAmount());
			JSONObject freqPayload = new JSONObject(loanData.get().getFrequency());
			if (freqPayload.has("idDesc")) {
				reqFields.setAppliedFrequency(freqPayload.getString("idDesc"));
			}

			if (StringUtils.isNotBlank(loanData.get().getPayload())) {
				Gson gson = new Gson();
				LoanDtls loanDtl = gson.fromJson(loanData.get().getPayload(), LoanDtls.class);
				InsuranceDetails insDtl = loanDtl.getInsurDtls();
				reqFields.setApplicantInsurance(insDtl.getMember());
				reqFields.setApplicantInsuranceAmt(insDtl.getApplicant_insurance_amt());
				reqFields.setSpouseInsurance(insDtl.getSpouse());
				reqFields.setSpouseInsuranceAmt(insDtl.getSpouse_insurance_amt());
			}

		}

		cbCheckReq.setAppId(cbRequest.getAppId());
		cbCheckReq.setUserId(cbRequest.getUserId());
		cbCheckReq.setInterfaceName(cbRequest.getInterfaceName());
		cbCheckReq.setRequestObj(reqFields);

		return Mono.just(cbCheckReq);
	}

	private Mono<Object> saveCbCheckData(Mono<Object> response, CbCheckRequest cbCheckRequest, Header header,
			CbRequest cbRequest, Timestamp reqTs, String schedulerFlag) {
		return response.flatMap(val -> {
			ResponseWrapper responseWrapper = adapterUtil.getResponseMapper(val, cbCheckRequest.getInterfaceName(),
					header, true);
			logger.debug("Printing CB Response:" + responseWrapper);
			String respObj = responseWrapper.getApiResponse().getResponseBody().getResponseObj();

			JSONObject cbResJson = new JSONObject(respObj);
			String applicationId = cbRequest.getRequestObj().getApplicationId();
			Optional<TbUaobCbResponse> cbRes = cbResRepository
					.findByAppIdAndApplicationIdOrderByVersionNumDesc(cbCheckRequest.getAppId(), applicationId);
			String crtflag = cbCheckRequest.getRequestObj().getCrtFlag();
			if (cbRes.isPresent()) {
				logger.debug("cbRes is present");
				logger.debug("CBResponse is present");
				TbUaobCbResponse cbResObj = cbRes.get();
				String resPayload = cbResObj.getResPayload();
				JSONObject cbResJson1 = new JSONObject(resPayload);
				logger.debug("cbResJson1 response" + cbResJson1);
				// Extract FOIR and EIR values
				String foir = cbResJson1.optString("Derived_Attribute_4", "Not available");
				logger.debug("foir response" + foir);
				String[] foirParts = foir.split("\\|");
				logger.debug("foirParts response" + foirParts);
				String foirValue = foirParts.length > 0 ? foirParts[0].replace("FOIR :", "").trim() : "Not available";
				logger.debug("foirValue response");
				String eir = cbResJson1.optString("eir", "Not available");
				logger.debug("eir value");
				// Fetch and update the ApplicationMaster record
				Optional<ApplicationMaster> opApplicationMasterRec1 = applicationMasterRepo
						.findTopByApplicationIdOrderByCreateTsDesc(applicationId);
				if (opApplicationMasterRec1.isPresent()) {
					ApplicationMaster applicationMasterRec = opApplicationMasterRec1.get();
					JSONObject addInfoJson;
					try {
						addInfoJson = new JSONObject(applicationMasterRec.getAddInfo());
					} catch (Exception e) {
						addInfoJson = new JSONObject();
					}
					// Add Derived_Attribute_4 and EIR to addInfo
					addInfoJson.put("Derived_Attribute_4", foirValue);
					addInfoJson.put("eir", eir);
					logger.debug("addInfoJson :" + addInfoJson);
					// Update and save ApplicationMaster
					applicationMasterRec.setAddInfo(addInfoJson.toString());
					logger.debug("Saving updated ApplicationMaster with AddInfo: " + addInfoJson.toString());
					applicationMasterRepo.save(applicationMasterRec);
				}
				Integer retryCnt = (cbResObj.getRetryCount() == null ? 0 : cbResObj.getRetryCount()) + 1;
				cbResObj.setRetryCount(retryCnt);
				cbResObj.setVersionNum(cbRequest.getRequestObj().getVersionNo());
				try {
					cbResObj.setReqPayload(new ObjectMapper().writeValueAsString(cbCheckRequest));
					cbResObj.setResPayload(respObj);
				} catch (JsonProcessingException e) {
					logger.error("error while saving cbResponse:", e);
				}
				cbResObj.setCbCheckstatus("FAILURE");
				if (cbResJson.has(CommonConstants.IRIS_MSG) && cbResJson.get(CommonConstants.IRIS_MSG) != null
						&& ((cbResJson.get(CommonConstants.IRIS_MSG).toString().equalsIgnoreCase("SUCCESS"))
								|| cbResJson.get(CommonConstants.IRIS_MSG).toString()
										.equalsIgnoreCase("bureau_data_issue_spouse_node_corrected_success"))) {
					cbResObj.setStatus("SUCCESS");
					if (cbResJson.isNull("Rejection_reason") || "1".equalsIgnoreCase(crtflag)
							|| cbResJson.getString("Rejection_reason").isEmpty()) {
						cbResObj.setCbCheckstatus("SUCCESS");

					}
					String cbDecision = cbResJson.getString("Final_Decision");
					if ("REJECT".equalsIgnoreCase(cbDecision)
							|| "Deviation".equalsIgnoreCase(cbDecision)) {
						cbResObj.setCbCheckstatus("FAILURE");
					}
				} else {
					cbResObj.setStatus("FAILURE");
					// CB Pending
					Integer stage_id = 3;
					logger.debug(
							"Updating for exiting saved AuditTrialDetails when CBCheck happened for CB Pending 3(1):"
									+ cbRequest);
					saveAuditTrialDetailsForCBCheck(cbRequest, stage_id);
				}
				cbResObj.setReqTs(reqTs);
				cbResObj.setResTs(new Timestamp(System.currentTimeMillis()));
				cbResRepository.save(cbResObj);
				// changes to update application_status if cb_failed :: START
				logger.debug("Printing CBResponse:" + cbResJson);
				if (cbResJson.has(CommonConstants.IRIS_MSG) && cbResJson.get(CommonConstants.IRIS_MSG) != null
						&& ((cbResJson.get(CommonConstants.IRIS_MSG).toString().equalsIgnoreCase("SUCCESS"))
								|| cbResJson.get(CommonConstants.IRIS_MSG).toString()
										.equalsIgnoreCase("bureau_data_issue_spouse_node_corrected_success"))) {
					logger.debug("inside if with new status");
					if (cbResJson.has("Final_Decision")) {
						String finalDecisionVal = cbResJson.getString("Final_Decision");
						if ("REJECT".equalsIgnoreCase(finalDecisionVal)
								|| "Deviation".equalsIgnoreCase(finalDecisionVal)) {							
							logger.debug("Final Decision is reject");
//							Optional<ApplicationMaster> opApplicationMasterRec = applicationMasterRepo
//									.findTopByApplicationId(applicationId);
							Optional<ApplicationMaster> opApplicationMasterRec = applicationMasterRepo
									.findTopByApplicationIdOrderByCreateTsDesc(applicationId);
//							Optional<ApplicationWorkflow> opApplicationWorkflow = applnWfRepository.findTopByApplicationId(applicationId);
							Optional<ApplicationWorkflow> opApplicationWorkflow = applnWfRepository
									.findTopByApplicationIdOrderByCreateTsDesc(applicationId);
							logger.debug("opApplicationMasterRec:" + opApplicationMasterRec);
							if (opApplicationMasterRec.isPresent() && opApplicationWorkflow.isPresent()) {
								ApplicationMaster applicationMasterRec = opApplicationMasterRec.get();
								ApplicationWorkflow applicationWorkflowRec = opApplicationWorkflow.get();

								// Sonar Fix as both condition is try
								// if (schedulerFlag != null && schedulerFlag.equalsIgnoreCase("Y")) {
								// Amit Changes to reject for the scheduler retry if its failure.
								applicationMasterRec.setApplicationStatus("REJECTED");
								applicationWorkflowRec.setApplicationStatus("REJECTED");
								// } else {
								// applicationMasterRec.setApplicationStatus("REJECTED");
								// applicationWorkflowRec.setApplicationStatus("REJECTED");
								// }
								logger.debug("Printing request:" + applicationMasterRec);
								logger.debug("applicationMasterRec after update:" + applicationMasterRec);
								logger.debug("applicationWorkflowRec after update:" + applicationWorkflowRec);
								applicationMasterRepo.save(applicationMasterRec);
								applnWfRepository.save(applicationWorkflowRec);
							}
							// CB FAILURE
							Integer stage_id = 4;
							logger.debug(
									"Updating exiting saved AuditTrialDetails when CBCheck happened for CB FAILURE 4(1):"
											+ cbRequest);
							saveAuditTrialDetailsForCBCheck(cbRequest, stage_id);
						} else {
							// CB SUCCESS
							// Amit Changes for schduler to check if approved amount is less then applied
							// amount then
							// Need to update Respective Fields.
							if (schedulerFlag != null && schedulerFlag.equalsIgnoreCase("Y")) {
								logger.debug("this is schdule case");
								try {
									validateApproveAmountandProcessLoanDeatails(cbCheckRequest, cbResJson);
								} catch (Exception e) {
									logger.debug("Exception " + e.getMessage());
								}
							}
							Integer stage_id = 5;
							logger.debug("Updating exiting saved AuditTrialDetails CBCheck for CB SUCCESS 5(1):"
									+ cbRequest);
							saveAuditTrialDetailsForCBCheck(cbRequest, stage_id);
							logger.debug("CB SUCC stage5 req---------->:" + cbRequest);
							// THis is to update KFS sheet as CB called 2nd time
							logger.debug("updateing The KFS");
							logger.debug("Calling Scheuler in case of success");
							// callandGenerateKFSScheudule(applicationId, "CBSUCCESS");
							callandGenerateKFSScheuduleForDownlaod(applicationId, "CBSUCCESS");
						}
						if (cbResJson.has(CommonConstants.IRIS_MSG) && cbResJson.get(CommonConstants.IRIS_MSG) != null
								&& ((cbResJson.get(CommonConstants.IRIS_MSG).toString().equalsIgnoreCase("SUCCESS"))
										|| cbResJson.get(CommonConstants.IRIS_MSG).toString()
												.equalsIgnoreCase("bureau_data_issue_spouse_node_corrected_success"))) {
							if (cbResJson.isNull("Rejection_reason") || "1".equalsIgnoreCase(crtflag)
									|| cbResJson.getString("Rejection_reason").isEmpty()) {
								String cbRecheck = cbRequest.getRequestObj().getCbRecheck();
								String schedulerEnabled = cbRequest.getRequestObj().getSchedulerEnabled();
								if ("Y".equalsIgnoreCase(schedulerEnabled)) {
									if (cbRecheck == null || !"Y".equalsIgnoreCase(cbRecheck)) {										
										String cbfinalDecision= cbResJson.getString("Final_Decision");
										if ("REJECT".equalsIgnoreCase(cbfinalDecision)
												|| "Deviation".equalsIgnoreCase(cbfinalDecision)) {
											
										}else {
											updateWorkflowAfterCBCheckScheduler(applicationId, cbResObj.getAppId(),
													cbCheckRequest.getRequestObj().getVersionNo());
										}							
										
										logger.debug("Updated the workflow by the sceduler");
									}
								}
							}
						}
					}
				}
				// changes to update application_status if cb_failed :: END
			} else {
				logger.debug("cbRes not present");
				logger.debug("No Record in the API repsonse table");
				String id = CommonUtils.generateRandomNumStr();
				TbUaobCbResponse cbResponse = new TbUaobCbResponse();
				cbResponse.setAppId(cbCheckRequest.getAppId());
				cbResponse.setRetryCount(0);
				cbResponse.setApplicationId(cbRequest.getRequestObj().getApplicationId());
				cbResponse.setVersionNum(cbRequest.getRequestObj().getVersionNo());
				Optional<TbUaobCustDtls> custDtl = custDtlsRepo
						.findByApplicationIdOrderByVersionNoDesc(cbRequest.getRequestObj().getApplicationId());
				if (custDtl.isPresent()) {
					cbResponse.setCustDtlId(custDtl.get().getCustDtlId());
				}
				cbResponse.setCbDtlId(id);
				try {
					cbResponse.setReqPayload(new ObjectMapper().writeValueAsString(cbCheckRequest));
					cbResponse.setResPayload(respObj);
				} catch (JsonProcessingException e) {
					logger.error("error while saving cbResponse:", e);
				}
				cbResponse.setCbCheckstatus("FAILURE");
				if (cbResJson.has(CommonConstants.IRIS_MSG) && cbResJson.get(CommonConstants.IRIS_MSG) != null
						&& ((cbResJson.get(CommonConstants.IRIS_MSG).toString().equalsIgnoreCase("SUCCESS"))
								|| cbResJson.get(CommonConstants.IRIS_MSG).toString()
										.equalsIgnoreCase("bureau_data_issue_spouse_node_corrected_success"))) {
					cbResponse.setStatus("SUCCESS");
					logger.debug("CB success first time");
					if (cbResJson.isNull("Rejection_reason") || "1".equalsIgnoreCase(crtflag)
							|| cbResJson.getString("Rejection_reason").isEmpty()) {
						cbResponse.setCbCheckstatus("SUCCESS");
						String cbRecheck = cbRequest.getRequestObj().getCbRecheck();
						String schedulerEnabled = cbRequest.getRequestObj().getSchedulerEnabled();
						if ("Y".equalsIgnoreCase(schedulerEnabled)) {
							if (cbRecheck == null || !"Y".equalsIgnoreCase(cbRecheck)) {
								logger.debug("Going to update work flow ");
								updateWorkflowAfterCBCheckScheduler(applicationId, cbRes.get().getAppId(),
										cbCheckRequest.getRequestObj().getVersionNo());
							}
						}
					}
					String cbDecision = cbResJson.getString("Final_Decision");
					if ("REJECT".equalsIgnoreCase(cbDecision)
							|| "Deviation".equalsIgnoreCase(cbDecision)) {
						cbResponse.setCbCheckstatus("FAILURE");
					}
				} else {
					cbResponse.setStatus("FAILURE");
					// CB PENDING
					Integer stage_id = 3;
					logger.debug("Updating exiting saved AuditTrialDetails CBCheck for CB Pending 3(2):" + cbRequest);
					saveAuditTrialDetailsForCBCheck(cbRequest, stage_id);
				}
				// changes to update application_status if cb_failed :: START
				if (cbResJson.has(CommonConstants.IRIS_MSG) && cbResJson.get(CommonConstants.IRIS_MSG) != null
						&& (cbResJson.get(CommonConstants.IRIS_MSG).toString().equalsIgnoreCase("SUCCESS"))) {
					if (cbResJson.has("Final_Decision")) {
						String finalDecisionVal = cbResJson.getString("Final_Decision");
						if ("REJECT".equalsIgnoreCase(finalDecisionVal)
								|| "Deviation".equalsIgnoreCase(finalDecisionVal)) {
							logger.debug("This is Reject case");
							Optional<ApplicationMaster> opApplicationMasterRec = applicationMasterRepo
									.findTopByApplicationId(applicationId);
							logger.debug("ApplicationMaster rec:" + opApplicationMasterRec.get());
							Optional<ApplicationWorkflow> opApplicationWorkflow = applnWfRepository
									.findTopByApplicationId(applicationId);
							// logger.debug("ApplicationWorkflow rec:"+opApplicationWorkflow.get());
							logger.debug("opApplicationMasterRec:" + opApplicationMasterRec);
							if (opApplicationMasterRec.isPresent()) {
								logger.debug("both ApplicationMaster and ApplicationWorkflow Rec is present");
								ApplicationMaster applicationMasterRec = opApplicationMasterRec.get();
								// ApplicationWorkflow applicationWorkflowRec = opApplicationWorkflow.get();
								applicationMasterRec.setApplicationStatus("INITIATE");
								// applicationWorkflowRec.setApplicationStatus("INPROGRESS");
								logger.debug("Printing request:" + applicationMasterRec);
								logger.debug("applicationMasterRec after update:" + applicationMasterRec);
								// logger.debug("applicationWorkflowRec after update:"+applicationWorkflowRec);
								applicationMasterRepo.save(applicationMasterRec);
								// applnWfRepository.save(applicationWorkflowRec);
							} else {
								logger.debug("both ApplicationMaster and ApplicationWorkflow Rec is not present");
							}
							// CB FAILURE
							Integer stage_id = 4;
							logger.debug("Updated saved AuditTrialDetails CBCheck for CB FAILURE 4(2):" + cbRequest);
							saveAuditTrialDetailsForCBCheck(cbRequest, stage_id);
						}
					}
				}
				// changes to update application_status if cb_failed :: END
				cbResponse.setReqTs(reqTs);
				cbResponse.setResTs(new Timestamp(System.currentTimeMillis()));
				cbResRepository.save(cbResponse);
				if ("SUCCESS".equalsIgnoreCase(cbResponse.getCbCheckstatus())) {
					// CB SUCCESS
					Integer stage_id = 5;
					logger.debug("Updating existing saved AuditTrialDetails CBCheck for CB SUCCESS 5(2):" + cbRequest);
					saveAuditTrialDetailsForCBCheck(cbRequest, stage_id);
					// Calling KFS scheuler here
					logger.debug("Calling Scheuler in case of success");
					// callandGenerateKFSScheudule(applicationId, "CBSUCCESS");
					callandGenerateKFSScheuduleForDownlaod(applicationId, "CBSUCCESS");
				}
				if (cbResJson.has(CommonConstants.IRIS_MSG) && cbResJson.get(CommonConstants.IRIS_MSG) != null
						&& ((cbResJson.get(CommonConstants.IRIS_MSG).toString().equalsIgnoreCase("SUCCESS"))
								|| cbResJson.get(CommonConstants.IRIS_MSG).toString()
										.equalsIgnoreCase("bureau_data_issue_spouse_node_corrected_success"))) {
					if (cbResJson.isNull("Rejection_reason") || "1".equalsIgnoreCase(crtflag)
							|| cbResJson.getString("Rejection_reason").isEmpty()) {
						String cbRecheck = cbRequest.getRequestObj().getCbRecheck();
						String schedulerEnabled = cbRequest.getRequestObj().getSchedulerEnabled();
						if ("Y".equalsIgnoreCase(schedulerEnabled)) {
							if (cbRecheck == null || !"Y".equalsIgnoreCase(cbRecheck)) {
								String finalDecision= cbResJson.getString("Final_Decision");
								if ("REJECT".equalsIgnoreCase(finalDecision)
										|| "Deviation".equalsIgnoreCase(finalDecision)) {

								} else {
									updateWorkflowAfterCBCheckScheduler(applicationId, cbRes.get().getAppId(),
											cbCheckRequest.getRequestObj().getVersionNo());
								}
								
							}
						}
					}
				}
			}
			return Mono.just(respObj);
		});
	}

	private void validateApproveAmountandProcessLoanDeatails(CbCheckRequest cbCheckRequest, JSONObject cbResJson) {
		logger.debug("Schduler Case cbRequest" + cbCheckRequest);
		logger.debug("Schduler Case cbResJson" + cbResJson);
		// String approvedAmount = cbResJson.getString("Approved_Loan_Amount");
		// String appliedAmount = cbCheckRequest.getRequestObj().getLoanAmount();
		double approvedAmount = cbResJson.getDouble("Approved_Loan_Amount");

		// Get applied loan amount (which is a string) and convert to double
		String appliedAmountStr = cbCheckRequest.getRequestObj().getLoanAmount();
		double appliedAmount = Double.parseDouble(appliedAmountStr);

		logger.debug("appliedAmount is " + appliedAmount + " And Approved Amount is " + approvedAmount);
		if (Double.compare(approvedAmount, appliedAmount) == 0) {
			logger.debug("Both are same");
			;
		} else {
			logger.debug("Both are differnt");
			updateRespectivedetails(cbCheckRequest, cbResJson);

		}

	}

	private void updateRespectivedetails(CbCheckRequest cbCheckRequest, JSONObject cbResJson) {

		String applicationId = cbCheckRequest.getRequestObj().getApplicationId();
		List<ApplicationMaster> applicationMasterList = applicationMasterRepo.findByApplicationId(applicationId);
		logger.debug("applicationMasterList updatingRecords:" + applicationMasterList);
		ApplicationMaster appMaster = applicationMasterList.get(0);

		FetchAppRequest appReq = new FetchAppRequest();
		appReq.setAppId(appMaster.getAppId());
		FetchAppRequestFields fields = new FetchAppRequestFields();
		fields.setApplicationId(applicationId);
		fields.setVersionNum(Integer.parseInt(appMaster.getVersionNum()));
		appReq.setRequestObj(fields);
		ApplyLoanRequestFields loanFields = getCustomerData(applicationMasterList, appReq);
		logger.debug("Fetching loanFields :" + loanFields);
		ApplicationDtls appDtl = loanFields.getApplicationdtls().get(0);
		CustomerDtls customerDtl = appDtl.getCustomerDetails();
		logger.debug("Fetching customerDtl :" + customerDtl);
		LoanDtls loanDtl = customerDtl.getLoanDtls();

		logger.debug("Loan Details are " + loanDtl);
		Gson gson = new Gson();
		String LoanjsonString = gson.toJson(loanDtl);
		logger.debug("LoanjsonString are " + LoanjsonString);

		logger.debug("Fetching loanDtl :" + loanDtl);

		InsuranceDetails insDet = loanDtl.getInsurDtls();
		logger.debug("Fetching insDet :" + insDet);
		int insurAmt = 0;

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
			// Handle parsing errors if needed
			// e.printStackTrace();
			logger.error("Exception occurred", e);
		}
		logger.debug("Fetching memberInsAmt :" + memberInsAmt);
		logger.debug("Fetching spouseInsAmt :" + spouseInsAmt);

		logger.debug("Fetching getMember :" + insDet.getMember());
		logger.debug("Fetching getSpouse:" + insDet.getSpouse());
		if ("Y".equalsIgnoreCase(insDet.getMember()) && "Y".equalsIgnoreCase(insDet.getSpouse())) {
			insurAmt = memberInsAmt + spouseInsAmt;
			insDet.setApplicant_insurance_amt(memberInsStr);
			insDet.setSpouse_insurance_amt(spouseInsStr);
		} else if ("Y".equalsIgnoreCase(insDet.getMember())) {
			insurAmt = memberInsAmt;
			insDet.setApplicant_insurance_amt(memberInsStr);
			insDet.setSpouse_insurance_amt("");
		} else if ("Y".equalsIgnoreCase(insDet.getSpouse())) {
			insurAmt = spouseInsAmt;
			insDet.setApplicant_insurance_amt("");
			insDet.setSpouse_insurance_amt(spouseInsStr);
		} else {
			insurAmt = 0;
			insDet.setApplicant_insurance_amt("");
			insDet.setSpouse_insurance_amt("");
		}

		insDet.setInsurCharges(String.valueOf(insurAmt));

		logger.debug("insDet post first changes" + insDet);
		loanDtl.setInsurDtls(insDet);

		logger.debug("loanDtl post first chagnes" + loanDtl);

		double aprxloancharges = cbResJson.getDouble("GST") + cbResJson.getDouble("Processing_fees_without_GST");
		logger.debug("Fetch aprxloancharges" + aprxloancharges);
		double aprxloanchargeswithins = aprxloancharges + insurAmt;
		logger.debug("Fetching aprxloanchargeswithins" + aprxloanchargeswithins);
		double aprxloanamount = (cbResJson.getDouble("Approved_Loan_Amount")
				- (cbResJson.getDouble("GST") + cbResJson.getDouble("Processing_fees_without_GST") + insurAmt));
		logger.debug("Fetch aprxloanamount" + aprxloanamount);

		DecimalFormat decimalFormat = new DecimalFormat("#.##");
		DecimalFormat intFormat = new DecimalFormat("0");

		// Sample conversion
		loanDtl.getChargeAndBreakupDtls().setLoanAmt(intFormat.format(cbResJson.optInt("Approved_Loan_Amount", 0)));
		loanDtl.getChargeAndBreakupDtls().setGST(decimalFormat.format(cbResJson.optDouble("GST", 0.0)));
		loanDtl.getChargeAndBreakupDtls().setAddInfo1(intFormat.format(aprxloanchargeswithins));
		loanDtl.getChargeAndBreakupDtls().setAprxLoanCharges(intFormat.format(aprxloancharges));
		loanDtl.getChargeAndBreakupDtls()
				.setLoanProcessingFee(decimalFormat.format(cbResJson.optDouble("Processing_fees_without_GST", 0.0)));
		loanDtl.getChargeAndBreakupDtls().setAprxLoanAmt(intFormat.format(aprxloanamount));
		loanDtl.getChargeAndBreakupDtls().setIscbUpdated("true");

		// window.KendraLoans.custFetchLoanAppRes.applicationdtls[0].amount=approvedAmt;

		if (appDtl.getProductType().equalsIgnoreCase("EL")) {
			loanDtl.getChargeAndBreakupDtls().setInterest_Fee(cbResJson.get("Interest_Fee") + "");
			loanDtl.getChargeAndBreakupDtls().setUpfront_Fee(cbResJson.get("Upfront_Fee") + "");
		} else {
			loanDtl.getChargeAndBreakupDtls().setInterest_Fee("");
			loanDtl.getChargeAndBreakupDtls().setUpfront_Fee("");
		}

		logger.debug("insDet post second changes" + insDet);
		loanDtl.setInsurDtls(insDet);

		logger.debug("loanDtl post second chagnes" + loanDtl);

		String LoanjsonStringPost = gson.toJson(loanDtl);
		logger.debug("before save LoanjsonStringPost changes" + LoanjsonStringPost);
		// Update Master Amount
		// Update Loan Details
		loanDtlsRepo.updateValuesPostRetrigger(String.valueOf(cbResJson.getInt("Approved_Loan_Amount")),
				LoanjsonStringPost, applicationId);
		applicationMasterRepo.updateApplicationAmount(new BigDecimal(cbResJson.get("Approved_Loan_Amount").toString()),
				applicationId);

		// Updating Insurance Table
		String insurancejsonStringPost = gson.toJson(insDet);
		logger.debug("before save insurancejsonStringPost changes" + insurancejsonStringPost);
		try {
			tbUacoInsuranceDtlsRepo.updateInsuranceValuesPostRetrigger(insurancejsonStringPost, applicationId);

		} catch (Exception e) {
			logger.error("exception occurred:", e);
		}
		// tbUacoInsuranceDtlsRepo.updateInsuranceValuesPostRetrigger(insurancejsonStringPost,
		// applicationId);

	}

	@Transactional
	public Response fetchApplication(FetchAppRequest request) {
		String applicationId = request.getRequestObj().getApplicationId();
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		response.setResponseHeader(responseHeader);
		ResponseBody responseBody = new ResponseBody();
		ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Optional: to write dates as ISO-8601
		List<ApplicationMaster> applicationMasterList = null;
		if (StringUtils.isNotBlank(request.getRequestObj().getCustomerId())) {
			applicationMasterList = applicationMasterRepo.findByCustomerId(request.getRequestObj().getCustomerId());
		} else {
			applicationMasterList = applicationMasterRepo.findByApplicationId(applicationId);
		}
		logger.debug("Priting applicationMasterList :: {}", applicationMasterList);
		if (!applicationMasterList.isEmpty()) {
			ApplyLoanRequestFields loanFields = getCustomerData(applicationMasterList, request);
			logger.debug("Printing loanFields :: {}", loanFields);
			String customerdata;
			try {
				customerdata = objectMapper.writeValueAsString(loanFields);
				logger.debug("Priting customerdata :: {}", customerdata);
				CommonUtils.generateHeaderForSuccess(responseHeader);
				responseBody.setResponseObj(customerdata);
				response.setResponseBody(responseBody);
			} catch (JsonProcessingException e) {
				logger.error("exception occurred:", e);
			}

			return response;
		} else {
			CommonUtils.generateHeaderForNoResult(responseHeader);
			responseBody.setResponseObj("");
			response.setResponseBody(responseBody);
			return response;
		}
	}

	// Prem Start
	public Response fetchApplicationListAfterOTPdrop(FetchAppRequest request) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		response.setResponseHeader(responseHeader);
		ResponseBody responseBody = new ResponseBody();
		ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Optional: to write dates as ISO-8601
		List<String> applicationListAfterOTPdrop = new ArrayList<>();
		String CustomerId = request.getRequestObj().getCustomerId();
		if (StringUtils.isNotBlank(CustomerId)) {
			// applicationListAfterOTPdrop =
			// cbResRepository.getApplicationListAfterOTPDrop(CustomerId);
			applicationListAfterOTPdrop = Optional
					.ofNullable(cbResRepository.getApplicationListAfterOTPDrop(CustomerId))
					.orElse(Collections.emptyList());
		}
		if (!applicationListAfterOTPdrop.isEmpty()) {

			String applicationList;
			try {
				applicationList = objectMapper.writeValueAsString(applicationListAfterOTPdrop);

				CommonUtils.generateHeaderForSuccess(responseHeader);
				responseBody.setResponseObj(applicationList);
				response.setResponseBody(responseBody);
			} catch (JsonProcessingException e) {
				logger.error("exception occurred:", e);
			}

			return response;
		} else {
			CommonUtils.generateHeaderForNoResult(responseHeader);
			responseBody.setResponseObj("");
			response.setResponseBody(responseBody);
			return response;
		}
	}

	@SuppressWarnings({ "unchecked" })
	public ApplyLoanRequestFields getCustomerData(List<ApplicationMaster> applicationMasterList,
			FetchAppRequest request) {
		logger.debug("Printing applicationMasterList" + applicationMasterList);
		logger.debug("Printing FetchAppRequest" + request);
		ApplyLoanRequestFields loanFields = new ApplyLoanRequestFields();
		List<ApplicationDtls> appDtlsList = new ArrayList<>();
		Gson gson = new Gson();
		List<AddressDtls> addressList = new ArrayList<>();
		try {
			for (ApplicationMaster appMaster : applicationMasterList) {
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
				appDtls.setDisbApiRes(null);
				String applicationId = appMaster.getApplicationId();
				Optional<TbUaobCustDtls> customerDetails = custDtlsRepo.findByApplicationId(applicationId);
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
					List<TbUaobAddressDetails> addressDetailsList = addressRepository
							.findByApplicationId(applicationId);
					logger.debug("Printing addressDetailsList records from DB:" + addressDetailsList);
					for (TbUaobAddressDetails addDtl : addressDetailsList) {
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
					Optional<TbUaobOccupationDtls> occupDtlOpt = tbUaobOccpationDtlRepo
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
					Optional<TbUalnLoanDtls> loanDtlOptional = loanDtlsRepo.findByApplicationId(applicationId);
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
						Optional<TbUacoInsuranceDtls> insuranceDtls = tbUacoInsuranceDtlsRepo
								.findByApplicationId(applicationId);
						logger.debug("Printing insuranceDtls records from DB:" + insuranceDtls);
						if (insuranceDtls.isPresent()) {
							InsuranceDetails insDts = gson.fromJson(insuranceDtls.get().getPayload(),
									InsuranceDetails.class);
							loanDtls.setInsurDtls(insDts);
						}
						Optional<TbUaobNomineeDetails> nomineeDtls = nomineeRepository
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

				// Optional<ApplicationWorkflow> workflow = applnWfRepository
				// .findTopByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumDesc(request.getAppId(),
				// applicationId, request.getRequestObj().getVersionNum());

				Optional<ApplicationWorkflow> workflow = applnWfRepository.findlatestWorkflowDetails(applicationId);

				logger.debug("Printing workflow records from DB:" + workflow);

				if (workflow.isPresent()) {
					ApplicationWorkflow applnWf = workflow.get();
					List<WorkflowDefinition> wfDefnLis = wfDefnRepoLn.findByFromStageId(applnWf.getNextWorkFlowStage());
					loanFields.setApplnWfDefinitionList(wfDefnLis);
					logger.debug("Printing wfDefnLis records from DB:" + wfDefnLis);
				}

				// Optional<TbUaobCbResponse> cbRes = cbResRepository
				// .findByAppIdAndApplicationIdOrderByVersionNumDesc(request.getAppId(),
				// applicationId);

				TbUaobCbResponse cbRes = cbResRepository.findByAppIdAndApplicationId(applicationId);
				logger.debug("Printing cbRes records from DB:" + cbRes);

				PrecloserResponse precloserResponse = tbUaobApiAuditLogsRepository
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
					appDtls.setDisbApiRes(precloserLoanResponse);
					}
				if (cbRes != null) {
					loanFields.setCbResponse(cbRes);
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

	
//	@CircuitBreaker(name = "fallback", fallbackMethod = "populateApplnWorkFlowFallback")
//	public Response populateApplnWorkFlow(PopulateapplnWFRequest request) {
//		logger.debug("Printing reqFields :" + request);
//		Response response = new Response();
//		ResponseHeader responseHeader = new ResponseHeader();
//		ResponseBody responseBody = new ResponseBody();
//		PopulateapplnWFRequestFields reqFields = request.getRequestObj();
//		List<ApplicationList> applicationDetailList = request.getRequestObj().getApplicationDetailList();
//		if (applicationDetailList != null && applicationDetailList.size() > 0) {
//			for (ApplicationList applnRec : applicationDetailList) {
//				reqFields.setApplicationId(applnRec.getApplicationId());
//				reqFields.setVersionNum(applnRec.getVersionNum());
//
//				// if(applnRec.getType()!=null) { reqFields.setType(applnRec.getType()); }
//
//				callToApproveSingleRec(reqFields);
//				deleteApplicationLockCustomer(applnRec.getApplicationId());
//			}
//		} else {
//			callToApproveSingleRec(reqFields);
//			deleteApplicationLockCustomer(reqFields.getApplicationId());
//		}
//		responseBody.setResponseObj("");
//		CommonUtils.generateHeaderForSuccess(responseHeader);
//		response.setResponseBody(responseBody);
//		response.setResponseHeader(responseHeader);
//		return response;
//	}
	 

	@CircuitBreaker(name = "fallback", fallbackMethod = "populateApplnWorkFlowFallback")
	public Response populateApplnWorkFlow(PopulateapplnWFRequest request) {
		logger.debug("Received populateApplnWorkFlow request: {}", request);

		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();

		PopulateapplnWFRequestFields reqFields = request.getRequestObj();
		List<ApplicationList> applicationDetailList = request.getRequestObj().getApplicationDetailList();

		if (applicationDetailList != null && applicationDetailList.size() > 0) {
			for (ApplicationList applnRec : applicationDetailList) {
				reqFields.setApplicationId(applnRec.getApplicationId());
				reqFields.setVersionNum(applnRec.getVersionNum());

				// if(applnRec.getType()!=null) { reqFields.setType(applnRec.getType()); }

				callToApproveSingleRec(reqFields);
				deleteApplicationLockCustomer(applnRec.getApplicationId());
			}
			responseBody.setResponseObj("");
		} else {
			logger.info("Processing single applicationId", reqFields.getApplicationId(), reqFields.getVersionNum());
			String applicationStatus = getApplicationStatus(reqFields.getApplicationId());
			List<String> invalidStatuses = Arrays.asList("REJECTED", "DISBURSED", "CANCELLED", "");

			if (invalidStatuses.contains(applicationStatus)) {
				logger.warn("ApplicationId={} cannot be processed. Invalid status={}", reqFields.getApplicationId(),
						applicationStatus);
				responseBody.setResponseObj(EXCEPTION_MSG);
			} else {
				logger.info("Approving applicationId={} with status={}", reqFields.getApplicationId(),
						applicationStatus);
				callToApproveSingleRec(reqFields);
				deleteApplicationLockCustomer(reqFields.getApplicationId());
				responseBody.setResponseObj("");
			}
		}
		CommonUtils.generateHeaderForSuccess(responseHeader);
		response.setResponseBody(responseBody);
		response.setResponseHeader(responseHeader);
		logger.debug("populateApplnWorkFlow completed successfully for request: {}", request);
		return response;
	}

	private String getApplicationStatus(String applicationId) {
		List<ApplicationMaster> applnMasterRec = applicationMasterRepo.findByApplicationId(applicationId);
		logger.debug("Fetched ApplicationMaster for applicationId={}, records={}", applicationId, applnMasterRec);
		return (applnMasterRec != null && !applnMasterRec.isEmpty()) ? applnMasterRec.get(0).getApplicationStatus()
				: "";
	}

	/*
	 * public Response populateApplnWorkFlow(PopulateapplnWFRequest request) {
	 * logger.debug("==== Inside populateApplnWorkFlow ====");
	 * logger.debug("Incoming Request: {}", request);
	 * 
	 * Response response = new Response(); ResponseHeader responseHeader = new
	 * ResponseHeader(); ResponseBody responseBody = new ResponseBody();
	 * PopulateapplnWFRequestFields reqFields = request.getRequestObj();
	 * List<ApplicationList> applicationDetailList =
	 * reqFields.getApplicationDetailList();
	 * 
	 * if (applicationDetailList != null && !applicationDetailList.isEmpty()) { //
	 * Extract application IDs to fetch data in bulk List<String> applicationIds =
	 * applicationDetailList.stream() .map(ApplicationList::getApplicationId)
	 * .collect(Collectors.toList());
	 * 
	 * // Fetch application details in bulk Map<String, ApplicationMaster>
	 * appMasterMap = applicationMasterRepo .findByApplicationIdIn(applicationIds)
	 * .stream() .collect(Collectors.toMap(ApplicationMaster::getApplicationId, app
	 * -> app));
	 * 
	 * for (ApplicationList applnRec : applicationDetailList) {
	 * reqFields.setApplicationId(applnRec.getApplicationId());
	 * reqFields.setVersionNum(applnRec.getVersionNum());
	 * callToApproveSingleRec(reqFields,
	 * appMasterMap.get(applnRec.getApplicationId()));
	 * deleteApplicationLockCustomer(reqFields.getApplicationId()); } } else {
	 * callToApproveSingleRec(reqFields,
	 * applicationMasterRepo.findByApplicationId(reqFields.getApplicationId()).
	 * stream().findFirst().orElse(null));
	 * 
	 * }
	 * 
	 * responseBody.setResponseObj("");
	 * CommonUtils.generateHeaderForSuccess(responseHeader);
	 * response.setResponseBody(responseBody);
	 * response.setResponseHeader(responseHeader); return response; }
	 */
	/*
	 * private void callToApproveSingleRec(PopulateapplnWFRequestFields reqFields,
	 * ApplicationMaster applicationMaster) {
	 * logger.debug("=== Inside callToApproveSingleRec ===");
	 * logger.debug("Processing applicationId: {}", reqFields.getApplicationId());
	 * 
	 * if (applicationMaster == null) {
	 * logger.warn("No ApplicationMaster record found for applicationId: {}",
	 * reqFields.getApplicationId()); return; }
	 * 
	 * Optional<ApplicationWorkflow> wfObj = applnWfRepository
	 * .findTopByAppIdAndApplicationIdOrderByWorkflowSeqNumDesc(reqFields.getAppId()
	 * , reqFields.getApplicationId());
	 * 
	 * int wfSeqNum = wfObj.map(ApplicationWorkflow::getWorkflowSeqNum).orElse(0) +
	 * 1; logger.debug("Workflow Sequence Number for applicationId {}: {}",
	 * reqFields.getApplicationId(), wfSeqNum);
	 * 
	 * WorkFlowDetails workFlow = reqFields.getWorkflow(); ApplicationWorkflow
	 * workFlowObj = new ApplicationWorkflow();
	 * workFlowObj.setAppId(reqFields.getAppId());
	 * workFlowObj.setApplicationId(reqFields.getApplicationId());
	 * workFlowObj.setApplicationStatus("REJECT".equalsIgnoreCase(workFlow.getAction
	 * ()) ? "REJECTED" : workFlow.getNextWorkflowStatus());
	 * workFlowObj.setCreatedBy(reqFields.getCreatedBy());
	 * workFlowObj.setCreateTs(LocalDateTime.now());
	 * workFlowObj.setNextWorkFlowStage(workFlow.getNextStageId());
	 * workFlowObj.setCurrentRole(workFlow.getCurrentRole());
	 * workFlowObj.setRemarks(workFlow.getRemarks());
	 * workFlowObj.setVersionNum(Integer.parseInt(reqFields.getVersionNum()));
	 * workFlowObj.setWorkflowSeqNum(wfSeqNum);
	 * 
	 * // Handle sanction workflow logic if
	 * (!"REJECT".equalsIgnoreCase(workFlow.getAction()) &&
	 * "SANCTIONED".equalsIgnoreCase(workFlow.getNextStageId())) {
	 * callandGenerateKFSScheudule(reqFields.getApplicationId(),
	 * "SANCTIONINPROGRESS"); }
	 * 
	 * // Self-assignment optimization String productGroupCode =
	 * applicationMaster.getProductGroupCode(); if
	 * ("BM".equalsIgnoreCase(productGroupCode)) {
	 * workFlow.setNextStageId("AMQUEUE");
	 * workFlow.setNextWorkflowStatus("SANCTIONINPROGRESS");
	 * workFlowObj.setNextWorkFlowStage("AMQUEUE");
	 * workFlowObj.setApplicationStatus("SANCTIONINPROGRESS"); }
	 * 
	 * logger.debug("Saving workflow record: {}", workFlowObj);
	 * applnWfRepository.save(workFlowObj);
	 * 
	 * logger.debug("Saving audit details for applicationId: {}",
	 * reqFields.getApplicationId()); //
	 * saveAuditTrialDetailsForKMActionPostCBCheck(reqFields); }
	 */

	private Response callToApproveSingleRec(PopulateapplnWFRequestFields reqFields) {
		logger.debug("Printing PopulateapplnWFRequestFields:" + reqFields);
		logger.debug("===Inside callToApproveSingleRec===");
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		int wfSeqNum = 1;
		ApplicationWorkflow applicationWorkflow = new ApplicationWorkflow();
		Optional<ApplicationWorkflow> wfObj = Optional.of(applicationWorkflow);
		try {
			wfObj = applnWfRepository.findTopByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumDesc(
					reqFields.getAppId(), reqFields.getApplicationId(), Integer.parseInt(reqFields.getVersionNum()));
		} catch (Exception e) {
			wfObj = applnWfRepository.findTopByAppIdAndApplicationIdOrderByWorkflowSeqNumDesc(reqFields.getAppId(),
					reqFields.getApplicationId());
		}
		// This is specific to CRT edit flow
		if (!wfObj.isPresent()) {
			wfObj = applnWfRepository.findTopByAppIdAndApplicationIdOrderByWorkflowSeqNumDesc(reqFields.getAppId(),
					reqFields.getApplicationId());
			logger.debug("Printing wfObj CRT :" + wfObj);
		}

		if (wfObj.isEmpty()) {
			Optional<TbUaobCbResponse> cbRes = cbResRepository
					.findTopByApplicationIdOrderByVersionNumDesc(reqFields.getApplicationId());
			logger.debug("Printing cbRes:" + cbRes);
			TbUaobCbResponse tbUaobCbResponse = cbRes.orElse(null);
			String action = reqFields.getWorkflow().getAction();
			if (!"REJECT".equalsIgnoreCase(action)) {
				if ((tbUaobCbResponse == null || !"SUCCESS".equalsIgnoreCase(tbUaobCbResponse.getCbCheckstatus()))
						&& "N".equalsIgnoreCase(reqFields.getCbApproveManual())) {
					logger.debug("CB Validation for WorkFlow Intiate:-Happy flow");
					JSONObject errorJson = new JSONObject();
					errorJson.put("message", "An exception occurred while updating the workflow.");
					responseBody.setResponseObj(errorJson.toString());
					CommonUtils.generateHeaderForSuccess(responseHeader);
					response.setResponseBody(responseBody);
					response.setResponseHeader(responseHeader);
					return response;
				} else if ((tbUaobCbResponse == null
						|| !"FAILURE".equalsIgnoreCase(tbUaobCbResponse.getCbCheckstatus()))
						&& "Y".equalsIgnoreCase(reqFields.getCbApproveManual())) {
					logger.debug("CB Validation for WorkFlow Intiate:-CRT flow");
					JSONObject errorJson = new JSONObject();
					errorJson.put("message", "An exception occurred while updating the workflow.");
					responseBody.setResponseObj(errorJson.toString());
					CommonUtils.generateHeaderForSuccess(responseHeader);
					response.setResponseBody(responseBody);
					response.setResponseHeader(responseHeader);
					return response;
				}
			}
			/*
			 * if("N".equalsIgnoreCase(reqFields.getCbApproveManual())) {
			 * reqFields.getWorkflow().setWorkflowId("LOANINPUT");
			 * reqFields.getWorkflow().setAction("REJECT");
			 * reqFields.getWorkflow().setNextStageId("USERREJECT");
			 * reqFields.getWorkflow().setCurrentRole("KM");
			 * reqFields.getWorkflow().setNextRole("KM"); reqFields.getWorkflow().
			 * setRemarks("Rejected by KM post CB screen-Amount changes.");
			 * reqFields.getWorkflow().setNextWorkflowStatus("USERREJECTED");
			 * reqFields.setRemarks("Rejected by KM post CB screen-Amount changes.");
			 * reqFields.setUserRole(reqFields.getUserRole());
			 * reqFields.setUserName(reqFields.getUserName());
			 * logger.debug("Rejected cases with CbApproveManual---N" +reqFields); }
			 */
		}
		logger.debug("Printing wfObj :" + wfObj);
		if (wfObj.isPresent()) {
			ApplicationWorkflow dbObj = wfObj.get();
			wfSeqNum = dbObj.getWorkflowSeqNum() + 1;
		}
		logger.debug("current WorkflowSeqNum:" + wfSeqNum);
		WorkFlowDetails workFlow = reqFields.getWorkflow();
		ApplicationWorkflow workFlowObj = new ApplicationWorkflow();
		workFlowObj.setAppId(reqFields.getAppId());
		workFlowObj.setApplicationId(reqFields.getApplicationId());
		String status = workFlow.getNextWorkflowStatus();

		String application_Stage = reqFields.getApplicationStatus();
		logger.debug("application_Stage:" + application_Stage);
		String cbApproveManual = reqFields.getCbApproveManual();

		workFlowObj.setApplicationStatus(status);
		String action = workFlow.getAction();
		//String currentRole = workFlow.getCurrentRole();

		//logger.debug("workflow currentRole:" + currentRole);
		logger.debug("getNextWorkflowStatus Role :" + workFlow.getNextWorkflowStatus());
		logger.debug("Current currentstageID :" + workFlow.getCurrentStage());
		logger.debug("Current NextstageId :" + workFlow.getNextStageId());
		logger.debug("Current workflowid :" + workFlow.getWorkflowId());
		logger.debug("Current CurrentStage:" + workFlow.getCurrentStage());
		logger.debug("Current NextRole:" + workFlow.getNextRole());

		String userRoleDynamic = reqFields.getUserRole();
		logger.debug("Printing userRoleDynamic:" + userRoleDynamic);
		Integer stage_id = null;
		boolean auditLogged = false;
		//String roleForAudit = currentRole;
		String roleForAudit = userRoleDynamic;
		
		if ("REJECT".equalsIgnoreCase(action) && "LOANINPUT".equalsIgnoreCase(workFlow.getWorkflowId())
				&& "USERREJECT".equalsIgnoreCase(workFlow.getNextStageId())) {
			workFlowObj.setApplicationStatus("REJECTED");
			stage_id = 6; // Application Rejected
			roleForAudit = userRoleDynamic;
			auditLogged = true;
		}

		if ("REJECT".equalsIgnoreCase(action) && "LOANINPUT".equalsIgnoreCase(workFlow.getWorkflowId())
				&& "N".equalsIgnoreCase(cbApproveManual) && "CRTREJECT".equalsIgnoreCase(workFlow.getNextStageId())) {
			stage_id = 7; // CRT Rejected
			auditLogged = true;
		}

		if ((workFlow.getWorkflowId() == null || "null".equalsIgnoreCase(workFlow.getWorkflowId()))
				&& "SANCTIONINPROGRESS".equalsIgnoreCase(workFlow.getNextWorkflowStatus())) {
			stage_id = 8; // PENDING SANCTION
			roleForAudit = userRoleDynamic;
			auditLogged = true;
		}

		if (!"REJECT".equalsIgnoreCase(application_Stage) && "Y".equalsIgnoreCase(cbApproveManual)) {
			stage_id = 9; // CRT approved
			roleForAudit = userRoleDynamic;
			auditLogged = true;
		}
		if ("REJECT".equalsIgnoreCase(action) && "LOANINPUT".equalsIgnoreCase(workFlow.getWorkflowId())
				&& "DRAFT".equalsIgnoreCase(workFlow.getNextStageId())) {
			stage_id = 19; // DRAFT Application Rejected cases
			auditLogged = true;
		}

		if (application_Stage != null) {
			logger.debug("current status:" + application_Stage);
			switch (application_Stage.toUpperCase()) {
			case "BMQUEUE":
				if ("REJECT".equalsIgnoreCase(action)) {
					stage_id = 10;// BM Sanction Rejected
				} else if ("NEXT".equalsIgnoreCase(action)) {
					stage_id = 13; // working (Loan Sanction approved by BM)
				} else if ("SUBMIT".equalsIgnoreCase(action)) {
					stage_id = 11;
				}
				break;

			case "AMQUEUE":
				if ("REJECT".equalsIgnoreCase(action)) {
					stage_id = 10;
				} else if ("NEXT".equalsIgnoreCase(action)) {
					stage_id = 13;
				} else if ("CONFIRM".equalsIgnoreCase(action)) {
					stage_id = 11;
				}
				break;

			case "APPROVALQUEUE":
				if ("REJECT".equalsIgnoreCase(action)) {
					stage_id = 12;
				} else if ("NEXT".equalsIgnoreCase(action)) {
					stage_id = 13;
				}
				break;
			case "SANCTIONED":
				if ("REJECT".equalsIgnoreCase(action) && "REJECT".equalsIgnoreCase(workFlow.getNextStageId())) {
					stage_id = 14; // For KM Sanction Rejected
				} else if ("NEXT".equalsIgnoreCase(action)) {
					stage_id = 15;// when KM disburse for next stage to BM
				}
				break;

			case "DISBURSEMENT":
				if ("REJECT".equalsIgnoreCase(action) && "REJECT".equalsIgnoreCase(workFlow.getNextStageId())) {
					stage_id = 16;// BM Disbursement Rejected
				} else if ("NEXT".equalsIgnoreCase(action)) {
					stage_id = 17;// Final Disbursement
				}
			}
			if (stage_id != null) {
				logger.debug("Printing reqFields_final:" + reqFields);
				logger.debug("Printing stage_id_final:" + stage_id);
				logger.debug("Printing roleForAudit_final:" + roleForAudit);
				saveAuditTrialDetailsForKMActionPostCBCheck(reqFields, stage_id, roleForAudit);
			}
		}
		logger.debug("Printing Action:" + action);
		if (!"REJECT".equalsIgnoreCase(action) && "SANCTIONED".equalsIgnoreCase(workFlow.getNextStageId())) {
			logger.debug("Entering when condition is SANCTIONED :{}" + workFlow.getNextStageId());
			callandGenerateKFSScheuduleForDownlaod(reqFields.getApplicationId(), "SANCTIONINPROGRESS");
		}
		workFlowObj.setCreatedBy(reqFields.getCreatedBy());
		workFlowObj.setCreateTs(LocalDateTime.now());
		if (workFlow != null) {
			workFlowObj.setNextWorkFlowStage(workFlow.getNextStageId());
			workFlowObj.setCurrentRole(workFlow.getCurrentRole());
			workFlowObj.setRemarks(workFlow.getRemarks());
		}
		workFlowObj.setVersionNum(Integer.parseInt(reqFields.getVersionNum()));
		workFlowObj.setWorkflowSeqNum(wfSeqNum);
		List<ApplicationMaster> applnMasterRec = applicationMasterRepo
				.findByApplicationId(reqFields.getApplicationId());
		String productGroupCode = "";
		if (applnMasterRec.size() > 0) {
			ApplicationMaster applicationMaster = applnMasterRec.get(0);
			productGroupCode = applicationMaster.getProductGroupCode();
			if ("BM".equalsIgnoreCase(productGroupCode)) {
				// workFlow.setNextRole("AM");
				workFlow.setNextStageId("AMQUEUE");
				workFlow.setNextWorkflowStatus("SANCTIONINPROGRESS");
				workFlow.setRemarks("");
				workFlowObj.setNextWorkFlowStage(workFlow.getNextStageId());
				workFlowObj.setApplicationStatus(workFlow.getNextWorkflowStatus());
				workFlowObj.setRemarks(workFlow.getRemarks());
			}
		}
		// self assignment :: END
		logger.debug("Printing workFlowObj: " + workFlowObj);
		applnWfRepository.save(workFlowObj);
		responseBody.setResponseObj("");
		CommonUtils.generateHeaderForSuccess(responseHeader);
		response.setResponseBody(responseBody);
		response.setResponseHeader(responseHeader);
		return response;
	}

	// METHOD TO CALL Schedule
	public Mono<byte[]> callandGenerateKFSScheudule(String applicationId, String stage) {
		logger.debug("=====Insertion for Sanction Letter Repay=====");
		Header header = new Header();
		String roi = "", loanAmt = "";
		header.setInterfaceId(LOAN_SCHEDULE_PROJECTOR);
		LocalDateTime currentDate = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String dateWithTime = currentDate.format(formatter);
		logger.debug("Printing date:" + dateWithTime);

		List<ApplicationMaster> applicationMasterList = applicationMasterRepo.findByApplicationId(applicationId);
		logger.debug("applicationMasterList for Report:" + applicationMasterList);

		ApplicationMaster appMaster = applicationMasterList.get(0);

		FetchAppRequest appReq = new FetchAppRequest();
		appReq.setAppId(appMaster.getAppId());
		FetchAppRequestFields fields = new FetchAppRequestFields();
		fields.setApplicationId(applicationId);
		fields.setVersionNum(Integer.parseInt(appMaster.getVersionNum()));
		appReq.setRequestObj(fields);
		ApplyLoanRequestFields loanFields = getCustomerData(applicationMasterList, appReq);
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
		Optional<TbUaobCbResponse> cbRes = cbResRepository.findTopByApplicationIdOrderByResTsDesc(applicationId);
		logger.debug("cbRes for Report:" + cbRes);
		if (cbRes.isPresent()) {
			TbUaobCbResponse cbResponse = cbRes.get();
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
							int updatedRows = loanDtlsRepo.updateInstallmentDetails(installmentValue, applicationId);
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

	public void callandGenerateKFSScheuduleForDownlaod(String applicationId, String stage) {
		logger.debug("=====Insertion for Sanction Letter Repay=====");
		Header header = new Header();
		String roi = "", loanAmt = "";
		header.setInterfaceId(LOAN_SCHEDULE_PROJECTOR);
		LocalDateTime currentDate = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		String dateWithTime = currentDate.format(formatter);
		logger.debug("Printing date:" + dateWithTime);

		List<ApplicationMaster> applicationMasterList = applicationMasterRepo.findByApplicationId(applicationId);
		logger.debug("applicationMasterList for Report:" + applicationMasterList);

		ApplicationMaster appMaster = applicationMasterList.get(0);

		FetchAppRequest appReq = new FetchAppRequest();
		appReq.setAppId(appMaster.getAppId());
		FetchAppRequestFields fields = new FetchAppRequestFields();
		fields.setApplicationId(applicationId);
		fields.setVersionNum(Integer.parseInt(appMaster.getVersionNum()));
		appReq.setRequestObj(fields);

		ApplyLoanRequestFields loanFields = getCustomerData(applicationMasterList, appReq);
		logger.debug("loanFields:" + loanFields);
		ApplicationDtls appDtl = loanFields.getApplicationdtls().get(0);
		logger.debug("appDtl:" + appDtl);
		CustomerDtls customerDtl = appDtl.getCustomerDetails();
		LoanDtls loanDtl = customerDtl.getLoanDtls();
		HashMap<String, String> freqObj = (HashMap<String, String>) loanDtl.getRepayFrequency();

		String productFullCode = loanDtl.getProduct();

		logger.debug("proudct full name is " + productFullCode);

		if (appMaster != null) {
			loanAmt = appMaster.getAmount().toString();
		}
		Optional<TbUaobCbResponse> cbRes = cbResRepository.findTopByApplicationIdOrderByResTsDesc(applicationId);
		logger.debug("cbRes for Report:" + cbRes);
		if (cbRes.isPresent()) {
			TbUaobCbResponse cbResponse = cbRes.get();
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
		logger.debug("Printing tenure without removing unit:" + tenure);
		String replacedTenure = "";
		if (tenure != null && !tenure.isEmpty()) {
			replacedTenure = tenure.replaceAll("[a-zA-Z]", "");
		}
		logger.debug("Printing replacedTenure:" + replacedTenure);
		sanctionLoanScheduleRequestFields.setTenure(replacedTenure);
		sanctionReportRequest.setRequestObj(sanctionLoanScheduleRequestFields);
		logger.debug("Printing final GetSanctionLoanSchedule request : " + sanctionReportRequest);

		Mono<Object> externalServiceResponse = interfaceAdapter.callExternalService(header, sanctionReportRequest,
				LOAN_SCHEDULE_PROJECTOR, true);

		externalServiceResponse.onErrorResume(error -> {
			logger.error("Error from external service, returning empty response", error);
			return Mono.empty(); // or Mono.just(someFallbackObject)
		}).doOnNext(val -> {
			logger.debug("Received response from external service Report: {}" + val);

			ObjectMapper objMapper = new ObjectMapper();
			String responseInStr = "";

			try {
				responseInStr = objMapper.writeValueAsString(val);
			} catch (JsonProcessingException e) {
				// e.printStackTrace();
				logger.error("Exception occurred", e);
			}
			JSONObject responseInJsonObj = new JSONObject(responseInStr);
			if (responseInJsonObj.has("body")) {
				// Going to delete old application Data and going to insert new data
				logger.debug("delete by APplicaiton ID " + applicationId);
				sanctionRepaymentScheduleRepository.deleteByApplicationId(applicationId);
				logger.debug("Delete done");
				JSONArray respBody = responseInJsonObj.getJSONArray("body");
				for (int i = 0; i < respBody.length(); i++) {
					JSONObject bodyIndivObj = respBody.getJSONObject(i);
					String parameter = "", installment = "";
					if (bodyIndivObj.has("SL.NO")) {
						parameter = bodyIndivObj.getString("SL.NO");
					}
					if (bodyIndivObj.has("Total Due")) {
						installment = bodyIndivObj.getString("Total Due");
					}

					// going to inside into loan table with value of installmentValue
					if (parameter.equalsIgnoreCase("2")) {
						logger.debug("Inside installment Details 2");
						if (installment == null || installment.trim().isEmpty() || applicationId == null
								|| applicationId.trim().isEmpty()) {
							logger.warn("Skipping update: installment or applicationId is null/empty");
							return;
						}
						try {
							int updatedRows = loanDtlsRepo.updateInstallmentDetails(installment, applicationId);
							logger.debug("updatedRows for applicationId: ", +updatedRows);
							if (updatedRows > 0) {
								logger.debug("Successfully updated installment details for applicationId: {}",
										applicationId);
							} else {
								logger.warn("No record updated for applicationId: {}", applicationId);
							}
						} catch (Exception e) {
							logger.error(
									"Unexpected error occurred while updating installment details for applicationId: {}",
									applicationId, e);
						}
						break;
					}
				}
			}
		}).subscribe();
	}

	/*
	 * @CircuitBreaker(name = "fallback", fallbackMethod =
	 * "updateStatusInMasterFallback") public void
	 * updateStatusInMaster(PopulateapplnWFRequest apiRequest) {
	 * List<ApplicationList> applicationDetailList =
	 * apiRequest.getRequestObj().getApplicationDetailList();
	 * 
	 * if (applicationDetailList == null || applicationDetailList.isEmpty()) {
	 * callToUpdateSingleRec(apiRequest.getRequestObj()); return; }
	 * 
	 * // Process each application using `callToUpdateSingleRec` for
	 * (ApplicationList applnRec : applicationDetailList) {
	 * PopulateapplnWFRequestFields requestObj = apiRequest.getRequestObj();
	 * requestObj.setApplicationId(applnRec.getApplicationId());
	 * requestObj.setVersionNum(applnRec.getVersionNum());
	 * callToUpdateSingleRec(requestObj); } }
	 */

	@CircuitBreaker(name = "fallback", fallbackMethod = "updateStatusInMasterFallback")
	public void updateStatusInMaster(PopulateapplnWFRequest apiRequest) {
		PopulateapplnWFRequestFields requestObj = apiRequest.getRequestObj();
		List<ApplicationList> applicationDetailList = apiRequest.getRequestObj().getApplicationDetailList();
		if (applicationDetailList != null && applicationDetailList.size() > 0) {
			for (ApplicationList applnRec : applicationDetailList) {
				requestObj.setApplicationId(applnRec.getApplicationId());
				requestObj.setVersionNum(applnRec.getVersionNum());
				callToUpdateSingleRec(requestObj);
			}
		} else {
			callToUpdateSingleRec(requestObj);
		}
	}

	/*
	 * private void callToUpdateSingleRec(PopulateapplnWFRequestFields requestObj) {
	 * logger.debug("=== Inside callToUpdateSingleRec ===");
	 * 
	 * // Fetch ApplicationMaster once ApplicationMaster appMaster =
	 * applicationMasterRepo .findByAppIdAndApplicationIdAndVersionNum(
	 * requestObj.getAppId(), requestObj.getApplicationId(),
	 * requestObj.getVersionNum()) .orElseGet(() ->
	 * applicationMasterRepo.findTopByApplicationId(requestObj.getApplicationId())
	 * .orElse(null));
	 * 
	 * if (appMaster == null) {
	 * logger.warn("ApplicationMaster not found for ID: {}",
	 * requestObj.getApplicationId()); return; }
	 * 
	 * // Update application status updateStatus(AppStatus.INPROGRESS.getValue(),
	 * appMaster, AppStatus.PENDING.getValue());
	 * 
	 * // Handle CB Response Optional<TbUaobCbResponse> cbRes = cbResRepository
	 * .findTopByApplicationIdOrderByVersionNumDesc(appMaster.getApplicationId());
	 * 
	 * cbRes.ifPresent(cb -> { if
	 * ("Y".equalsIgnoreCase(requestObj.getCbApproveManual())) {
	 * cb.setStatus("SUCCESS"); cb.setCbCheckstatus("SUCCESS");
	 * appMaster.setApplicationStatus(AppStatus.PENDING.getValue()); } else {
	 * cb.setStatus(AppStatus.REJECTED.getValue());
	 * cb.setCbCheckstatus(AppStatus.REJECTED.getValue());
	 * appMaster.setApplicationStatus(AppStatus.REJECTED.getValue()); }
	 * cbResRepository.save(cb); });
	 * 
	 * // Save updated application applicationMasterRepo.save(appMaster); }
	 */

	private void callToUpdateSingleRec(PopulateapplnWFRequestFields requestObj) {
		logger.debug("===Inside callToUpdateSingleRec===");

		ApplicationMaster applicationMasterRes = new ApplicationMaster();
		Optional<ApplicationMaster> appMaster = Optional.of(applicationMasterRes);
		try {
			appMaster = applicationMasterRepo.findByAppIdAndApplicationIdAndVersionNum(requestObj.getAppId(),
					requestObj.getApplicationId(), requestObj.getVersionNum());
		} catch (Exception e) {
			logger.error("Exception Occured " + e);
		}

		logger.debug("appMaster with version Provided:" + appMaster);
		if (appMaster.isEmpty()) {
			// code to get versionNumber for bulk approve:: START
			appMaster = applicationMasterRepo.findTopByApplicationId(requestObj.getApplicationId());
			logger.debug("appMaster:" + appMaster);
			String versionNum = "";
			if (appMaster.isPresent()) {
				ApplicationMaster applicationMasterRec = appMaster.get();
				versionNum = applicationMasterRec.getVersionNum();
				requestObj.setVersionNum(versionNum);
				logger.debug("version Num updated:" + requestObj);
			}
			// code to get versionNumber for bulk approve:: END
		}

		if (appMaster.isPresent()) {
			logger.debug("appMaster present");
			ApplicationMaster appMasterObj = appMaster.get();
			updateStatus(AppStatus.INPROGRESS.getValue(), appMasterObj, AppStatus.PENDING.getValue());
			if (!CommonUtils.isNullOrEmpty(appMasterObj.getApplicationId())) {
				Optional<ApplicationMaster> appMasterRelated = applicationMasterRepo
						.findByAppIdAndApplicationIdAndVersionNum(requestObj.getAppId(),
								appMasterObj.getApplicationId(), requestObj.getVersionNum());
				logger.debug("appMaster present and appMasterRelated val:" + appMasterRelated);
				if (appMasterRelated.isPresent()) {
					logger.debug("appMasterRelated present:");
					ApplicationMaster appMasterObjRelated = appMasterRelated.get();
					updateStatus(AppStatus.INPROGRESS.getValue(), appMasterObjRelated, AppStatus.PENDING.getValue());
				}
			}
			if ("Y".equalsIgnoreCase(requestObj.getCbApproveManual())) {
				logger.debug("CbApproveManual is Y");
				TbUaobCbResponse tbUaobCbResponse = new TbUaobCbResponse();
				Optional<TbUaobCbResponse> cbRes = Optional.of(tbUaobCbResponse);
				try {
					cbRes = cbResRepository.findByAppIdAndApplicationIdOrderByVersionNumDesc(appMasterObj.getAppId(),
							appMasterObj.getApplicationId());
				} catch (Exception e) {
					cbRes = cbResRepository
							.findTopByApplicationIdOrderByVersionNumDesc(appMasterObj.getApplicationId());
				}
				logger.debug("cbResRepository Response is" + cbRes);
				if (cbRes.isPresent()) {
					TbUaobCbResponse cbResObj = cbRes.get();
					if (!"FAILURE".equalsIgnoreCase(cbResObj.getCbCheckstatus())) {
						return;
					} else {
						cbResObj.setStatus("SUCCESS");
						cbResObj.setCbCheckstatus("SUCCESS");
						logger.debug("Printing cbResObj before save:" + cbResObj);
						cbResRepository.save(cbResObj);
						logger.debug("Application saved TB_UAOB_CB_RESPONSE");
						logger.debug("Printing appMasterObj:" + appMasterObj);
						applicationMasterRepo.save(appMasterObj);
					}
				}
// below line is commented because while CRT upload file in application master table applicationStatus is updating as PENDING 
				// appMasterObj.setApplicationStatus(AppStatus.PENDING.getValue());

			}
			if ("N".equalsIgnoreCase(requestObj.getCbApproveManual())
					&& ("REJECTED".equalsIgnoreCase(requestObj.getWorkflow().getAction())
							|| "REJECT".equalsIgnoreCase(requestObj.getWorkflow().getAction()))) {
				logger.debug("CbApproveManual is N with rejected flow");
				TbUaobCbResponse tbUaobCbResponse = new TbUaobCbResponse();
				Optional<TbUaobCbResponse> cbRes = Optional.of(tbUaobCbResponse);
				try {
					cbRes = cbResRepository.findByAppIdAndApplicationIdOrderByVersionNumDesc(appMasterObj.getAppId(),
							appMasterObj.getApplicationId());
				} catch (Exception e) {
					cbRes = cbResRepository
							.findTopByApplicationIdOrderByVersionNumDesc(appMasterObj.getApplicationId());
				}
				logger.debug("cbResRepository Response is" + cbRes);
				if (cbRes.isPresent()) {
					TbUaobCbResponse cbResObj = cbRes.get();
					cbResObj.setStatus(AppStatus.REJECTED.getValue());
					cbResObj.setCbCheckstatus(AppStatus.REJECTED.getValue());
					logger.debug("Printing cbResObj before save:" + cbResObj);
					cbResRepository.save(cbResObj);
					logger.debug("Application saved TB_UAOB_CB_RESPONSE");
				}
				appMasterObj.setApplicationStatus(AppStatus.REJECTED.getValue());
				logger.debug("Printing appMasterObj:" + appMasterObj);
				applicationMasterRepo.save(appMasterObj);
				logger.debug("Application saved TB_UACO_APPLICATION_MASTER");
			}
			logger.debug("Printing getNextStageId:" + requestObj.getWorkflow());
			if ("DRAFT".equalsIgnoreCase(requestObj.getWorkflow().getNextStageId())) {
				logger.debug("Printing getNextStageId:" + requestObj.getWorkflow().getNextStageId());
				appMasterObj.setApplicationStatus(AppStatus.REJECTED.getValue());
				logger.debug("Printing Draft Application reject cases:" + appMasterObj);
				applicationMasterRepo.save(appMasterObj);
			}
			if ("REJECT".equalsIgnoreCase(requestObj.getWorkflow().getAction())
					&& "LOANINPUT".equalsIgnoreCase(requestObj.getWorkflow().getWorkflowId())
					&& "USERREJECT".equalsIgnoreCase(requestObj.getWorkflow().getNextStageId())) {
				appMasterObj.setApplicationStatus(AppStatus.REJECTED.getValue());
				logger.debug("Printing User reject cases:" + appMasterObj);
				applicationMasterRepo.save(appMasterObj);
			}
		}
	}

	public void updateWorkflowAfterCBCheckScheduler(String applicationId, String appId, String versionNo) {
		logger.debug("updateWorkflowAfterCBCheckScheduler started ...");
		try {
			PopulateapplnWFRequest req = new PopulateapplnWFRequest();
			PopulateapplnWFRequestFields reqFields = new PopulateapplnWFRequestFields();
			reqFields.setAppId(appId);
			reqFields.setApplicationId(applicationId);
			reqFields.setApplicationStatus("Move to BM");
			reqFields.setVersionNum(versionNo);

			WorkFlowDetails wrkFlow = new WorkFlowDetails();
			wrkFlow.setNextWorkflowStatus("SANCTIONINPROGRESS");
			wrkFlow.setNextStageId("BMQUEUE");
			wrkFlow.setNextRole("BM");
			reqFields.setWorkflow(wrkFlow);
			req.setRequestObj(reqFields);

			// below code for self assignment : akshay.shahane :: START
			List<ApplicationMaster> appMasterListForAM = applicationMasterRepo
					.findByApplicationId(cbRequest.getRequestObj().getApplicationId());
			if (appMasterListForAM != null && appMasterListForAM.size() > 0) {
				ApplicationMaster appMaster = appMasterListForAM.get(0);
				String productGroupCode = appMaster.getProductGroupCode();
				if ("BM".equalsIgnoreCase(productGroupCode)) {
					WorkFlowDetails wrkFlowAM = new WorkFlowDetails();
					wrkFlowAM.setNextWorkflowStatus("SANCTIONINPROGRESS");
					wrkFlowAM.setNextStageId("AMQUEUE");
					wrkFlowAM.setNextRole("AM");
					reqFields.setApplicationStatus("Move to AM");
					reqFields.setWorkflow(wrkFlowAM);
					req.setRequestObj(reqFields);
				}
			}
			// self assignment :: END

			int wfSeqNum = 1;
			Optional<ApplicationWorkflow> wfObj = applnWfRepository
					.findTopByAppIdAndApplicationIdAndVersionNumOrderByWorkflowSeqNumDesc(reqFields.getAppId(),
							reqFields.getApplicationId(), Integer.parseInt(reqFields.getVersionNum()));
			if (wfObj.isPresent()) {
				ApplicationWorkflow dbObj = wfObj.get();
				wfSeqNum = dbObj.getWorkflowSeqNum() + 1;
			}
			WorkFlowDetails workFlow = reqFields.getWorkflow();
			ApplicationWorkflow workFlowObj = new ApplicationWorkflow();
			workFlowObj.setAppId(reqFields.getAppId());
			workFlowObj.setApplicationId(reqFields.getApplicationId());

			String status = workFlow.getNextWorkflowStatus();
			workFlowObj.setApplicationStatus(status);
			workFlowObj.setCreatedBy(reqFields.getCreatedBy());
			workFlowObj.setCreateTs(LocalDateTime.now());
			if (workFlow != null) {
				workFlowObj.setNextWorkFlowStage(workFlow.getNextStageId());
				workFlowObj.setCurrentRole(workFlow.getCurrentRole());
				workFlowObj.setRemarks(workFlow.getRemarks());
			}

			workFlowObj.setVersionNum(Integer.parseInt(reqFields.getVersionNum()));
			workFlowObj.setWorkflowSeqNum(wfSeqNum);
			logger.debug("updateWorkflowAfterCBCheckScheduler workflow request :: " + workFlowObj);
			applnWfRepository.save(workFlowObj);
			saveAuditTrialDetailsForKMActionPostCBCheck(reqFields, 8, "SCHEDULER");

			// List<ApplicationMaster> appMasterList = applicationMasterRepo
			// .findByApplicationId(cbRequest.getRequestObj().getApplicationId());
			// if (appMasterList != null && appMasterList.size() > 0) {
			// ApplicationMaster appMaster = appMasterList.get(0);
			// appMaster.setApplicationStatus(AppStatus.PENDING.getValue());
			// applicationMasterRepo.save(appMaster);
			// }
		} catch (Exception exp) {
			logger.error("Error occurred while updating the workflow in scheduler :: " + exp);
		}
	}

	public void updateStatus(String fromStatus, ApplicationMaster appMasterObj, String toStatus) {
		if (fromStatus.equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
			appMasterObj.setApplicationStatus(toStatus);
			applicationMasterRepo.save(appMasterObj);
		}
	}

	public Response fetchApplicationList(List<String> kmId) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		response.setResponseHeader(responseHeader);
		ResponseBody responseBody = new ResponseBody();
		ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

		// List<ApplicationMaster> applicationMasterList =
		// applicationMasterRepo.findByKmId(kmId);
		List<ApplicationDetailsDto> applicationDtlsDTO = fetchDashBoardData(kmId);
		if (!applicationDtlsDTO.isEmpty()) {

			CommonUtils.generateHeaderForSuccess(responseHeader);
			try {
				String responseString = objectMapper.writeValueAsString(applicationDtlsDTO);
				responseBody.setResponseObj(responseString);
			} catch (JsonProcessingException e) {
				logger.error("exception occurred: ", e);
			}
			logger.debug("application master list: {}", applicationDtlsDTO);
		} else {
			CommonUtils.generateHeaderForNoResult(responseHeader);
			responseBody.setResponseObj("");
		}
		response.setResponseBody(responseBody);
		logger.debug("Response of fetchApplicationList : {}", response);
		return response;
	}

	public Response fetchApplicationList1(JSONObject requestObj) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		response.setResponseHeader(responseHeader);
		ResponseBody responseBody = new ResponseBody();
		ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule())
				.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
		logger.debug("fetchApplicationList1 Request Object " + requestObj);
		List<ApplicationDetailsDto> applicationDtlsDTO = fetchDashBoardData1(requestObj);

		if (!applicationDtlsDTO.isEmpty()) {

			CommonUtils.generateHeaderForSuccess(responseHeader);
			try {
				String responseString = objectMapper.writeValueAsString(applicationDtlsDTO);
				responseBody.setResponseObj(responseString);
			} catch (JsonProcessingException e) {
				logger.error("exception occurred: ", e);
			}
			logger.debug("application master list: {}", applicationDtlsDTO);
		} else {
			CommonUtils.generateHeaderForNoResult(responseHeader);
			responseBody.setResponseObj("");
		}
		response.setResponseBody(responseBody);
		logger.debug("Response of fetchApplicationList : {}", response);
		return response;
	}

	@Transactional
	public List<ApplicationDetailsDto> fetchDashBoardData1(JSONObject requestObj) {

		logger.debug("Fetching requestObj: " + requestObj);
		List<ApplicationDetailsDto> applicationDtlsList = new ArrayList<>();

		List<String> allRoles = Arrays.asList("AM", "RM", "DM", "ZM", "SH");
		List<String> hierarchyList = new ArrayList<>();

		String role = null;
		String kmId = null;
		try {

			if (requestObj.has("role")) {
				logger.debug("In the first if ");
				role = requestObj.getString("role");
				logger.debug("Role is: " + role);
			} else if (requestObj.getJSONObject("apiRequest").getJSONObject("requestObj").has("role")) {
				logger.debug("In the second if ");
				role = requestObj.getJSONObject("apiRequest").getJSONObject("requestObj").getString("role");
				logger.debug("Role is: " + role);
			}

			else {
				logger.debug("In the third if");
				logger.warn("Role is missing or empty.");
			}
		} catch (Exception e) {
			logger.debug("Error while fetching role value:" + e);
		}
		// approval-matrix change to fetch role data :: START
		try {
			String areaId1 = "";
			List<String> branchLists = new ArrayList<>();
			String roleName = "";
			if (requestObj.getJSONObject("apiRequest").has("role")) {

				areaId1 = requestObj.getJSONObject("apiRequest").getJSONObject("requestObj").getString("areaId");
				logger.debug("Printing areadId : " + areaId1);

				roleName = requestObj.getJSONObject("apiRequest").getJSONObject("requestObj").getString("role");

				if (!roleName.isEmpty()) {
					if (roleName.equalsIgnoreCase("AM")) {
						branchLists = tbOfficeDataRepository.findByAreaId(areaId1);
						return fetchSanctionApprovalListForAM(branchLists);
					} else if ((roleName.equalsIgnoreCase("RM")) || (roleName.equalsIgnoreCase("DM"))) {
						branchLists = tbOfficeDataRepository.findByRegionId(areaId1);
						return fetchSanctionApprovalListForRM(branchLists);
					} else if (roleName.equalsIgnoreCase("ZM")) {
						branchLists = tbOfficeDataRepository.findByZoneId(areaId1);
						return fetchSanctionApprovalListForZM(branchLists);
					} else if (roleName.equalsIgnoreCase("SH")) {
						branchLists = tbOfficeDataRepository.findByStateId(areaId1);
						return fetchAllSanctionApplicationLists(branchLists);
					}
					

					if (!roleName.equalsIgnoreCase("SH") && !roleName.equalsIgnoreCase("KM")
							&& !roleName.equalsIgnoreCase("AM") && !roleName.equalsIgnoreCase("DM")
							&& !roleName.equalsIgnoreCase("RM") && !roleName.equalsIgnoreCase("ZM")) {
						return fetchSanctionApprovalList(branchLists);
					}
				}
				logger.debug("Printing branchLists for fetch DeshboardAPI : " + branchLists);
			} else {
				logger.error("apiRequest does not have role");
			}
			logger.debug("Printing branchLists for fetch DeshboardAPI : " + branchLists);
		} catch (Exception e1) {
			logger.error("Error Occured :" + e1);
		}

		if (role != null && !role.isEmpty()) {
			if (role.equalsIgnoreCase("DEO")) {
				kmId = requestObj.getString("branchId");
			}
		} else {
			kmId = requestObj.getJSONObject("apiRequest").getJSONObject("requestObj").getString("kmId");
		}

		JSONArray KendraArray = new JSONArray();
		List<String> kendraId = new ArrayList<>();

		boolean isBranch = false;
		if (kmId != null) {
			isBranch = kmId.toUpperCase().contains("IN");
		}
		if (!isBranch) {
			KendraArray = requestObj.getJSONObject("apiRequest").getJSONObject("requestObj").getJSONArray("kendra");
			for (int i = 0; i < KendraArray.length(); i++) {
				kendraId.add("" + KendraArray.getInt(i));
			}
		}

		String sqlQuery = "WITH latest_workflow_view AS ( " + "    SELECT " + "        aw.APPLICATION_ID, "
				+ "        MAX(aw.workflow_seq_no) AS max_workflow_seq_no " + "    FROM "
				+ "        public.tb_uawf_appln_workflow aw " + "    GROUP BY " + "        aw.APPLICATION_ID " + "), "
				+ "dashboard_data AS ( " + "    SELECT " + "        am.APP_ID, " + "        am.APPLICATION_ID, "
				+ "        am.LATEST_VERSION_NO, " + "        am.APPLICATION_DATE, " + "        am.APPLICATION_STATUS, "
				+ "        am.APPLICATION_TYPE, " + "        am.BRANCH_CODE, " + "        am.CB_CHECK, "
				+ "        am.CREATE_TS, " + "        am.CREATED_BY, " + "        am.CURRENT_SCREEN_ID, "
				+ "        am.CURRENT_STAGE, " + "        am.CUSTOMER_ID, " + "        am.KENDRA_ID, "
				+ "        am.KENDRANAME, " + "        am.KMID, " + "        am.KYC_TYPE, " + "        am.LEADER, "
				+ "        am.LOANMODE, " + "        am.PRODUCT_CODE, " + "        am.PRODUCT_GROUP_CODE, "
				+ "        am.REMARKS, " + "        am.CUSTOMER_NAME, " + "        am.amount, "
				+ "        aw.workflow_seq_no, " + "        aw.application_status, "
				+ "        aw.created_ts AS aw_create_ts, " + "        aw.created_by AS aw_created_by, "
				+ "        am.ADD_INFO " + "    FROM " + "        public.tb_uaco_application_master am "
				+ "    INNER JOIN latest_workflow_view lw ON lw.APPLICATION_ID = am.APPLICATION_ID "
				+ "    INNER JOIN public.tb_uawf_appln_workflow aw "
				+ "        ON aw.APPLICATION_ID = lw.APPLICATION_ID "
				+ "        AND aw.workflow_seq_no = lw.max_workflow_seq_no " + " WHERE "
				+ (isBranch ? " am.BRANCH_CODE =:branchCode " : " am.KENDRA_ID IN :kendraIds ") + " "
				+ "        AND am.APPLICATION_STATUS NOT IN ('CANCELLED', 'REJECTED') "
				+ "        AND am.APPLICATION_TYPE IS NULL " + ") " + "SELECT * " + "FROM dashboard_data ";

		logger.debug("Final SQL query is " + sqlQuery);

		jakarta.persistence.Query query = entityManager.createNativeQuery(sqlQuery);
		if (isBranch) {
			query.setParameter("branchCode", kmId);
		} else {
			query.setParameter("kendraIds", kendraId);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> objList = query.getResultList();
		for (Object[] objarr : objList) {
			ApplicationDetailsDto app = new ApplicationDetailsDto();
			app.setAppId((String) objarr[0]);
			app.setApplicationId((String) objarr[1]);
			app.setVersionNo((String) objarr[2]);
			app.setApplicationDate((Date) objarr[3]);
			app.setApplicationStatus((String) objarr[4]);
			app.setApplicationType((String) objarr[5]);
			app.setBranchCode((String) objarr[6]);
			app.setCbCheck((String) objarr[7]);
			app.setCreateTs((Timestamp) objarr[8]);
			app.setCreatedBy((String) objarr[9]);
			app.setCurrentScreenId((String) objarr[10]);
			app.setCurrentStage((String) objarr[11]);
			app.setCustomerId((String) objarr[12]);
			app.setKendraId((String) objarr[13]);
			app.setKendraName((String) objarr[14]);
			app.setKmid((String) objarr[15]);
			app.setKycType((String) objarr[16]);
			app.setLeader((String) objarr[17]);
			app.setLoanMode((String) objarr[18]);
			app.setProductCode((String) objarr[19]);
			app.setProductGroupCode((String) objarr[20]);
			app.setRemarks((String) objarr[21]);
			app.setCustomerName((String) objarr[22]);
			app.setAmount((BigDecimal) objarr[23]);
			app.setWorkflowSeqNo((int) objarr[24]);
			app.setWfStatus((String) objarr[25]);
			app.setWfCreateTs((Timestamp) objarr[26]);
			app.setWfCreatedBy((String) objarr[27]);
			app.setAddInfo((String) objarr[28]);
			applicationDtlsList.add(app);
		}
		return applicationDtlsList;
	}

	public List<ApplicationDetailsDto> fetchAMData(List<String> hierarchyRoles, List<String> branchDataOfAm) {
		logger.debug("Inside fetchAMData service");
		logger.debug("hierarchyRoles are ::" + hierarchyRoles);
		List<ApplicationDetailsDto> finalResponse = new ArrayList<>();

		String sql = "SELECT * FROM( SELECT am.APP_ID, am.APPLICATION_ID, am.LATEST_VERSION_NO, am.APPLICATION_DATE, am.APPLICATION_STATUS, am.APPLICATION_TYPE, "
				+ "am.BRANCH_CODE, am.CB_CHECK, am.CREATE_TS, am.CREATED_BY, am.CURRENT_SCREEN_ID, am.CURRENT_STAGE, am.CUSTOMER_ID, am.KENDRA_ID, am.KENDRANAME, "
				+ "am.KMID, am.KYC_TYPE, am.LEADER, am.LOANMODE, am.PRODUCT_CODE, am.PRODUCT_GROUP_CODE, am.REMARKS, am.CUSTOMER_NAME, am.amount, aw.workflow_seq_no, "
				+ "aw.application_status, aw.created_ts AS aw_CREATE_TS, aw.created_by AS aw_CREATED_BY, aw.present_role, am.ADD_INFO FROM public.tb_uaco_application_master am"
				+ " LEFT JOIN public.tb_uawf_appln_workflow aw ON CAST(am.APPLICATION_ID AS text) = CAST(aw.APPLICATION_ID AS text) AND "
				+ "aw.workflow_seq_no = ( SELECT MAX(sub_aw.workflow_seq_no) FROM public.tb_uawf_appln_workflow sub_aw WHERE "
				+ "CAST(sub_aw.APPLICATION_ID AS text) = CAST(am.APPLICATION_ID AS text)) WHERE aw.present_role IN (:hierarchyRoles) AND "
				+ "am.branch_code IN (:branchDataOfAm) AND am.APPLICATION_ID = aw.APPLICATION_ID AND am.APPLICATION_STATUS NOT IN ('CANCELLED', 'REJECTED') AND "
				+ "am.APPLICATION_TYPE IS NULL )";

		logger.debug("Final SQL query is " + sql);

		jakarta.persistence.Query query = entityManager.createNativeQuery(sql);

		query.setParameter("hierarchyRoles", hierarchyRoles);
		query.setParameter("branchDataOfAm", branchDataOfAm);

		@SuppressWarnings("unchecked")
		List<Object[]> objList = query.getResultList();
		for (Object[] objarr : objList) {
			ApplicationDetailsDto app = new ApplicationDetailsDto();
			app.setAppId((String) objarr[0]);
			app.setApplicationId((String) objarr[1]);
			app.setVersionNo((String) objarr[2]);
			app.setApplicationDate((Date) objarr[3]);
			app.setApplicationStatus((String) objarr[4]);
			app.setApplicationType((String) objarr[5]);
			app.setBranchCode((String) objarr[6]);
			app.setCbCheck((String) objarr[7]);
			app.setCreateTs((Timestamp) objarr[8]);
			app.setCreatedBy((String) objarr[9]);
			app.setCurrentScreenId((String) objarr[10]);
			app.setCurrentStage((String) objarr[11]);
			app.setCustomerId((String) objarr[12]);
			app.setKendraId((String) objarr[13]);
			app.setKendraName((String) objarr[14]);
			app.setKmid((String) objarr[15]);
			app.setKycType((String) objarr[16]);
			app.setLeader((String) objarr[17]);
			app.setLoanMode((String) objarr[18]);
			app.setProductCode((String) objarr[19]);
			app.setProductGroupCode((String) objarr[20]);
			app.setRemarks((String) objarr[21]);
			app.setCustomerName((String) objarr[22]);
			app.setAmount((BigDecimal) objarr[23]);
			// below are the workflow table fields
			app.setWorkflowSeqNo((int) objarr[24]);
			app.setWfStatus((String) objarr[25]);
			app.setWfCreateTs((Timestamp) objarr[26]);
			app.setWfCreatedBy((String) objarr[27]);
			app.setPresentRole((String) objarr[28]);
			app.setAddInfo((String) objarr[29]);
			finalResponse.add(app);
		}
		logger.debug("Printing Final response:" + finalResponse);
		return finalResponse;
	}

	public List<ApplicationDetailsDto> fetchSanctionApprovalList(List<String> branchLists) {
		logger.debug("Inside fetchAMData service:" + branchLists);
		List<ApplicationDetailsDto> finalResponse = new ArrayList<>();

		String inSql = branchLists.stream().map(b -> "?").collect(Collectors.joining(", "));
		logger.debug("Fetch for fetchSanctionApprovalList inSql:" + inSql);

		String sql = "WITH latest_workflow AS ("
				+ "          SELECT aw.APPLICATION_ID, MAX(aw.workflow_seq_no) AS max_workflow_seq_no "
				+ "          FROM public.tb_uawf_appln_workflow aw GROUP BY aw.APPLICATION_ID) SELECT "
				+ "          am.APP_ID, am.APPLICATION_ID, am.LATEST_VERSION_NO, am.APPLICATION_DATE, "
				+ "          am.APPLICATION_STATUS, am.APPLICATION_TYPE, am.BRANCH_CODE, am.CB_CHECK, "
				+ "          am.CREATE_TS, am.CREATED_BY, am.CURRENT_SCREEN_ID, am.CURRENT_STAGE, "
				+ "          am.CUSTOMER_ID, am.KENDRA_ID, am.KENDRANAME, am.KMID, am.KYC_TYPE, "
				+ "          am.LEADER, am.LOANMODE, am.PRODUCT_CODE, am.PRODUCT_GROUP_CODE, "
				+ "          am.REMARKS, am.CUSTOMER_NAME, am.amount, aw.workflow_seq_no, "
				+ "          aw.application_status AS aw_application_status, aw.created_ts AS aw_create_ts, "
				+ "          aw.created_by AS aw_created_by, aw.present_role, am.ADD_INFO "
				+ "          FROM public.tb_uaco_application_master am "
				+ "          JOIN latest_workflow lw ON am.APPLICATION_ID = lw.APPLICATION_ID "
				+ "          JOIN public.tb_uawf_appln_workflow aw ON lw.APPLICATION_ID = aw.APPLICATION_ID "
				+ "          AND lw.max_workflow_seq_no = aw.workflow_seq_no "
				+ "          WHERE aw.application_status IN ('SANCTIONINPROGRESS', 'APPROVALINPROGRESS') "
				+ "          AND am.BRANCH_CODE IN (" + inSql + ") "
				+ "          AND am.APPLICATION_STATUS NOT IN ('CANCELLED', 'REJECTED', 'DISBURSED') "
				+ "          AND am.APPLICATION_TYPE IS NULL";

		logger.debug("Final branch SQL query is " + sql);

		jakarta.persistence.Query query = entityManager.createNativeQuery(sql);
		// query.setParameter("branchDataOfAm", branchLists);

		for (int i = 0; i < branchLists.size(); i++) {
			query.setParameter(i + 1, branchLists.get(i));
		}

		@SuppressWarnings("unchecked")
		List<Object[]> objList = query.getResultList();
		for (Object[] objarr : objList) {
			ApplicationDetailsDto app = new ApplicationDetailsDto();
			app.setAppId((String) objarr[0]);
			app.setApplicationId((String) objarr[1]);
			app.setVersionNo((String) objarr[2]);
			app.setApplicationDate((Date) objarr[3]);
			app.setApplicationStatus((String) objarr[4]);
			app.setApplicationType((String) objarr[5]);
			app.setBranchCode((String) objarr[6]);
			app.setCbCheck((String) objarr[7]);
			app.setCreateTs((Timestamp) objarr[8]);
			app.setCreatedBy((String) objarr[9]);
			app.setCurrentScreenId((String) objarr[10]);
			app.setCurrentStage((String) objarr[11]);
			app.setCustomerId((String) objarr[12]);
			app.setKendraId((String) objarr[13]);
			app.setKendraName((String) objarr[14]);
			app.setKmid((String) objarr[15]);
			app.setKycType((String) objarr[16]);
			app.setLeader((String) objarr[17]);
			app.setLoanMode((String) objarr[18]);
			app.setProductCode((String) objarr[19]);
			app.setProductGroupCode((String) objarr[20]);
			app.setRemarks((String) objarr[21]);
			app.setCustomerName((String) objarr[22]);
			app.setAmount((BigDecimal) objarr[23]);
			// below are the workflow table fields
			app.setWorkflowSeqNo((int) objarr[24]);
			app.setWfStatus((String) objarr[25]);
			app.setWfCreateTs((Timestamp) objarr[26]);
			app.setWfCreatedBy((String) objarr[27]);
			app.setPresentRole((String) objarr[28]);
			app.setAddInfo((String) objarr[29]);
			finalResponse.add(app);
		}

		logger.debug("Printing Final response:" + finalResponse);
		return finalResponse;
	}

	public List<ApplicationDetailsDto> fetchSanctionApprovalListForAM(List<String> branchLists) {
		logger.debug("Inside fetchAMData service:" + branchLists);
		List<ApplicationDetailsDto> finalResponse = new ArrayList<>();

		String inSql = branchLists.stream().map(b -> "?").collect(Collectors.joining(", "));
		logger.debug("Fetch for AM inSql:" + inSql);

		String sql = "WITH latest_workflow AS ("
				+ "          SELECT aw.APPLICATION_ID, MAX(aw.workflow_seq_no) AS max_workflow_seq_no "
				+ "          FROM public.tb_uawf_appln_workflow aw GROUP BY aw.APPLICATION_ID) SELECT "
				+ "          am.APP_ID, am.APPLICATION_ID, am.LATEST_VERSION_NO, am.APPLICATION_DATE, "
				+ "          am.APPLICATION_STATUS, am.APPLICATION_TYPE, am.BRANCH_CODE, am.CB_CHECK, "
				+ "          am.CREATE_TS, am.CREATED_BY, am.CURRENT_SCREEN_ID, am.CURRENT_STAGE, "
				+ "          am.CUSTOMER_ID, am.KENDRA_ID, am.KENDRANAME, am.KMID, am.KYC_TYPE, "
				+ "          am.LEADER, am.LOANMODE, am.PRODUCT_CODE, am.PRODUCT_GROUP_CODE, "
				+ "          am.REMARKS, am.CUSTOMER_NAME, am.amount, aw.workflow_seq_no, "
				+ "          aw.application_status AS aw_application_status, aw.created_ts AS aw_create_ts, "
				+ "          aw.created_by AS aw_created_by, aw.present_role, am.ADD_INFO "
				+ "          FROM public.tb_uaco_application_master am "
				+ "          JOIN latest_workflow lw ON am.APPLICATION_ID = lw.APPLICATION_ID "
				+ "          JOIN public.tb_uawf_appln_workflow aw ON lw.APPLICATION_ID = aw.APPLICATION_ID "
				+ "          AND lw.max_workflow_seq_no = aw.workflow_seq_no "
				+ "          WHERE aw.application_status IN ('SANCTIONINPROGRESS', 'APPROVALINPROGRESS') "
				+ "          AND am.BRANCH_CODE IN (" + inSql + ") "
				+ "          AND am.APPLICATION_STATUS NOT IN ('CANCELLED', 'REJECTED', 'DISBURSED') "
				+ "          AND am.LEADER IN ('BA','BAR','BARD','BARDZ','BARDZS') " + "          AND am.APPLICATION_TYPE IS NULL";
		
		logger.debug("Final branch SQL query is " + sql);

		jakarta.persistence.Query query = entityManager.createNativeQuery(sql);
		// query.setParameter("branchDataOfAm", branchLists);

		for (int i = 0; i < branchLists.size(); i++) {
			query.setParameter(i + 1, branchLists.get(i));
		}

		@SuppressWarnings("unchecked")
		List<Object[]> objList = query.getResultList();
		for (Object[] objarr : objList) {
			ApplicationDetailsDto app = new ApplicationDetailsDto();
			app.setAppId((String) objarr[0]);
			app.setApplicationId((String) objarr[1]);
			app.setVersionNo((String) objarr[2]);
			app.setApplicationDate((Date) objarr[3]);
			app.setApplicationStatus((String) objarr[4]);
			app.setApplicationType((String) objarr[5]);
			app.setBranchCode((String) objarr[6]);
			app.setCbCheck((String) objarr[7]);
			app.setCreateTs((Timestamp) objarr[8]);
			app.setCreatedBy((String) objarr[9]);
			app.setCurrentScreenId((String) objarr[10]);
			app.setCurrentStage((String) objarr[11]);
			app.setCustomerId((String) objarr[12]);
			app.setKendraId((String) objarr[13]);
			app.setKendraName((String) objarr[14]);
			app.setKmid((String) objarr[15]);
			app.setKycType((String) objarr[16]);
			app.setLeader((String) objarr[17]);
			app.setLoanMode((String) objarr[18]);
			app.setProductCode((String) objarr[19]);
			app.setProductGroupCode((String) objarr[20]);
			app.setRemarks((String) objarr[21]);
			app.setCustomerName((String) objarr[22]);
			app.setAmount((BigDecimal) objarr[23]);
			// below are the workflow table fields
			app.setWorkflowSeqNo((int) objarr[24]);
			app.setWfStatus((String) objarr[25]);
			app.setWfCreateTs((Timestamp) objarr[26]);
			app.setWfCreatedBy((String) objarr[27]);
			app.setPresentRole((String) objarr[28]);
			app.setAddInfo((String) objarr[29]);
			finalResponse.add(app);
		}

		logger.debug("Printing Final response:" + finalResponse);
		return finalResponse;
	}

	public List<ApplicationDetailsDto> fetchSanctionApprovalListForRM(List<String> branchLists) {
		logger.debug("Inside fetchRMData service:" + branchLists);
		List<ApplicationDetailsDto> finalResponse = new ArrayList<>();

		String inSql = branchLists.stream().map(b -> "?").collect(Collectors.joining(", "));
		logger.debug("Fetch inSql:" + inSql);

		String sql = "WITH latest_workflow AS ("
				+ "          SELECT aw.APPLICATION_ID, MAX(aw.workflow_seq_no) AS max_workflow_seq_no "
				+ "          FROM public.tb_uawf_appln_workflow aw GROUP BY aw.APPLICATION_ID) SELECT "
				+ "          am.APP_ID, am.APPLICATION_ID, am.LATEST_VERSION_NO, am.APPLICATION_DATE, "
				+ "          am.APPLICATION_STATUS, am.APPLICATION_TYPE, am.BRANCH_CODE, am.CB_CHECK, "
				+ "          am.CREATE_TS, am.CREATED_BY, am.CURRENT_SCREEN_ID, am.CURRENT_STAGE, "
				+ "          am.CUSTOMER_ID, am.KENDRA_ID, am.KENDRANAME, am.KMID, am.KYC_TYPE, "
				+ "          am.LEADER, am.LOANMODE, am.PRODUCT_CODE, am.PRODUCT_GROUP_CODE, "
				+ "          am.REMARKS, am.CUSTOMER_NAME, am.amount, aw.workflow_seq_no, "
				+ "          aw.application_status AS aw_application_status, aw.created_ts AS aw_create_ts, "
				+ "          aw.created_by AS aw_created_by, aw.present_role, am.ADD_INFO "
				+ "          FROM public.tb_uaco_application_master am "
				+ "          JOIN latest_workflow lw ON am.APPLICATION_ID = lw.APPLICATION_ID "
				+ "          JOIN public.tb_uawf_appln_workflow aw ON lw.APPLICATION_ID = aw.APPLICATION_ID "
				+ "          AND lw.max_workflow_seq_no = aw.workflow_seq_no "
				+ "          WHERE aw.application_status IN ('SANCTIONINPROGRESS', 'APPROVALINPROGRESS') "
				+ "          AND am.BRANCH_CODE IN (" + inSql + ") "
				+ "          AND am.APPLICATION_STATUS NOT IN ('CANCELLED', 'REJECTED', 'DISBURSED') "
				+ "          AND am.LEADER IN ('BAR','BARD','BARDZ','BARDZS') " + "          AND am.APPLICATION_TYPE IS NULL";
		
		logger.debug("Final branch RM SQL query is " + sql);

		jakarta.persistence.Query query = entityManager.createNativeQuery(sql);
		// query.setParameter("branchDataOfRM", branchLists);
		for (int i = 0; i < branchLists.size(); i++) {
			query.setParameter(i + 1, branchLists.get(i));
		}

		@SuppressWarnings("unchecked")
		List<Object[]> objList = query.getResultList();
		for (Object[] objarr : objList) {
			ApplicationDetailsDto app = new ApplicationDetailsDto();
			app.setAppId((String) objarr[0]);
			app.setApplicationId((String) objarr[1]);
			app.setVersionNo((String) objarr[2]);
			app.setApplicationDate((Date) objarr[3]);
			app.setApplicationStatus((String) objarr[4]);
			app.setApplicationType((String) objarr[5]);
			app.setBranchCode((String) objarr[6]);
			app.setCbCheck((String) objarr[7]);
			app.setCreateTs((Timestamp) objarr[8]);
			app.setCreatedBy((String) objarr[9]);
			app.setCurrentScreenId((String) objarr[10]);
			app.setCurrentStage((String) objarr[11]);
			app.setCustomerId((String) objarr[12]);
			app.setKendraId((String) objarr[13]);
			app.setKendraName((String) objarr[14]);
			app.setKmid((String) objarr[15]);
			app.setKycType((String) objarr[16]);
			app.setLeader((String) objarr[17]);
			app.setLoanMode((String) objarr[18]);
			app.setProductCode((String) objarr[19]);
			app.setProductGroupCode((String) objarr[20]);
			app.setRemarks((String) objarr[21]);
			app.setCustomerName((String) objarr[22]);
			app.setAmount((BigDecimal) objarr[23]);
			// below are the workflow table fields
			app.setWorkflowSeqNo((int) objarr[24]);
			app.setWfStatus((String) objarr[25]);
			app.setWfCreateTs((Timestamp) objarr[26]);
			app.setWfCreatedBy((String) objarr[27]);
			app.setPresentRole((String) objarr[28]);
			app.setAddInfo((String) objarr[29]);

			finalResponse.add(app);
		}
		logger.debug("Printing Final response:" + finalResponse);
		return finalResponse;
	}

	public List<ApplicationDetailsDto> fetchSanctionApprovalListForZM(List<String> branchLists) {
		logger.debug("Inside fetchZMData service :" + branchLists);
		List<ApplicationDetailsDto> finalResponse = new ArrayList<>();

		String inSql = branchLists.stream().map(b -> "?").collect(Collectors.joining(", "));
		logger.debug("FetchZMData inSql :" + inSql);

		String sql = "WITH latest_workflow AS ("
				+ "          SELECT aw.APPLICATION_ID, MAX(aw.workflow_seq_no) AS max_workflow_seq_no "
				+ "          FROM public.tb_uawf_appln_workflow aw GROUP BY aw.APPLICATION_ID) SELECT "
				+ "          am.APP_ID, am.APPLICATION_ID, am.LATEST_VERSION_NO, am.APPLICATION_DATE, "
				+ "          am.APPLICATION_STATUS, am.APPLICATION_TYPE, am.BRANCH_CODE, am.CB_CHECK, "
				+ "          am.CREATE_TS, am.CREATED_BY, am.CURRENT_SCREEN_ID, am.CURRENT_STAGE, "
				+ "          am.CUSTOMER_ID, am.KENDRA_ID, am.KENDRANAME, am.KMID, am.KYC_TYPE, "
				+ "          am.LEADER, am.LOANMODE, am.PRODUCT_CODE, am.PRODUCT_GROUP_CODE, "
				+ "          am.REMARKS, am.CUSTOMER_NAME, am.amount, aw.workflow_seq_no, "
				+ "          aw.application_status AS aw_application_status, aw.created_ts AS aw_create_ts, "
				+ "          aw.created_by AS aw_created_by, aw.present_role, am.ADD_INFO "
				+ "          FROM public.tb_uaco_application_master am "
				+ "          JOIN latest_workflow lw ON am.APPLICATION_ID = lw.APPLICATION_ID "
				+ "          JOIN public.tb_uawf_appln_workflow aw ON lw.APPLICATION_ID = aw.APPLICATION_ID "
				+ "          AND lw.max_workflow_seq_no = aw.workflow_seq_no "
				+ "          WHERE aw.application_status IN ('SANCTIONINPROGRESS', 'APPROVALINPROGRESS') "
				+ "          AND am.BRANCH_CODE IN (" + inSql + ") "
				+ "          AND am.APPLICATION_STATUS NOT IN ('CANCELLED', 'REJECTED', 'DISBURSED') "
				+ "          AND am.LEADER IN ('BARDZ','BARDZS') " + "          AND am.APPLICATION_TYPE IS NULL";

		logger.debug("Final branch ZM SQL query is " + sql);

		jakarta.persistence.Query query = entityManager.createNativeQuery(sql);
		// query.setParameter("branchDataOfZM", branchLists);

		for (int i = 0; i < branchLists.size(); i++) {
			query.setParameter(i + 1, branchLists.get(i));
		}

		@SuppressWarnings("unchecked")
		List<Object[]> objList = query.getResultList();
		for (Object[] objarr : objList) {
			ApplicationDetailsDto app = new ApplicationDetailsDto();
			app.setAppId((String) objarr[0]);
			app.setApplicationId((String) objarr[1]);
			app.setVersionNo((String) objarr[2]);
			app.setApplicationDate((Date) objarr[3]);
			app.setApplicationStatus((String) objarr[4]);
			app.setApplicationType((String) objarr[5]);
			app.setBranchCode((String) objarr[6]);
			app.setCbCheck((String) objarr[7]);
			app.setCreateTs((Timestamp) objarr[8]);
			app.setCreatedBy((String) objarr[9]);
			app.setCurrentScreenId((String) objarr[10]);
			app.setCurrentStage((String) objarr[11]);
			app.setCustomerId((String) objarr[12]);
			app.setKendraId((String) objarr[13]);
			app.setKendraName((String) objarr[14]);
			app.setKmid((String) objarr[15]);
			app.setKycType((String) objarr[16]);
			app.setLeader((String) objarr[17]);
			app.setLoanMode((String) objarr[18]);
			app.setProductCode((String) objarr[19]);
			app.setProductGroupCode((String) objarr[20]);
			app.setRemarks((String) objarr[21]);
			app.setCustomerName((String) objarr[22]);
			app.setAmount((BigDecimal) objarr[23]);
			// below are the workflow table fields
			app.setWorkflowSeqNo((int) objarr[24]);
			app.setWfStatus((String) objarr[25]);
			app.setWfCreateTs((Timestamp) objarr[26]);
			app.setWfCreatedBy((String) objarr[27]);
			app.setPresentRole((String) objarr[28]);
			app.setAddInfo((String) objarr[29]);
			finalResponse.add(app);
		}
		logger.debug("Printing Final response:" + finalResponse);
		return finalResponse;
	}

	public List<ApplicationDetailsDto> fetchAllSanctionApplicationLists(List<String> branchLists) {
		logger.debug("Inside fetchSHData service:");
		List<ApplicationDetailsDto> finalResponse = new ArrayList<>();

		String inSql = branchLists.stream().map(b -> "?").collect(Collectors.joining(", "));
		logger.debug("FetchSHData inSql :" + inSql);

		String sql = "WITH latest_workflow AS ("
				+ "          SELECT aw.APPLICATION_ID, MAX(aw.workflow_seq_no) AS max_workflow_seq_no "
				+ "          FROM public.tb_uawf_appln_workflow aw GROUP BY aw.APPLICATION_ID) SELECT "
				+ "          am.APP_ID, am.APPLICATION_ID, am.LATEST_VERSION_NO, am.APPLICATION_DATE, "
				+ "          am.APPLICATION_STATUS, am.APPLICATION_TYPE, am.BRANCH_CODE, am.CB_CHECK, "
				+ "          am.CREATE_TS, am.CREATED_BY, am.CURRENT_SCREEN_ID, am.CURRENT_STAGE, "
				+ "          am.CUSTOMER_ID, am.KENDRA_ID, am.KENDRANAME, am.KMID, am.KYC_TYPE, "
				+ "          am.LEADER, am.LOANMODE, am.PRODUCT_CODE, am.PRODUCT_GROUP_CODE, "
				+ "          am.REMARKS, am.CUSTOMER_NAME, am.amount, aw.workflow_seq_no, "
				+ "          aw.application_status AS aw_application_status, aw.created_ts AS aw_create_ts, "
				+ "          aw.created_by AS aw_created_by, aw.present_role, am.ADD_INFO "
				+ "          FROM public.tb_uaco_application_master am "
				+ "          JOIN latest_workflow lw ON am.APPLICATION_ID = lw.APPLICATION_ID "
				+ "          JOIN public.tb_uawf_appln_workflow aw ON lw.APPLICATION_ID = aw.APPLICATION_ID "
				+ "          AND lw.max_workflow_seq_no = aw.workflow_seq_no "
				+ "          WHERE aw.application_status IN ('SANCTIONINPROGRESS', 'APPROVALINPROGRESS') "
				+ "          AND am.BRANCH_CODE IN (" + inSql + ") "
				+ "          AND am.APPLICATION_STATUS NOT IN ('CANCELLED', 'REJECTED', 'DISBURSED') "
				+ "          AND am.LEADER IN ('BARDZS') " + " AND am.APPLICATION_TYPE IS NULL";

		logger.debug("Final SH SQL query is " + sql);
		jakarta.persistence.Query query = entityManager.createNativeQuery(sql);
		
		for (int i = 0; i < branchLists.size(); i++) {
			query.setParameter(i + 1, branchLists.get(i));
		}

		@SuppressWarnings("unchecked")
		List<Object[]> objList = query.getResultList();
		for (Object[] objarr : objList) {
			ApplicationDetailsDto app = new ApplicationDetailsDto();
			app.setAppId((String) objarr[0]);
			app.setApplicationId((String) objarr[1]);
			app.setVersionNo((String) objarr[2]);
			app.setApplicationDate((Date) objarr[3]);
			app.setApplicationStatus((String) objarr[4]);
			app.setApplicationType((String) objarr[5]);
			app.setBranchCode((String) objarr[6]);
			app.setCbCheck((String) objarr[7]);
			app.setCreateTs((Timestamp) objarr[8]);
			app.setCreatedBy((String) objarr[9]);
			app.setCurrentScreenId((String) objarr[10]);
			app.setCurrentStage((String) objarr[11]);
			app.setCustomerId((String) objarr[12]);
			app.setKendraId((String) objarr[13]);
			app.setKendraName((String) objarr[14]);
			app.setKmid((String) objarr[15]);
			app.setKycType((String) objarr[16]);
			app.setLeader((String) objarr[17]);
			app.setLoanMode((String) objarr[18]);
			app.setProductCode((String) objarr[19]);
			app.setProductGroupCode((String) objarr[20]);
			app.setRemarks((String) objarr[21]);
			app.setCustomerName((String) objarr[22]);
			app.setAmount((BigDecimal) objarr[23]);
			// below are the workflow table fields
			app.setWorkflowSeqNo((int) objarr[24]);
			app.setWfStatus((String) objarr[25]);
			app.setWfCreateTs((Timestamp) objarr[26]);
			app.setWfCreatedBy((String) objarr[27]);
			app.setPresentRole((String) objarr[28]);
			app.setAddInfo((String) objarr[29]);
			finalResponse.add(app);
		}
		logger.debug("Printing Final response:" + finalResponse);
		return finalResponse;
	}

	@Transactional
	public List<ApplicationDetailsDto> fetchDashBoardData(List<String> kmId) {

		List<ApplicationDetailsDto> applicationDtlsList = new ArrayList<>();

		boolean isBranch = false;
		if (kmId != null && kmId.size() > 0) {
			isBranch = kmId.stream().collect(Collectors.joining("")).toUpperCase().contains("IN");
		}
		String sql = "SELECT am.APP_ID, am.APPLICATION_ID, am.LATEST_VERSION_NO, am.APPLICATION_DATE, am.APPLICATION_STATUS, "
				+ "am.APPLICATION_TYPE, am.BRANCH_CODE, am.CB_CHECK, am.CREATE_TS, am.CREATED_BY, am.CURRENT_SCREEN_ID, "
				+ "am.CURRENT_STAGE, am.CUSTOMER_ID, am.KENDRA_ID, am.KENDRANAME, am.KMID, am.KYC_TYPE, am.LEADER, "
				+ "am.LOANMODE, am.PRODUCT_CODE, am.PRODUCT_GROUP_CODE, am.REMARKS, am.CUSTOMER_NAME, am.amount, "
				+ "aw.workflow_seq_no, aw.application_status, "
				+ "aw.created_ts AS aw_CREATE_TS, aw.created_by AS aw_CREATED_BY "
				+ "FROM public.tb_uaco_application_master am "
				+ "LEFT JOIN public.tb_uawf_appln_workflow aw ON cast(am.APPLICATION_ID as text) = cast(aw.APPLICATION_ID as text) "
				+ "AND aw.workflow_seq_no = (" + "    SELECT MAX(sub_aw.workflow_seq_no) "
				+ "    FROM public.tb_uawf_appln_workflow sub_aw "
				+ "    WHERE cast(sub_aw.APPLICATION_ID as text) = cast(am.APPLICATION_ID as text)" + ") " + "WHERE "
				+ (isBranch ? " am.BRANCH_CODE in :branchCode " : " am.KMID IN :kmId ")
				+ " AND am.APPLICATION_ID = aw.APPLICATION_ID AND am.APPLICATION_STATUS !='CANCELLED'";
		jakarta.persistence.Query query = entityManager.createNativeQuery(sql);
		if (isBranch) {
			query.setParameter("branchCode", kmId);
		} else {
			query.setParameter("kmId", kmId);
		}

		@SuppressWarnings("unchecked")
		List<Object[]> objList = query.getResultList();
		for (Object[] objarr : objList) {
			ApplicationDetailsDto app = new ApplicationDetailsDto();
			app.setAppId((String) objarr[0]);
			app.setApplicationId((String) objarr[1]);
			app.setVersionNo((String) objarr[2]);
			app.setApplicationDate((Date) objarr[3]);
			app.setApplicationStatus((String) objarr[4]);
			app.setApplicationType((String) objarr[5]);
			app.setBranchCode((String) objarr[6]);
			app.setCbCheck((String) objarr[7]);
			app.setCreateTs((Timestamp) objarr[8]);
			app.setCreatedBy((String) objarr[9]);
			app.setCurrentScreenId((String) objarr[10]);
			app.setCurrentStage((String) objarr[11]);
			app.setCustomerId((String) objarr[12]);
			app.setKendraId((String) objarr[13]);
			app.setKendraName((String) objarr[14]);
			app.setKmid((String) objarr[15]);
			app.setKycType((String) objarr[16]);
			app.setLeader((String) objarr[17]);
			app.setLoanMode((String) objarr[18]);
			app.setProductCode((String) objarr[19]);
			app.setProductGroupCode((String) objarr[20]);
			app.setRemarks((String) objarr[21]);
			app.setCustomerName((String) objarr[22]);
			app.setAmount((BigDecimal) objarr[23]);
			app.setWorkflowSeqNo((int) objarr[24]);
			app.setWfStatus((String) objarr[25]);
			app.setWfCreateTs((Timestamp) objarr[26]);
			app.setWfCreatedBy((String) objarr[27]);
			applicationDtlsList.add(app);
		}
		return applicationDtlsList;
	}

	@CircuitBreaker(name = "fallback", fallbackMethod = "fetchRoleFallback")
	public Response fetchRole(FetchRoleRequest apiRequest) {
		boolean customLoginFlow = false;
		Gson gson = new Gson();
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		FetchRoleRequestFields requestObj = apiRequest.getRequestObj();
		String roleId = "";
		String branchCode = "";
		if (!(CommonUtils.isNullOrEmpty(requestObj.getUserId()))) {// custom login flow
			customLoginFlow = true;
		}
		if (customLoginFlow) {
			roleId = fetchRoleId(apiRequest.getAppId(), requestObj.getUserId());
			branchCode = fetchBranchCode(apiRequest.getAppId(), requestObj.getUserId());
		} else {
			roleId = requestObj.getRoleId();
		}
		RoleAccessMap objDb = fetchRoleAccessMapObj(apiRequest.getAppId(), roleId);
		if (customLoginFlow && objDb != null) {
			List<String> statusList = fetchAllowedStatusListForRole(objDb, CommonConstants.FEATURE_DASHBOARD_WIDGETS);
			int numOfRecords = Integer.parseInt(numOfRecordsInWidget);
			long numOfDays = Long.parseLong(numOfDaysRecords);
			LocalDate toDay = LocalDate.now();
			LocalDate fromDay = toDay.minusDays(numOfDays);
			Pageable page = PageRequest.of(0, numOfRecords);
			List<ApplicationDetailsDto> appMasterList = new ArrayList<>();
			// List<ApplicationMaster> appMasterSubList;
			for (String status : statusList) {
				// appMasterList =
				// this.fetchDashBoardData(branchCode);

				// appMasterSubList = pageObj.getContent();
				// appMasterList.addAll(appMasterSubList);
			}
			List<List<ApplicationDetailsDto>> finalList = formQueueStatus(appMasterList, requestObj.getUserId(),
					objDb.getAccessPermission());
			JSONObject resjson = new JSONObject();
			resjson.put("roleAccessMap", objDb);
			resjson.put("appMasterData", appMasterList); // Stop sending this after system is stabilized.
			resjson.put("inprogress", finalList.get(0));
			resjson.put("pending", finalList.get(1));
			resjson.put("rejected", finalList.get(2));
			resjson.put("deleted", finalList.get(3));
			resjson.put("completed", finalList.get(4));
			resjson.put("allowedStatus", fetchAllowedStatusListForRole(objDb, CommonConstants.FEATURE_SEARCH));
			responseBody.setResponseObj(gson.toJson(resjson));
		} else {
			responseBody.setResponseObj(gson.toJson(objDb));
		}
		CommonUtils.generateHeaderForSuccess(responseHeader);
		response.setResponseBody(responseBody);
		response.setResponseHeader(responseHeader);
		return response;
	}

	public List<String> fetchAllowedStatusListForRole(RoleAccessMap roleAccessMapObj, String requiredFeature) {
		String allowedFeatures = roleAccessMapObj.getAllowedFeature();
		JSONObject json = new JSONObject(allowedFeatures);
		JSONArray jsonArray = json.getJSONArray(requiredFeature);
		ArrayList<String> dbFeaturesList = new ArrayList<>();
		for (Object arrayElement : jsonArray) {
			dbFeaturesList.add((String) arrayElement);
		}
		return dbFeaturesList;
	}

	private String fetchBranchCode(String appId, String userId) {
		Optional<User> userDb = userRepository.findById(new UserId(appId, userId));
		if (userDb.isPresent()) {
			User user = userDb.get();
			return user.getAddInfo2();
		}
		return null;
	}

	private List<List<ApplicationDetailsDto>> formQueueStatus(List<ApplicationDetailsDto> appMasterList,
			String loggedInUserId, String accessPermission) {
		List<ApplicationDetailsDto> appMasterListInProgress = new ArrayList<>();
		List<ApplicationDetailsDto> appMasterListPending = new ArrayList<>();
		List<ApplicationDetailsDto> appMasterListRejected = new ArrayList<>();
		List<ApplicationDetailsDto> appMasterListDeleted = new ArrayList<>();
		List<ApplicationDetailsDto> appMasterListCompleted = new ArrayList<>();
		List<List<ApplicationDetailsDto>> finalList = new ArrayList<>();
		for (ApplicationDetailsDto appMasterObj : appMasterList) {
			// Generic logic; either merge or customize based on requirement
			if (accessPermission.equalsIgnoreCase(CommonConstants.ACCESS_PERMISSION_INITIATOR)) {
				formQueueStatusInitiator(appMasterObj, appMasterListInProgress, appMasterListCompleted,
						appMasterListRejected, appMasterListDeleted, appMasterListPending, loggedInUserId);
			} else if (accessPermission.equalsIgnoreCase(CommonConstants.ACCESS_PERMISSION_APPROVER)) {
				formQueueStatusApprover(appMasterObj, appMasterListInProgress, appMasterListCompleted,
						appMasterListRejected, appMasterListDeleted, appMasterListPending, loggedInUserId);
			} else if (accessPermission.equalsIgnoreCase(CommonConstants.ACCESS_PERMISSION_BOTH)) {
				if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
						&& CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy())
						&& AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
					appMasterObj.setQueueStatus(WidgetQueueStatus.PARTIAL_BY_CUSTOMER.getValue());
					appMasterListInProgress.add(appMasterObj);
				} else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
						&& !CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy())
						&& appMasterObj.getCreatedBy().equalsIgnoreCase(loggedInUserId)
						&& AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
					appMasterObj.setQueueStatus(WidgetQueueStatus.PARTIAL_BY_SELF.getValue());
					appMasterListInProgress.add(appMasterObj);
				} else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
						&& !CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy())
						&& !appMasterObj.getCreatedBy().equalsIgnoreCase(loggedInUserId)
						&& AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
					appMasterObj.setQueueStatus(WidgetQueueStatus.PARTIAL_BY_OTHERS.getValue());
					appMasterListInProgress.add(appMasterObj);
				} else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
						&& !AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
					formQueueStatusForInProgress(appMasterObj, appMasterListInProgress);
				} else if (AppStatus.PENDING.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
					formQueueStatusForPending(appMasterObj, appMasterListPending,
							CommonConstants.ACCESS_PERMISSION_BOTH);
				} else if (AppStatus.APPROVED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
					appMasterObj.setQueueStatus(WidgetQueueStatus.COMPLETED.getValue());
					appMasterListCompleted.add(appMasterObj);
				} else if (AppStatus.REJECTED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
					appMasterObj.setQueueStatus(WidgetQueueStatus.REJECTED.getValue());
					appMasterListRejected.add(appMasterObj);
				} else if (AppStatus.DELETED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
					appMasterObj.setQueueStatus(WidgetQueueStatus.DELETED.getValue());
					appMasterListDeleted.add(appMasterObj);
				}
			} else if (accessPermission.equalsIgnoreCase(CommonConstants.ACCESS_PERMISSION_VIEWONLY)) {
				if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
						&& CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy())
						&& AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
					appMasterObj.setQueueStatus(WidgetQueueStatus.PARTIAL_BY_CUSTOMER.getValue());
					appMasterListInProgress.add(appMasterObj);
				} else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
						&& !CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy())
						&& !appMasterObj.getCreatedBy().equalsIgnoreCase(loggedInUserId)
						&& AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
					appMasterObj.setQueueStatus(WidgetQueueStatus.PARTIAL_BY_OTHERS.getValue());
					appMasterListInProgress.add(appMasterObj);
				} else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
						&& !CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy())
						&& appMasterObj.getCreatedBy().equalsIgnoreCase(loggedInUserId)
						&& AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
					appMasterObj.setQueueStatus(WidgetQueueStatus.PARTIAL_BY_SELF.getValue());
					appMasterListInProgress.add(appMasterObj);
				} else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
						&& !AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
					formQueueStatusForInProgress(appMasterObj, appMasterListInProgress);
				} else if (AppStatus.PENDING.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
					formQueueStatusForPending(appMasterObj, appMasterListPending,
							CommonConstants.ACCESS_PERMISSION_VIEWONLY);
				} else if (AppStatus.APPROVED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
					appMasterObj.setQueueStatus(WidgetQueueStatus.COMPLETED.getValue());
					appMasterListCompleted.add(appMasterObj);
				} else if (AppStatus.REJECTED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
					appMasterObj.setQueueStatus(WidgetQueueStatus.REJECTED.getValue());
					appMasterListRejected.add(appMasterObj);
				} else if (AppStatus.DELETED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
					appMasterObj.setQueueStatus(WidgetQueueStatus.DELETED.getValue());
					appMasterListDeleted.add(appMasterObj);
				}
			} else if (accessPermission.equalsIgnoreCase(CommonConstants.ACCESS_PERMISSION_VERIFIER)) {
				if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
						&& CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy())
						&& AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
					appMasterObj.setQueueStatus(WidgetQueueStatus.PARTIAL_BY_CUSTOMER.getValue());
					appMasterListInProgress.add(appMasterObj);
				} else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
						&& !CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy())
						&& !appMasterObj.getCreatedBy().equalsIgnoreCase(loggedInUserId)
						&& AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
					appMasterObj.setQueueStatus(WidgetQueueStatus.PARTIAL_BY_OTHERS.getValue());
					appMasterListInProgress.add(appMasterObj);
				} else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
						&& !CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy())
						&& appMasterObj.getCreatedBy().equalsIgnoreCase(loggedInUserId)
						&& AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
					appMasterObj.setQueueStatus(WidgetQueueStatus.PARTIAL_BY_SELF.getValue());
					appMasterListInProgress.add(appMasterObj);
				} else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
						&& !AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
					formQueueStatusForInProgress(appMasterObj, appMasterListInProgress);
				} else if (AppStatus.PENDING.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
					formQueueStatusForPending(appMasterObj, appMasterListPending,
							CommonConstants.ACCESS_PERMISSION_VERIFIER);
				} else if (AppStatus.APPROVED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
					appMasterObj.setQueueStatus(WidgetQueueStatus.COMPLETED.getValue());
					appMasterListCompleted.add(appMasterObj);
				} else if (AppStatus.REJECTED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
					if (loggedInUserId.equalsIgnoreCase(appMasterObj.getWfCreatedBy())) { // verifier should not see
																							// rejected applications
																							// rejected by others
						appMasterObj.setQueueStatus(WidgetQueueStatus.REJECTED.getValue());
						appMasterListRejected.add(appMasterObj);
					}
				} else if (AppStatus.DELETED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
					appMasterObj.setQueueStatus(WidgetQueueStatus.DELETED.getValue());
					appMasterListDeleted.add(appMasterObj);
				}
			}
		}
		finalList.add(appMasterListInProgress);
		finalList.add(appMasterListPending);
		finalList.add(appMasterListRejected);
		finalList.add(appMasterListDeleted);
		finalList.add(appMasterListCompleted);
		return finalList;
	}

	private void formQueueStatusApprover(ApplicationDetailsDto appMasterObj,
			List<ApplicationDetailsDto> appMasterListInProgress, List<ApplicationDetailsDto> appMasterListCompleted,
			List<ApplicationDetailsDto> appMasterListRejected, List<ApplicationDetailsDto> appMasterListDeleted,
			List<ApplicationDetailsDto> appMasterListPending, String loggedInUserId) {
		if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
				&& CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy())
				&& AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
			appMasterObj.setQueueStatus(WidgetQueueStatus.PARTIAL_BY_CUSTOMER.getValue());
			appMasterListInProgress.add(appMasterObj);
		} else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
				&& !CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy())
				&& !appMasterObj.getCreatedBy().equalsIgnoreCase(loggedInUserId)
				&& AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
			appMasterObj.setQueueStatus(WidgetQueueStatus.PARTIAL_BY_OTHERS.getValue());
			appMasterListInProgress.add(appMasterObj);
		} else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
				&& !AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
			formQueueStatusForInProgress(appMasterObj, appMasterListInProgress);
		} else if (AppStatus.PENDING.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
			formQueueStatusForPending(appMasterObj, appMasterListPending, CommonConstants.ACCESS_PERMISSION_APPROVER);
		} else if (AppStatus.APPROVED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
			formQueueStatusApproverApproved(loggedInUserId, appMasterObj, appMasterListCompleted);
		} else if (AppStatus.REJECTED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
			if (loggedInUserId.equalsIgnoreCase(appMasterObj.getWfCreatedBy())) { // approver should not see rejected
																					// applications rejected by others
				appMasterObj.setQueueStatus(WidgetQueueStatus.REJECTED.getValue());
				appMasterListRejected.add(appMasterObj);
			}
		} else if (AppStatus.DELETED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
			appMasterObj.setQueueStatus(WidgetQueueStatus.DELETED.getValue());
			appMasterListDeleted.add(appMasterObj);
		}
	}

	private void formQueueStatusApproverApproved(String loggedInUserId, ApplicationDetailsDto appMasterObj,
			List<ApplicationDetailsDto> appMasterListCompleted) {
		if (loggedInUserId.equalsIgnoreCase(appMasterObj.getWfCreatedBy())) { // approver should not see completed
																				// applications approved by others
			appMasterObj.setQueueStatus(WidgetQueueStatus.COMPLETED.getValue());
			appMasterListCompleted.add(appMasterObj);
		}
		String stpFlag = "";

		if (Products.CASA.getKey().equalsIgnoreCase(appMasterObj.getProductGroupCode())) {
			stpFlag = accountSTP;
		} else if (Products.DEPOSIT.getKey().equalsIgnoreCase(appMasterObj.getProductGroupCode())) {
			stpFlag = depositSTP;
		} else if (Products.CARDS.getKey().equalsIgnoreCase(appMasterObj.getProductGroupCode())) {
			stpFlag = cardSTP;
		} else if (Products.LOAN.getKey().equalsIgnoreCase(appMasterObj.getProductGroupCode())) {
			stpFlag = loanSTP;
		}
		if (CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy()) && "Y".equalsIgnoreCase(stpFlag)) { // self
																										// onboarding
																										// applications
																										// with STP flag
																										// Y should be
																										// shown.
			appMasterObj.setQueueStatus(WidgetQueueStatus.COMPLETED.getValue());
			appMasterListCompleted.add(appMasterObj);
		}
	}

	private void formQueueStatusInitiator(ApplicationDetailsDto appMasterObj,
			List<ApplicationDetailsDto> appMasterListInProgress, List<ApplicationDetailsDto> appMasterListCompleted,
			List<ApplicationDetailsDto> appMasterListRejected, List<ApplicationDetailsDto> appMasterListDeleted,
			List<ApplicationDetailsDto> appMasterListPending, String loggedInUserId) {
		if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
				&& CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy())
				&& AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
			appMasterObj.setQueueStatus(WidgetQueueStatus.PARTIAL_BY_CUSTOMER.getValue());
			appMasterListInProgress.add(appMasterObj);
		} else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
				&& !CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy())
				&& appMasterObj.getCreatedBy().equalsIgnoreCase(loggedInUserId)
				&& AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
			appMasterObj.setQueueStatus(WidgetQueueStatus.PARTIAL_BY_SELF.getValue());
			appMasterListInProgress.add(appMasterObj);
		} else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
				&& !CommonUtils.isNullOrEmpty(appMasterObj.getCreatedBy())
				&& !appMasterObj.getCreatedBy().equalsIgnoreCase(loggedInUserId)
				&& AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
			appMasterObj.setQueueStatus(WidgetQueueStatus.PARTIAL_BY_OTHERS.getValue());
			appMasterListInProgress.add(appMasterObj);
		} else if (AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())
				&& !AppStatus.INPROGRESS.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
			formQueueStatusForInProgress(appMasterObj, appMasterListInProgress);
		} else if (AppStatus.PENDING.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
			formQueueStatusForPending(appMasterObj, appMasterListPending, CommonConstants.ACCESS_PERMISSION_INITIATOR);
		} else if (AppStatus.APPROVED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
			appMasterObj.setQueueStatus(WidgetQueueStatus.COMPLETED.getValue());
			appMasterListCompleted.add(appMasterObj);
		} else if (AppStatus.REJECTED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
			appMasterObj.setQueueStatus(WidgetQueueStatus.REJECTED.getValue());
			appMasterListRejected.add(appMasterObj);
		} else if (AppStatus.DELETED.getValue().equalsIgnoreCase(appMasterObj.getApplicationStatus())) {
			appMasterObj.setQueueStatus(WidgetQueueStatus.DELETED.getValue());
			appMasterListDeleted.add(appMasterObj);
		}
	}

	private void formQueueStatusForPending(ApplicationDetailsDto appMasterObj,
			List<ApplicationDetailsDto> appMasterListPending, String accessPermission) {
		if (CommonConstants.ACCESS_PERMISSION_VERIFIER.equalsIgnoreCase(accessPermission)) { // verifier should not see
																								// pending for approval
			if (WorkflowStatus.PENDING_FOR_VERIFICATION.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
				appMasterObj.setQueueStatus(WidgetQueueStatus.PENDING_FOR_VERIFICATION.getValue());
				appMasterListPending.add(appMasterObj);
			}
		} else if (CommonConstants.ACCESS_PERMISSION_APPROVER.equalsIgnoreCase(accessPermission)) { // approver should
																									// not see pending
																									// for verification
			if (WorkflowStatus.PENDING_FOR_APPROVAL.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
				appMasterObj.setQueueStatus(WidgetQueueStatus.PENDING_FOR_APPROVAL.getValue());
				appMasterListPending.add(appMasterObj);
			}
		} else { // generic logic for initiator, view only, both accessPermission. Change based
					// on requirement.
			if (WorkflowStatus.PENDING_FOR_VERIFICATION.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
				appMasterObj.setQueueStatus(WidgetQueueStatus.PENDING_FOR_VERIFICATION.getValue());
				appMasterListPending.add(appMasterObj);
			} else if (WorkflowStatus.PENDING_FOR_APPROVAL.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
				appMasterObj.setQueueStatus(WidgetQueueStatus.PENDING_FOR_APPROVAL.getValue());
				appMasterListPending.add(appMasterObj);
			}
		}
	}

	private void formQueueStatusForInProgress(ApplicationDetailsDto appMasterObj,
			List<ApplicationDetailsDto> appMasterListInProgress) {
		if (WorkflowStatus.PENDING_IN_QUEUE.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
			appMasterObj.setQueueStatus(WidgetQueueStatus.PENDING_IN_QUEUE.getValue());
			appMasterListInProgress.add(appMasterObj);
		} else if (WorkflowStatus.QUEUED_ASSIGNED.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
			appMasterObj.setQueueStatus(WidgetQueueStatus.ASSIGNED.getValue());
			appMasterListInProgress.add(appMasterObj);
		} else if (WorkflowStatus.PENDING_FOR_VERIFICATION.getValue().equalsIgnoreCase(appMasterObj.getWfStatus())) {
			appMasterObj.setQueueStatus(WidgetQueueStatus.PENDING_FOR_VERIFICATION.getValue());
			appMasterListInProgress.add(appMasterObj);
		}
	}

	@CircuitBreaker(name = "fallback", fallbackMethod = "fetchRoleAccessMapObjFallback")
	public RoleAccessMap fetchRoleAccessMapObj(String appId, String roleId) {
		RoleAccessMap objDb = null;
		RoleAccessMapId id = new RoleAccessMapId(appId, roleId);
		Optional<RoleAccessMap> obj = roleAccessMapRepository.findById(id);
		if (obj.isPresent()) {
			objDb = obj.get();
		}
		return objDb;
	}

	@CircuitBreaker(name = "fallback", fallbackMethod = "fetchRoleIdFallback")
	public String fetchRoleId(String appId, String userId) {
		String roleId = "";
		Optional<UserRole> objDb = userRoleRepository.findByAppIdAndUserId(appId, userId);
		if (objDb.isPresent()) {
			UserRole obj = objDb.get();
			roleId = obj.getRoleId();
		}
		return roleId;
	}

	private String fetchRoleIdFallback(String appId, String userId, Exception e) {
		logger.error("fetchRoleIdFallback error : ", e);
		return "";
	}

	private void updateStatusInMasterFallback(PopulateapplnWFRequest apiRequest, Exception e) {
		logger.error("updateStatusInMasterFallback error : request is : {} and error is: {} ", apiRequest, e);
	}

	private Response populateApplnWorkFlowFallback(PopulateapplnWFRequest request, Exception e) {
		logger.error("populateApplnWorkFlowFallback error : ", e);
		return FallbackUtils.genericFallback();
	}

	public Mono<Object> cbCheckFallback(CbRequest cbRequest, Header header, String flag, Throwable ex) {
		logger.error("cbCheck fallback error {}: , request is:{} and header is : {} ", ex, cbRequest, header);
		return FallbackUtils.genericFallbackMonoObject();
	}

	public Response fetchMatrixData(FetchRoleRequest apiRequest) {
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();
		Gson gson = new Gson();
		logger.debug("inside fetchMatrixData:{} ", apiRequest);
		try {
			Object[] matrixData = applicationMasterRepo.getMatrixData(apiRequest.getRequestObj().getUserId());
			HashMap<String, String> matrix = new HashMap<String, String>();
			matrix.put("cbSuccessCnt", "0");
			matrix.put("cbPendingCnt", "0");
			matrix.put("cbFailureCnt", "0");
			matrix.put("cbHoldCnt", "0");
			if (matrixData != null && matrixData.length > 0) {
				for (Object row : matrixData) {
					Object[] rowData = (Object[]) row;
					String key = "" + rowData[0];
					String val = "" + rowData[1];
					if ("SUCCESS".equalsIgnoreCase(key)) {
						matrix.put("cbSuccessCnt", val);
					} else if ("FAILURE".equalsIgnoreCase(key)) {
						matrix.put("cbFailureCnt", val);
					} else if ("HOLD".equalsIgnoreCase(key)) {
						matrix.put("cbHoldCnt", val);
					} else if ("PENDING".equalsIgnoreCase(key)) {
						matrix.put("cbPendingCnt", val);
					} else {
						matrix.put(key, "null".equals(val) ? "0" : val);
					}
				}
			}
			respBody.setResponseObj(gson.toJson(matrix));
			CommonUtils.generateHeaderForSuccess(respHeader);
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
		} catch (Exception e) {
			logger.error("exception at fetchMatrixData: ", e);
			respBody.setResponseObj(EXCEPTION_MSG);
			CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
		}
		return response;
	}

	public Mono<ResponseWrapper> callCBRecordService(FetchCBReportRequest apiRequest, Header header) {
		logger.debug("Inside callCBRecordService");
		logger.debug("Incoming APIRequest : " + apiRequest);
		Mono<Object> externalServiceResponse = Mono.empty();
		Mono<ResponseWrapper> monoResWrapper = null;
		logger.debug("CBRecordService API External Service Call :: STARTED ");
		try {
			header.setInterfaceId(CBCHECK_REPORT_INTERFACEID);
			externalServiceResponse = interfaceAdapter.callExternalService(header, apiRequest,
					CBCHECK_REPORT_INTERFACEID, true);

			return externalServiceResponse.flatMap(responseMono -> {
				logger.warn("Response : {}" + responseMono);
				Response res = new Response();
				ResponseWrapper respWrapper = new ResponseWrapper();
				ResponseBody resBody = new ResponseBody();
				resBody.setResponseObj(responseMono.toString());
				res.setResponseBody(resBody);
				ResponseHeader resHeader = new ResponseHeader();
				CommonUtils.generateHeaderForSuccess(resHeader);
				respWrapper.setApiResponse(res);
				return Mono.just(respWrapper);
			});

		} catch (Exception e) {
			logger.error("exception at dedupeCheck: ", e);
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

	public Mono<ResponseWrapper> callFetchLoanScheduleService(FetchLoanScheduleRequest apiRequest, Header header) {
		logger.debug("Inside callFetchLoanScheduleService");
		logger.debug("Incoming callFetchLoanScheduleService APIRequest : " + apiRequest);
		Mono<Object> externalServiceResponse = Mono.empty();
		try {
			header.setInterfaceId(FETCH_LOAN_SCHEDULE_INTERFACEID);
			externalServiceResponse = interfaceAdapter.callExternalService(header, apiRequest,
					FETCH_LOAN_SCHEDULE_INTERFACEID, true);

			return externalServiceResponse.flatMap(responseMono -> {
				logger.warn("Response Of FetchLoanScheduleService: {}" + responseMono);
				Response res = new Response();
				ResponseWrapper respWrapper = new ResponseWrapper();
				ResponseBody resBody = new ResponseBody();
				resBody.setResponseObj(responseMono.toString());
				res.setResponseBody(resBody);
				ResponseHeader resHeader = new ResponseHeader();
				CommonUtils.generateHeaderForSuccess(resHeader);
				respWrapper.setApiResponse(res);
				return Mono.just(respWrapper);
			});

		} catch (Exception e) {
			logger.error("exception at callFetchLoanScheduleService: ", e);
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

	public Mono<ResponseWrapper> callFetchSanctionLoanScheduleService(SanctionLoanScheduleRequest apiRequest,
			Header header) {
		logger.debug("Inside callFetchSanctionLoanScheduleService");
		logger.debug("Incoming SanctionLoanScheduleService APIRequest : " + apiRequest);
		Mono<Object> externalServiceResponse = Mono.empty();
		try {
			header.setInterfaceId(FETCH_SANCTION_LOAN_SCHEDULE_INTERFACEID);
			externalServiceResponse = interfaceAdapter.callExternalService(header, apiRequest,
					FETCH_SANCTION_LOAN_SCHEDULE_INTERFACEID, true);
			logger.debug("ExternalServiceResponse: {}" + externalServiceResponse);
			return externalServiceResponse.flatMap(responseMono -> {
				logger.warn("Response Of SanctionLoanScheduleService: {}" + responseMono);
				logger.debug("responseMono: {}" + responseMono);
				Response res = new Response();
				ResponseWrapper respWrapper = new ResponseWrapper();
				ResponseBody resBody = new ResponseBody();
				resBody.setResponseObj(new Gson().toJson(responseMono));
				res.setResponseBody(resBody);
				ResponseHeader resHeader = new ResponseHeader();
				CommonUtils.generateHeaderForSuccess(resHeader);
				respWrapper.setApiResponse(res);
				return Mono.just(respWrapper);
			});

		} catch (Exception e) {
			logger.error("exception at SanctionLoanScheduleService: ", e);
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

	private Response deleteApplicationLockCustomer(String applicationId) {
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

	@Transactional
	public boolean deleteApplication(String applicationId) {
		// if (applicationMasterRepo.existsById(applicationId) {
		int isDeleted = applicationMasterRepo.deleteByApplicationId(applicationId);
		logger.debug("isDeleted :: " + isDeleted);
		if (isDeleted == 1) {
			return true;
		} else {
			return false;
		}

	}

	@Transactional
	public Response fetchAuditTrialData(AuditTrailRequest apiRequests) {
		logger.debug("Printing apiRequests: {}", apiRequests);
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();
		JSONObject responseObject = new JSONObject();
		try {
			List<AuditTrailEntity> auditTrailEntities = new ArrayList<>();
			if (apiRequests.getRequestObj().getUserRole().equalsIgnoreCase("KM")) {
				if (!apiRequests.getRequestObj().getKendraId().isEmpty()) {
					auditTrailEntities = auditTrailRepo
							.fetchAuditDetailsBasedOnKendraIds(apiRequests.getRequestObj().getKendraId());
				} else {
					auditTrailEntities = auditTrailRepo
							.findByUserIdBasedOnUserRole(apiRequests.getRequestObj().getUserId());
				}
			} else {
				auditTrailEntities = auditTrailRepo.findUserBasedOnBranchId(apiRequests.getRequestObj().getBranchId());
			}
			logger.debug("Fetch auditTrailEntities : {}", auditTrailEntities);
			List<AuditTrailResponse> auditTrailResponses = auditTrailEntities.stream().map(entity -> {
				AuditTrailResponse auditTrailResponse = new AuditTrailResponse();
				auditTrailResponse.setAppId(entity.getAppId());
				auditTrailResponse.setApplicationId(entity.getApplicationId());
				auditTrailResponse.setUserId(entity.getUserId());
				auditTrailResponse.setUserRole(entity.getUserRole());
				auditTrailResponse.setStageId(entity.getStageid());
				auditTrailResponse.setCreateTs(entity.getCreateTs());
				auditTrailResponse.setCreateDate(entity.getCreateDate());
				auditTrailResponse.setAddInfo1(entity.getAddInfo1());
				auditTrailResponse.setRemarks(entity.getAddInfo2());
				auditTrailResponse.setAddInfo3(entity.getAddInfo3());
				auditTrailResponse.setAddInfo4(entity.getAddInfo4());
				auditTrailResponse.setLoanAmt(entity.getLoanAmount());
				auditTrailResponse.setMobileNumber(entity.getMobileNumber());
				auditTrailResponse.setPurpose(entity.getPurpose() != null ? entity.getPurpose().toString() : "");
				auditTrailResponse.setRepaymentFrequecy(
						entity.getRepaymentFrequency() != null ? entity.getRepaymentFrequency().toString() : "");
				auditTrailResponse.setBranchId(entity.getBranchId());
				auditTrailResponse.setPayload(entity.getPayload());
				auditTrailResponse.setProductId(entity.getProductId());
				auditTrailResponse.setCustomerId(entity.getCustomerId());
				auditTrailResponse.setKendreName(entity.getKendraName());
				auditTrailResponse.setCustomerName(entity.getCustomerName());
				auditTrailResponse.setSpouse(entity.getSpouse());
				auditTrailResponse.setUserName(entity.getUserName());
				auditTrailResponse.setKendraId(entity.getKendraId());
				auditTrailResponse.setAppVersion(entity.getAppVersion());
				return auditTrailResponse;
			}).toList();
			if (auditTrailResponses.isEmpty()) {
				respHeader.setResponseCode("2");
				respHeader.setResponseMessage("No audit trail data found.");
				respHeader.setHttpStatus(HttpStatus.NO_CONTENT);
				respBody.setResponseObj("{}");
			} else {
				respHeader.setResponseCode("0");
				respHeader.setResponseMessage("Audit Trail data fetched successfully.");
				respHeader.setHttpStatus(HttpStatus.OK);
				responseObject.put("auditTrailData", auditTrailResponses);
				respBody.setResponseObj(responseObject.toString());
			}
		} catch (Exception e) {
			logger.error("An error occurred while fetching the audit trail data: {}", e.getMessage(), e);
			respHeader.setResponseCode("1");
			respHeader.setResponseMessage("An error occurred while fetching the audit trail.");
			respHeader.setErrorMessage(e.getMessage());
			respHeader.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			respBody.setResponseObj("{}");
		}
		response.setResponseHeader(respHeader);
		response.setResponseBody(respBody);
		return response;
	}

	@Transactional
	public Response fetchAuditTrialApplicationId(ApplyApplicationIdRequest apiRequest) {
		logger.debug("Printing ApplyApplicationIdRequest: {}", apiRequest);
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();
		JSONObject responseObject = new JSONObject();
		try {
			List<AuditTrailEntity> auditTrailEntities = auditTrailRepo
					.findAllTrailDetails(apiRequest.getRequestObj().getApplicationId());
			logger.debug("Entering auditTrailEntities : {}", auditTrailEntities);

			List<AuditTrailResponse> auditTrailResponses = auditTrailEntities.stream().map(entity -> {
				AuditTrailResponse auditTrailResponse = new AuditTrailResponse();
				auditTrailResponse.setAppId(entity.getAppId());
				auditTrailResponse.setApplicationId(entity.getApplicationId());
				auditTrailResponse.setUserId(entity.getUserId());
				auditTrailResponse.setUserRole(entity.getUserRole());
				auditTrailResponse.setStageId(entity.getStageid());
				auditTrailResponse.setCreateTs(entity.getCreateTs());
				auditTrailResponse.setCreateDate(entity.getCreateDate());
				auditTrailResponse.setAddInfo1(entity.getAddInfo1());
				auditTrailResponse.setRemarks(entity.getAddInfo2());
				auditTrailResponse.setAddInfo3(entity.getAddInfo3());
				auditTrailResponse.setAddInfo4(entity.getAddInfo4());
				auditTrailResponse.setLoanAmt(entity.getLoanAmount());
				auditTrailResponse.setMobileNumber(entity.getMobileNumber());
				auditTrailResponse.setPurpose(entity.getPurpose() != null ? entity.getPurpose().toString() : "");
				auditTrailResponse.setRepaymentFrequecy(
						entity.getRepaymentFrequency() != null ? entity.getRepaymentFrequency().toString() : "");
				auditTrailResponse.setBranchId(entity.getBranchId());
				auditTrailResponse.setPayload(entity.getPayload());
				auditTrailResponse.setProductId(entity.getProductId());
				auditTrailResponse.setCustomerId(entity.getCustomerId());
				auditTrailResponse.setKendreName(entity.getKendraName());
				auditTrailResponse.setCustomerName(entity.getCustomerName());
				auditTrailResponse.setSpouse(entity.getSpouse());
				auditTrailResponse.setUserName(entity.getUserName());
				auditTrailResponse.setKendraId(entity.getKendraId());
				auditTrailResponse.setAppVersion(entity.getAppVersion());
				return auditTrailResponse;
			}).toList();
			if (auditTrailResponses.isEmpty()) {
				respHeader.setResponseCode("2");
				respHeader.setResponseMessage("No audit trail data found.");
				respHeader.setHttpStatus(HttpStatus.NO_CONTENT);
				respBody.setResponseObj("{}");
			} else {
				respHeader.setResponseCode("0");
				respHeader.setResponseMessage("Audit Trail data fetched successfully.");
				respHeader.setHttpStatus(HttpStatus.OK);
				responseObject.put("auditTrailData", auditTrailResponses);
				respBody.setResponseObj(responseObject.toString());
			}
		} catch (Exception e) {
			logger.error("An error occurred while fetching the audit trail data: {}", e.getMessage(), e);
			respHeader.setResponseCode("1");
			respHeader.setResponseMessage("An error occurred while fetching the audit trail.");
			respHeader.setErrorMessage(e.getMessage());
			respHeader.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
			respBody.setResponseObj("{}");

		}
		response.setResponseHeader(respHeader);
		response.setResponseBody(respBody);
		return response;

	}

	@Transactional
	public Map<String, Object> saveAuditTrialDetails(ApplyLoanRequest apiRequest, String applicationId) {
		logger.debug("Printing for saveAuditTrialDetails with apiRequest: {}", apiRequest);
		Optional<String> krandraId = Optional.ofNullable(apiRequest).map(ApplyLoanRequest::getRequestObj)
				.map(ApplyLoanRequestFields::getApplicationdtls).orElse(Collections.emptyList()).stream()
				.map(ApplicationDtls::getKendraId).filter(Objects::nonNull).findFirst();
		Optional<String> appVersion = Optional.ofNullable(apiRequest).map(ApplyLoanRequest::getRequestObj)
				.map(ApplyLoanRequestFields::getApplicationdtls).orElse(Collections.emptyList()).stream()
				.map(ApplicationDtls::getAppVersion).filter(Objects::nonNull).findFirst();
		Optional<String> outstandingInt = Optional.ofNullable(apiRequest).map(ApplyLoanRequest::getRequestObj)
				.map(ApplyLoanRequestFields::getApplicationdtls).orElse(Collections.emptyList()).stream()
				.map(ApplicationDtls::getOutstandingInterest).filter(Objects::nonNull).findFirst();
		Optional<String> outstandingPri = Optional.ofNullable(apiRequest).map(ApplyLoanRequest::getRequestObj)
				.map(ApplyLoanRequestFields::getApplicationdtls).orElse(Collections.emptyList()).stream()
				.map(ApplicationDtls::getOutstandingPrincipal).filter(Objects::nonNull).findFirst();
		Optional<String> activeLoanCount = Optional.ofNullable(apiRequest).map(ApplyLoanRequest::getRequestObj)
				.map(ApplyLoanRequestFields::getApplicationdtls).orElse(Collections.emptyList()).stream()
				.map(ApplicationDtls::getActiveLoanCount).filter(Objects::nonNull).findFirst();
		Optional<String> groupId = Optional.ofNullable(apiRequest).map(ApplyLoanRequest::getRequestObj)
				.map(ApplyLoanRequestFields::getApplicationdtls).orElse(Collections.emptyList()).stream()
				.map(ApplicationDtls::getGroupId).filter(Objects::nonNull).findFirst();
		Optional<String> meetingDay = Optional.ofNullable(apiRequest).map(ApplyLoanRequest::getRequestObj)
				.map(ApplyLoanRequestFields::getApplicationdtls).orElse(Collections.emptyList()).stream()
				.map(ApplicationDtls::getMeetingday).filter(Objects::nonNull).findFirst();
		Optional<String> kendraFrequency = Optional.ofNullable(apiRequest).map(ApplyLoanRequest::getRequestObj)
				.map(ApplyLoanRequestFields::getApplicationdtls).orElse(Collections.emptyList()).stream()
				.map(ApplicationDtls::getKendrafrequency).filter(Objects::nonNull).findFirst();
		Optional<String> loanFrequency = Optional.ofNullable(apiRequest).map(ApplyLoanRequest::getRequestObj)
				.map(ApplyLoanRequestFields::getApplicationdtls).orElse(Collections.emptyList()).stream()
				.map(ApplicationDtls::getLoanfrequency).filter(Objects::nonNull).findFirst();
		Optional<ChargeAndBreakupDetails> chargeDetailsOpt = Optional.ofNullable(apiRequest)
				.map(ApplyLoanRequest::getRequestObj).map(ApplyLoanRequestFields::getApplicationdtls)
				.filter(list -> !list.isEmpty()).map(list -> list.get(0)).map(ApplicationDtls::getCustomerDetails)
				.map(CustomerDtls::getLoanDtls).map(LoanDtls::getChargeAndBreakupDtls);
		Optional<InsuranceDetails> insuranceDtsOpt = Optional.ofNullable(apiRequest)
				.map(ApplyLoanRequest::getRequestObj).map(ApplyLoanRequestFields::getApplicationdtls)
				.filter(list -> !list.isEmpty()).map(list -> list.get(0)).map(ApplicationDtls::getCustomerDetails)
				.map(CustomerDtls::getLoanDtls).map(LoanDtls::getInsurDtls);
			
		List<Map<String, Object>> activeLoanDetails = apiRequest.getRequestObj()
		        .getApplicationdtls()
		        .stream()
		        .map(ApplicationDtls::getCustomerDetails)
		        .filter(Objects::nonNull)
		        .map(CustomerDtls::getLoanDtls)
		        .filter(Objects::nonNull)
		        .map(LoanDtls::getActiveLoanDtls)
		        .filter(Objects::nonNull)
		        .flatMap(List::stream)
		        .filter(Objects::nonNull)
		        .map(obj -> {
		            @SuppressWarnings("unchecked")
		            Map<String, Object> map = (Map<String, Object>) obj; 
		            Map<String, Object> selected = new HashMap<>();
		            selected.put("loanId", map.get("loanId"));
		            selected.put("status", map.get("status"));
		            selected.put("product", map.get("product"));
		            return selected;
		        })
		        .collect(Collectors.toList());
		logger.debug("Printing activeLoanDetails: {}", activeLoanDetails);
		
		Map<String, Object> chargeDetailsMap = chargeDetailsOpt
				.map(details -> new ObjectMapper().convertValue(details, Map.class)).orElse(Collections.emptyMap());
		logger.debug("Printing chargeDetailsMap: {}", chargeDetailsOpt);
		Map<String, Object> insuranceDetailsMap = insuranceDtsOpt
				.map(details -> new ObjectMapper().convertValue(details, Map.class)).orElse(Collections.emptyMap());
		logger.debug("Printing insuranceDtsOpt: {}", insuranceDtsOpt);
		Map<String, Object> response = new HashMap<>();
		AuditTrailEntity auditTrail = new AuditTrailEntity();
		Optional<String> applicationDate = Optional.empty();
		try {
			applicationDate = apiRequest.getRequestObj().getApplicationdtls().stream().findFirst()
					.map(ApplicationDtls::getApplicationDate);
		} catch (Exception e) {
			logger.debug("Error extracting applicationDate: {}", e.getMessage());
		}
		Optional<String> status = apiRequest.getRequestObj().getApplicationdtls().stream().findFirst()
				.map(ApplicationDtls::getStatus);
		Optional<String> createdBy = Optional.empty();
		try {
			createdBy = apiRequest.getRequestObj().getApplicationdtls().stream().findFirst()
					.map(ApplicationDtls::getCreatedBy);
			if (createdBy.isPresent()) {
				auditTrail.setUserId(createdBy.get());
			} else {
				auditTrail.setUserId("");
			}
		} catch (Exception e) {
			logger.debug("Error extracting createdBy: {}", e.getMessage());
		}
		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		String timestampString = currentTimestamp.toString();
		try {
			if (apiRequest != null && apiRequest.getRequestObj() != null) {
				Object addInfoObj = apiRequest.getRequestObj().getApplicationdtls().stream().findFirst()
						.map(ApplicationDtls::getAddInfo).orElse(null);
				Boolean isDraft = true;
				String foir = "";
				String apr = "";
				if (addInfoObj != null) {
					try {
						ObjectMapper objectMapper = new ObjectMapper();
						String addInfoStr = objectMapper.writeValueAsString(addInfoObj);
						logger.debug("Fetched addInfoStr : {}", addInfoStr);
						JsonNode rootNode = objectMapper.readTree(addInfoStr);
						JsonNode foirNode = rootNode.get("FOIR");
						JsonNode aprNode = rootNode.get("APR");
						logger.debug("Fetched aprNode : {},aprNode: {}", aprNode, aprNode);
						foir = foirNode != null && !foirNode.isNull() ? foirNode.asText() : "";
						apr = aprNode != null && !aprNode.isNull() ? aprNode.asText() : "";
						JsonNode incomeStatusNode = rootNode.get("incomeStatus");
						logger.debug("Fetched headerNode : {}", incomeStatusNode);
						if (incomeStatusNode != null && !incomeStatusNode.isNull()) {
							String incomeStatus = incomeStatusNode.asText();
							if ("PENDINGBYRPC".equalsIgnoreCase(incomeStatus)) {
								isDraft = false;
								logger.debug("Fetched isDraft : {}", isDraft);
							}
						}
					} catch (Exception e) {
						logger.debug("Error parsing addInfo JSON", e);
					}
				}
				String branchId = null;
				if (addInfoObj != null) {
					try {
						ObjectMapper objectMapper = new ObjectMapper();
						String addInfoStr = objectMapper.writeValueAsString(addInfoObj);
						JsonNode addInfoNode = objectMapper.readTree(addInfoStr);
						if (addInfoNode.has("branchId")) {
							branchId = addInfoNode.get("branchId").asText();
						}
					} catch (Exception e) {
						logger.debug("Error parsing addInfo JSON: ", e);
					}
				}
				if (branchId != null) {
					auditTrail.setBranchId(branchId);
				} else {
					auditTrail.setBranchId("");
				}
				auditTrail.setAppId(apiRequest.getAppId());
				if (krandraId.isPresent()) {
					auditTrail.setKendraId(krandraId.get());
				}
				auditTrail.setAddInfo2(apiRequest.getRequestObj().getRemarks());
				String productGroupCode = null;
				try {
					productGroupCode = apiRequest.getRequestObj().getApplicationdtls().stream().findFirst()
							.map(ApplicationDtls::getProductGroupCode).orElse(null);
					if (applicationId != null) {
						auditTrail.setApplicationId(applicationId);
						if (isDraft) {
							auditTrail.setStageid("1");
						} else {
							auditTrail.setStageid("2");
						}
						MisReport misReport = new MisReport();
						misReport.setApplicationId(applicationId);
						misReport.setCreateDate(timestampString);
						misReport.setUpdateDate(timestampString);
						misReport.setRemarks(apiRequest.getRequestObj().getRemarks());
						misReport.setUserrole(productGroupCode);
						misReport.setCreatedBy(createdBy.get());
						if (meetingDay.isPresent()) {
							misReport.setMeetingDay(meetingDay.get());
						}
						if (kendraFrequency.isPresent()) {
							misReport.setKendraFrequency(kendraFrequency.get());
						}
						if (loanFrequency.isPresent()) {
							misReport.setLoanFrequency(loanFrequency.get());
						}
						misReport.setKendraId(krandraId.get());
						if (groupId.isPresent()) {
							misReport.setGroupId(groupId.get());
						}
						if (appVersion.isPresent()) {
							misReport.setAppVersion(appVersion.get());
						}
						if (outstandingInt.isPresent()) {
							misReport.setOutstandingInt(outstandingInt.get());
						}
						if (outstandingPri.isPresent()) {
							misReport.setOutstandingPrincipal(outstandingPri.get());
						}
						misReport.setModifyBy(createdBy.get());
						misReport.setBranchId(branchId);
						if (activeLoanCount.isPresent()) {
							misReport.setActiveLoanCount(activeLoanCount.get());
						}
						misReport.setAddInfo("");
						misReport.setAddInfo2("");
						if (isDraft) {
							misReport.setApplicationStatus("DRAFT APPLICATION");
							misReport.setStageID("1");
						} else {
							misReport.setApplicationStatus("RPC VERIFICATION PENDING");
							misReport.setStageID("2");
						}
						logger.debug("MIS Report create  1 2:::", misReport);
						misReportRepository.save(misReport);
					}
					auditTrail.setUserRole(productGroupCode);
				} catch (Exception e) {
					logger.debug("Error extracting applicationId for save: {}", e.getMessage());
				}
				Optional<String> customerName = Optional.empty();
				try {
					customerName = apiRequest.getRequestObj().getApplicationdtls().stream().findFirst()
							.map(ApplicationDtls::getCustomerName);
					if (customerName.isPresent()) {
						auditTrail.setCustomerName(customerName.get());
					}
				} catch (Exception e) {
					logger.debug("Error extracting cutomerName: {}", e.getMessage());
				}
				auditTrail.setCreateTs(timestampString);
				Date currentDate = new Date(System.currentTimeMillis());
				String dateString = currentDate.toString();
				auditTrail.setCreateDate(dateString);
				Optional<String> firstAmount = Optional.empty();
				try {
					firstAmount = Optional.ofNullable(apiRequest.getRequestObj().getApplicationdtls().stream()
							.findFirst().map(ApplicationDtls::getCustomerDetails).map(CustomerDtls::getLoanDtls)
							.map(LoanDtls::getChargeAndBreakupDtls).get().getLoanAmt());
					if (firstAmount.isPresent()) {
						auditTrail.setLoanAmount(firstAmount.get());
					} else {
						auditTrail.setLoanAmount("0");
						logger.debug("No loan amount found, setting default value.");
					}
				} catch (Exception e) {
					logger.debug("Error extracting loan amount from request: {}", e.getMessage());
					auditTrail.setLoanAmount("0");
				}
				Optional<String> mobileNum = Optional.empty();
				try {
					mobileNum = apiRequest.getRequestObj().getApplicationdtls().stream().findFirst()
							.map(ApplicationDtls::getCustomerDetails).map(CustomerDtls::getKycDtls)
							.map(KYCDetails::getMobileNum);
					if (mobileNum.isPresent() && !mobileNum.get().isEmpty()) {
						auditTrail.setMobileNumber(mobileNum.get());
					} else {
						logger.debug("mobileNum is empty or not present.");
					}
				} catch (Exception e) {
					logger.debug("Error extracting mobileNum: ", e);
				}
				try {
					Optional<Object> purpose = apiRequest.getRequestObj().getApplicationdtls().stream().findFirst()
							.map(ApplicationDtls::getCustomerDetails).map(CustomerDtls::getLoanDtls)
							.map(LoanDtls::getPurpose);
					String purposeString = purpose.map(Object::toString).orElse("");
					auditTrail.setPurpose(purposeString);
				} catch (Exception e) {
					logger.debug("Error extracting purpose: {}", e.getMessage());
				}
				try {
					Optional<String> productId = apiRequest.getRequestObj().getApplicationdtls().stream().findFirst()
							.map(ApplicationDtls::getCustomerDetails).map(CustomerDtls::getLoanDtls)
							.map(LoanDtls::getProductId);
					if (productId.isPresent()) {
						auditTrail.setProductId(productId.get());
					}
				} catch (Exception e) {
					logger.debug("Error extracting productId: {}", e.getMessage());
				}
				Optional<String> customerId = Optional.empty();
				try {
					customerId = apiRequest.getRequestObj().getApplicationdtls().stream().findFirst()
							.map(ApplicationDtls::getCustomerId);
					// Sonar qube issue fixed
					if (customerId.isPresent()) {
						auditTrail.setCustomerId(customerId.get());
					}	
				} catch (Exception e) {
					logger.debug("Error extracting customerId: {}", e.getMessage());
				}
				Optional<String> spouse = Optional.empty();
				try {
					spouse = apiRequest.getRequestObj().getApplicationdtls().stream().findFirst()
							.map(ApplicationDtls::getCustomerDetails).map(CustomerDtls::getLoanDtls)
							.map(LoanDtls::getNomineeDtls).map(NomineeDtls::getNomineeName);
					if (spouse.isPresent() && !spouse.get().isEmpty()) {
						auditTrail.setSpouse(spouse.get());
					} else {
						auditTrail.setSpouse("");
						logger.debug("mobileNum is empty or not present.");
					}
				} catch (Exception e) {
					logger.debug("Error extracting nomineeName: {}", e.getMessage());
				}
				Optional<String> addInfo1 = Optional.empty();
				try {
					addInfo1 = Optional.ofNullable(apiRequest.getRequestObj().getApplicationdtls().stream().findFirst()
							.map(ApplicationDtls::getCustomerDetails).map(CustomerDtls::getLoanDtls)
							.map(LoanDtls::getChargeAndBreakupDtls).get().getAddInfo1());
					auditTrail.setAddInfo1(addInfo1.orElse(""));
				} catch (Exception e) {
					logger.debug("Error extracting addInfo1: {}", e.getMessage());
				}
				Optional<String> addInfo2 = Optional.empty();
				try {
					addInfo2 = Optional.ofNullable(apiRequest.getRequestObj().getApplicationdtls().stream().findFirst()
							.map(ApplicationDtls::getCustomerDetails).map(CustomerDtls::getLoanDtls)
							.map(LoanDtls::getChargeAndBreakupDtls).get().getAddInfo2());
					auditTrail.setAddInfo2(addInfo2.orElse(""));
				} catch (Exception e) {
					logger.debug("Error extracting addInfo2: {}", e.getMessage());
				}
				Optional<String> kendraName = Optional.empty();
				try {
					kendraName = apiRequest.getRequestObj().getApplicationdtls().stream().findFirst()
							.map(ApplicationDtls::getKendraName);
					if (kendraName.isPresent()) {
						auditTrail.setKendraName(kendraName.get());
					} else {
						auditTrail.setKendraName("");
					}
				} catch (Exception e) {
					logger.debug("Error extracting kendraName: {}", e.getMessage());
				}
				String userName = null;
				try {
					userName = apiRequest.getRequestObj().getUserName();
				} catch (Exception e) {
					logger.debug("Error extracting userName: {}", e.getMessage());
				}
				if (userName != null && !userName.trim().isEmpty()) {
					auditTrail.setUserName(userName);
				} else {
					auditTrail.setUserName("");
				}
				String appversion = null;
				try {
					appversion = apiRequest.getRequestObj().getAppVersion();
				} catch (Exception e) {
					logger.debug("Error extracting appversion: {}", e.getMessage());
				}
				if (appversion != null && !appversion.trim().isEmpty()) {
					auditTrail.setAppVersion(appversion);
				} else {
					auditTrail.setAppVersion("");
				}
				String remarks = null;
				try {
					remarks = apiRequest.getRequestObj().getRemarks();
				} catch (Exception e) {
					logger.debug("Error extracting remarks: {}", e.getMessage());
				}
				if (remarks != null && !remarks.trim().isEmpty()) {
					auditTrail.setAddInfo2(remarks);
				} else {
					auditTrail.setAddInfo2("");
				}
				String repayment_Frequency = null;
				try {
					Optional<Object> repaymentFrequency = apiRequest.getRequestObj().getApplicationdtls().stream()
							.findFirst().map(ApplicationDtls::getCustomerDetails).map(CustomerDtls::getLoanDtls)
							.map(LoanDtls::getRepayFrequency);
					repayment_Frequency = repaymentFrequency.map(Object::toString).orElse("");
					auditTrail.setRepaymentFrequency(repayment_Frequency);
				} catch (Exception e) {
					logger.debug("Error extracting repaymentFrequency: {}", e.getMessage());
				}
				Optional<String> updatedBy = apiRequest.getRequestObj().getApplicationdtls().stream().findFirst()
						.map(ApplicationDtls::getUpdatedBy);
				Optional<String> product = apiRequest.getRequestObj().getApplicationdtls().stream().findFirst()
						.map(ApplicationDtls::getCustomerDetails).map(CustomerDtls::getLoanDtls)
						.map(LoanDtls::getProduct);
				Optional<String> productId = apiRequest.getRequestObj().getApplicationdtls().stream().findFirst()
						.map(ApplicationDtls::getCustomerDetails).map(CustomerDtls::getLoanDtls)
						.map(LoanDtls::getProductId);
				Optional<String> productType = apiRequest.getRequestObj().getApplicationdtls().stream().findFirst()
						.map(ApplicationDtls::getCustomerDetails).map(CustomerDtls::getLoanDtls)
						.map(LoanDtls::getProductType);
				Optional<String> shortDesc = apiRequest.getRequestObj().getApplicationdtls().stream().findFirst()
						.map(ApplicationDtls::getCustomerDetails).map(CustomerDtls::getLoanDtls)
						.map(LoanDtls::getShortDesc);
				Optional<String> nomineeName = Optional.empty();
				try {
					nomineeName = apiRequest.getRequestObj().getApplicationdtls().stream().findFirst()
							.map(ApplicationDtls::getCustomerDetails).map(CustomerDtls::getLoanDtls)
							.map(LoanDtls::getNomineeDtls).map(NomineeDtls::getNomineeName);
				} catch (Exception e) {
					logger.debug("Error processing nomineeName: {}", e.getMessage());
				}
				Optional<String> relationWithMember = Optional.empty();
				try {
					relationWithMember = apiRequest.getRequestObj().getApplicationdtls().stream().findFirst()
							.map(ApplicationDtls::getCustomerDetails).map(CustomerDtls::getLoanDtls)
							.map(LoanDtls::getNomineeDtls).map(NomineeDtls::getRelationWithMember);
				} catch (Exception e) {
					logger.debug("Error processing nomineeName: {}", e.getMessage());
				}
				Optional<String> insuranceProvider = Optional.empty();
				try {
					insuranceProvider = apiRequest.getRequestObj().getApplicationdtls().stream().findFirst()
							.map(ApplicationDtls::getCustomerDetails).map(CustomerDtls::getLoanDtls)
							.map(LoanDtls::getInsurDtls).map(InsuranceDetails::getInsuranceProvider);
				} catch (Exception e) {
					logger.debug("Error processing nomineeName: {}", e.getMessage());
				}
				ObjectMapper objectMapper = new ObjectMapper();
				Map<String, String> payloadMap = new HashMap<>();	
				payloadMap.put("app_id", apiRequest.getAppId());
				payloadMap.put("applicationId", applicationId != null ? applicationId : null);
				payloadMap.put("applicationDate", applicationDate.orElse(""));
				payloadMap.put("createdBy", createdBy.orElse(""));
				payloadMap.put("UserRole", productGroupCode);
				payloadMap.put("updatedBy", updatedBy.orElse(""));
				payloadMap.put("status", status.orElse(""));
				payloadMap.put("productType", productType.orElse(""));
				payloadMap.put("customerId", customerId.orElse(""));
				payloadMap.put("customerName", customerName.orElse(""));
				payloadMap.put("mobile_Num", mobileNum.orElse(""));
				payloadMap.put("product", product.orElse(""));
				payloadMap.put("productId", productId.orElse(""));
				payloadMap.put("shortDesc", shortDesc.orElse(""));
				payloadMap.put("user_id", createdBy.get());
				payloadMap.put("create_ts", timestampString);
				payloadMap.put("dateString", dateString);
				payloadMap.put("branchId", branchId);
				payloadMap.put("kendraName", kendraName.orElse(""));
				payloadMap.put("repaymentFrequency", repayment_Frequency);
				payloadMap.put("spouse", spouse.orElse(""));
				payloadMap.put("nomineeName", nomineeName.orElse(""));
				payloadMap.put("relationWithMember", relationWithMember.orElse(""));
				payloadMap.put("insuranceProvider", insuranceProvider.orElse(""));
				payloadMap.put("chargeAndBreakupDetails", chargeDetailsMap.toString());
				payloadMap.put("insuranceDetails", insuranceDetailsMap.toString());
				payloadMap.put("FOIR", foir);
				payloadMap.put("APR", apr);
				if (!activeLoanDetails.isEmpty()) {
				    logger.debug("Printing activeLoanDetails : {}", activeLoanDetails);
				    try {
				        String activeLoanJson = objectMapper.writeValueAsString(activeLoanDetails);
				        payloadMap.put("activeLoanDetails", activeLoanJson);
				    } catch (Exception e) {
				        logger.error("Error while converting activeLoanDetails to JSON", e);
				        payloadMap.put("activeLoanDetails", "[]");
				    }
				} else {
				    payloadMap.put("activeLoanDetails", "[]");
				}
				String payloadJson;
				try {
					payloadJson = objectMapper.writeValueAsString(payloadMap);
					auditTrail.setPayload(payloadJson);
					logger.debug("payloadJson for save : {}", payloadJson);
				} catch (JsonProcessingException e) {
					logger.error("Exception occurred", e);
				}
				logger.debug("Create Application Audit Trail Entities before save: {}", auditTrail);
				auditTrailRepo.save(auditTrail);
				response.put("status", "success");
				response.put("message", "Audit trail saved successfully.");
				response.put("applicationId", auditTrail.getApplicationId());
			} else {
				response.put("status", "failure");
				response.put("message", "Invalid request: AuditTrailRequest or RequestObj is null.");
			}
		} catch (Exception e) {
			logger.error("An error occurred while saving the audit trail data: {}", e.getMessage());
			response.put("status", "error");
			response.put("message", "An error occurred while saving the audit trail.");
			response.put("errorDetails", e.getMessage());
		}
		return response;
	}

	@Transactional
	private void saveAuditTrialDetailsForUpdated(ApplicationDtls appDtls, String applicationIdDecimal, String userRole,
			String userId, String remarks, String userName, String appVersion) {
		logger.debug("Updated appDtls: {}, userRole (edit flow): {}, userId (edit flow): {}", appDtls, userRole,
				userId);
		String applicationId = applicationIdDecimal;
		String amount = appDtls.getAmount() != null ? appDtls.getAmount().toPlainString() : null;
		String repaymentFrequency = Optional.ofNullable(appDtls).map(ApplicationDtls::getCustomerDetails)
				.map(CustomerDtls::getLoanDtls).map(LoanDtls::getRepayFrequency).map(Object::toString).orElse("");
		String purpose = Optional.ofNullable(appDtls).map(ApplicationDtls::getCustomerDetails)
				.map(CustomerDtls::getLoanDtls).map(LoanDtls::getPurpose).map(Object::toString).orElse("");
		String productId = Optional.ofNullable(appDtls).map(ApplicationDtls::getCustomerDetails)
				.map(CustomerDtls::getLoanDtls).map(LoanDtls::getProductId).map(Object::toString).orElse("");
		Optional<ChargeAndBreakupDetails> chargeAndBreakupDetailsOpt = Optional.ofNullable(appDtls)
				.map(ApplicationDtls::getCustomerDetails).map(CustomerDtls::getLoanDtls)
				.map(LoanDtls::getChargeAndBreakupDtls);
		Optional<InsuranceDetails> insuranaceDtlOpt = Optional.ofNullable(appDtls)
				.map(ApplicationDtls::getCustomerDetails).map(CustomerDtls::getLoanDtls).map(LoanDtls::getInsurDtls);
		String branchId = appDtls.getBranchId();
		String customerId = appDtls.getCustomerId();
		String kendraId = appDtls.getKendraId();
		String kendraName = appDtls.getKendraName();
		String customerName = appDtls.getCustomerName();
		Object addInfo = appDtls.getAddInfo();
		String foir = "";
		String apr = "";
		String incomeStatus = "";
		if (addInfo instanceof Map) {
			@SuppressWarnings("unchecked")
			Map<String, Object> addInfoMap = (Map<String, Object>) addInfo;
			if (addInfoMap.containsKey("FOIR")) {
				foir = String.valueOf(addInfoMap.get("FOIR"));
			}
			if (addInfoMap.containsKey("APR")) {
				apr = String.valueOf(addInfoMap.get("APR"));
			}
			if (addInfoMap.containsKey("incomeStatus")) {
				incomeStatus = String.valueOf(addInfoMap.get("incomeStatus"));
			}
		}
		Optional<AuditTrailEntity> optionalAudit = auditTrailRepo.findTopByApplicationId(applicationId);
		logger.debug("Printing optionalAudit: {}", optionalAudit);
		if (optionalAudit.isPresent()) {
			AuditTrailEntity audit = optionalAudit.get();
			AuditTrailEntity auditTrailEntity = new AuditTrailEntity();
			auditTrailEntity.setUserId(userId);
			auditTrailEntity.setAppId(audit.getAppId());
			auditTrailEntity.setUserRole(userRole);
			if ("PENDINGBYRPC".equalsIgnoreCase(incomeStatus)) {
				auditTrailEntity.setStageid("2");
			} else {
				auditTrailEntity.setStageid(audit.getStageid());
			}
			Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
			String timestampString = currentTimestamp.toString();
			auditTrailEntity.setCreateTs(timestampString);
			auditTrailEntity.setCreateDate(LocalDate.now().toString());
			if (branchId != null) {
				auditTrailEntity.setBranchId(branchId);
			} else {
				auditTrailEntity.setBranchId(audit.getBranchId());
			}
			if (customerId != null) {
				auditTrailEntity.setCustomerId(customerId);
			} else {
				auditTrailEntity.setCustomerId(audit.getCustomerId());
			}
			if (kendraId != null) {
				auditTrailEntity.setKendraId(kendraId);
			} else {
				auditTrailEntity.setKendraId(audit.getKendraId());
			}
			if (kendraName != null) {
				auditTrailEntity.setKendraName(kendraName);
			} else {
				auditTrailEntity.setKendraName(audit.getKendraName());
			}
			if (customerName != null) {
				auditTrailEntity.setCustomerName(customerName);
			} else {
				auditTrailEntity.setCustomerName(audit.getCustomerName());
			}
			if (amount != null) {
				auditTrailEntity.setLoanAmount(amount);
			} else {
				auditTrailEntity.setLoanAmount(audit.getLoanAmount());
			}
			auditTrailEntity.setSpouse(audit.getSpouse());
			auditTrailEntity.setUserName(userName);
			auditTrailEntity.setAppVersion(appVersion);
			auditTrailEntity.setAddInfo1(audit.getAddInfo1());
			auditTrailEntity.setAddInfo2(remarks);
			auditTrailEntity.setAddInfo3(audit.getAddInfo3());
			auditTrailEntity.setAddInfo4(audit.getAddInfo4());
			auditTrailEntity.setPayload(audit.getPayload());
			if (repaymentFrequency != null) {
				auditTrailEntity.setRepaymentFrequency(repaymentFrequency);
			} else {
				auditTrailEntity.setRepaymentFrequency(audit.getRepaymentFrequency());
			}
			if (purpose != null) {
				auditTrailEntity.setPurpose(purpose);
			} else {
				auditTrailEntity.setPurpose(audit.getPurpose());
			}
			if (productId != null) {
				auditTrailEntity.setProductId(productId);
			} else {
				auditTrailEntity.setProductId(audit.getProductId());
			}
			auditTrailEntity.setApplicationId(applicationId);
			auditTrailEntity.setMobileNumber(audit.getMobileNumber());
			JSONObject updatedPayloadJson = new JSONObject(Optional.ofNullable(audit.getPayload()).orElse("{}"));
			logger.debug("updatedPayloadJson: {}", updatedPayloadJson);
			if (chargeAndBreakupDetailsOpt.isPresent()) {
				ChargeAndBreakupDetails details = chargeAndBreakupDetailsOpt.get();
				logger.debug("Printing details: {}", details);
				JSONObject chargeAndBreakupJson = new JSONObject();
				if (details.getLoanAmt() != null && !details.getLoanAmt().isEmpty())
					chargeAndBreakupJson.put("loanAmt", details.getLoanAmt());
				if (details.getAprxLoanCharges() != null && !details.getAprxLoanCharges().isEmpty())
					chargeAndBreakupJson.put("aprxLoanCharges", details.getAprxLoanCharges());
				if (details.getLoanProcessingFee() != null && !details.getLoanProcessingFee().isEmpty())
					chargeAndBreakupJson.put("loanProcessingFee", details.getLoanProcessingFee());
				if (details.getAprxLoanAmt() != null && !details.getAprxLoanAmt().isEmpty())
					chargeAndBreakupJson.put("aprxLoanAmt", details.getAprxLoanAmt());
				if (details.getAprxInstallmentAmt() != null && !details.getAprxInstallmentAmt().isEmpty())
					chargeAndBreakupJson.put("aprxInstallmentAmt", details.getAprxInstallmentAmt());
				if (details.getGST() != null && !details.getGST().isEmpty())
					chargeAndBreakupJson.put("GST", details.getGST());
				if (details.getAddInfo1() != null && !details.getAddInfo1().isEmpty())
					chargeAndBreakupJson.put("addInfo1", details.getAddInfo1());
				if (details.getAddInfo2() != null && !details.getAddInfo2().isEmpty())
					chargeAndBreakupJson.put("addInfo2", details.getAddInfo2());
				if (details.getIscbUpdated() != null && !details.getIscbUpdated().isEmpty())
					chargeAndBreakupJson.put("iscbUpdated", details.getIscbUpdated());
				if (details.getInterest_Fee() != null && !details.getInterest_Fee().isEmpty())
					chargeAndBreakupJson.put("interest_Fee", details.getInterest_Fee());
				if (details.getUpfront_Fee() != null && !details.getUpfront_Fee().isEmpty())
					chargeAndBreakupJson.put("upfront_Fee", details.getUpfront_Fee());
				updatedPayloadJson.put("chargeAndBreakupDetails", chargeAndBreakupJson);
			}
			if (insuranaceDtlOpt.isPresent()) {
				InsuranceDetails insuranceDetails = insuranaceDtlOpt.get();
				JSONObject insuranceJson = new JSONObject();
				if (insuranceDetails.getMember() != null && !insuranceDetails.getMember().isEmpty())
					insuranceJson.put("member", insuranceDetails.getMember());
				if (insuranceDetails.getSpouse() != null && !insuranceDetails.getSpouse().isEmpty())
					insuranceJson.put("Spouse", insuranceDetails.getSpouse());
				if (insuranceDetails.getApplicant_insurance_amt() != null
						&& !insuranceDetails.getApplicant_insurance_amt().isEmpty())
					insuranceJson.put("applicant_insurance_amt", insuranceDetails.getApplicant_insurance_amt());
				if (insuranceDetails.getSpouse_insurance_amt() != null
						&& !insuranceDetails.getSpouse_insurance_amt().isEmpty())
					insuranceJson.put("spouse_insurance_amt", insuranceDetails.getSpouse_insurance_amt());
				if (insuranceDetails.getInsuranceProvider() != null
						&& !insuranceDetails.getInsuranceProvider().isEmpty())
					insuranceJson.put("insuranceProvider", insuranceDetails.getInsuranceProvider());
				if (insuranceDetails.getInsurCharges() != null && !insuranceDetails.getInsurCharges().isEmpty())
					insuranceJson.put("insurCharges", insuranceDetails.getInsurCharges());
				updatedPayloadJson.put("insuranceDetails", insuranceJson);
			}
			if (repaymentFrequency != null && !repaymentFrequency.isEmpty()) {
				updatedPayloadJson.put("repaymentFrequency", repaymentFrequency);
			}
			if (customerId != null && !customerId.isEmpty()) {
				updatedPayloadJson.put("customerId", customerId);
			}
			if (productId != null && !productId.isEmpty()) {
				updatedPayloadJson.put("productId", productId);
			}
			if (branchId != null && !branchId.isEmpty()) {
				updatedPayloadJson.put("branchId", branchId);
			}
			if (kendraName != null && !kendraName.isEmpty()) {
				updatedPayloadJson.put("kendraName", kendraName);
			}
			if (foir != null && !foir.isEmpty()) {
				updatedPayloadJson.put("FOIR", foir);
			}
			if (apr != null && !apr.isEmpty()) {
				updatedPayloadJson.put("APR", apr);
			}
			logger.debug("updatedPayloadJsontoString(): {}", updatedPayloadJson.toString());
			auditTrailEntity.setPayload(updatedPayloadJson.toString());
			logger.debug("Updated/Edited Existing ApplicationID Audit Entries: {}", audit);
			auditTrailRepo.save(auditTrailEntity);
			logger.debug("AuditTrailEntity updated successfully for applicationId: {}", applicationId);
		} else {
			logger.debug("No existing audit trail found for applicationId: {}", applicationId);
		}
		Optional<MisReport> optionalExisting = misReportRepository.findByApplicationId(applicationId);
		logger.debug("OptionalExisting for updated before save: {}", optionalExisting);
		if (optionalExisting.isPresent()) {
			MisReport existingReport = optionalExisting.get();
			Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
			String timestampString = currentTimestamp.toString();
			existingReport.setUpdateDate(timestampString);
			logger.debug("Printing incomeStatus: {}", incomeStatus);
			if ("PENDINGBYRPC".equalsIgnoreCase(incomeStatus)) {
				existingReport.setStageID("2");
				existingReport.setApplicationStatus("RPC VERIFICATION PENDING");
			} else {
				existingReport.setStageID(existingReport.getStageID());
			}
			logger.debug("Printing Updated/Edited existingReport: {}", existingReport);
			misReportRepository.save(existingReport);
		}
	}

	@Transactional
	public Map<String, Object> saveAuditTrialDetailsForCBCheck(CbRequest apiRequest, Integer stage_id) {
		logger.debug("Printing SaveAuditTrialDetailsForCBCheck with apiRequest: {}, stage_id: {}", apiRequest,
				stage_id);
		Map<String, Object> response = new HashMap<>();
		String crtFlag = apiRequest.getRequestObj().getCrtFlag();
		String cbRecheck = apiRequest.getRequestObj().getCbRecheck();
		String schedulerEnabled = apiRequest.getRequestObj().getSchedulerEnabled();
		if ("1".equalsIgnoreCase(crtFlag)) {
			logger.debug("CRT flag is '1', skipping audit trail and MIS report save.");
			// response.put("status", "skipped");
			// response.put("message", "CRT flag is 1, skipping DB save.");
			return response;
		} else {
			try {
				AuditTrailEntity existingAuditTrail = auditTrailRepo
						.findApplicationId(apiRequest.getRequestObj().getApplicationId());
				logger.debug("AuditTrailEntity already exists when the application is created :: {}",
						existingAuditTrail);
				AuditTrailEntity newAuditTrail = new AuditTrailEntity();
				DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
				String timestampString = currentTimestamp.toString();
				if (existingAuditTrail != null) {
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
					
					if ("Y".equalsIgnoreCase(schedulerEnabled)) {
						if ("Y".equalsIgnoreCase(cbRecheck)) {
							newAuditTrail.setUserId("SCHEDULER");
							newAuditTrail.setUserRole("SCHEDULER");
							newAuditTrail.setAppVersion("NA");
							newAuditTrail.setUserName("SCHEDULER");
							if ("4".equalsIgnoreCase(stage_id.toString())) {
								newAuditTrail.setStageid("18");
								newAuditTrail.setAddInfo2("The Loan Reject By Schedular ~CB queue retry cases.");
								newAuditTrail.setUserName("SCHEDULER");
							} else {
								newAuditTrail.setStageid(existingAuditTrail.getStageid());
								newAuditTrail.setAddInfo2("The Loan CB rechecked by SCHEDULER ~CB queue retry cases.");
								newAuditTrail.setUserName("SCHEDULER");
							}
						} else {
							newAuditTrail.setUserId("SCHEDULER");
							newAuditTrail.setUserRole("SCHEDULER");
							newAuditTrail.setAppVersion("NA");
							newAuditTrail.setAddInfo2("The Loan CB rechecked by SCHEDULER ~CB queue retry cases.");
							newAuditTrail.setUserName("SCHEDULER");
							newAuditTrail.setStageid(stage_id.toString());
						}
					} else {
						if ("Y".equalsIgnoreCase(cbRecheck)) {
							if (apiRequest.getRequestObj().getRemarks() == null
									|| apiRequest.getRequestObj().getRemarks().isEmpty()) {
								newAuditTrail.setAddInfo2("The Loan CB recheck with OTP");
								newAuditTrail.setAppVersion(existingAuditTrail.getAppVersion());
								newAuditTrail.setUserName(existingAuditTrail.getUserName());
							} else {
								newAuditTrail.setAddInfo2(apiRequest.getRequestObj().getRemarks());
								newAuditTrail.setAppVersion(apiRequest.getRequestObj().getAppVersion());
								newAuditTrail.setUserName(apiRequest.getRequestObj().getUserName());
							}
							if ("5".equalsIgnoreCase(stage_id.toString())) {
								newAuditTrail.setStageid(existingAuditTrail.getStageid());
							} else {
								newAuditTrail.setStageid(stage_id.toString());
							}
							newAuditTrail.setUserId(apiRequest.getRequestObj().getUserId());
							newAuditTrail.setUserRole(apiRequest.getRequestObj().getUserRole());
						} else {
							if (apiRequest.getRequestObj().getRemarks() == null
									|| apiRequest.getRequestObj().getRemarks().isEmpty()) {
								newAuditTrail.setAddInfo2("The Loan CB check with OTP");
								newAuditTrail.setAppVersion(existingAuditTrail.getAppVersion());
								newAuditTrail.setUserName(existingAuditTrail.getUserName());
							} else {
								newAuditTrail.setAddInfo2(apiRequest.getRequestObj().getRemarks());
								newAuditTrail.setAppVersion(apiRequest.getRequestObj().getAppVersion());
								newAuditTrail.setUserName(apiRequest.getRequestObj().getUserName());
							}
							newAuditTrail.setStageid(stage_id.toString());
							newAuditTrail.setUserId(existingAuditTrail.getUserId());
							newAuditTrail.setUserRole(existingAuditTrail.getUserRole());
						}
					}
					logger.debug("CB Check AuditTrailEntity before save: {}", newAuditTrail);
					auditTrailRepo.save(newAuditTrail);
				}
				Optional<MisReport> optionalExisting = misReportRepository
						.findByApplicationId(apiRequest.getRequestObj().getApplicationId());
				logger.debug("Existing MisReport CB Check before save: {}", optionalExisting);
				if (optionalExisting.isPresent()) {
					MisReport existingReport = optionalExisting.get();
					existingReport.setUpdateDate(timestampString);
					existingReport.setModifyBy(apiRequest.getUserId());
					existingReport.setRemarks(newAuditTrail.getAddInfo2());
					String stageIdStr = stage_id.toString();
					String stageStatus = "";
					switch (stageIdStr) {
					case "3":
						stageStatus = "CB PENDING";
						break;
					case "4":
						stageStatus = "CRT PENDING";
						break;
					case "5":
						stageStatus = "CB PASS";
						break;
					default:
						logger.debug("Unhandled stage_id: " + stageIdStr);
						break;
					}
					if ("Y".equalsIgnoreCase(schedulerEnabled)) {
						if ("Y".equalsIgnoreCase(cbRecheck)) {
							if ("4".equalsIgnoreCase(stage_id.toString())) {
								existingReport.setStageID("18");
								existingReport.setApplicationStatus("CANCELLED BY SCHEDULER");
								existingReport.setRemarks("The Loan Reject By Schedular ~CB queue retry cases.");
							} else {
								existingReport.setRemarks("The Loan CB rechecked by SCHEDULER ~CB queue retry cases.");
							}
						} else {
							existingReport.setStageID(stageIdStr);
							existingReport.setApplicationStatus(stageStatus);
						}
					} else {
						if ("Y".equalsIgnoreCase(cbRecheck)) {
							if ("5".equalsIgnoreCase(stage_id.toString())) {
								existingReport.setStageID(existingReport.getStageID());
								existingReport.setApplicationStatus(existingReport.getApplicationStatus());
								existingReport.setRemarks("The Loan CB rechecked ~Edited cases.");
							} else {
								existingReport.setApplicationStatus(stageStatus);
								existingReport.setStageID(stageIdStr);
							}
						} else {
							existingReport.setApplicationStatus(stageStatus);
							existingReport.setStageID(stageIdStr);
						}
					}
					logger.debug("Saved updated/Edited MIS Report for stages 3/4/5: {}", existingReport);
					misReportRepository.save(existingReport);
				}
				response.put("status", "success");
				response.put("message", "Audit trail and MIS report updated successfully.");
			} catch (Exception e) {
				logger.error("An error occurred while saving the audit trail data: {}", e.getMessage(), e);
				response.put("status", "error");
				response.put("message", "An error occurred while saving the audit trail.");
				response.put("errorDetails", e.getMessage());
			}
		}
		return response;
	}

	@Transactional
	public Map<String, Object> saveAuditTrialDetailsForKMActionPostCBCheck(PopulateapplnWFRequestFields requestObj,
			Integer stage_id, String currentRole) {
		logger.debug(
				"Printing saveAuditTrialDetailsForKMActionPostCBCheck with requestObj: {}, stage_id: {}, currentRole: {}",
				requestObj, stage_id, currentRole);
		Map<String, Object> response = new HashMap<>();
		Timestamp currentTimestamp = new Timestamp(System.currentTimeMillis());
		String timestampString = currentTimestamp.toString();
		try {
			AuditTrailEntity existingAuditTrail = auditTrailRepo.findApplicationId(requestObj.getApplicationId());
			logger.debug("Existing AuditTrailEntity When PostCBCheck : {}", existingAuditTrail);
			ApplicationMaster appMasterDtls = applicationMasterRepo.findApplicationID(requestObj.getApplicationId());
			logger.debug("Printing appMasterDtls: {}", appMasterDtls);
			BigDecimal Updatedamount = appMasterDtls.getAmount();
			double foirValue = 0;
			double aprValue = 0;
			if (appMasterDtls != null) {
				String addInfo = appMasterDtls.getAddInfo();
				logger.debug("Printing addInfo: {}", addInfo);
				if (addInfo != null && !addInfo.isEmpty()) {
					JSONObject json = new JSONObject(addInfo);
					logger.debug("Printing json: {}", json);
					if (json.optBoolean("isApprovedCRT", false)) {
						String foir = json.optString("FOIR", null);
						String apr = json.optString("APR", null);
						if (foir != null && apr != null) {
							try {
								foirValue = Double.parseDouble(foir);
								aprValue = Double.parseDouble(apr);
								String existingPayload = existingAuditTrail.getPayload();
								JSONObject payloadJson = new JSONObject(existingPayload);
								payloadJson.put("APR", String.valueOf(aprValue));
								payloadJson.put("FOIR", String.valueOf(foirValue));
								String updatedPayload = payloadJson.toString();
								logger.debug("Printing updatedPayload: {}", updatedPayload);
								existingAuditTrail.setPayload(updatedPayload);
								existingAuditTrail.setLoanAmount(Updatedamount.toEngineeringString());
							} catch (NumberFormatException e) {
								logger.debug("Unable to parse FOIR or APR from addInfo JSON. FOIR: {}, APR: {}", foir,
										apr, e);
							}
						}
					}
				}
			}
			AuditTrailEntity newAuditTrail = new AuditTrailEntity();
			DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			if (existingAuditTrail != null) {
				newAuditTrail.setAppId(existingAuditTrail.getAppId());
				newAuditTrail.setApplicationId(existingAuditTrail.getApplicationId());
				newAuditTrail.setUserId(requestObj.getCreatedBy());
				newAuditTrail.setUserRole(currentRole);
				newAuditTrail.setLoanAmount(existingAuditTrail.getLoanAmount());
				newAuditTrail.setMobileNumber(existingAuditTrail.getMobileNumber());
				newAuditTrail.setPurpose(existingAuditTrail.getPurpose());
				newAuditTrail.setBranchId(existingAuditTrail.getBranchId());
				newAuditTrail.setProductId(existingAuditTrail.getProductId());
				newAuditTrail.setCustomerId(existingAuditTrail.getCustomerId());
				newAuditTrail.setKendraName(existingAuditTrail.getKendraName());
				newAuditTrail.setUserName(requestObj.getUserName());
				newAuditTrail.setSpouse(existingAuditTrail.getSpouse());
				newAuditTrail.setCustomerName(existingAuditTrail.getCustomerName());
				newAuditTrail.setPayload(existingAuditTrail.getPayload());
				newAuditTrail.setRepaymentFrequency(existingAuditTrail.getRepaymentFrequency());
				newAuditTrail.setAddInfo1(existingAuditTrail.getAddInfo1());
				newAuditTrail.setAddInfo2(requestObj.getRemarks());
				newAuditTrail.setAddInfo3(existingAuditTrail.getAddInfo3());
				newAuditTrail.setAddInfo4(existingAuditTrail.getAddInfo4());
				newAuditTrail.setCreateTs(timestampString);
				newAuditTrail.setCreateDate(LocalDate.now().format(dateFormatter));
				newAuditTrail.setKendraId(existingAuditTrail.getKendraId());
				newAuditTrail.setStageid(stage_id.toString());
				newAuditTrail.setAppVersion(requestObj.getAppVersion());
				if (currentRole != null && "SCHEDULER".equalsIgnoreCase(currentRole.trim())) {
					newAuditTrail.setUserName(currentRole);
					newAuditTrail.setUserId(currentRole);
					newAuditTrail.setAddInfo2("The Loan Submitted by Scheduler");
				}
				logger.debug("Workflow Saved AuditTrailEntity before save: {}", newAuditTrail);
				auditTrailRepo.save(newAuditTrail);
			}
			Optional<MisReport> optionalExisting = misReportRepository
					.findByApplicationId(requestObj.getApplicationId());
			logger.debug("The MIS report has been successfully fatched following KMActionPostCBCheck {}",
					optionalExisting);
			if (optionalExisting.isPresent()) {
				MisReport existingReport = optionalExisting.get();
				existingReport.setUpdateDate(timestampString);
				existingReport.setModifyBy(requestObj.getCreatedBy());
				existingReport.setRemarks(newAuditTrail.getAddInfo2());
				switch (stage_id.toString()) {
				case "6":
					existingReport.setApplicationStatus("REJECTED DURING LOAN INPUT");
					break;
				case "7":
					existingReport.setApplicationStatus("CRT REJECTION");
					break;
				case "8":
					existingReport.setApplicationStatus("PENDING SANCTION");
					break;
				case "9":
					existingReport.setApplicationStatus("CRT APPROVED ~ PENDING SANCTION");
					break;
				case "10":
					existingReport.setApplicationStatus("SANCTION REJECTED");
					break;
				case "11":
					existingReport.setApplicationStatus("RECOMMENDED FOR SANCTION");
					break;
				case "12":
					existingReport.setApplicationStatus("RECOMMENDATION REJECTED");
					break;
				case "13":
					existingReport.setApplicationStatus("PENDING DISBURSEMENT");
					break;
				case "14":
					existingReport.setApplicationStatus("DISBURSEMENT REJECTED");
					break;
				case "15":
					existingReport.setApplicationStatus("PENDING FOR DISBURSEMENT APPROVAL");
					break;
				case "16":
					existingReport.setApplicationStatus("DISBURSEMENT APPROVAL REJECTED");
					break;
				case "17":
					existingReport.setApplicationStatus("DISBURSED");
					break;
				case "19":
					existingReport.setApplicationStatus("REJECTED (DRAFT)");
					break;
				default:
					logger.debug("Unhandled stage_id: {}", stage_id);
				}
				existingReport.setStageID(stage_id.toString());
				if (currentRole != null && "SCHEDULER".equalsIgnoreCase(currentRole.trim())) {
					existingReport.setModifyBy(currentRole);
					existingReport.setRemarks("The Loan Submitted by Scheduler");
				}
				logger.debug("Data saved successfully after generating the KMActionCBCheck MIS report {}",
						existingReport);
				misReportRepository.save(existingReport);
			}
			response.put("status", "success");
			response.put("message", "Audit trail and MIS report updated successfully.");
		} catch (Exception e) {
			logger.error("Error while saving audit trail or MIS report: {}", e.getMessage(), e);
			response.put("status", "error");
			response.put("message", "An error occurred while saving the audit trail.");
			response.put("errorDetails", e.getMessage());
		}
		return response;
	}

	@Transactional
	public Response FetchStateData(String code) {
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();
		JSONObject responeObject = new JSONObject();
		try {
			if (code == null || code.isEmpty()) {
				logger.debug("Entered FetchStateData with code empty: ", code);
				List<String> stateList = fetchStateRepository.findDistinctKeyvalues();
				responeObject.put("States List", stateList);
				respBody.setResponseObj(responeObject.toString());
			} else {
				logger.debug("Entered FetchStateData with code value: ", code);
				List<String> stateRecords = fetchStateRepository.findDescriptionsByKeyvalue(code);
				if (stateRecords != null) {
					responeObject.put("District List", stateRecords);
					respBody.setResponseObj(responeObject.toString());
					CommonUtils.generateHeaderForSuccess(respHeader);
				} else {
					logger.debug("No District found for the given keyvalue: ", code);
					responeObject.put("message", "No District found for the given keyvalue: " + code);
					CommonUtils.generateHeaderForFailure(respHeader, "DISTRICT_NOT_FOUND");
				}
			}
			respBody.setResponseObj(responeObject.toString());
			CommonUtils.generateHeaderForSuccess(respHeader);
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
			return response;
		} catch (Exception e) {
			logger.error("Exception occurred while fetching state data: ", e);
			respBody.setResponseObj("An error occurred while fetching state data.");
			CommonUtils.generateHeaderForFailure(respHeader, "EXCEPTION_OCCURRED");
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
			return response;
		}
	}

	@Transactional
	public Map<String, Object> updateCRTFlow(UpdateCRTFlowRequest apiRequest) {
		Map<String, Object> response = new HashMap<>();
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			ApplicationMaster applicationMaster = applicationMasterRepo
					.findApplicationIdForCRTFlow(apiRequest.getRequestObj().getApplicationId());
			if (applicationMaster != null) {
				String applicationId = applicationMaster.getApplicationId();
				String updatedApplicationId = applicationId + "_R";
				applnWfRepository.updateApplicationId(apiRequest.getRequestObj().getApplicationId(),
						updatedApplicationId);
				if (applicationMaster != null) {
					String addInfo = applicationMaster.getAddInfo();
					Map<String, Object> addInfoMap = new HashMap<>();
					if (addInfo != null && !addInfo.isEmpty()) {
						addInfoMap = objectMapper.readValue(addInfo, new TypeReference<Map<String, Object>>() {
						});
					}
					addInfoMap.put("iseditCRT", true);
					String updatedAddInfo = objectMapper.writeValueAsString(addInfoMap);
					applicationMaster.setAddInfo(updatedAddInfo);
					applicationMasterRepo.updateApplicationStatus("INITIATE", applicationId);
					applicationMasterRepo.save(applicationMaster);
					response.put("status", "success");
					response.put("message", "Application ID updated successfully.");
				}
			} else {
				response.put("status", "failed");
				response.put("message", "Application ID not found.");
			}
		} catch (Exception e) {
			logger.error("Exception occurred in updateCRTFlow: {}", e.getMessage(), e);
			response.put("status", "error");
			response.put("message", "An exception occurred while updating the workflow.");
			response.put("errorDetails", e.getMessage());
		}
		return response;
	}

	@Transactional
	public Mono<byte[]> generatePassbook(PassbookRequest apiRequest, Header header) {
		logger.debug("Passbook apiRequest:" + apiRequest);
		logger.debug("Passbook header:" + header);
		String fetchLoanIdfromAuditable = "";
		try {
			header.setInterfaceId(GENERATEPASSBOOK);
			String loanId = apiRequest.getRequestObj().getLoanId();
			logger.debug("Passbook loanId:" + loanId);
			String applicationId = apiRequest.getRequestObj().getApplicationId();
			logger.debug("Passbook applicationId:" + applicationId);
			// If loanId is missing or blank, try to fetch it from audit logs
			if ((loanId == null || loanId.isBlank()) && (applicationId == null || applicationId.isBlank())) {
				logger.error("Both loanId and applicationId are missing. Cannot proceed with passbook generation.");
				logger.debug("Both loanId and applicationId are missing. Cannot proceed with passbook generation.");
			}
			if (loanId == null || loanId.isBlank()) {
				if (applicationId != null && !applicationId.isBlank()) {
					TbUaobAuditLogs auditLog = tbUaobApiAuditLogsRepository.findApplicationId(applicationId);
					logger.debug("PassBook auditLog:" + auditLog);
					if (auditLog != null && auditLog.getResponsePayload() != null) {
						try {
							ObjectMapper objectMapper = new ObjectMapper();
							JsonNode jsonNode = objectMapper.readTree(auditLog.getResponsePayload());
							logger.debug("passbook jsonNode:" + jsonNode);
							JsonNode headerNode = jsonNode.get("header");
							logger.debug("passbook headerNode:" + headerNode);
							if (headerNode != null && !headerNode.isNull()) {
								JsonNode idNode = headerNode.get("id");
								logger.debug("passbook idNode:" + idNode);
								if (idNode != null && !idNode.isNull()) {
									fetchLoanIdfromAuditable = idNode.asText();
								} else {
									logger.warn("ID node not found in header");
								}
							} else {
								logger.warn("Header node not found in response JSON");
							}
						} catch (Exception e) {
							logger.error("Error parsing loanID from audit log", e);
						}
					}
				}
				logger.debug("passbook fetchLoanIdfromAuditable from root header id: {}", fetchLoanIdfromAuditable);
			} else {
				fetchLoanIdfromAuditable = loanId;
				logger.debug("passbook loanId from root header id: {}", loanId);
			}
			PassbookExternalRequestFields passbookExternalRequestFields = new PassbookExternalRequestFields();
			passbookExternalRequestFields.setPassword("Digi@2019");
			passbookExternalRequestFields.setUserName("MOBAPI");
			passbookExternalRequestFields.setColumnName("Y.LOAN.ID");
			passbookExternalRequestFields.setCompany("");
			passbookExternalRequestFields.setOperand("EQ");
			passbookExternalRequestFields.setCriteriaValue(fetchLoanIdfromAuditable);
			logger.debug("passbookRequestFields:" + passbookExternalRequestFields);
			String loanAccNumber = passbookExternalRequestFields.getCriteriaValue();
			Mono<Object> passbookResponse = interfaceAdapter.callExternalService(header, passbookExternalRequestFields,
					GENERATEPASSBOOK, true);
			passbookResponse.doOnNext(value -> logger.debug("passbookResponse: {}", value));
			return passbookResponse.flatMap(res -> {
				logger.debug("passbookResponse: {}", res);
				Mono<ResponseWrapper> monoResWrapper = adapterUtil.generateRespWrapper(Mono.just(res), GENERATEPASSBOOK,
						header, true);
				logger.debug("monoResponseWrapper is :" + monoResWrapper);
				return monoResWrapper.flatMap(val -> {
					logger.debug("LOAN ID is :" + loanAccNumber);
					logger.debug("passbookResponse: {}", val);
					try {
						int scheduleCount = 0;
						List<SanctionRepaymentSchedule> schedules = new ArrayList<>();
						JSONObject responseJsonObj = new JSONObject(new ObjectMapper().writeValueAsString(val));
						logger.debug("responseJsonObj: {}", responseJsonObj);
						if (responseJsonObj.has("apiResponse")) {
							JSONObject apiResponse = responseJsonObj.getJSONObject("apiResponse");
							if (apiResponse.has("ResponseBody")) {
								JSONObject responseBody = apiResponse.getJSONObject("ResponseBody");
								if (responseBody.has("responseObj")) {
									String responseObjString = responseBody.getString("responseObj");
									JSONObject responseObj = new JSONObject(responseObjString);
									if (responseObj.has("LoanScheduleResponse")) {
										JSONObject loanScheduleResponse = responseObj
												.getJSONObject("LoanScheduleResponse");

										if (loanScheduleResponse.has("Status")
												&& loanScheduleResponse.getJSONObject("Status")
														.getString("successIndicator").equals("Success")) {

											if (loanScheduleResponse.has("GKLNSCHEDENQMFILMMFLType")) {
												JSONObject scheduleType = loanScheduleResponse
														.getJSONObject("GKLNSCHEDENQMFILMMFLType");
												logger.debug("scheduleType: {}", scheduleType);
												if (scheduleType.has("gGKLNSCHEDENQMFILMMFLDetailType")) {
													JSONObject detailType = scheduleType
															.getJSONObject("gGKLNSCHEDENQMFILMMFLDetailType");
													logger.debug("detailType: {}", detailType);

													if (detailType.has("mGKLNSCHEDENQMFILMMFLDetailType")) {
														Object rawDetail = detailType
																.get("mGKLNSCHEDENQMFILMMFLDetailType");
														if (rawDetail instanceof JSONArray) {
															JSONArray respBody = (JSONArray) rawDetail;
															if (respBody.length() > 0) {
																JSONObject firstObj = respBody.getJSONObject(0);
																if ("Schedule not present"
																		.equalsIgnoreCase(firstObj.optString("SNO"))) {
																	logger.warn(
																			"Schedule not present. Skipping report generation.");
																	return Mono.error(new RuntimeException(
																			"Schedule not present for loan ID: "
																					+ loanAccNumber));
																}

																logger.debug("respBody: {}", respBody);
																sanctionRepaymentScheduleRepository
																		.deleteByApplicationId(loanAccNumber);

																DateTimeFormatter inputFormatter = DateTimeFormatter
																		.ofPattern("yyyyMMdd");
																DateTimeFormatter outputFormatter = DateTimeFormatter
																		.ofPattern("dd MMM yyyy", Locale.ENGLISH);
																String uniqueId = new CommonUtilsCBS()
																		.generateReferenceNumber(5);

																for (int i = 0; i < respBody.length(); i++) {
																	JSONObject obj = respBody.getJSONObject(i);
																	SanctionRepaymentSchedule schedule = new SanctionRepaymentSchedule();
																	schedule.setParameter(obj.optString("SNO", ""));
																	schedule.setOutstandingPrincipal(
																			"Rs " + obj.optString("PRINCIPALOS", "0"));
																	schedule.setPrincipal(
																			"Rs " + obj.optString("PRINCIPAL", "0"));
																	schedule.setInterest(
																			"Rs " + obj.optString("INTEREST", "0"));
																	schedule.setInstallment(
																			"Rs " + obj.optString("TOTAL", "0"));
																	schedule.setSanctRepoId(loanAccNumber + uniqueId);
																	schedule.setApplicationId(loanAccNumber);
																	schedule.setTypeOfStage("generatePassBook");

																	String dateStr = obj.optString("DATE", "");
																	if (!dateStr.isBlank()) {
																		try {
																			LocalDate date = LocalDate.parse(dateStr,
																					inputFormatter);
																			String formattedDate = date
																					.format(outputFormatter);
																			schedule.setCreateTs(formattedDate);
																		} catch (DateTimeParseException e) {
																			logger.warn("Invalid date format: {}",
																					dateStr);
																			schedule.setCreateTs(dateStr);
																		}
																	}
																	logger.debug("schedule: {}", schedule);
																	schedules.add(schedule);
																	scheduleCount = i;
																}

																if (!schedules.isEmpty()) {
																	List<SanctionRepaymentSchedule> savedSchedules = StreamSupport
																			.stream(sanctionRepaymentScheduleRepository
																					.saveAll(schedules).spliterator(),
																					false)
																			.collect(Collectors.toList());
																	savedSchedules.forEach(
																			s -> logger.debug("Saved Schedule: {}", s));
																}
															} else {
																logger.warn("Schedule array is empty for loan ID: {}",
																		loanAccNumber);
																return Mono.error(new RuntimeException(
																		"Schedule data missing for loan ID: "
																				+ loanAccNumber));
															}
														} else {
															logger.warn(
																	"Schedule detail type is not an array (unexpected structure). Value: {}",
																	rawDetail);
															return Mono.error(new RuntimeException(
																	"Invalid schedule format for loan ID: "
																			+ loanAccNumber));
														}
													}
												}
											}
										} else {
											logger.warn("Loan schedule response indicates failure: {}",
													loanScheduleResponse);
										}
									} else {
										logger.warn("LoanScheduleResponse not found in the response JSON.");
									}
								} else {
									logger.warn("responseObj not found in the ResponseBody.");
								}
							} else {
								logger.warn("ResponseBody not found in the apiResponse.");
							}
						} else {
							logger.warn("apiResponse not found in the response JSON.");
						}
						logger.warn("Sanction report downloaded successfully for applicationId: {}", applicationId);

						String memberId = apiRequest.getRequestObj().getMemberId();
						String memberName = apiRequest.getRequestObj().getMemberName();
						String spouseName = apiRequest.getRequestObj().getSpouseName();
						String kendraName = apiRequest.getRequestObj().getKendraName();
						String loanAmount = apiRequest.getRequestObj().getLoanAmt();
						String productName = apiRequest.getRequestObj().getProductName();
						String moratorium = apiRequest.getRequestObj().getMoratorium();
						String roi = apiRequest.getRequestObj().getRoi();
						String loanPurpose = apiRequest.getRequestObj().getLoanPurpose();
						String disbursementDate = apiRequest.getRequestObj().getDisbursementDate();
						String termofLoan = apiRequest.getRequestObj().getTermOfLoan();
						String processingFee = apiRequest.getRequestObj().getProcessingFee();
						String insuranceforMemeber = apiRequest.getRequestObj().getInsurancePremiumMember();
						String insuranceSpouse = apiRequest.getRequestObj().getInsurancePremiumSpouse();
						String phone = apiRequest.getRequestObj().getPhone();
						String type = apiRequest.getRequestObj().getType();

						logger.debug(" going to generate report for loan id : " + loanAccNumber);

						byte[] fileBytes = reportBuildService.generateAndDownloadDPassBookReport(memberId, memberName,
								spouseName, kendraName, loanAmount, productName, moratorium, roi, loanPurpose,
								disbursementDate, termofLoan, processingFee, insuranceforMemeber, insuranceSpouse,
								phone, loanAccNumber, type, scheduleCount);
						logger.debug("fileBytes:  " + fileBytes);
						logger.debug("Going to delete again from the schdule table ");
						sanctionRepaymentScheduleRepository.deleteByApplicationId(loanAccNumber);
						return Mono.just(fileBytes);

					} catch (Exception e) {
						logger.error("Exception during Passbook generation JSON processing", e);
						return Mono.error(e);
					}
				});
			});
		} catch (Exception e) {
			logger.error("Exception at generatePassbook service: ", e);
			return Mono.error(e);
		}
	}

	@Transactional
	public Response fetchLoanList(FetchLoanRequest fetchLoanRequest) {
		logger.debug("fetchLoanRequest:  " + fetchLoanRequest);
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();
		JSONObject responseObject = new JSONObject();
		String userId = fetchLoanRequest.getRequestObj().getUserId();
		String userRole = fetchLoanRequest.getRequestObj().getUserRole();
		String branchId = fetchLoanRequest.getRequestObj().getBranchId();
		try {
			String paramUserId = null;
			String paramBranchId = null;
			if ("KM".equalsIgnoreCase(userRole)) {
				paramUserId = userId;
			} else {
				paramBranchId = branchId;
			}
			List<Map<String, Object>> loanDetails = applicationMasterRepo.findLoanList(paramUserId, paramBranchId);
			JSONArray loanArray = new JSONArray(loanDetails);
			logger.debug("loanArray:  " + loanArray);
			responseObject.put("loanList", loanArray);
			respBody.setResponseObj(responseObject.toString());
			logger.debug("respBody:  " + respBody);
			CommonUtils.generateHeaderForSuccess(respHeader);
		} catch (Exception e) {
			logger.error("Exception occurred while fetching loan list: ", e);
			respBody.setResponseObj("An error occurred while fetching loan list.");
			CommonUtils.generateHeaderForFailure(respHeader, "EXCEPTION_OCCURRED");
		}
		response.setResponseBody(respBody);
		response.setResponseHeader(respHeader);
		return response;
	}

	@Transactional
	public Response fetchTarget(FetchLoanTargetRequest apiRequest) {
		Response response = new Response();
		ResponseHeader responseHeader = new ResponseHeader();
		ResponseBody responseBody = new ResponseBody();
		JSONObject jsonObject = new JSONObject();
		String branchId = apiRequest.getRequestObj().getBranchId();
		String userId = apiRequest.getRequestObj().getUserId();
		try {
			List<BusinessTarget> businessTarget = businessTargetRepository.fetchTargetDetails(branchId, userId);
			logger.debug("businessTarget:  " + businessTarget);
			if (businessTarget != null && !businessTarget.isEmpty()) {
				JSONArray loanArray = new JSONArray(businessTarget);
				logger.debug("loanArray: {}", loanArray);
				jsonObject.put("FetchTarget", loanArray);
				responseBody.setResponseObj(jsonObject.toString());
				CommonUtils.generateHeaderForSuccess(responseHeader);
			} else {
				responseBody.setResponseObj("No target found for given branch and user.");
				CommonUtils.generateHeaderForFailure(responseHeader, "NOT_FOUND");
			}
		} catch (Exception e) {
			logger.error("Exception occurred while fetching target list: ", e);
			responseBody.setResponseObj("An error occurred while fetching target list.");
			CommonUtils.generateHeaderForFailure(responseHeader, "EXCEPTION_OCCURRED");
		}
		response.setResponseBody(responseBody);
		response.setResponseHeader(responseHeader);
		return response;
	}

	
	@Transactional
	public Response fetchEmiProductList(FetchEmiProductRequest apiRequest) {
		logger.debug("FetchEmiProductRequest:  " + apiRequest);
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();
		JSONObject responseObject = new JSONObject();
		try {
			List<EmiProductList> emiProductList = emiProductListRepository.findAll();
			if (emiProductList != null && !emiProductList.isEmpty()) {
				JSONArray jsonArray = new JSONArray(emiProductList);
				responseObject.put("EmiProductList", jsonArray);
				respBody.setResponseObj(responseObject.toString());
				CommonUtils.generateHeaderForSuccess(respHeader);
			} else {
				respBody.setResponseObj("Fetch Emi product List not found.");
				CommonUtils.generateHeaderForFailure(respHeader, "NOT_FOUND");
			}
		} catch (Exception e) {
			logger.error("Exception occurred while fetching Emi Product list: ", e);
			respBody.setResponseObj("An error occurred while fetching Emi Product list.");
			CommonUtils.generateHeaderForFailure(respHeader, "EXCEPTION_OCCURRED");
		}
		response.setResponseBody(respBody);
		response.setResponseHeader(respHeader);
		return response;
	}
}
