package com.iexceed.appzillonbanking.kendra.rest;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.iexceed.appzillonbanking.kendra.domain.cus.KendraManagementEntity;
import com.iexceed.appzillonbanking.kendra.domain.cus.KendraUserId;
import com.iexceed.appzillonbanking.kendra.service.AppService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(description = "/fetch/assigned", name = "ASSIGNEDKENDRA")
public class AppController {

	@Autowired
	AppService ser;

	Logger logger = LoggerFactory.getLogger(AppController.class);

	@ApiResponses({ @ApiResponse(responseCode = "200", description = "AppzillonBanking API reachable"),
		@ApiResponse(responseCode = "408", description = "Service Timed Out"),
		@ApiResponse(responseCode = "500", description = "Internal Server Error"),
		@ApiResponse(responseCode = "404", description = "AppzillonBanking not reachable") })
	@Operation(summary = "Assigned Kendra Fetch", description = "API to Fetch Assigned Kendra Record from DB")
	@PostMapping("/fetch/assigned/kendra")
	public List<String> getList(@RequestBody KendraUserId userId) {
		logger.debug("kendra assignment API invoked.....");

		String excludedFlag = "N";
		try {
			excludedFlag = userId.getExcludedFlag();
			logger.debug("Excluded flag is " + excludedFlag);
		} catch (Exception e) {
			logger.debug("Exception is " + e.getMessage());
		}

		if (excludedFlag == null || excludedFlag.equalsIgnoreCase("N")) {

			logger.debug("This is the case when Temp Assinged list comes");

			List<String> kList = new ArrayList<String>();
			for (KendraManagementEntity entity : ser.getList(userId.getUserId())) {
				kList.add(entity.getKendraId());
			}
			if(!kList.isEmpty())
			{
				logger.debug("new Kendra's : "+kList+" assigned to the usser id "+userId.getUserId());
			}
			else
			{
				logger.debug("No new Kendra's are assigned to the user id :"+userId.getUserId());
			}
			return kList;
			
		}
		else
		{
			logger.debug("THis is the case when Temp Assinged list comes");
			
			List<String> kList = new ArrayList<String>();
			for (KendraManagementEntity entity : ser.getExculedList(userId.getUserId())) {
				kList.add(entity.getKendraId());
			}
			if(!kList.isEmpty())
			{
				logger.debug("new Kendra's : "+kList+" assigned to the usser id "+userId.getUserId());
			}
			else
			{
				logger.debug("No new Kendra's are assigned to the user id :"+userId.getUserId());
			}
			return kList;
			
		}
		
		
	
	}

}
