package com.iexceed.appzillonbanking.cagl.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import com.iexceed.appzillonbanking.cagl.dto.*;
import com.iexceed.appzillonbanking.cagl.repository.cus.DigitalCollectionRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.client.RestTemplate;
import com.iexceed.appzillonbanking.cagl.constants.CommonConstants;
import com.iexceed.appzillonbanking.cagl.domain.cus.GkKendraAssignment;
import com.iexceed.appzillonbanking.cagl.domain.cus.GkKendraUserId;
import com.iexceed.appzillonbanking.cagl.domain.cus.GkLoanData;
import com.iexceed.appzillonbanking.cagl.domain.cus.GkMLoanProduct;
import com.iexceed.appzillonbanking.cagl.entity.GkCustomerData;
import com.iexceed.appzillonbanking.cagl.entity.GkKendraData;
import com.iexceed.appzillonbanking.cagl.entity.GkUserData;
import com.iexceed.appzillonbanking.cagl.payload.AsmiUserData;
import com.iexceed.appzillonbanking.cagl.payload.AsmiUserResponse;
import com.iexceed.appzillonbanking.cagl.payload.KendraIdsLatLongProjection;
import com.iexceed.appzillonbanking.cagl.payload.KendraRequestField;
import com.iexceed.appzillonbanking.cagl.repository.cus.CustomerDataRepository;
import com.iexceed.appzillonbanking.cagl.repository.cus.GkLoanDataRepo;
import com.iexceed.appzillonbanking.cagl.repository.cus.KendraDataRepository;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.common.util.StringUtils;

@Service
public class CollectionsService {

	private static final Logger logger = LogManager.getLogger(CollectionsService.class);

	@Autowired
	private CdhDao cdhDao;

	@Autowired
	private FetchDetailsService fetchDetailsService;

	@Autowired
	private KendraDataRepository kendraRepo;
	
	@Autowired
	private GkLoanDataRepo gkLoanDataRepo;
	
	@Autowired
	private CustomerDataRepository customerDataRepository;

	@Autowired
	private DigitalCollectionRepository digitalCollectionRepository;
	
	private static final String OVERDUE_PRINCIPAL = "overduePrincipal";
	private static final String OVERDUE_INTEREST = "overdueInterest";
	
	@Value("${url.kendra.amiuserdetails.service}")
	String fetchUserDataUrl;
	
	@Autowired
	private RestTemplate template;
	
	@CircuitBreaker(name = "Fetch_userdata", fallbackMethod = "asmiFallback")
	public AsmiUserResponse getUserDetailsData(GkKendraUserId userId) throws URISyntaxException {
		logger.error("userId for ASMI: {}", userId);
		URI url = new URI(fetchUserDataUrl);
		logger.debug("Fetched branchName & branchId based userId of collection for URL is: {}", url);
		logger.error("Fetched branchName & branchId based userId of collection for URL is: {}", url);
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<GkKendraUserId> entity = new HttpEntity<>(userId, header);
		AsmiUserResponse userDetails = new AsmiUserResponse();
		try {
			ResponseEntity<AsmiUserResponse> response = template.exchange(url, HttpMethod.POST, entity,
					AsmiUserResponse.class);
			logger.debug("Fetched branchName & branchId from ASMI table of collection: {}", response);
			logger.error("Fetched branchName & branchId from ASMI table of collection: {}", response);
			logger.debug("Fetching branchName & branchId from ASMI table of collection:{}", userDetails);
			logger.error("Fetching branchName & branchId from ASMI table of collection: {}", userDetails);
			if (response.getBody() == null) {
				logger.error("No user details returned for userId: {}", userId);
				logger.error("ASMI response body is NULL, treating as APIIssue");
				userDetails.setAddInfo1("APIIssue");
				userDetails.setAddInfo2("APIIssue");
				return userDetails;
			}
			return response.getBody();
		} catch (Exception ex) {
			userDetails.setAddInfo1("APIIssue");
			userDetails.setAddInfo2("APIIssue");
			// logger.error("Error during API call: {}", ex.getMessage(), ex);
			// throw new RestClientException("Failed to fetch user details", ex);
			logger.error("Exception during ASMI API call", ex);
			logger.error("Exception during ASMI API call of collection for userId: {}", userId, ex);
			return userDetails;
		}
	}



