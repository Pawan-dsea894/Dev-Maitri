package com.iexceed.appzillonbanking.cagl.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtils;
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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iexceed.appzillonbanking.cagl.constants.CommonConstants;
import com.iexceed.appzillonbanking.cagl.customer.payload.CustomerResponseObject;
import com.iexceed.appzillonbanking.cagl.domain.cus.GkEarningMember;
import com.iexceed.appzillonbanking.cagl.domain.cus.GkIncomeAssesment;
import com.iexceed.appzillonbanking.cagl.domain.cus.GkKendraAssignment;
import com.iexceed.appzillonbanking.cagl.domain.cus.GkKendraUserId;
import com.iexceed.appzillonbanking.cagl.domain.cus.GkLoanData;
import com.iexceed.appzillonbanking.cagl.domain.cus.GkLoanPurpose;
import com.iexceed.appzillonbanking.cagl.domain.cus.GkMLoanProduct;
import com.iexceed.appzillonbanking.cagl.domain.cus.GkProductSubPurpose;
import com.iexceed.appzillonbanking.cagl.domain.cus.ProductResponseObject;
import com.iexceed.appzillonbanking.cagl.dto.CustData;
import com.iexceed.appzillonbanking.cagl.dto.CustEarnings;
import com.iexceed.appzillonbanking.cagl.dto.CustomerLoanDtlsDto;
import com.iexceed.appzillonbanking.cagl.dto.IncomeAssesment;
import com.iexceed.appzillonbanking.cagl.dto.KendraData;
import com.iexceed.appzillonbanking.cagl.dto.KendraDetailsDto;
import com.iexceed.appzillonbanking.cagl.dto.LoanData;
import com.iexceed.appzillonbanking.cagl.dto.LoanEligible;
import com.iexceed.appzillonbanking.cagl.dto.OfficeDataDto;
import com.iexceed.appzillonbanking.cagl.dto.ProductData;
import com.iexceed.appzillonbanking.cagl.dto.ProductPurpose;
import com.iexceed.appzillonbanking.cagl.entity.BranchLatlong;
import com.iexceed.appzillonbanking.cagl.entity.GkCustomerData;
import com.iexceed.appzillonbanking.cagl.entity.GkKendraData;
import com.iexceed.appzillonbanking.cagl.entity.GkUnifiedData;
import com.iexceed.appzillonbanking.cagl.entity.GkUserData;
import com.iexceed.appzillonbanking.cagl.entity.KendraLatLongEntity;
import com.iexceed.appzillonbanking.cagl.entity.OfficeData;
import com.iexceed.appzillonbanking.cagl.payload.AsmiUserData;
import com.iexceed.appzillonbanking.cagl.payload.AsmiUserResponse;
import com.iexceed.appzillonbanking.cagl.payload.KendraRequestField;
import com.iexceed.appzillonbanking.cagl.payload.ResponseObject;
import com.iexceed.appzillonbanking.cagl.repository.cus.GkUnifiedDataRepository;
import com.iexceed.appzillonbanking.cagl.user.roles.payload.UserResponseObject;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.payload.ResponseWrapper;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.micrometer.common.util.StringUtils;
import jakarta.transaction.Transactional;

@Service
public class FetchDetailsService {

	private static final Logger logger = LogManager.getLogger(FetchDetailsService.class);

	public static final String EXCEPTION_MSG = "Something went wrong, Please try again!!";

	@Autowired
	CdhDao dao;

	@Value("${url.kendra.assignment.service}")
	String kendraAssinedUrl;
	
	@Value("${url.kendra.amiuserdetails.service}")
	String fetchUserDataUrl;
	

	@Autowired
	private RestTemplate template;

	@Autowired
	private GkUnifiedDataRepository gkunifiedrepo;
	
		
	@CircuitBreaker(name = "Fetch_userdata", fallbackMethod = "fallbackMethod")
	public AsmiUserResponse getUserDetailsData(GkKendraUserId userId) throws URISyntaxException {
		logger.debug("userId is: {}", userId);
		URI url = new URI(fetchUserDataUrl);
		logger.debug("Kendra userId URL is: {}", url);
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<GkKendraUserId> entity = new HttpEntity<>(userId, header);
		try {
			ResponseEntity<AsmiUserResponse> response = template.exchange(url, HttpMethod.POST, entity,
					AsmiUserResponse.class);
			logger.debug("Fetched user details: {}", response);
			AsmiUserResponse userDetails = response.getBody();
			logger.debug("Fetched user details: {}", userDetails);
			if (userDetails == null) {
				logger.warn("No user details returned for userId: {}", userId);
				return null;
			}
			return userDetails;
		} catch (Exception ex) {
			logger.error("Error during API call: {}", ex.getMessage(), ex);
			throw new RestClientException("Failed to fetch user details", ex);
		}
	}

