package com.iexceed.appzillonbanking.scheduler.service;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDateTime;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.iexceed.appzillonbanking.core.constants.CommonConstants;
import com.iexceed.appzillonbanking.scheduler.domain.ab.T24ServerDate;
import com.iexceed.appzillonbanking.scheduler.repository.ab.T24ServerDateRepository;

@Service
public class T24DateScheduler {

	private static final Logger logger = LogManager.getLogger(T24DateScheduler.class.getName());

	private static final RestTemplate template = new RestTemplate();

	@Autowired
	private T24ServerDateRepository t24ServerDateRepository;

	String response;

	@Value("${spring.t24date.url}")
	String t24dateUrl;

	/*
	@Scheduled(cron = "${ab.common.T24DateSchedulerCron}")
	public void scheduleTask() throws URISyntaxException, JsonProcessingException {
		try {
			logger.debug("DateT24 scheduler started at " + LocalDateTime.now().toString());
			updateDate();
			logger.debug("DateT24 scheduler ended at " + LocalDateTime.now().toString());
		} catch (Exception e) {
			logger.error("Exception in execute method of ExecuteScheduler in ", e);
		}
	}
*/
	public void updateDate() throws URISyntaxException {
		// TODO Auto-generated method stub
		//t24dateUrl = "http://localhost:9285/appzillonbankingcagl/application/loan/cdh/kendra/server-date";
		logger.debug("t24dateUrl:" + t24dateUrl);
		URI url = new URI(t24dateUrl);

		HttpHeaders header = new HttpHeaders();
		header.setContentType(MediaType.APPLICATION_JSON);
		header.set("appId", CommonConstants.APP_ID);
		header.set("interfaceId", "");
		header.set("userId", "");
		header.set("masterTxnRefNo", "");
		header.set("deviceId", "Android");

		JSONObject req = new JSONObject();

		logger.debug("Going to call URL.");
		logger.debug("URL is " + url);

		try {
			logger.debug("=====Deleting Existing all records: START======");
			try {
//				t24ServerDateRepository.deleteAll();
				deleteAllRecords();
			} catch (Exception e) {
				System.out.println("Error while deleteALl");
			}
			logger.debug("=====Deleting Existing all records: END======");

			RequestEntity<String> entity = new RequestEntity<>(header, HttpMethod.GET, url);
			System.out.println("Printing entity:" + entity);
			System.out.println("=======================");
			ResponseEntity<String> response = template.exchange(entity, String.class);
			logger.debug("Printing Response:" + response);

			JSONObject payload = new JSONObject(response);
			String serviceResponse = "";
			if (payload.has("body")) {
				serviceResponse = payload.getString("body");
			}
			JSONArray serviceResponseJson = new JSONArray(serviceResponse);
			for (int i = 0; i < serviceResponseJson.length(); i++) {
				String jsonObjStr = serviceResponseJson.get(i).toString();
				JSONObject serviceResponseJsonObj = new JSONObject(jsonObjStr);
				logger.debug("Printing serviceResponseJson:" + serviceResponseJson);
				System.out.println("Printing serviceResponseJson:" + serviceResponseJson);

				String curtDate = "", nextWorkDt = "";
				int updateddt = 0, id = 0;
				if (serviceResponseJsonObj.has("currentDate")) {
					curtDate = serviceResponseJsonObj.getString("currentDate");
				}
				if (serviceResponseJsonObj.has("nextWorkDate")) {
					nextWorkDt = serviceResponseJsonObj.getString("nextWorkDate");
				}
				if (serviceResponseJsonObj.has("updatedDate")) {
					updateddt = serviceResponseJsonObj.getInt("updatedDate");
				}
				if (serviceResponseJsonObj.has("id")) {
					id = serviceResponseJsonObj.getInt("id");
				}
				logger.debug("currentDate:" + curtDate);
				logger.debug("nextWorkDate:" + nextWorkDt);
				logger.debug("updatedDate:" + updateddt);

				T24ServerDate t24ServerDate = new T24ServerDate();
				t24ServerDate.setId(id);
				t24ServerDate.setCurrentDate(curtDate);
				t24ServerDate.setNextWorkDate(nextWorkDt);
				t24ServerDate.setUpdatedDate(updateddt+"");
				logger.debug("Final Request before insertion:" + t24ServerDate);
				System.out.println("Final Request before insertion:" + t24ServerDate);

				logger.debug("=====Saving Record======");
				System.out.println("=====Saving Record======");
				t24ServerDateRepository.save(t24ServerDate);
				logger.debug("=====Record Saved======");
				System.out.println("=====Record Saved======");
			}
		} catch (Exception exp) {
			System.out.println("Error Occurred :: " + exp);
			logger.error("Error Occurred :: " + exp);
		}
	}

	@Transactional
	public void deleteAllRecords() {
		t24ServerDateRepository.deleteAll();
	}
	
}