	public List<CollectionsData> getCollectionDtlsListByKendra(KendraRequestField kendraRequestField) {
		logger.debug("Fetching kendraRequestField for kendraIds : {}", kendraRequestField);
		List<CollectionsData> collectionListResp = new ArrayList<>();
		try {

			logger.debug("Before Collection logic execution::{}", LocalDateTime.now());

			String userId = kendraRequestField.getUserId();
			String roleId = kendraRequestField.getRoleId();
			String meetingDate = kendraRequestField.getMeetingDate();

			GkKendraAssignment assignedKendra = new GkKendraAssignment();
			assignedKendra.setUserId(userId);

			// These Kendra List need to include as it assigned Temp
			assignedKendra.setExcludedFlag("N");
			List<String> kList = fetchDetailsService.getAssignedKendraList(assignedKendra);

			// These kendra list need to exclude as it has moved from current KM list
			assignedKendra.setExcludedFlag("Y");
			List<String> excludedkList = fetchDetailsService.getAssignedKendraList(assignedKendra);

			//Exiting code
			//GkUserData userData = cdhDao.fetchUserRole(userId);
			//String designation = (null != userData) ? userData.getUserdesignation() : "";
			//end

			GkKendraUserId gkKendraUserId = new GkKendraUserId();
			gkKendraUserId.setUserId(userId);
			AsmiUserResponse asmiUserResponse = getUserDetailsData(gkKendraUserId);
			// Fetch user designation to check if BM
			GkUserData tempUserData = cdhDao.fetchUserRole(userId);
			String actualDesignation = (tempUserData != null) ? tempUserData.getUserdesignation() : "";
			//Altering addInfo1 when the roleId is DEO.
			if (CommonConstants.DEO_ROLE.equalsIgnoreCase(roleId)
					&& CommonConstants.BM_ROLE.equalsIgnoreCase(actualDesignation)) {
				logger.debug("User is BM but logged in with DEO role. Overriding addInfo1 to KM.");

				if (asmiUserResponse == null) {
					asmiUserResponse = new AsmiUserResponse();
				}
				asmiUserResponse.setAddInfo1(CommonConstants.KM_ROLE);
			}
			logger.debug("asmiUserResponse for DEO of collection:: {}", asmiUserResponse);
			logger.error("asmiUserResponse for DEO of collection:: {}", asmiUserResponse);
			boolean isAsmiSource = true;
			GkUserData userData = null;
			AsmiUserData asmiUserData = null;
			String designation = "";
			if (asmiUserResponse == null || "APIIssue".equalsIgnoreCase(asmiUserResponse.getAddInfo1())
					|| "APIIssue".equalsIgnoreCase(asmiUserResponse.getAddInfo2())) {
				  userData = cdhDao.fetchUserRole(userId);
				logger.debug("userData for DEO of collection:: {}", userData);
				logger.error("userData for DEO of collection:: {}", userData);
				 isAsmiSource = false;
				 designation = (null != userData) ? userData.getUserdesignation() : "";
				logger.debug("CDH Hierarchy for DEO of collection: : {}", designation);
				logger.error("CDH Hierarchy for DEO of collection: : {}", designation);
			} else {
				String userRole = asmiUserResponse.getAddInfo1();
				String branchId = asmiUserResponse.getAddInfo2();
			    asmiUserData = new AsmiUserData();
				asmiUserData.setUserId(userId);
				asmiUserData.setUserdesignation(userRole);
				asmiUserData.setHierarchyId(branchId);
				logger.debug("ASMI asmiUserData of collection: : {}", asmiUserData);
				logger.error("ASMI asmiUserData of collection: : {}", asmiUserData);
				designation = (null != asmiUserData) ? asmiUserData.getUserdesignation() : "";
				logger.debug("ASMI designation of collection: : {}", designation);
				logger.error("ASMI designation of collection:: {}", designation);
			}

			List<GkKendraData> kendraList = new ArrayList<>();
			if (roleId != null && !roleId.isEmpty()) {
				if (roleId.equalsIgnoreCase(CommonConstants.DEO_ROLE)) {
					logger.debug("assigned kendra list inside DEO Role : {}", kList);
					if(isAsmiSource) {
						kendraList = cdhDao.fetchKendraDetailsForDEONew(userId, asmiUserData, CommonConstants.KENDRA_STATUS,
								kList);
						logger.debug("Printing isAsmiSource with : {}", isAsmiSource);
					}else {
						kendraList = cdhDao.fetchKendraDetailsForDEO(userId, userData, CommonConstants.KENDRA_STATUS,
								kList);
						logger.debug("Printing kendraList with exiting code inside DEO Role of collection: {}", kendraList);

					}

				} else if (roleId.equalsIgnoreCase("KM")) {
					logger.debug("assigned kendra list inside KM Role of collection: {}", kList);
					if(isAsmiSource) {
						kendraList = cdhDao.fetchKendraDetailsNew(userId, asmiUserData, CommonConstants.KENDRA_STATUS, kList,
								excludedkList, roleId);
					}else {
						kendraList = cdhDao.fetchKendraDetails(userId, userData, CommonConstants.KENDRA_STATUS, kList,
								excludedkList, roleId);
						logger.debug("Printing kendraList with exiting code inside KM Role of collection: {}", kendraList);

					}
				}
			} else {
				if(isAsmiSource) {
					kendraList = cdhDao.fetchKendraDetailsNew(userId, asmiUserData, CommonConstants.KENDRA_STATUS, kList,
							excludedkList, roleId);
				}
				else {
					kendraList = cdhDao.fetchKendraDetails(userId, userData, CommonConstants.KENDRA_STATUS, kList,
							excludedkList, roleId);
					logger.debug("Printing kendraList with exiting code inside KM Role of collection>>>: {}", kendraList);

				}

			}

			logger.debug("Kendra List is:{}", kendraList);
			logger.debug("ASMI designation : {}", kendraList);
			if ((StringUtils.isNotBlank(designation) && CommonConstants.KM_ROLE.equalsIgnoreCase(designation)
					&& !kendraList.isEmpty()) || "KM".equalsIgnoreCase(roleId)) {

				List<Integer> kendraIds = kendraList.stream().map(GkKendraData::getKendraId).distinct().toList();
				logger.debug("Printing  kendraIds :: {} " , kendraIds);
				List<String> branchIds = kendraList.stream().map(GkKendraData::getBranchId).distinct().toList();
				List<GkCustomerData> custDtlsLst = customerDataRepository.findByKendraIdIn(kendraIds);

				List<GkMLoanProduct> loanProducts = cdhDao.fetchLoanProducts();

				//List<KendraLatLongEntity> kendraLatLongList = cdhDao.fetchAllKendraLatLong();

				 List<KendraIdsLatLongProjection> kendraLatLongList = cdhDao.fetchAllKendraLatLong(kendraIds);
				 logger.debug("Printing kendraLatLongList for kendraIds :: {} " , kendraIds);

				List<CollectionDetailsDto> collectionsDataDto;
				if (!CommonUtils.checkStringNullOrEmpty(kendraRequestField.getMeetingDate())) {
					collectionsDataDto = kendraRepo.fetchMeetingDateCollectionDataForKendra(branchIds,
							kendraIds, kendraRequestField.getMeetingDate());
				logger.debug("collectionsDataDto inside if test1 :: {} " , collectionsDataDto);

				} else {
					collectionsDataDto = kendraRepo.fetchCollectionDataForKendra(branchIds, kendraIds);
					logger.debug("collectionsDataDto inside else test1 :: {} " , collectionsDataDto);
				}
				logger.debug("collectionsDataDto size::{}", collectionsDataDto.size());
				for (CollectionDetailsDto value : collectionsDataDto) {
					int count = 0;
					CollectionDtls collDtls = frameCollectionDtls(value, custDtlsLst, loanProducts, count, meetingDate);

					List<KendraIdsLatLongProjection> kendraLatLong = kendraLatLongList.stream()
							.filter(val -> val.getKendraId() == value.getKendraId()).toList();
					String lat = "";
					String longitude = "";
					if (!kendraLatLong.isEmpty()) {
						lat = kendraLatLong.get(0).getLat();
						longitude = kendraLatLong.get(0).getLongit();
					}
					CollectionsData collectionsData = CollectionsData.builder()
							.kendraId(String.valueOf(value.getKendraId())).kendraName(value.getKendraName())
							.branchId(value.getBranchId()).kmId(userId).meetingDate(value.getNextMeetingDate())
							.meetingDay(value.getMeetingDay()).startTime(value.getMeetingStartingTime()).lat(lat)
							.longitude(longitude).updLoc("").colldtls(collDtls).build();
					collectionListResp.add(collectionsData);
				}
			}
		} catch (Exception ex) {
			logger.error(CommonConstants.EXCEP_OCCURED, ex);
		}
		logger.debug("Final Collection data::{}", collectionListResp);
		logger.debug("After Collection logic execution::{}", LocalDateTime.now());
		return collectionListResp;
	}