	public Map<String, Object> fetchCdhKendraDetailsForDeo(String userId, List<String> kList,
			KendraRequestField kendraRequestField) {
		List<ResponseObject> respObjList = new ArrayList<>();
		List<OfficeData> officeList = null;
		Map<Integer, List<CustData>> customerListByKendra = null;
		Map<String, Object> responseMap = new HashMap<>();
		try {
	        GkKendraUserId gkKendraUserId = new GkKendraUserId();
	        gkKendraUserId.setUserId(userId);
	        
	        logger.debug("gkKendraUserId is: {}", gkKendraUserId);  
	        logger.error("gkKendraUserId is: {}", gkKendraUserId);

	        AsmiUserResponse asmiUserResponse = getUserDetailsData(gkKendraUserId);
	        logger.debug("userDetails is: {}", asmiUserResponse);  
	        logger.error("userDetails is: {}", asmiUserResponse); 
	        
	        String userRole =  asmiUserResponse.getAddInfo1();
	        String branchId =  asmiUserResponse.getAddInfo2();
	        
	        logger.debug("userRole is: {}", userRole);  
	        logger.error("userRole is: {}", userRole);  
	        
	        logger.debug("branchId is: {}", branchId);  
	        logger.error("branchId is: {}", branchId);  
	         
			GkUserData userData = dao.fetchUserRole(userId);
			
			AsmiUserData asmiUserData = new AsmiUserData();
			asmiUserData.setUserId(userId);
			asmiUserData.setUserdesignation(userRole);
			asmiUserData.setHierarchyId(branchId);
			
			 logger.debug("asmiUserData is>>>: {}", asmiUserData);  
		     logger.error("asmiUserData is>>: {}", asmiUserData);  
			
			String designation = (null != asmiUserData) ? asmiUserData.getUserdesignation() : "";
			List<GkKendraData> kendraList = dao.fetchKendraDetailsForDEONew(userId, asmiUserData,
					CommonConstants.KENDRA_STATUS, kList);
			
			 logger.debug("kendraList for DEO: {}", kendraList);  
		     logger.error("kendraList for DEO: {}", kendraList); 

			if (StringUtils.isNotBlank(designation) && CommonConstants.KM_ROLE.equalsIgnoreCase(designation)
					&& !kendraList.isEmpty()) {
				List<Integer> kendraIds = kendraList.stream().map(GkKendraData::getKendraId).distinct().toList();
				List<String> branchIds = kendraList.stream().map(GkKendraData::getBranchId).distinct().toList();
				
				List<CustData> customerList =null;
				//List<CustData> customerList = getCustomerDetailListByKendra(kendraIds);
				//customerListByKendra = customerList.stream().collect(Collectors.groupingBy(CustData::getKendraId));
				
				officeList = dao.fetchKendraOfficeDataList(branchIds);
			}
			for (GkKendraData gk : kendraList) {
				ResponseObject respobj = new ResponseObject();
				KendraData kendra = new KendraData();
				kendra.setKendraId(gk.getKendraId());
				kendra.setKendraName(gk.getKendraName());
				kendra.setKmName(gk.getKmName());
				kendra.setBranchId(gk.getBranchId());
				kendra.setVillageType(gk.getVillageType());
				kendra.setKendraAddr(gk.getKendraAddr());
				kendra.setState(gk.getState());
				kendra.setDistrict(gk.getDistrict());
				kendra.setTaluk(gk.getTaluk());
				kendra.setAreaType(gk.getAreaType());
				kendra.setVillage(gk.getVillage());
				kendra.setPincode(gk.getPincode());
				kendra.setMeetingFrequency(gk.getMeetingFrequency());
				kendra.setFirstMeetingDate(gk.getFirstMeetingDate());
				kendra.setNextMeetingDate(gk.getNextMeetingDate());
				kendra.setMeetingDay(gk.getMeetingDay());
				kendra.setMeetingPlace(gk.getMeetingPlace());
				kendra.setMeetingStartTime(gk.getMeetingStartTime());
				kendra.setEndingTime(gk.getEndingTime());
				kendra.setDistance(gk.getDistance());
				kendra.setLeader(gk.getLeader());
				kendra.setSecretary(gk.getSecretary());
				kendra.setCreatedBy(gk.getCreatedBy());
				kendra.setCreatedTS(gk.getCreatedTS());
				kendra.setUpdatedBy(gk.getUpdatedBy());
				kendra.setKendraStatus(gk.getKendraStatus());
				kendra.setActivationDate(gk.getActivationDate());
				kendra.setKmId(gk.getKmId());

				if (customerListByKendra != null && customerListByKendra.get(gk.getKendraId()) != null) {
					kendra.setCustomerDtls(customerListByKendra.get(gk.getKendraId()));
				}
				if (officeList != null && !officeList.isEmpty()) {
					List<OfficeData> officeDataFiltered = officeList.stream()
							.filter(office -> gk.getBranchId().equals(office.getBranchId())).toList();
					if (!officeDataFiltered.isEmpty()) {
						OfficeData office = officeDataFiltered.get(0);
						OfficeDataDto officeDto = new OfficeDataDto();
						BeanUtils.copyProperties(officeDto, office);
						kendra.setOfficeData(officeDto);
					}
				}
				respobj.setKendraDtls(kendra);
				respObjList.add(respobj);
			}
			responseMap.put("loans", respObjList);
		} catch (Exception exp) {
			logger.debug(CommonConstants.EXCEP_OCCURED, exp);
		}
		logger.debug("Final Response Time::{}", LocalDateTime.now());
		logger.debug("Kendra Response object::{}", responseMap);
		return responseMap;
	}

