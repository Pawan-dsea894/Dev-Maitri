package com.iexceed.appzillonbanking.kendra.service;

import java.util.*;

import com.iexceed.appzillonbanking.kendra.domain.cus.OfficeData;
import com.iexceed.appzillonbanking.kendra.payload.BranchDetailsFetchRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.kendra.payload.OfficeDetailsFetchRequest;
import com.iexceed.appzillonbanking.kendra.repository.cus.TOfficeDetailsRepo;

@Service
public class FetchOfficeDetailsService {

	private static final Logger logger = LogManager.getLogger(FetchOfficeDetailsService.class);

	public static final String EXCEPTION_MSG = "Something went wrong, Please try again!! :";
	public static final String EXCEPTION_OCCURED = "Exception occurred :";

	@Autowired
	private TOfficeDetailsRepo tOffRepo;

	public Response fetchOfficeDetailRecords(OfficeDetailsFetchRequest apiRequest) {
		logger.debug("Inside fetchOfficeDetailRecord service layer of fetchOfficeDetail");
		logger.debug("Printing Incoming apiRequest:" + apiRequest);
		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();

		List<String> branchData = new ArrayList<>();
		List<String> areaData = new ArrayList<>();
		List<String> zoneData = new ArrayList<>();
		List<String> regionData = new ArrayList<>();
		List<String> stateData = new ArrayList<>();
		LinkedHashMap<String, Object> resp = new LinkedHashMap<>();

		try {
			String locationType = apiRequest.getRequestObj().getLocationType();

			if (locationType != null) {
				String[] locations = locationType.split("\\|");

				for (int i = 0; i < locations.length; i++) {
					if (locations[i].equalsIgnoreCase("Branch")) {
						branchData = tOffRepo.findBranchNames();
						resp.put("Branch", branchData);
					} else if (locations[i].equalsIgnoreCase("Area")) {
						areaData = tOffRepo.findAreaNames();
						resp.put("Area", areaData);
					} else if (locations[i].equalsIgnoreCase("Zone")) {
						zoneData = tOffRepo.findZoneNames();
						resp.put("Zone", zoneData);
					} else if (locations[i].equalsIgnoreCase("Region")) {
						regionData = tOffRepo.findRegionNames();
						resp.put("Region", regionData);
					} else if (locations[i].equalsIgnoreCase("State")) {
						stateData = tOffRepo.findStateNames();
						resp.put("State", stateData);
				 	}
				}

				if (resp.isEmpty() || locationType.contains("All")) {
					branchData = tOffRepo.findBranchNames();
					resp.put("Branch", branchData);
					areaData = tOffRepo.findAreaNames();
					resp.put("Area", areaData);
					zoneData = tOffRepo.findZoneNames();
					resp.put("Zone", zoneData);
					regionData = tOffRepo.findRegionNames();
					resp.put("Region", regionData);
					stateData = tOffRepo.findStateNames();
					resp.put("State", stateData );
				}

				String finalResp = new Gson().toJson(resp, LinkedHashMap.class);
				respBody.setResponseObj(finalResp);
				CommonUtils.generateHeaderForSuccess(respHeader);
				respHeader.setResponseMessage("Fetching Success");
				response.setResponseBody(respBody);
				response.setResponseHeader(respHeader);
			}
		} catch (Exception e) {
			logger.error(EXCEPTION_OCCURED + e);
			respBody.setResponseObj(e.getMessage().toString());
			CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
			respHeader.setResponseMessage(EXCEPTION_OCCURED + e.getMessage().toString());
			response.setResponseBody(respBody);
			response.setResponseHeader(respHeader);
		}
		return response;
	}

	public Response fetchBranchDetails(BranchDetailsFetchRequest apiRequest) {
		logger.debug("Inside fetchBranchDetails service layer");
		logger.debug("Incoming apiRequest: {}", apiRequest);

		Response response = new Response();
		ResponseHeader respHeader = new ResponseHeader();
		ResponseBody respBody = new ResponseBody();

		LinkedHashMap<String, Object> resp = new LinkedHashMap<>();

		try {
			String locationType = apiRequest.getRequestObj().getLocationType();
			String locationIdStr  = apiRequest.getRequestObj().getLocationId();
			
			logger.debug("locationType: {}, locationId: {}", locationType, locationIdStr );
			
			if (locationType == null || locationIdStr  == null || locationType.isBlank() || locationIdStr.isBlank()) {
				CommonUtils.generateHeaderForFailure(respHeader, "Invalid request: locationType or locationId is missing");
				respHeader.setResponseMessage("Invalid request parameters");
				respBody.setResponseObj("locationType and locationId are required");
				response.setResponseHeader(respHeader);
				response.setResponseBody(respBody);
				return response;
			}

			List<String> locationId = Arrays.stream(locationIdStr.split(","))
			        .map(String::trim)
			        .map(id -> id.replace("\"", ""))
			        .filter(id -> !id.isEmpty())
			        .toList();

			logger.debug("Parsed locationId: {}", locationId);
			
			List<OfficeData> branchList = tOffRepo.findByLocation(locationId);
			logger.debug("Fetched records from t24_office: {}", branchList.size());

			if (branchList.isEmpty()) {
				CommonUtils.generateHeaderForFailure(respHeader, "No records found");
				respHeader.setResponseMessage("No branches found for given location");
				respBody.setResponseObj("[]");
			} else {
				resp.put("locationType", locationType);
				resp.put("locationId", locationId);
				resp.put("branchCount", branchList.size());
				resp.put("branchDetails", branchList);			
				Gson gson = new GsonBuilder().serializeNulls().create();
				 String finalResp = gson.toJson(resp,LinkedHashMap.class);
				//String finalResp = new Gson().toJson(resp, LinkedHashMap.class);			
				respBody.setResponseObj(finalResp);
				CommonUtils.generateHeaderForSuccess(respHeader);
				respHeader.setResponseMessage("Branch details fetched successfully");
			}
			response.setResponseHeader(respHeader);
			response.setResponseBody(respBody);
		} catch (Exception e) {
			logger.error("Exception occurred in fetchBranchDetails: ", e);
			respBody.setResponseObj(EXCEPTION_MSG + e.getMessage());
			CommonUtils.generateHeaderForFailure(respHeader, EXCEPTION_OCCURED);
			respHeader.setResponseMessage(EXCEPTION_OCCURED + e.getMessage());
			response.setResponseHeader(respHeader);
			response.setResponseBody(respBody);
		}
		return response;
	}
}