	public List<GkKendraData> fetchKendraDetails(String userId, GkUserData userData, String kendraStatus,
			List<String> newKedraList, List<String> excludedkList, String roleId) {
		List<GkKendraData> klist = new ArrayList<>();
		try {
			logger.debug("Inside fetchKendraDetails: user id is::{}, UserData is::{}", userId, userData);
			String branchId = (null != userData) ? userData.getHierarchyId() : "";
			if ((null != userData && CommonConstants.KM_ROLE.equalsIgnoreCase(userData.getUserdesignation()))
					|| roleId.equalsIgnoreCase("KM")) {
				logger.debug("Fetching Kendra data for the Kendra Manager");
				if (newKedraList.isEmpty() && excludedkList.isEmpty()) {
					logger.debug("no kendra assigened and no kendra excluded, normal flow");
					// normal flow : when no kendra assigned to km and no kendras are excluded
					klist = kendraRepo.findKendraList(userId, kendraStatus, newKedraList, excludedkList, branchId);
				} else if (newKedraList.isEmpty()) {
					logger.debug("assigned kendra empty and excluded kendra we have:");
					klist = kendraRepo.findListForOnlyExcludedKendra(userId, kendraStatus, excludedkList, branchId);
				} else if (excludedkList.isEmpty()) {
					logger.debug("assigned kendra we have and excluded kendra empty:");
					klist = kendraRepo.findListForOnlyIncludedKendra(userId, kendraStatus, newKedraList);
				} else {
					logger.debug("either kendra assigened or kendra excluded");
					klist = kendraRepo.findListForAssignedAndExcludedKendra(userId, kendraStatus, newKedraList,
							excludedkList, branchId);
				}
			} else if (null != userData && CommonConstants.BM_ROLE.equalsIgnoreCase(userData.getUserdesignation())) {
				logger.debug("Fetching Kendra data for the Branch manager");
				klist = kendraRepo.findByBranchId(userData.getHierarchyId());
			}
		} catch (Exception ex) {
			logger.error(CommonConstants.EXCEP_OCCURED, ex);
		}
		return klist;
	}