	public Map<String, Object> fetchCdhKendraDetails(String userId, List<String> kList, List<String> excludedkList, String roleId,KendraRequestField kendraRequestField) { 
		List<ResponseObject> respObjList = new ArrayList<>();
		List<OfficeData> officeList = null;
		Map<Integer, List<CustData>> customerListByKendra = null;
		Map<String, Object> responseMap = new HashMap<>();
		try {
			GkKendraUserId gkKendraUserId = new GkKendraUserId();
			gkKendraUserId.setUserId(userId);
			logger.debug("gkKendraUserId >>>>>is: {}", gkKendraUserId);

			AsmiUserResponse asmiUserResponse = getUserDetailsData(gkKendraUserId);
			logger.debug("userDetails >>>>> is: {}", asmiUserResponse);

			String userRole = asmiUserResponse.getAddInfo1();
			String branchId = asmiUserResponse.getAddInfo2();

			logger.debug("userRole>> is: {}", userRole);
			logger.error("userRole >>is: {}", userRole);

			logger.debug("branchId>>> is: {}", branchId);
			logger.error("branchId >>>is: {}", branchId);

			GkUserData userData = dao.fetchUserRole(userId);
			logger.error("userData " +userData);
			logger.debug("userData {}" +userData);
			
			AsmiUserData asmiUserData = new AsmiUserData();
			asmiUserData.setUserId(userId);
			asmiUserData.setUserdesignation(userRole);
			asmiUserData.setHierarchyId(branchId);

			logger.error("asmiUserData " +asmiUserData);
			logger.debug("asmiUserData {}" +asmiUserData);
		
			String designation = (null != asmiUserData) ? asmiUserData.getUserdesignation() : "";
			List<GkKendraData> kendraList = dao.fetchKendraDetailsNew(userId, asmiUserData, CommonConstants.KENDRA_STATUS, kList,excludedkList, roleId);
			 logger.debug("kendraList for KM: {}", kendraList);  
		     logger.error("kendraList for KM: {}", kendraList); 
			if ((StringUtils.isNotBlank(designation) && CommonConstants.KM_ROLE.equalsIgnoreCase(designation)
					&& !kendraList.isEmpty()  ) || roleId.equalsIgnoreCase("KM")) {
				
				logger.error("role ID is "+ roleId);
				logger.error("Kendra List is "+ kendraList);
				
				List<Integer> kendraIds = kendraList.stream().map(GkKendraData::getKendraId).distinct().toList();
				logger.error("kendraIds is "+ kendraIds);
				logger.debug("kendraIds is "+ kendraIds);
				List<String> branchIds = kendraList.stream().map(GkKendraData::getBranchId).distinct().toList();
				logger.error("branchIds is "+ branchIds);
				logger.debug("branchIds is "+ branchIds);
				List<CustData> customerList = getCustomerDetailListByKendra(kendraIds);
				logger.error("customerList is "+ customerList);
				logger.debug("customerList is "+ customerList);
				customerListByKendra = customerList.stream().collect(Collectors.groupingBy(CustData::getKendraId));

				logger.error("customerListByKendra "+ customerListByKendra);
				logger.debug("customerListByKendra "+ customerListByKendra);

				logger.debug("customerList based On kendraId{}:",customerListByKendra);

				officeList = dao.fetchKendraOfficeDataList(branchIds);
				logger.error("officeList "+ officeList);
				logger.debug("officeList "+ officeList);
			}
			for (GkKendraData gk : kendraList) {
				ResponseObject respobj = new ResponseObject();
				KendraData kendra = new KendraData();
				kendra.setKendraId(gk.getKendraId());
				kendra.setKendraName(gk.getKendraName());
				kendra.setKmName(gk.getKmName());
				kendra.setBranchId(gk.getBranchId());
				kendra.setVillageType(gk.getVillageType());
				kendra.setKendraAddr(gk.getKendraAddr());
				kendra.setState(gk.getState());
				kendra.setDistrict(gk.getDistrict());
				kendra.setTaluk(gk.getTaluk());
				kendra.setAreaType(gk.getAreaType());
				kendra.setVillage(gk.getVillage());
				kendra.setPincode(gk.getPincode());
				kendra.setMeetingFrequency(gk.getMeetingFrequency());
				kendra.setFirstMeetingDate(gk.getFirstMeetingDate());
				kendra.setNextMeetingDate(gk.getNextMeetingDate());
				kendra.setMeetingDay(gk.getMeetingDay());
				kendra.setMeetingPlace(gk.getMeetingPlace());
				kendra.setMeetingStartTime(gk.getMeetingStartTime());
				kendra.setEndingTime(gk.getEndingTime());
				kendra.setDistance(gk.getDistance());
				kendra.setLeader(gk.getLeader());
				kendra.setSecretary(gk.getSecretary());
				kendra.setCreatedBy(gk.getCreatedBy());
				kendra.setCreatedTS(gk.getCreatedTS());
				kendra.setUpdatedBy(gk.getUpdatedBy());
				kendra.setKendraStatus(gk.getKendraStatus());
				kendra.setActivationDate(gk.getActivationDate());
				kendra.setKmId(gk.getKmId());

				if (customerListByKendra != null && customerListByKendra.get(gk.getKendraId()) != null) {
					kendra.setCustomerDtls(customerListByKendra.get(gk.getKendraId()));
				}
				if (officeList != null && !officeList.isEmpty()) {
					List<OfficeData> officeDataFiltered = officeList.stream()
							.filter(office -> gk.getBranchId().equals(office.getBranchId())).toList();
					if (!officeDataFiltered.isEmpty()) {
						OfficeData office = officeDataFiltered.get(0);
						OfficeDataDto officeDto = new OfficeDataDto();
						BeanUtils.copyProperties(officeDto, office);
						kendra.setOfficeData(officeDto);
					}
				}
				respobj.setKendraDtls(kendra);
				respObjList.add(respobj);
			}
			responseMap.put("loans", respObjList);
		} catch (Exception exp) {
			logger.debug(CommonConstants.EXCEP_OCCURED, exp);
		}
		logger.debug("Final Response Time::{}", LocalDateTime.now());
		logger.debug("Kendra Response object::{}", responseMap);
		return responseMap;
	}

	
	public Map<String, Object> fetchCDHCustomerDetails(String customerId) { 
		List<ResponseObject> respObjList = new ArrayList<>();
		Map<Integer, List<CustData>> customerListByKendra = null;
		Map<String, Object> responseMap = new HashMap<>();
				List<CustData> customerList = getCustomerDetailListByCustomerId(customerId);
				logger.error("Customer Details By CustomerID" + customerList);
				customerListByKendra = customerList.stream().collect(Collectors.groupingBy(CustData::getKendraId));
				logger.error("Customer Details By CustomerID" + customerListByKendra);
				
				ResponseObject respobj = new ResponseObject();
				KendraData kendra = new KendraData();
				
				if (customerListByKendra != null ) {
					kendra.setCustomerDtls(customerListByKendra.get(customerList.get(0).getKendraId()));
				}
				
				respobj.setKendraDtls(kendra);
				respObjList.add(respobj);
				responseMap.put("loans", respObjList);
				logger.error("responseMap" + responseMap);
				return responseMap;
		}
		
	
	
