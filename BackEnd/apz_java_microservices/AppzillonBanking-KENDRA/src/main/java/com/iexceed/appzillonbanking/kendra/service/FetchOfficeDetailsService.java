package com.iexceed.appzillonbanking.kendra.service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
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
}