	private CollectionDtls frameCollectionDtls(CollectionDetailsDto value, List<GkCustomerData> custDtlsLst,
			List<GkMLoanProduct> loanProducts, int count, String meetingDate) {
		List<CollectionGroupDtls> collectionGrpDtlsList = new ArrayList<>();

		String[] groupIds = value.getGroupId().split("#", -1);
		String[] totalDues = value.getGroupTotalDue().split("#", -1);
		String[] totalAdvances = value.getGroupTotalAdvance().split("#", -1);
		String[] netDues = value.getGroupNetDue().split("#", -1);
		for (int i = 0; i < groupIds.length; i++) {
			List<CollectionMemberDtls> memberDetailsList = frameCollectionMemberDtls(value, custDtlsLst, loanProducts,
					i, count, meetingDate);
			CollectionGroupDtls collectionGroupDtls = CollectionGroupDtls.builder().id(groupIds[i])
					.totalDue(Double.parseDouble(totalDues[i])).totalAdv(Double.parseDouble(totalAdvances[i]))
					.netDue(Double.parseDouble(netDues[i])).collectionMemberDtls(memberDetailsList).build();
			collectionGrpDtlsList.add(collectionGroupDtls);
			count += memberDetailsList.size();
			logger.debug("Count value for group no::{}, is::{}", groupIds[i], count);
		}
		Timestamp txnDate = value.getTransactionDate();
		String colDate = "";
		String cdhKMId = value.getKmId();
		if (null != txnDate) {
			LocalDate localDate = txnDate.toLocalDateTime().toLocalDate();
			DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
			colDate = localDate.format(outputFormatter);
		}
		return CollectionDtls.builder().collDate(colDate).collId(value.getCollectionId())
				.collectionGroupDtls(collectionGrpDtlsList).cdhKmId(cdhKMId).build();
	}