	public List<CustData> getCustomerDetailListByKendra(List<Integer> kendraIds) {
		List<CustData> custList = new ArrayList<>();
		/*
		 * String jpql =
		 * "SELECT cus,ln,el,em,pdt,ia FROM GkCustomerData cus LEFT JOIN GkLoanData ln on cus.customerId = ln.customerId"
		 * +
		 * " LEFT JOIN GkEligibleLoans el on cus.customerId = el.customerId LEFT JOIN GkMLoanProduct pdt on pdt.shortDesc = el.productType "
		 * + " LEFT JOIN GkIncomeAssesment ia on cus.customerId = ia.customerId " +
		 * " LEFT JOIN GkEarningMember em on cus.customerId = em.customerId where cus.kendraId in (:kendraId)"
		 * ; TypedQuery<Object[]> query = entityManager.createQuery(jpql,
		 * Object[].class); query.setParameter("kendraId", kendraIds); List<Object[]>
		 * results = query.getResultList();
		 */
		try {
			logger.debug("Before Execution Time::{}", LocalDateTime.now());
			// Loan Product
			// List<GkMLoanProduct> productdata = gkloanprodctrepo.findAllLoanData();
			// logger.error("Loan Products List: {}", productdata);
			List<GkUnifiedData> results = gkunifiedrepo.fetchCustomerDataNew1(kendraIds);
			logger.debug("After Execution Time::{}", LocalDateTime.now());
			if (results != null && !results.isEmpty()) {
				List<LoanData> loanDtlList = new ArrayList<>();
				List<LoanEligible> loanElgList = new ArrayList<>();
				List<CustEarnings> earnList = new ArrayList<>();
				List<IncomeAssesment> incomeList = new ArrayList<>();
				// logger.error("GkUnifiedData size is: {}" ,results.size());
				for (GkUnifiedData result : results) {
					// logger.error("GkUnifiedData: {}" ,result);
					this.appendCustListDataNew(result, custList);
					this.appendLoanDtlListDataNew(result, loanDtlList);
					this.appendEligibleLoanListDataNew(result, loanElgList, null);
					this.appendEarningMemberListDataNew(result, earnList);
					this.appendIncomeAssementListDataNew(result, incomeList);
				}
				// logger.error("loanDtlList: {}" ,loanDtlList);
				custList = custList.stream().collect(Collectors.toMap(CustData::getCustomerId, person -> person,
						(existing, replacement) -> existing)).values().stream().toList();
				// logger.error("custList: {}" ,custList);
				custList.forEach(cusData -> {
					List<LoanData> loanDtlListFiltered = loanDtlList.stream()
							.filter(loan -> cusData.getCustomerId().equals(loan.getCustomerId())).collect(Collectors
									.toMap(LoanData::getLoanId, loan -> loan, (existing, replacement) -> existing))
							.values().stream().toList();
					cusData.setLoanDtls(loanDtlListFiltered);

					// logger.error("custList: {}" ,loanDtlListFiltered);
					List<LoanEligible> loanElgListFiltered = loanElgList.stream()
							.filter(elg -> cusData.getCustomerId().equals(elg.getCustomerId()))
							.collect(Collectors.toMap(LoanEligible::getCustomerId, elg -> elg,
									(existing, replacement) -> existing))
							.values().stream().toList();
					cusData.setEligibleLoan(loanElgListFiltered);

					List<CustEarnings> earnListFiltered = earnList.stream()
							.filter(earn -> cusData.getCustomerId().equals(earn.getCustomerId())).collect(Collectors
									.toMap(CustEarnings::getRecId, earn -> earn, (existing, replacement) -> existing))
							.values().stream().toList();
					cusData.setEarnings(earnListFiltered);

					List<IncomeAssesment> incomeListFiltered = incomeList.stream()
							.filter(loanDta -> cusData.getCustomerId().equals(loanDta.getCustomerId())).toList();
					if (!incomeListFiltered.isEmpty()) {
						cusData.setIncome(incomeListFiltered.subList(0, 1));
					}
				});
			}
		} catch (Exception exp) {
			logger.error(CommonConstants.EXCEP_OCCURED, exp);
		}
		logger.debug("Final response from getCustomerDetailListByKendra method::{}", custList);
		logger.debug("Final Execution Time::{}", LocalDateTime.now());
		return custList;
	}
	
	
	public List<CustData> getCustomerDetailListByCustomerId(String customerId) {
		List<CustData> custList = new ArrayList<>();
		try {
			logger.error("Before Execution Time::{}", LocalDateTime.now());
			List<GkUnifiedData> results = gkunifiedrepo.fetchCustomerDataNewByCustomerId(customerId);
			logger.error("After Execution Time::{}", LocalDateTime.now());
			/*
			 * if (results != null && !results.isEmpty()) {
			 * logger.error("Result is not empty"); for (GkUnifiedData result : results) {
			 * this.appendCustListDataNew(result, custList); } custList =
			 * custList.stream().collect(Collectors.toMap(CustData::getCustomerId, person ->
			 * person, (existing, replacement) -> existing)).values().stream().toList(); }
			 */
			if (results != null && !results.isEmpty()) {
				List<LoanData> loanDtlList = new ArrayList<>();
				List<LoanEligible> loanElgList = new ArrayList<>();
				List<CustEarnings> earnList = new ArrayList<>();
				List<IncomeAssesment> incomeList = new ArrayList<>();
				// logger.error("GkUnifiedData size is: {}" ,results.size());
				for (GkUnifiedData result : results) {
					// logger.error("GkUnifiedData: {}" ,result);
					this.appendCustListDataNew(result, custList);
					this.appendLoanDtlListDataNew(result, loanDtlList);
					this.appendEligibleLoanListDataNew(result, loanElgList, null);
					this.appendEarningMemberListDataNew(result, earnList);
					this.appendIncomeAssementListDataNew(result, incomeList);
				}
				// logger.error("loanDtlList: {}" ,loanDtlList);
				custList = custList.stream().collect(Collectors.toMap(CustData::getCustomerId, person -> person,
						(existing, replacement) -> existing)).values().stream().toList();
				// logger.error("custList: {}" ,custList);
				custList.forEach(cusData -> {
					List<LoanData> loanDtlListFiltered = loanDtlList.stream()
							.filter(loan -> cusData.getCustomerId().equals(loan.getCustomerId())).collect(Collectors
									.toMap(LoanData::getLoanId, loan -> loan, (existing, replacement) -> existing))
							.values().stream().toList();
					cusData.setLoanDtls(loanDtlListFiltered);

					// logger.error("custList: {}" ,loanDtlListFiltered);
					List<LoanEligible> loanElgListFiltered = loanElgList.stream()
							.filter(elg -> cusData.getCustomerId().equals(elg.getCustomerId()))
							.collect(Collectors.toMap(LoanEligible::getCustomerId, elg -> elg,
									(existing, replacement) -> existing))
							.values().stream().toList();
					cusData.setEligibleLoan(loanElgListFiltered);

					List<CustEarnings> earnListFiltered = earnList.stream()
							.filter(earn -> cusData.getCustomerId().equals(earn.getCustomerId())).collect(Collectors
									.toMap(CustEarnings::getRecId, earn -> earn, (existing, replacement) -> existing))
							.values().stream().toList();
					cusData.setEarnings(earnListFiltered);

					List<IncomeAssesment> incomeListFiltered = incomeList.stream()
							.filter(loanDta -> cusData.getCustomerId().equals(loanDta.getCustomerId())).toList();
					if (!incomeListFiltered.isEmpty()) {
						cusData.setIncome(incomeListFiltered.subList(0, 1));
					}
				});
			}
		} catch (Exception exp) {
			logger.error(CommonConstants.EXCEP_OCCURED, exp);
		}
		logger.error("Final response from getCustomerDetailListByKendra method::{}", custList);
		logger.error("Final Execution Time::{}", LocalDateTime.now());
		return custList;
	}
	
