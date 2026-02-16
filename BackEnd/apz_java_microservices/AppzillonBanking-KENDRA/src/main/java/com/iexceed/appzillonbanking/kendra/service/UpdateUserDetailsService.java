package com.iexceed.appzillonbanking.kendra.service;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.kendra.domain.cus.LocationMappingDetails;
import com.iexceed.appzillonbanking.kendra.domain.cus.TbAshsUser;
import com.iexceed.appzillonbanking.kendra.domain.cus.TbAsmiUser;
import com.iexceed.appzillonbanking.kendra.domain.cus.TbAsmiUserDevices;
import com.iexceed.appzillonbanking.kendra.domain.cus.TbAsmiUserDevicesPK;
import com.iexceed.appzillonbanking.kendra.domain.cus.TbAsmiUserRole;
import com.iexceed.appzillonbanking.kendra.payload.UserDetailsUpdateRequest;
import com.iexceed.appzillonbanking.kendra.repository.cus.ApplicationMasterRepository;
import com.iexceed.appzillonbanking.kendra.repository.cus.ApplicationWorkflowRepository;
import com.iexceed.appzillonbanking.kendra.repository.cus.TbAshsUserRepo;
import com.iexceed.appzillonbanking.kendra.repository.cus.TbAsmiUserDeviceRepo;
import com.iexceed.appzillonbanking.kendra.repository.cus.TbAsmiUserRepo;
import com.iexceed.appzillonbanking.kendra.repository.cus.TbAsmiUserRoleRepo;
import com.iexceed.appzillonbanking.kendra.repository.cus.TbUaumLocationDetailsRepo;
import com.iexceed.appzillonbanking.kendra.service.UpdateUserDetailsService;

@Service
public class UpdateUserDetailsService {

	private static final Logger logger = LogManager.getLogger(UpdateUserDetailsService.class);

	public static final String EXCEPTION_MSG = "Something went wrong, Please try again!! :";
	public static final String EXCEPTION_OCCURED = "Exception occurred :";

	@Autowired
	private TbAsmiUserRepo tbAsmiUserRepo;

	@Autowired
	private TbAsmiUserRoleRepo tbAsmiUserRoleRepo;
	
	@Autowired
	private TbAshsUserRepo tbAshsUserRepo;

	@Autowired
	private TbUaumLocationDetailsRepo tbUaumLocationDetailsRepo;
	
	@Autowired
	private TbAsmiUserDeviceRepo tbAsmiUserDeviceRepo;
	
	@Autowired
	private ApplicationMasterRepository applicationMasterRepository;
	
	@Autowired
	private ApplicationWorkflowRepository applicationWorkflowRepository;

