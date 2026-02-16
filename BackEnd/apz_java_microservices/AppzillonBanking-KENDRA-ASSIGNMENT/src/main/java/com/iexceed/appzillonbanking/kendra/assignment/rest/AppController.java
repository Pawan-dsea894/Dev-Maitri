package com.iexceed.appzillonbanking.kendra.assignment.rest;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.iexceed.appzillonbanking.kendra.assignment.domain.KendraUserId;
import com.iexceed.appzillonbanking.kendra.assignment.entty.KendraManagementEntity;
import com.iexceed.appzillonbanking.kendra.assignment.service.AppService;

@RestController
public class AppController {

	@Autowired
	AppService ser;

	Logger logger = LoggerFactory.getLogger(AppController.class);

	@PostMapping("/fetch/assigned/kendra")
	public List<String> getList(@RequestBody KendraUserId userId) {
		logger.debug("kendra assignment API invoked.....");
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

}