	private void appendCustListDataNew(GkUnifiedData gkCustData, List<CustData> custList) {

		try {
			if (gkCustData == null) {
				return;
			}
			CustData cusData = CustData.builder().customerId(gkCustData.getCustomerId())
					.customerName(gkCustData.getCustomerName()).kendraId(safeParseInt(gkCustData.getKendraId()))
					.groupId(safeParseInt(gkCustData.getGroupId())).branchName(gkCustData.getBranchName())
					.primaryType(gkCustData.getPrimaryType()).primaryId(gkCustData.getPrimaryId())
					.dob(gkCustData.getDob()).maritalStatus(gkCustData.getMaritalStatus())
					.address(gkCustData.getAddress()).bankAccNo(gkCustData.getBankAccountNumber())
					.bankAccName(gkCustData.getBankAccountName()).bankBranchName(gkCustData.getBankBranchName())
					.bankName(gkCustData.getBankName()).bankIfscCode(gkCustData.getBankIfscCode())
					.memRelation(gkCustData.getMemberRelation()).mobileNum(gkCustData.getMobileNumber())
					.permAddLine1(gkCustData.getPermanentAddressLine1())
					.permAddLine2(gkCustData.getPermanentAddressLine2()).permanentState(gkCustData.getPermanentState())
					.permanentDistrict(gkCustData.getPermanentDistrict())
					.permanentVillageLocality(gkCustData.getPermanentVillageLocality())
					.permanentPincode(gkCustData.getPermanentPincode()).permanentTaluk(gkCustData.getPermanentTaluk())
					.commAddLIne1(gkCustData.getCommunicationAddressLine1())
					.commAddLIne2(gkCustData.getCommunicationAddressLine2())
					.commState(gkCustData.getCommunicationState()).commDistrict(gkCustData.getCommunicationDistrict())
					.commVillageLocality(gkCustData.getCommunicationVillageLocality())
					.commPincode(gkCustData.getCommunicationPincode()).commTaluk(gkCustData.getCommunicationTaluk())
					.custVintage(gkCustData.getCustomerVintage())
					.activationDate(gkCustData.getActivationDate()).gender(gkCustData.getGender())
					.depDocId(gkCustData.getDependentDocId()).depDob(gkCustData.getDependentDob())
					.depDocType(gkCustData.getDependentDocType()).depname(gkCustData.getDependentName())
					.custStatus(gkCustData.getCustomerStatus()).custQualify(gkCustData.getCustomerQualification())
					.build();

			custList.add(cusData);

		} catch (Exception ex) {
			logger.error(CommonConstants.EXCEP_OCCURED, ex);
		}
	}

	private void appendLoanDtlListData(CustomerLoanDtlsDto result, List<LoanData> loanDtlList) {
		try {
			if (null != result.getLoanData()) {
				LoanData loanData = new LoanData();
				BeanUtils.copyProperties(loanData, result.getLoanData());
				loanDtlList.add(loanData);
			}
		} catch (Exception ex) {
			logger.error(CommonConstants.EXCEP_OCCURED, ex);
		}
	}
	

	private void appendLoanDtlListDataNew(GkUnifiedData loanData, List<LoanData> loanDtlList) {
		try {

			if (loanData == null) {
				return; // Early exit if input data is null
			}

			// Check if customerId exists
			String customerId = loanData.getCustomerId();
			if (customerId == null) {
				return; // Early exit if customerId is null
			}

			List<Object> otherValues = Arrays.asList(loanData.getLoanId(), loanData.getAmount(),
					loanData.getApprovedAmount(), loanData.getStatus(), loanData.getFrequency(), loanData.getTerm(),
					loanData.getProduct(), loanData.getLoanValueDate(), loanData.getLoanMaturityDate(),
					loanData.getInterestRate(), loanData.getOverduePrincipal(), loanData.getOverdueInterest(),
					loanData.getOverdueStatus(), loanData.getOutstandingPrincipal());

			// Validate that at least one other value is non-null
			if (otherValues.stream().noneMatch(Objects::nonNull)) {
				return; // Early exit if no relevant fields are provided
			}

			// Check if customerId is non-null and at least one other value is non-null
			LoanData loan = LoanData.builder().loanId(loanData.getLoanId()).customerId(customerId)
					.amount(loanData.getAmount()).approvedAmt(loanData.getApprovedAmount()).status(loanData.getStatus())
					.freq(loanData.getFrequency()).term(loanData.getTerm()).product(loanData.getProduct())
					.lnValueDate(loanData.getLoanValueDate()).lnMatDate(loanData.getLoanMaturityDate())
					.interestRate(loanData.getInterestRate()).overduePrincipal(loanData.getOverduePrincipal())
					.overdueInterest(loanData.getOverdueInterest()).overDueStatus(loanData.getOverdueStatus())
					.loanPurpose(loanData.getLoanPurpose()).pf(loanData.getPf()).GST(loanData.getGST())
					.mem_insu(loanData.getMem_insu()).sp_insu(loanData.getSp_insu()).APR(loanData.getAPR())
					.outstandingPrincipal(loanData.getOutstandingPrincipal()).build();
			loanDtlList.add(loan);

		} catch (Exception ex) {
			logger.error(CommonConstants.EXCEP_OCCURED, ex);
		}

	}

	
	// Need to corrected
	private void appendEligibleLoanListDataNew(GkUnifiedData gkEligLoans, List<LoanEligible> loanElgList,
			List<GkMLoanProduct> productdata) {

		if (gkEligLoans == null) {
			return; // Early exit if input is null
		}

		try {

			String customerId = gkEligLoans.getCustomerId();
			if (customerId == null) {
				return; // Early exit if customerId is null
			}
			List<Object> otherValues = Arrays.asList(gkEligLoans.getOverallCbEligibleAmount(),
					gkEligLoans.getEligibleCaglAmount(), gkEligLoans.getEligibleCaglProduct());

			if (otherValues.stream().noneMatch(Objects::nonNull)) {
				return; // Early exit if all relevant fields are null
			}

			// logger.error("product after compraision is "+ gkLoanProduct);

			LoanEligible loanElData = LoanEligible.builder()
					.cbAmt(parseDoubleSafely(gkEligLoans.getEligibleCaglAmount())) // interchanged the value as requested by business
					.caglAmt(parseDoubleSafely(gkEligLoans.getOverallCbEligibleAmount()))
					.productType(gkEligLoans.getEligibleCaglProduct()).product(gkEligLoans.getProductType()).intRate(21)
					.customerId(customerId).build();
			loanElgList.add(loanElData);

		} catch (Exception ex) {
			logger.error(CommonConstants.EXCEP_OCCURED, ex);
		}

	}