	private List<CollectionMemberDtls> frameCollectionMemberDtls(CollectionDetailsDto value,
			List<GkCustomerData> custDtlsLst, List<GkMLoanProduct> loanProducts, int groupIndex, int count, String meetingDate) {
		List<CollectionMemberDtls> memberDetailsList = new ArrayList<>();
		String[] memberGroupIds = new String[0];
		if (!CommonUtils.checkStringNullOrEmpty(value.getGroupCustomerId())) {
			memberGroupIds = value.getGroupCustomerId().split("#", -1)[groupIndex].split("\\*");
		}
		for (int j = 0; j < memberGroupIds.length; j++) {
			String memberId = memberGroupIds[j];
			if (!CommonUtils.checkStringNullOrEmpty((memberId))) {
				GkCustomerData userData = custDtlsLst.stream()
						.filter(customerDtl -> customerDtl.getCustomerId().equals(memberId))
						.findFirst().orElse(null);

				String depName = "";
				String mobileNumber = "";
				String primaryId = "";
				if (userData != null) {
					depName = null != userData.getDepname() ? userData.getDepname() : "";
					mobileNumber = null != userData.getMobileNum() ? userData.getMobileNum() : "";
					primaryId = null != userData.getPrimaryId() ? userData.getPrimaryId() : "";
				}
				String[] memberNames = value.getCustomerName().split("#", -1);
				String[] custColAmt = value.getCustCollectedAmount().split("#", -1);
				String[] custTotalAdvAmt = value.getCustTotalAdvance().split("#", -1);
				String[] custTotalDue = value.getCustTotalDue().split("#", -1);
				String[] custNetDue = value.getCustNetDue().split("#", -1);
				String[] custAttend = value.getCustAttendance().split("#", -1);
				String[] parFlg = value.getCustomerFlg().split("#", -1);
				String parFlgStr = "PARTIAL".equalsIgnoreCase(parFlg[count]) ? "Y" : "N";
				double parAmt = Double.parseDouble("0");
				if("Y".equalsIgnoreCase(parFlgStr) && null != custTotalDue[count] && null != custColAmt[count]) {
					parAmt = Double.parseDouble(custTotalDue[count]) - Double.parseDouble(custColAmt[count]);
				}
				List<CollectionLoanDtls> loanDetailsList = frameLoanDetailsList(value, loanProducts, memberId, count);
				logger.info("Fetching MAHI Collection | memberId={}", memberId);
				List<MahiCollectionDetails> mahiCollectionDetails = frameMahiCollectionDetails(memberId, meetingDate);
				CollectionMemberDtls collectionMemberDtls = CollectionMemberDtls.builder().id(memberGroupIds[j])
						.name(memberNames[count]).depname(depName).totalDue(Double.parseDouble(custTotalDue[count]))
						.totalAdv(Double.parseDouble(custTotalAdvAmt[count]))
						.primaryId(primaryId)
						.mobileNumber(mobileNumber)
						.netDue(Double.parseDouble(custNetDue[count])).collAmount(Double.parseDouble(custColAmt[count]))
						.attend(custAttend[count]).parFlg(parFlgStr).parAmt(parAmt)
						.prevParAmt(parAmt)
						.advAmt(Double.parseDouble("0")).collectionLoanDtls(loanDetailsList)
						.mahiCollectionDetails(mahiCollectionDetails).build();
				logger.debug("collectionMemberDtls::{}", collectionMemberDtls);
				memberDetailsList.add(collectionMemberDtls);
				count++;
			}
		}
		logger.debug("List {} member details::{}", groupIndex, memberDetailsList);
		return memberDetailsList;
	}