	public Response updateUserDetailRecord(UserDetailsUpdateRequest apiRequest) {
		logger.debug("Inside UpdateUserDetailRecord service layer of updateUserDetail");
		logger.debug("Printing Incoming apiRequest:" + apiRequest);
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();

		try {
			String userId = apiRequest.getRequestObj().getUserId();
			String[] rolesId = apiRequest.getRequestObj().getRoleId().split("~");
			if (userId != null) {
				Optional<TbAsmiUser> userDetailRec = tbAsmiUserRepo.findByUserId(userId);
				Optional<List<TbAsmiUserRole>> userRoleRec = tbAsmiUserRoleRepo.findByUserId(userId);
				Optional<LocationMappingDetails> userLocRec = tbUaumLocationDetailsRepo.findByUserId(userId);

				if (userDetailRec.isPresent()) {
					
					TbAsmiUser userDetUpdate = userDetailRec.get();
					
					TbAshsUser userHisUpdate = new TbAshsUser();
					
					ObjectMapper map = new ObjectMapper();
					String userDet = map.writeValueAsString(userDetUpdate);
					
					userHisUpdate = map.readValue(userDet, TbAshsUser.class);
					
					tbAshsUserRepo.save(userHisUpdate);
					
					
					int version_num = userDetUpdate.getVersionNum();
					
					userDetUpdate.setUserLocked(apiRequest.getRequestObj().getUserLocked());
					userDetUpdate.setUserLockTs(new Timestamp(System.currentTimeMillis()));
					userDetUpdate.setCheckerTs(new Timestamp(System.currentTimeMillis()));
					userDetUpdate.setMakerTs(new Timestamp(System.currentTimeMillis()));
					userDetUpdate.setUserActive(apiRequest.getRequestObj().getUserStat());
					userDetUpdate.setUserName(apiRequest.getRequestObj().getEmpName());
					userDetUpdate.setUserEml1(apiRequest.getRequestObj().getOffMailId());
					userDetUpdate.setUserPhone1(apiRequest.getRequestObj().getPhNum());
					userDetUpdate.setVersionNum(version_num + 1);

					if (userLocRec.isPresent()) {
						try {
							String locArrayString = apiRequest.getRequestObj().getLocDetails();
							logger.debug("locArrayString" + locArrayString);
							JSONArray locArrayjson = new JSONArray(locArrayString);
							logger.debug("locArrayjson" + locArrayjson);

							// this code is for RPC CRT user where multiple Region can come
							String regionIds = "";
							String regionNames = "";

							for (int i = 0; i < locArrayjson.length(); i++) {
								JSONObject locjsonregion = locArrayjson.getJSONObject(i);
								String locationregionRole = locjsonregion.getString("roleId");
								logger.debug("Processing location: " + locjsonregion.toString());
								if (locationregionRole.equalsIgnoreCase("RPC")
										|| locationregionRole.equalsIgnoreCase("CRT")) {
									logger.debug("This is CRT/RPC Case");
									regionIds = regionIds + locjsonregion.getString("locID") + ",";
									regionNames = regionNames + locjsonregion.getString("locName") + ",";
								}
							}
							logger.debug("regionIds :" + regionIds.toString());
							logger.debug("regionNames :" + regionNames.toString());

							// This logic is for BRANCH ID
							JSONObject locjson = locArrayjson.getJSONObject(0);
							logger.debug("locjson" + locjson.toString());
							String branchId = locjson.getString("locID");
							String locName = locjson.getString("locName");
							String roleId = locjson.getString("roleId");
							userDetUpdate.setAddInfo2(branchId);
							userDetUpdate.setAddInfo1(roleId);
							// Notification flag colum is addinfo3
							userDetUpdate.setNotificationFlag(locName);

							// This logic is for RPC or CRT

							if (!regionIds.isEmpty()) {

								userDetUpdate.setAddInfo2(trimLastComma(regionIds, ","));
								userDetUpdate.setNotificationFlag(trimLastComma(regionNames, ","));
							}

							logger.debug("final Values set in the Additional Info are " + userDetUpdate.getAddInfo1()
									+ userDetUpdate.getAddInfo2() + userDetUpdate.getNotificationFlag());

						} catch (Exception e) {
							logger.debug(e.getMessage());
						}
					}
					// Amit Change Start. Going to check the role 1 also based on that only will update the user 
					
					if(rolesId.length>=1)
					{
						userDetUpdate.setAddInfo1(rolesId[0]);
						logger.debug("role ID going to set finally is "+ userDetUpdate.getAddInfo1());
					}
					// Amit Changes End

					tbAsmiUserRepo.save(userDetUpdate);

					List<TbAsmiUserRole> userRoleRowsPresent = userRoleRec.get();

					for (TbAsmiUserRole userRole : userRoleRowsPresent) {
						tbAsmiUserRoleRepo.delete(userRole);
					}

					TbAsmiUserRole userRoleUpdate = new TbAsmiUserRole();
					userRoleUpdate.setAppId("APZCBO");
					userRoleUpdate.setCreateUserId("admin");
					userRoleUpdate.setUserId(userId);
					userRoleUpdate.setVersionNum(1);
					userRoleUpdate.setCreateTs(new Timestamp(System.currentTimeMillis()));

					for (int i = 0; i < rolesId.length; i++) {
						userRoleUpdate.setRoleId(rolesId[i]);
						tbAsmiUserRoleRepo.save(userRoleUpdate);
					}

					if(rolesId.length == 2) {
						
						
						TbAsmiUserDevices tbAsmiUserDevices = new TbAsmiUserDevices();
						TbAsmiUserDevicesPK tbAsmiUserDevicesPK = new TbAsmiUserDevicesPK();
						
						tbAsmiUserDevicesPK.setAppId("APZCBO");
						tbAsmiUserDevicesPK.setDeviceId("WEB");
						tbAsmiUserDevicesPK.setUserId(userId);
						
						tbAsmiUserDevices.setId(tbAsmiUserDevicesPK);
						tbAsmiUserDevices.setNotifRegId(null);
						tbAsmiUserDevices.setCreateUserId("ADMIN");
						tbAsmiUserDevices.setDeviceStatus("ACTIVE");
						tbAsmiUserDevices.setVersionNo(1);
						tbAsmiUserDevices.setCreateTs(new Timestamp(System.currentTimeMillis()));
						
						tbAsmiUserDeviceRepo.save(tbAsmiUserDevices);
					}

					if (userLocRec.isPresent()) {
						
						LocationMappingDetails mappingDet = userLocRec.get();
						
						mappingDet.setLocationDetails(apiRequest.getRequestObj().getLocDetails());
						mappingDet.setLocationType(apiRequest.getRequestObj().getLocTypes());
						mappingDet.setDepartment(apiRequest.getRequestObj().getDept());
						mappingDet.setHrisLocation(apiRequest.getRequestObj().getHrisLocID());
						mappingDet.setReportManagerId(apiRequest.getRequestObj().getRepManagerID());
						mappingDet.setReportManagerName(apiRequest.getRequestObj().getRepManagerName());
						mappingDet.setSubFunction(apiRequest.getRequestObj().getSubFunc());
						
						tbUaumLocationDetailsRepo.save(mappingDet);
					} else {
						
						LocationMappingDetails mappingDet = new LocationMappingDetails();
						
						mappingDet.setLocationType(apiRequest.getRequestObj().getLocTypes());
						mappingDet.setLocationDetails(apiRequest.getRequestObj().getLocDetails());
						mappingDet.setUserId(userId);
						mappingDet.setDepartment(apiRequest.getRequestObj().getDept());
						mappingDet.setHrisLocation(apiRequest.getRequestObj().getHrisLocID());
						mappingDet.setReportManagerId(apiRequest.getRequestObj().getRepManagerID());
						mappingDet.setReportManagerName(apiRequest.getRequestObj().getRepManagerName());
						mappingDet.setSubFunction(apiRequest.getRequestObj().getSubFunc());
						
						tbUaumLocationDetailsRepo.save(mappingDet);
					}

					respBody.setResponseObj("User Updation successfull.");
					CommonUtils.generateHeaderForSuccess(respHeader);
					respHeader.setResponseMessage("Fetching Success");
					response.setResponseBody(respBody);
					response.setResponseHeader(respHeader);

				} else {
					logger.error("No User Present !!!");
					respBody.setResponseObj("User is not present in Database");
					CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
					respHeader.setResponseMessage(EXCEPTION_OCCURED + "User is not present in Database");
					response.setResponseBody(respBody);
					response.setResponseHeader(respHeader);
				}

			}
		} catch (Exception e) {
			logger.error(EXCEPTION_OCCURED + e);
			respBody.setResponseObj(e.getMessage().toString());
			CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
			respHeader.setResponseMessage(EXCEPTION_OCCURED + e.getMessage().toString());
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
		}
		String roleId = apiRequest.getRequestObj().getRoleId();
		String userRole = null;

		if (roleId != null && !roleId.isBlank()) {
		    userRole = roleId.toUpperCase().contains("DEO") ? "DEO" : roleId.trim();
		}
		logger.debug("Extracted UserRole: {}", userRole);

		if ("DEO".equalsIgnoreCase(userRole)) {

			String curDeoUserId = apiRequest.getRequestObj().getUserId();
			String branchId = apiRequest.getRequestObj().getBranchId();
			String meetingDate = apiRequest.getRequestObj().getMeetingDate();

			logger.debug("Current UserId: {}, BranchId: {}, MeetingDate: {}",
					curDeoUserId != null ? curDeoUserId : "N/A", branchId != null ? branchId : "N/A",
					meetingDate != null ? meetingDate : "N/A");

			if (branchId != null && meetingDate != null) {
				List<String> kmidList = applicationMasterRepository.findDistinctKmIdsWithDeo(branchId,
						LocalDate.parse(meetingDate));

				if (!kmidList.isEmpty()) {
					logger.debug("KMID list for DEO in ADD_INFO column: {}", kmidList);
					int updatedCountRemarks = applicationMasterRepository.updateRemarksFromAddInfo(branchId,
							LocalDate.parse(meetingDate), kmidList);

					logger.info("Updated {} records: copied ADD_INFO -> REMARKS", updatedCountRemarks);
					int updatedCount = applicationMasterRepository.updateDeoRecords(curDeoUserId, branchId,
							LocalDate.parse(meetingDate), kmidList);

					logger.info("Updated {} records: set CREATED_BY and KMID = {}, ADD_INFO = DEO~{}", updatedCount,
							curDeoUserId, curDeoUserId);

					List<String> applicationIdList = applicationMasterRepository.findApplicationIdsWithDeo(branchId,
							LocalDate.parse(meetingDate));
					logger.debug("ApplicationId list for DEO in ADD_INFO column: {}", applicationIdList);
					int updatedWorkflowCount = applicationWorkflowRepository
							.updateCreatedByForApplicationIds(curDeoUserId, applicationIdList);
					logger.info("Updated {} workflow records: set CREATED_BY = {}", updatedWorkflowCount, curDeoUserId);

				} else {
					logger.debug("No matching records found to move into history table.");
				}

			} else {
				logger.debug("No KMIDs found for BranchId: {} and MeetingDate: {}", branchId, meetingDate);
			}
		} else {
			logger.warn("BranchId or MeetingDate is null, skipping DB query.");
		}

		return response;
	}
	public String trimLastComma(String str,String lastchar) {
	    return (str != null && str.endsWith(lastchar)) ? str.substring(0, str.length() - 1) : str;
	}
}