	private static double parseDoubleSafely(String value) {
		if (value == null || value.trim().isEmpty()) {
			return 0.0; // Default value when input is null or empty
		}
		try {
			return Double.parseDouble(value.trim());
		} catch (NumberFormatException e) {
			// Log the error and return a default value
			System.err.println("Invalid number format: " + value);
			return 0.0;
		}
	}

	private Integer safeParseInt(String value) {
		try {
			return value != null ? Integer.parseInt(value) : null;
		} catch (NumberFormatException ex) {
			logger.error("Invalid integer value: {}", value, ex);
			return null;
		}
	}
	
	
	private void appendEarningMemberListDataNew(GkUnifiedData earnings, List<CustEarnings> earnList) {

		if (earnings == null) {
			return; // Early exit if input is null
		}

		try {

			String customerId = earnings.getCustomerId();
			if (customerId == null) {
				return; // Early exit if customerId is null
			}

			List<Object> otherValues = Arrays.asList(earnings.getRecordId(), earnings.getName(), earnings.getDobe(),
					earnings.getMemberRelationE(), earnings.getLegalDocumentName(), earnings.getLegalId());

			if (otherValues.stream().noneMatch(Objects::nonNull)) {
				return; // Early exit if all relevant fields are null
			}

			CustEarnings earnData = CustEarnings.builder().recId(earnings.getRecordId()).customerId(customerId)
					.name(earnings.getName()).dob(earnings.getDobe()).memRelation(earnings.getMemberRelationE())
					.legaldocName(earnings.getLegalDocumentName()).legaldocId(earnings.getLegalId()).build();

			earnList.add(earnData);

		} catch (Exception ex) {
			logger.error(CommonConstants.EXCEP_OCCURED, ex);
		}

	}

	
	
	private void appendIncomeAssementListDataNew(GkUnifiedData gkIncomeAssesment, List<IncomeAssesment> incomeList) {

		if (gkIncomeAssesment == null) {
			return; // Early exit if input is null
		}

		try {

			String customerId = gkIncomeAssesment.getCustomerId();
			if (customerId == null) {
				return; // Early exit if customerId is null
			}

			List<Object> otherValues = Arrays.asList(gkIncomeAssesment.getTotalIncome(),
					gkIncomeAssesment.getTotalExpenses(), gkIncomeAssesment.getAssessmentDate());

			// Check if customerId is non-null and at least one other value is non-null
			if (otherValues.stream().noneMatch(Objects::nonNull)) {
				return; // Early exit if all relevant fields are null
			}
			IncomeAssesment incomeData = IncomeAssesment.builder().customerId(customerId)
					.totIncome(gkIncomeAssesment.getTotalIncome()).totExpense(gkIncomeAssesment.getTotalExpenses())
					.assesmentDt(gkIncomeAssesment.getAssessmentDate()).build();
			incomeList.add(incomeData);

		} catch (Exception ex) {
			logger.error(CommonConstants.EXCEP_OCCURED, ex);
		}
	}

	public List<CustData> custList(int kendraId) {
		List<CustData> custList = new ArrayList<>();
		try {
			List<GkCustomerData> customerList = dao.fetchCustData(kendraId);
			for (GkCustomerData gk : customerList) {
				CustData c = new CustData();
				c.setCustomerId(gk.getCustomerId());
				c.setCustomerName(gk.getCustomerName());
				c.setKendraId(gk.getKendraId());
				c.setGroupId(gk.getGroupId());
				c.setBranchName(gk.getBranchName());
				c.setPrimaryType(gk.getPrimaryType());
				c.setPrimaryId(gk.getPrimaryId());
				c.setDob(gk.getDob());
				c.setMaritalStatus(gk.getMaritalStatus());
				c.setAddress(gk.getAddress());
				c.setBankAccNo(gk.getBankAccNo());
				c.setBankAccName(gk.getBankAccName());
				c.setBankBranchName(gk.getBankBranchName());
				c.setBankName(gk.getBankName());
				c.setBankIfscCode(gk.getBankIfscCode());
				c.setMemRelation(gk.getMemRelation());
				c.setMobileNum(gk.getMobileNum());
				c.setPermAddLine1(gk.getPermAddLine1());
				c.setPermAddLine2(gk.getPermAddLine2());
				c.setPermanentState(gk.getPermanentState());
				c.setPermanentDistrict(gk.getPermanentDistrict());
				c.setPermanentVillageLocality(gk.getPermanentVillageLocality());
				c.setPermanentPincode(gk.getPermanentPincode());
				c.setPermanentTaluk(gk.getPermanentTaluk());
				c.setCommAddLIne1(gk.getCommAddLIne1());
				c.setCommAddLIne2(gk.getCommAddLIne2());
				c.setCommState(gk.getCommState());
				c.setCommDistrict(gk.getCommDistrict());
				c.setCommVillageLocality(gk.getCommVillageLocality());
				c.setCommPincode(gk.getCommPincode());
				c.setCommTaluk(gk.getCommTaluk());
				c.setCustVintage(gk.getCustVintage());
				c.setActivationDate(gk.getActivationDate());
				c.setGender(gk.getGender());
				c.setDepDob(gk.getDepDob());
				c.setDepDocId(gk.getDepDocId());
				c.setDepDocType(gk.getDepDocType());
				c.setDepname(gk.getDepname());
				c.setLoanDtls(loanList(gk.getCustomerId()));
				c.setEligibleLoan(loanEligList(gk.getCustomerId()));
				c.setIncome(custIncomeList(gk.getCustomerId()));
				c.setEarnings(custEarningsList(gk.getCustomerId()));
				custList.add(c);
			}
		} catch (Exception ex) {
			logger.error(CommonConstants.EXCEP_OCCURED, ex);
		}
		logger.debug("custList method response::{}", custList);
		return custList;
	}

	public List<LoanData> loanList(String custId) {
		List<LoanData> loanList = new ArrayList<>();
		for (GkLoanData gk : dao.fetchLoanDatails(custId)) {
			LoanData l = new LoanData();
			l.setLoanId(gk.getLoanId());
			l.setCustomerId(gk.getCustomerId());
			l.setAmount(gk.getAmount());
			l.setApprovedAmt(gk.getApprovedAmt());
			l.setStatus(gk.getStatus());
			l.setFreq(gk.getFreq());
			l.setTerm(gk.getTerm());
			l.setProduct(gk.getProduct());
			l.setLnValueDate(gk.getLnValueDate());
			l.setLnMatDate(gk.getLnMatDate());
			l.setInterestRate(gk.getInterestRate());
			l.setOverduePrincipal(gk.getOverduePrincipal());
			l.setOverdueInterest(gk.getOverdueInterest());
			l.setOverDueStatus(gk.getOverDueStatus());
			l.setOutstandingPrincipal(gk.getOutstandingPrincipal());
			loanList.add(l);
		}
		if (!loanList.isEmpty()) {
			logger.debug("Loan data response object successfully generated for cust in service class::{}", custId);
		} else {
			logger.debug("Loan data response object generated empty in service class for cust::{}", custId);
		}
		return loanList;
	}