	private List<CollectionLoanDtls> frameLoanDetailsList(CollectionDetailsDto value,
			List<GkMLoanProduct> loanProducts, String memberId, int count) {
		List<CollectionLoanDtls> loanDetailsList = new ArrayList<>();
		List<GkLoanData> gkLoanDataLst = gkLoanDataRepo.findByCustomerId(memberId);
		logger.debug("Loan Data for customerId {} is::{}", memberId, gkLoanDataLst);

		String[] listLoanIds = new String[0];
		if (!CommonUtils.checkStringNullOrEmpty(value.getLoanId()) && count < value.getLoanId().split("#", -1).length) {
			listLoanIds = value.getLoanId().split("#", -1)[count].split("\\*");
			String[] listLoanDues = new String[0];
			if (!CommonUtils.checkStringNullOrEmpty(value.getLoanDue())
					&& count < value.getLoanDue().split("#", -1).length) {
				listLoanDues = value.getLoanDue().split("#", -1)[count].split("\\*");
			}
			String[] listLoanOSAmt = new String[0];
			if (!CommonUtils.checkStringNullOrEmpty(value.getLoanOutstandingAmount())
					&& count < value.getLoanOutstandingAmount().split("#", -1).length) {
				listLoanOSAmt = value.getLoanOutstandingAmount().split("#", -1)[count].split("\\*");
			}
			String[] listLoanProdId = new String[0];
			if (!CommonUtils.checkStringNullOrEmpty(value.getLoanProductId())
					&& count < value.getLoanProductId().split("#", -1).length) {
				listLoanProdId = value.getLoanProductId().split("#", -1)[count].split("\\*");
			}
			for (int l = 0; l < listLoanIds.length; l++) {
				String loanId = listLoanIds[l];
				// Code to check for the loan product id from T24_Collection_sheet response.
				String prodCode = "";
				if (null != listLoanProdId && l < listLoanProdId.length) {
					String loanProdId = listLoanProdId[l];
					prodCode = loanProducts.stream()
							.filter(lnProd -> null != lnProd.getProductId() && null != loanProdId
									&& lnProd.getProductId().equalsIgnoreCase(loanProdId))
							.map(GkMLoanProduct::getShortDesc).findFirst().orElse("");
				}
				Optional<Map<String, String>> result = gkLoanDataLst.stream()
						.filter(loanDtl -> loanDtl.getCustomerId().equals(memberId)
								&& ((null != loanDtl.getLoanId() && loanDtl.getLoanId().equals(loanId))
										|| (null != loanDtl.getDocRefNum() && loanDtl.getDocRefNum().equals(loanId))))
						.map(loanDtl -> {
							Map<String, String> loanData = new HashMap<>();
							loanData.put("product", loanDtl.getProduct());
							loanData.put(OVERDUE_INTEREST, loanDtl.getOverdueInterest());
							loanData.put(OVERDUE_PRINCIPAL, loanDtl.getOverduePrincipal());
							return loanData;
						}).findFirst();
				double parAmount = 0;
				if (result.isPresent()) {
					if (CommonUtils.checkStringNullOrEmpty(prodCode)) {
						prodCode = loanProducts.stream()
								.filter(lnProd -> null != lnProd.getProductId()
										&& lnProd.getProductId().equalsIgnoreCase(result.get().get("product")))
								.map(GkMLoanProduct::getShortDesc).findFirst().orElse("");
					}
					if (null != result.get().get(OVERDUE_PRINCIPAL) && null != result.get().get(OVERDUE_INTEREST)) {
						parAmount = Double.parseDouble(result.get().get(OVERDUE_PRINCIPAL))
								+ Double.parseDouble(result.get().get(OVERDUE_INTEREST));
					}
				}
				double dueAmount = 0;
				if (null != listLoanDues && listLoanDues.length > 0  && l < listLoanDues.length && !ObjectUtils.isEmpty(listLoanDues[l])) {
					dueAmount = Double.parseDouble(listLoanDues[l]);
				}
				double osAmount = 0;
				if (null != listLoanOSAmt && listLoanOSAmt.length > 0 && l < listLoanOSAmt.length && !ObjectUtils.isEmpty(listLoanOSAmt[l])) {
					osAmount = Double.parseDouble(listLoanOSAmt[l]);
				}
				CollectionLoanDtls loanDtls = CollectionLoanDtls.builder().id(loanId).cashAmt(0L).dueAmt(dueAmount)
						.osAmt(osAmount).prevParAmt(parAmount).parAmt(parAmount).prodCode(prodCode).upiAmt(0L).build();
				loanDetailsList.add(loanDtls);
			}
		}
		return loanDetailsList;
	}

