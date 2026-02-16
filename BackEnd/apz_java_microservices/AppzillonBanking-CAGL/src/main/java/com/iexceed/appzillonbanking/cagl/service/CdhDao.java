package com.iexceed.appzillonbanking.cagl.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import com.iexceed.appzillonbanking.cagl.constants.CommonConstants;
import com.iexceed.appzillonbanking.cagl.custom.exception.KendraFetchException;
import com.iexceed.appzillonbanking.cagl.domain.cus.GkEarningMember;
import com.iexceed.appzillonbanking.cagl.domain.cus.GkIncomeAssesment;
import com.iexceed.appzillonbanking.cagl.domain.cus.GkKendraUserId;
import com.iexceed.appzillonbanking.cagl.domain.cus.GkLoanData;
import com.iexceed.appzillonbanking.cagl.domain.cus.GkLoanPurpose;
import com.iexceed.appzillonbanking.cagl.domain.cus.GkMLoanProduct;
import com.iexceed.appzillonbanking.cagl.domain.cus.GkProductSubPurpose;
import com.iexceed.appzillonbanking.cagl.dto.KendraDetailsDto;
import com.iexceed.appzillonbanking.cagl.dto.LoanEligible;
import com.iexceed.appzillonbanking.cagl.entity.BranchLatlong;
import com.iexceed.appzillonbanking.cagl.entity.GkCustomerData;
import com.iexceed.appzillonbanking.cagl.entity.GkKendraData;
import com.iexceed.appzillonbanking.cagl.entity.GkUserData;
import com.iexceed.appzillonbanking.cagl.entity.KendraLatLongEntity;
import com.iexceed.appzillonbanking.cagl.entity.OfficeData;
import com.iexceed.appzillonbanking.cagl.payload.AsmiUserData;
import com.iexceed.appzillonbanking.cagl.repository.cus.CustomerDataRepository;
import com.iexceed.appzillonbanking.cagl.repository.cus.CustomerIncomeRepository;
import com.iexceed.appzillonbanking.cagl.repository.cus.EarningMemberRepository;
import com.iexceed.appzillonbanking.cagl.repository.cus.EligibleLoanRepository;
import com.iexceed.appzillonbanking.cagl.repository.cus.KendrLatLongRepo;
import com.iexceed.appzillonbanking.cagl.repository.cus.KendraDataRepository;
import com.iexceed.appzillonbanking.cagl.repository.cus.LatLongRepo;
import com.iexceed.appzillonbanking.cagl.repository.cus.LoanDataRepository;
import com.iexceed.appzillonbanking.cagl.repository.cus.LoanProductRepo;
import com.iexceed.appzillonbanking.cagl.repository.cus.LoanPurposeRepo;
import com.iexceed.appzillonbanking.cagl.repository.cus.OfficeDataRepo;
import com.iexceed.appzillonbanking.cagl.repository.cus.ProductSubPurposeRepo;
import com.iexceed.appzillonbanking.cagl.repository.cus.UserDetailsRepo;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class CdhDao {

	private static final Logger logger = LogManager.getLogger(CdhDao.class);

	@Autowired
	private LoanDataRepository loanDataRepo;

	@Autowired
	private KendraDataRepository kendraRepo;

	@Autowired
	private EligibleLoanRepository eligibleRepo;

	@Autowired
	private EarningMemberRepository earningRepo;

	@Autowired
	private CustomerIncomeRepository custIncomeRepo;

	@Autowired
	private CustomerDataRepository custRepo;

	@Autowired
	private LoanProductRepo prodRepo;

	@Autowired
	private LoanPurposeRepo prodPurpRepo;

	@Autowired
	private ProductSubPurposeRepo subPurpRepo;

	@Autowired
	private OfficeDataRepo officeRepo;

	@Autowired
	private UserDetailsRepo userRepo;

	@Autowired
	private LatLongRepo latLongRepo;

	@Autowired
	private KendrLatLongRepo kendrLatLongRepo;
	
	@Value("${url.kendra.fetchbranchId.service}")
	String kendraBranchIdUrl;
	
	@Autowired
	private RestTemplate template;
	
	@CircuitBreaker(name = "kendra_branch", fallbackMethod = "fallbackMethod")
	public String getBranchId(GkKendraUserId userId) throws URISyntaxException {
		logger.debug("userId is:{}" +userId);
		logger.error("userId is: ", userId);
	    URI url = new URI(kendraBranchIdUrl);
	    logger.debug("Kendra userId url is:{}" +url);
	    HttpHeaders header = new HttpHeaders();
	    header.setContentType(MediaType.APPLICATION_JSON);
	    HttpEntity<GkKendraUserId> entity = new HttpEntity<>(userId, header);
	    String branchId;
	    try {
	        branchId = template.exchange(
	            url,
	            HttpMethod.POST,
	            entity,
	            String.class
	        ).getBody();
	    } catch (Exception ex) {
	        logger.error("Error during API call: {}", ex.getMessage());
	        throw new RestClientException("Failed to fetch branch ID", ex);
	    }
	    if (branchId == null || branchId.isEmpty()) {
	        logger.warn("No branch ID returned for userId: {}", userId);
	        return null;
	    }
	    logger.debug("Fetched branchId: {}" +branchId);
	    return branchId;
	}

	public List<GkKendraData> fetchKendraDetails(String userId, GkUserData userData, String kendraStatus, List<String> newKedraList, List<String> excludedkList, String roleId) {
	    List<GkKendraData> klist = new ArrayList<>();
	    logger.error("userId:" + userId);
	    logger.debug("kendraStatus:" + kendraStatus);
	    logger.error("kendraStatus:" + kendraStatus);
	    logger.debug("newKedraList:" + newKedraList);
	    logger.error("newKedraList:" + newKedraList);
	    logger.debug("excludedkList:" + excludedkList);
	    logger.error("excludedkList:" + excludedkList);
	    try {
	        logger.debug("Inside fetchKendraDetails: user id is::{}, UserData is::{}", userId, userData);
	        String branchId = getBranchId(new GkKendraUserId(userId));
	        logger.debug("branchId for userId {}" + branchId);
	        logger.error("branchId for userId " + branchId);
	        
	        if ((null != userData && CommonConstants.KM_ROLE.equalsIgnoreCase(userData.getUserdesignation())) || roleId.equalsIgnoreCase("KM")) {
	            logger.error("Fetching Kendra data for the Kendra Manager");

	            if (newKedraList.isEmpty() && excludedkList.isEmpty()) {
	                logger.debug("no kendra assigned and no kendra excluded, normal flow");
	                logger.error("no kendra assigned and no kendra excluded, normal flow");
	                klist = kendraRepo.findKendraList(userId, kendraStatus, newKedraList, excludedkList, branchId);
	                logger.debug("klist1 {}" + klist);
	                logger.error("klist1 " + klist);
	            } else {
	                logger.debug("either kendra assigned or kendra excluded");
	                logger.error("either kendra assigned and no kendra excluded");

	                if (newKedraList != null && newKedraList.isEmpty()) {
	                    logger.error("assigned kendra empty and excluded kendra we have:");
	                    klist = kendraRepo.findListForOnlyExcludedKendra(userId, kendraStatus, excludedkList, branchId);
	                    logger.debug("klist2 {} " + klist);
	                    logger.error("klist2 " + klist);
	                } else if (excludedkList != null && excludedkList.isEmpty()) {
	                    logger.error("assigned kendra we have and excluded kendra empty:");
	                    klist = kendraRepo.findListForOnlyIncludedKendra(userId, kendraStatus, newKedraList);
	                    logger.debug("klist3 {}" + klist);
	                    logger.error("klist3 " + klist);
	                } else {
	                    klist = kendraRepo.findListForAssignedAndExcludedKendra(userId, kendraStatus, newKedraList, excludedkList, branchId);
	                    logger.debug("klist4 {}" + klist);
	                    logger.error("klist4 " + klist);
	                }
	            }
	        } else if (null != userData && CommonConstants.BM_ROLE.equalsIgnoreCase(userData.getUserdesignation())) {
	            logger.error("Fetching Kendra data for the Branch manager");
	            klist = kendraRepo.findByBranchId(userData.getHierarchyId());
	            logger.debug("klist for BM Role  {}" + klist);
	        }
	    } catch (Exception ex) {
	        logger.error(CommonConstants.EXCEP_OCCURED, ex);
	    }
	    logger.debug("FetchKendraDetails method response::{}", klist);
	    return klist;
	}

	public List<GkKendraData> fetchKendraDetailsForDEO(String userId, GkUserData userData, String kendraStatus, List<String> newKedraList) { 
	    List<GkKendraData> klist = new ArrayList<>();
	    try {
	        klist = kendraRepo.findByBranchId(userData.getHierarchyId());
	    } catch (Exception ex) {
	        logger.error(CommonConstants.EXCEP_OCCURED, ex);
	    }
	    return klist;
	}
	
	public List<GkKendraData> fetchKendraDetailsNew(String userId,  AsmiUserData asmiUserData, String kendraStatus, List<String> newKedraList, List<String> excludedkList, String roleId) {
		List<GkKendraData> klist = new ArrayList<>();
		logger.error("userId:" + userId);
		logger.debug("kendraStatus:" + kendraStatus);
		logger.error("kendraStatus:" + kendraStatus);
		logger.debug("newKedraList:" + newKedraList);
		logger.error("newKedraList:" + newKedraList);
		logger.debug("excludedkList:" + excludedkList);
		logger.error("excludedkList:" + excludedkList);
		try {
			logger.debug("Inside fetchKendraDetails: user id is::{}, UserData is::{}", userId, asmiUserData);
			String  branchId = getBranchId(new GkKendraUserId(userId));
			logger.debug("branchId for userId {}" +branchId);
			logger.error("branchId for userId " +branchId);
			if ((null != asmiUserData && CommonConstants.KM_ROLE.equalsIgnoreCase(asmiUserData.getUserdesignation())) || roleId.equalsIgnoreCase("KM") ) {
				logger.error("Fetching Kendra data for the Kendra Manager");
				
				if (newKedraList.isEmpty() && excludedkList.isEmpty()) {
					logger.debug("no kendra assigened and no kendra excluded, normal flow");
					logger.error("no kendra assigened and no kendra excluded, normal flow");
					// normal flow : when no kendra assigned to km and no kendras are excluded
					klist = kendraRepo.findKendraList(userId, kendraStatus, newKedraList, excludedkList,branchId);
					logger.debug("klist1 {}" +klist);
					logger.error("klist1 " +klist);
				} else {
					logger.debug("either kendra assigened or kendra excluded");
					logger.error("either kendra assigened and no kendra excluded");
					// flow : when either kendra assigned to km or kendras are excluded 33
					if (newKedraList != null && newKedraList.isEmpty()) {
						logger.error("assigned kendra empty and excluded kendra we have:");
						klist = kendraRepo.findListForOnlyExcludedKendra(userId, kendraStatus, excludedkList,branchId);
						logger.debug("klist2 {} " +klist);
						logger.error("klist2 " +klist);
					} else if (excludedkList != null && excludedkList.isEmpty()) {
						logger.error("assigned kendra we have and excluded kendra empty:");
						klist = kendraRepo.findListForOnlyIncludedKendra(userId, kendraStatus, newKedraList);
						logger.debug("klist3 {}" +klist);
						logger.error("klist3 " +klist);
					} else {
						klist = kendraRepo.findListForAssignedAndExcludedKendra(userId, kendraStatus, newKedraList,
								excludedkList,branchId);
						logger.debug("klist4 {}" +klist);
						logger.error("klist4 "  +klist);
					}
					// klist = kendraRepo.findListForAssignedAndExcludedKendra(userId, kendraStatus,
					// newKedraList, excludedkList);
				}
				// klist = kendraRepo.findKendraList(userId, kendraStatus);
			} else if (null != asmiUserData && CommonConstants.BM_ROLE.equalsIgnoreCase(asmiUserData.getUserdesignation())) {
				logger.error("Fetching Kendra data for the Branch manager");
				klist = kendraRepo.findByBranchId(asmiUserData.getHierarchyId());
				logger.debug("klist for BM Role  {}" + klist);
			}
		} catch (Exception ex) {
			logger.error(CommonConstants.EXCEP_OCCURED, ex);
		}
		logger.debug("FetchKendraDetails method response::{}", klist);
		return klist;
	}
	
	
	
	
	public List<GkKendraData> fetchKendraDetailsForDEONew(String userId, AsmiUserData asmiUserData, String kendraStatus, List<String> newKedraList) {
		List<GkKendraData> klist = new ArrayList<>();
		try {
			klist = kendraRepo.findByBranchId(asmiUserData.getHierarchyId());
		} catch (Exception ex) {
			logger.error(CommonConstants.EXCEP_OCCURED, ex);
		}
		return klist;
	}

	public List<GkLoanData> fetchLoanDatails(String custId) {
		logger.debug("Loan detail fetch started");
		return loanDataRepo.findByCustomerId(custId);
	}

	public List<LoanEligible> fetchEligibleLoans(String custId) {
		List<LoanEligible> eligList = new ArrayList<>();
		try {
			List<Object[]> objList = eligibleRepo.findLoanEligibleDetailsByCustomerId(custId);
			// Query q = em.createNativeQuery(CommonConstants.El_QUERY +
			// String.format("'%s'", custId));
			// @SuppressWarnings("unchecked")
			// List<Object[]> objList = q.getResultList();
			if (!eligList.isEmpty()) {
				for (Object[] objarr : objList) {
					LoanEligible l = new LoanEligible();
					l.setCbAmt((Double) objarr[0]);
					l.setCaglAmt((Double) objarr[1]);
					l.setProductType((String) objarr[2]);
					l.setProduct((String) objarr[3]);
					l.setIntRate(21);
					eligList.add(l);
				}
			} else {
				logger.debug("eligible loan fetch is Empty for cust id:{}", custId);
			}
		} catch (Exception ex) {
			logger.error(CommonConstants.EXCEP_OCCURED, ex);
		}
		logger.debug("eligible loan fetch is success::{}", eligList);
		return eligList;
	}

	public List<GkEarningMember> fetchEarningMemberDetails(String custId) {
		return earningRepo.findByCustomerId(custId);
	}

	public List<GkIncomeAssesment> fetchCustIncomeDetails(String custId) {
		return custIncomeRepo.findByCustomerId(custId);
	}

	public List<GkCustomerData> fetchCustData(int kendraId) {
		return custRepo.findByKendraId(kendraId);
	}

	public List<GkMLoanProduct> fetchLoanProducts() {
		return prodRepo.findAll();
	}

	public List<GkLoanPurpose> fetchProductPurpose(String prodId) {
		return prodPurpRepo.findByProductId(prodId);
	}

	public List<GkProductSubPurpose> fetchProdSubPurpose(String prodId, String purpId) {
		return subPurpRepo.findByProductIdAndPurposeId(prodId, purpId);
	}

	
	public GkUserData fetchUserRole(String userId) {
	    return userRepo.findById(userId)
	        .orElseThrow(() -> new KendraFetchException(
	            "Invalid user : " + userId,
	            "User id is not present in user master table"
	        ));
	}

	
	
	public List<GkCustomerData> fetchCustomerData(String custId) {
		List<GkCustomerData> cList = custRepo.findByCustomerId(custId);
		if (cList.isEmpty()) {
			throw new KendraFetchException("Invalid Customer Id : " + custId,
					"customer id is not present in gk_cust_data table");
		} else {
			return cList;
		}
	}

	public List<GkKendraData> fetchBranchKendra(String userId) {
		return kendraRepo.findByBranchId(userId);
	}

	public Optional<OfficeData> fetchKendraOfficeData(String branchId) {
		return officeRepo.findById(branchId);
	}

	public List<OfficeData> fetchKendraOfficeDataList(List<String> branchIds) {
		return officeRepo.findAllById(branchIds);
	}

		public String insertBranchLatLongRecords(BranchLatlong branchLatLoang) {
		String msg = null;
		try {
			Optional<BranchLatlong> branchLatLongOpt = latLongRepo.findById(branchLatLoang.getBranch_ID());
			String recordsExistFlag = branchLatLongOpt.isPresent() ? "Y" : "N";
			if(branchLatLongOpt.isPresent()) {
				logger.debug("Latitude Longitude record is present, record is : "+branchLatLongOpt.get());
				BranchLatlong branchLatlongRec = branchLatLongOpt.get();
				branchLatlongRec.setLatitude(branchLatLoang.getLatitude());
				branchLatlongRec.setLongitude(branchLatLoang.getLongitude());
				branchLatlongRec.setUpdatedOn(branchLatLoang.getUpdatedOn());
				branchLatlongRec.setBranch(branchLatLoang.getBranch());
				branchLatlongRec.setUpdatedBy(branchLatLoang.getUpdatedBy());
				latLongRepo.save(branchLatlongRec);
				logger.debug("LatLong record got successfully updated for the branch::{}", branchLatLoang.getBranch_ID());
			} else {
				logger.debug("Latitude Longitude record is not present");
				BranchLatlong latLong = new BranchLatlong();
				latLong.setBranch(branchLatLoang.getBranch());
				latLong.setBranch_ID(branchLatLoang.getBranch_ID());
				latLong.setLatitude(branchLatLoang.getLatitude());
				latLong.setLongitude(branchLatLoang.getLongitude());
				latLong.setUpdatedBy(branchLatLoang.getUpdatedBy());
				latLong.setUpdatedOn(branchLatLoang.getUpdatedOn());
				latLongRepo.save(latLong);
				logger.debug("LatLong record got successfully inserted for the branch::{}", branchLatLoang.getBranch_ID());
			}
			msg = recordsExistFlag.equalsIgnoreCase("Y") ? "records updated successfully"
					: "records inserted successfully";
		} catch (Exception e) {
			logger.error(CommonConstants.EXCEP_OCCURED, e);
		}
		return msg;
	}

	public String insertKendraLatLongRecords(KendraLatLongEntity kendraLatLong) {
		String msg = null;
		try {
			Optional<KendraLatLongEntity> kendraLatLongOpt = kendrLatLongRepo.findById(kendraLatLong.getKendraID());
			String recordExistFlag = kendraLatLongOpt.isPresent() ? "Y" : "N";
			// kendraLatLongOpt.get().getKendraID();
			KendraLatLongEntity latLong = new KendraLatLongEntity();
			latLong.setKendraID(kendraLatLong.getKendraID());
			latLong.setLat(kendraLatLong.getLat());
			latLong.setLongit(kendraLatLong.getLongit());
			latLong.setUpdatedAt(kendraLatLong.getUpdatedAt());
			latLong.setUpdatedBy(kendraLatLong.getUpdatedBy());
			latLong.setAddress(kendraLatLong.getAddress());
			kendrLatLongRepo.save(latLong);
			logger.debug("Kendra LatLong record got successfully updated for the branch : {}",
					kendraLatLong.getKendraID());
			msg = "Y".equalsIgnoreCase(recordExistFlag) ? "records updated successfully"
					: "records inserted successfully";
		} catch (Exception e) {
			logger.error(CommonConstants.EXCEP_OCCURED, e);
		}
		return msg;
	}

	public BranchLatlong fetchBranchLatLong(String branchId) {
		try {
			Optional<BranchLatlong> branchLatLongOpt = latLongRepo.findById(branchId);
			return (branchLatLongOpt.isPresent() ? branchLatLongOpt.get() : null);
		} catch (Exception e) {
			logger.error(CommonConstants.EXCEP_OCCURED, e);
		}
		return null;
	}

	public KendraLatLongEntity fetchKendraLatLong(int kendraId) {
		try {
			Optional<KendraLatLongEntity> kendraLatLongOpt = kendrLatLongRepo.findById(kendraId);
			return (kendraLatLongOpt.isPresent() ? kendraLatLongOpt.get() : null);
		} catch (Exception e) {
			logger.error(CommonConstants.EXCEP_OCCURED, e);
		}
		return null;
	}

	public List<KendraLatLongEntity> fetchAllKendraLatLong() {
		try {
			List<KendraLatLongEntity> kendraLatLongLst = kendrLatLongRepo.findAll();
			return (!kendraLatLongLst.isEmpty() ? kendraLatLongLst : new ArrayList<>());
		} catch (Exception e) {
			logger.error(CommonConstants.EXCEP_OCCURED, e);
		}
		return new ArrayList<>();
	}
	
	public List<KendraDetailsDto> fetchKendraInfo(String branchId, String nextMeetingDt) {
		List<KendraDetailsDto> klist = new ArrayList<>();
		try {
			logger.debug("Inside fetchKendraInfo: branchId id is::{}, nextMeetingDt is::{}", branchId, nextMeetingDt);
			return kendraRepo.fetchKendraInfo(branchId, CommonConstants.KM_ROLE, nextMeetingDt,
					CommonConstants.KENDRA_STATUS);
		} catch (Exception ex) {
			logger.error(CommonConstants.EXCEP_OCCURED, ex);
		}
		logger.debug("klist:>>>+++++++++>>>:{}", klist);
		return klist;
	}
	
	public List<KendraDetailsDto> fetchKendraInfoForBranchId(String branchId) {
		List<KendraDetailsDto> klist = new ArrayList<>();
		try {
			logger.debug("Inside fetchKendraInfo: branchId id is::{}", branchId);
			return kendraRepo.fetchKendraInfoForBranchId(branchId, CommonConstants.KM_ROLE,
					CommonConstants.KENDRA_STATUS);
		} catch (Exception ex) {
			logger.error(CommonConstants.EXCEP_OCCURED, ex);
		}
		logger.debug("klist:>>>>>>:{}", klist);
		return klist;
		
	}
	
	
	public List<KendraDetailsDto> fetchKendraInfoForBranchIdByRole(String branchId) {
		List<KendraDetailsDto> klist = new ArrayList<>();
		try {
			logger.debug("Inside fetchKendraInfo: branchId id is::{}", branchId);
			return kendraRepo.fetchKendraInfoByRole(branchId, CommonConstants.KM_ROLE,
					CommonConstants.KENDRA_STATUS);
		} catch (Exception ex) {
			logger.error(CommonConstants.EXCEP_OCCURED, ex);
		}
		return klist;
	}
}