	public List<LoanEligible> loanEligList(String custId) {
		return dao.fetchEligibleLoans(custId);
	}

	public List<IncomeAssesment> custIncomeList(String custId) {
		List<IncomeAssesment> iList = new ArrayList<>();

		for (GkIncomeAssesment gk : dao.fetchCustIncomeDetails(custId)) {
			IncomeAssesment i = new IncomeAssesment();
			i.setCustomerId(gk.getCustomerId());
			i.setTotIncome(gk.getTotIncome());
			i.setTotExpense(gk.getTotExpense());
			i.setAssesmentDt(gk.getAssesmentDt());
			iList.add(i);
		}
		if (!iList.isEmpty()) {
			logger.debug("Income assesment data response object successfully generated for cust in service class:{}",
					custId);
		} else {
			logger.debug("Income assesment response object generated empty in service class for cust:{}", custId);
		}
		return iList;
	}

	public List<CustEarnings> custEarningsList(String custId) {
		List<CustEarnings> eList = new ArrayList<>();
		for (GkEarningMember gk : dao.fetchEarningMemberDetails(custId)) {
			CustEarnings c = new CustEarnings();
			c.setCustomerId(gk.getCustomerId());
			c.setName(gk.getName());
			c.setDob(gk.getDob());
			c.setMemRelation(gk.getMemRelation());
			c.setLegaldocName(gk.getLegaldocName());
			c.setLegaldocId(gk.getLegaldocId());
			eList.add(c);
		}
		if (!eList.isEmpty()) {
			logger.debug("Earning member data response object successfully generated for cust in service class {}",
					custId);
		} else {
			logger.debug("Earning member response object generated empty in service class for cust {}", custId);
		}
		return eList;
	}

	public List<ProductResponseObject> fetchProductDetals() {
		List<ProductResponseObject> respObjList = new ArrayList<>();
		for (GkMLoanProduct gk : dao.fetchLoanProducts()) {
			ProductResponseObject respobj = new ProductResponseObject();
			ProductData prd = new ProductData();
			prd.setProductId(gk.getProductId());
			prd.setDescription(gk.getDescription());
			prd.setProductType(gk.getProductType());
			prd.setShortDesc(gk.getShortDesc());
			prd.setAmountLimit(gk.getAmountLimit());
			prd.setAmountMin(gk.getAmountMin());
			prd.setAmountMax(gk.getAmountMax());
			prd.setAmountDefault(gk.getAmountDefault());
			prd.setSpouseInsurance(gk.getSpouseInsurance());
			prd.setInsLnamount(gk.getInsLnamount());
			prd.setDisbOTP(gk.getDisbOTP()); 
			prd.setTerm(gk.getTerm());
			prd.setFreq(gk.getFreq().replace("#", "~"));
			prd.setInsuranceProvider(gk.getInsuProvider().replace("#", "~"));
			prd.setInsurancePercentage(CommonConstants.INSURANCE_PER);
			prd.setProductType(gk.getProductType());
			prd.setProductStatus(gk.getProduct_status());
			prd.setLoanProdType(gk.getLoan_prod_type());
			prd.setDisbursementType(gk.getDisbursementType());
			prd.setMemInsurance(gk.getMemInsurance());
			prd.setFeeCharge(gk.getFeeCharge());
			prd.setGst(gk.getGst());
			prd.setConsentType(gk.getConsentType());
			prd.setProdPurpose(productPurposeList(gk.getProductId()));
			prd.setPayload(gk.getPayload()); 
			respobj.setProductDtls(prd);
			respObjList.add(respobj);
		}
		if (!respObjList.isEmpty()) {
			logger.debug("Product response object successfully generated for in service class");
		} else {
			logger.debug("Product response object generated empty in service class");
		}
		return respObjList;
	}

	public List<ProductPurpose> productPurposeList(String prodId) {
		List<ProductPurpose> pList = new ArrayList<>();
		for (GkLoanPurpose gk : dao.fetchProductPurpose(prodId)) {
			List<String> list = new ArrayList<>();
			ProductPurpose p = new ProductPurpose();
			p.setProductId(gk.getProductId());
			p.setPurpose(gk.getPurpose());
			p.setPurposeDesc(gk.getPurposeDesc());
			for (GkProductSubPurpose gkp : dao.fetchProdSubPurpose(gk.getProductId(), gk.getPurpose())) {
				list.add(gkp.getSubPurpose().replace(".", " "));
			}
			p.setProductSubPurpose(list);
			pList.add(p);
		}
		if (!pList.isEmpty()) {
			logger.debug("Product purpose successfully generated for in service class ");
		} else {
			logger.debug("Product purpose generated empty in service class ");
		}
		return pList;
	}

	public UserResponseObject fetchUserDesignation(String userId) {
		UserResponseObject respObject = new UserResponseObject();
		respObject.setUserRoles(dao.fetchUserRole(userId));
		return respObject;
	}