	private List<MahiCollectionDetails> frameMahiCollectionDetails(String memberId, String meetingDate) {

		logger.debug("frameMahiCollectionDetails | Start | memberId={} | meetingDate={}", memberId, meetingDate);

		Double amount = null;
		try {
			LocalDate date = LocalDate.parse(meetingDate);

			String startDateTime = meetingDate + " 00:00:00";
			String endDateTime = date.plusDays(1) + " 00:00:00";

			logger.debug("frameMahiCollectionDetails | Date Range | start={} | end={}",
					startDateTime, endDateTime);

			amount = digitalCollectionRepository.findMahiAmountByCustomerIdAndDateRange(memberId, startDateTime, endDateTime);
			logger.debug("frameMahiCollectionDetails | Fetched MAHI amount={} for memberId={} | meetingDate={}", amount, memberId, meetingDate);
		} catch (Exception e) {
			logger.error("frameMahiCollectionDetails | Exception while fetching MAHI amount | memberId={} | meetingDate={}",
					memberId, meetingDate, e);
		}
		String mahiFlag = "N";
		String mahiAmount = "0";
		if (amount != null) {
			mahiFlag = "Y";
			mahiAmount = String.valueOf(amount);
		}
		MahiCollectionDetails mahiCollectionDetails = MahiCollectionDetails.builder().mahiFlag(mahiFlag)
				.mahiAmount(mahiAmount)
				.build();
		logger.debug("frameMahiCollectionDetails | Response | memberId={} | mahiFlag={} | mahiAmount={} | meetingDate={}",
				memberId, mahiFlag, mahiAmount, meetingDate);
		logger.debug("frameMahiCollectionDetails | End | memberId={} | meetingDate={} ", memberId, meetingDate);
		return Collections.singletonList(mahiCollectionDetails);
	}
}