	public List<CustomerResponseObject> fetchCustomerDetails(String custId) {
		List<CustomerResponseObject> respObjList = new ArrayList<>();
		for (GkCustomerData gk : dao.fetchCustomerData(custId)) {
			GkCustomerData c = new GkCustomerData();
			CustomerResponseObject respObject = new CustomerResponseObject();
			c.setCustomerId(gk.getCustomerId());
			c.setCustomerName(gk.getCustomerName());
			c.setKendraId(gk.getKendraId());
			c.setGroupId(gk.getGroupId());
			c.setBranchId(gk.getBranchId());
			c.setBranchName(gk.getBranchName());
			c.setPrimaryType(gk.getPrimaryType());
			c.setPrimaryId(gk.getPrimaryId());
			c.setDob(gk.getDob());
			c.setMaritalStatus(gk.getMaritalStatus());
			c.setAddress(gk.getAddress());
			c.setBankAccNo(gk.getBankAccNo());
			c.setBankAccName(gk.getBankAccName());
			c.setBankBranchName(gk.getBankBranchName());
			c.setBankName(gk.getBankName());
			c.setBankIfscCode(gk.getBankIfscCode());
			c.setDepname(gk.getDepname());
			c.setDepDob(gk.getDepDob());
			c.setDepDocId(gk.getDepDocId());
			c.setDepDocType(gk.getDepDocType());
			c.setRecordType(gk.getRecordType());
			c.setCustQualify(gk.getCustQualify());
			c.setReligion(gk.getReligion());
			c.setCaste(gk.getCaste());
			c.setCity(gk.getCity());
			c.setVillage(gk.getVillage());
			c.setPincode(gk.getPincode());
			c.setMemRelation(gk.getMemRelation());
			c.setMobileNum(gk.getMobileNum());
			c.setPermAddLine1(gk.getPermAddLine1());
			c.setPermAddLine2(gk.getPermAddLine2());
			c.setPermanentState(gk.getPermanentState());
			c.setPermanentDistrict(gk.getPermanentDistrict());
			c.setPermanentVillageLocality(gk.getPermanentVillageLocality());
			c.setPermanentPincode(gk.getPermanentPincode());
			c.setPermanentTaluk(gk.getPermanentTaluk());
			c.setCommAddLIne1(gk.getCommAddLIne1());
			c.setCommAddLIne2(gk.getCommAddLIne2());
			c.setCommState(gk.getCommState());
			c.setCommDistrict(gk.getCommDistrict());
			c.setCommVillageLocality(gk.getCommVillageLocality());
			c.setCommPincode(gk.getCommPincode());
			c.setCommTaluk(gk.getCommTaluk());
			c.setCustVintage(gk.getCustVintage());
			c.setActivationDate(gk.getActivationDate());
			c.setCustStatus(gk.getCustStatus());
			respObject.setCustData(c);
			respObjList.add(respObject);
		}
		return respObjList;
	}

	@CircuitBreaker(name = "kendra_assignment", fallbackMethod = "fallbackMethod")
	public List<String> getAssignedKendraList(GkKendraAssignment kmid) throws URISyntaxException {
		URI url = new URI(kendraAssinedUrl);
		logger.info("Kendra assignment url is:{}", url);
		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<GkKendraAssignment> entity = new HttpEntity<>(kmid, header);
		List<String> kList = template.exchange(url, HttpMethod.POST, entity, List.class).getBody();
		return kList;
	}

	public List<String> fallbackMethod(Exception exc) {

		List<String> errMessge = new ArrayList<>();
		errMessge.add("error occured in the Kendra ssignment service API :" + exc);
		logger.error(exc);
		return errMessge;
	}

	public ResponseWrapper createBranchLatLongRecordsService(BranchLatlong branchLatLoang) {
		String qryResult = dao.insertBranchLatLongRecords(branchLatLoang);
		return mapToResponseWrapper(qryResult);
	}

	public String getTimeStamp() {
		return new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
	}

	public ResponseWrapper createKendraLatLongRecordsService(KendraLatLongEntity kendraLatLoang) {
		String qryResult = dao.insertKendraLatLongRecords(kendraLatLoang);
		return mapToResponseWrapper(qryResult);
	}

	public ResponseWrapper fetchBranchLatLong(String branchId) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String resp = mapper.writeValueAsString(dao.fetchBranchLatLong(branchId));
		return mapToResponseWrapper(resp);
	}

	public ResponseWrapper fetchKendrLatLong(int kendraId) throws JsonProcessingException {
		ObjectMapper mapper = new ObjectMapper();
		String resp = mapper.writeValueAsString(dao.fetchKendraLatLong(kendraId));
		return mapToResponseWrapper(resp);

	}

	private ResponseWrapper mapToResponseWrapper(String responseString) {
		ResponseWrapper resWrapper = new ResponseWrapper();
		ResponseBody responseBody = new ResponseBody();
		ResponseHeader resHeader = new ResponseHeader();
		Response response = new Response();
		responseBody.setResponseObj(responseString);
		response.setResponseBody(responseBody);
		resHeader.setResponseCode(CommonConstants.SUCCESS);
		resHeader.setResponseMessage(CommonConstants.RESP_SUCCESS_STATUS);
		response.setResponseHeader(resHeader);
		resWrapper.setApiResponse(response);
		return resWrapper;
	}

	/*
	 * Method to fetch basic KendraDetailsDto based on branchId.
	 * 
	 */
	@Transactional
	public Response fetchCdhKendraInfo(String branchId, String nextMeetingDt) {
		logger.debug("branchId{}", branchId);
		logger.debug("nextMeetingDt::{}", nextMeetingDt);
		Response response;
		try {
			List<KendraDetailsDto> kendraList;
			if (CommonUtils.checkStringNullOrEmpty(nextMeetingDt)) {
				kendraList = dao.fetchKendraInfoForBranchId(branchId);
				logger.debug("kendraList>>>><<<<<<<>>::{}", kendraList);
				
			}
			/*
			 * else if ("CASHIER".equalsIgnoreCase(roleName)) { kendraList =
			 * dao.fetchKendraInfoForBranchIdByRole(branchId); }
			 */
			else {
				kendraList = dao.fetchKendraInfo(branchId, nextMeetingDt);
				logger.debug("kendraList>>>>>>::{}", kendraList);
			}
			logger.debug("kendraList::{}", kendraList);
			String kendraRespObj = new ObjectMapper().writeValueAsString(kendraList);
			logger.debug("kendraRespObj::{}", kendraRespObj);
			ResponseHeader respHeader = ResponseHeader.builder().responseCode(CommonConstants.SUCCESS)
					.responseMessage("").build();
			logger.debug("respHeader::{}", respHeader);
			ResponseBody respBody = ResponseBody.builder().responseObj(kendraRespObj).build();
			logger.debug("respBody::{}", respBody);
			response = Response.builder().responseHeader(respHeader).responseBody(respBody).build();
		} catch (Exception exp) {
			logger.debug(CommonConstants.EXCEP_OCCURED, exp);
			ResponseHeader respHeader = ResponseHeader.builder().responseCode(CommonConstants.FAILURE)
					.responseMessage(CommonConstants.RESP_FAILURE_MSG).build();
			ResponseBody respBody = ResponseBody.builder().responseObj("").build();
			response = Response.builder().responseHeader(respHeader).responseBody(respBody).build();
		}
		logger.debug("Kendra Response object::{}", response);
		return response;
	}
}
