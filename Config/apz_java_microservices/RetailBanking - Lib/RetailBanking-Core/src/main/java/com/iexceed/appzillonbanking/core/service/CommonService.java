package com.iexceed.appzillonbanking.core.service;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import com.iexceed.appzillonbanking.core.domain.ab.TbAbmiLovMaster;
import com.iexceed.appzillonbanking.core.payload.LovMaintenanceRequest;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.repository.ab.TbAbmiLovRepository;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import com.iexceed.appzillonbanking.core.utils.Errors;
import com.iexceed.appzillonbanking.core.utils.FallbackUtils;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;

@Service
public class CommonService {

	private static final Logger logger = LogManager.getLogger(CommonService.class);

	@Autowired
	private TbAbmiLovRepository lovRepository;

	@CircuitBreaker(name = "fallback", fallbackMethod = "fetchLovDetailsFallback")
	public Response fetchLovDetails(String lovName, String language) {

		Response response = new Response();
		ResponseBody respBody = new ResponseBody();
		ResponseHeader respHeader = new ResponseHeader();

		Optional<TbAbmiLovMaster> lovMasterDomain = lovRepository.findByLovNameAndLanguage(lovName, language);

		if (lovMasterDomain.isPresent()) {
			String lovDtls = lovMasterDomain.get().getLovDtls();

			respBody.setResponseObj(lovDtls);
			CommonUtils.generateHeaderForSuccess(respHeader);
		} else {
			respBody.setResponseObj("");
			CommonUtils.generateHeaderForNoResult(respHeader);

		}
		response.setResponseHeader(respHeader);
		response.setResponseBody(respBody);

		logger.debug("End fetchLovDetails with response: {} ", response);

		return response;
	}

	@CircuitBreaker(name = "fallback", fallbackMethod = "fetchLovNameFallback")
	public Response fetchLovName(String appId) {
		Response response = new Response();
		ResponseBody respBody = new ResponseBody();
		ResponseHeader respHeader = new ResponseHeader();

		Gson gson = new Gson();

		List<String> lovNames = new ArrayList<>();

		Iterable<TbAbmiLovMaster> lovMasterDomainList = lovRepository.findAllByAppId(appId);

		if (lovMasterDomainList != null) {
			for (TbAbmiLovMaster lovMasterDomain : lovMasterDomainList) {

				if (!(lovNames.contains(lovMasterDomain.getLovName()))) {

					lovNames.add(lovMasterDomain.getLovName());
				}
			}

			CommonUtils.generateHeaderForSuccess(respHeader);
			respBody.setResponseObj(gson.toJson(lovNames));
		} else {
			CommonUtils.generateHeaderForNoResult(respHeader);
			respBody.setResponseObj("");
		}
		response.setResponseBody(respBody);
		response.setResponseHeader(respHeader);

		logger.debug("End fetchLovName with response: {} ", response);

		return response;
	}

	@CircuitBreaker(name = "fallback", fallbackMethod = "savelovdetailsFallback")
	public Response savelovdetails(LovMaintenanceRequest lovMaintenanceRequest) {
		Response response = new Response();
		ResponseBody respBody = new ResponseBody();
		ResponseHeader respHeader = new ResponseHeader();

		logger.debug("savelovdetails service start :: ");
		String userInput = lovMaintenanceRequest.getRequestObj().getUserInput();

		switch (userInput) {
		case "typing":

			Optional<TbAbmiLovMaster> lovMasterDomain = lovRepository.findByLovNameAndLanguage(
					lovMaintenanceRequest.getRequestObj().getLovName().toUpperCase(),
					lovMaintenanceRequest.getRequestObj().getLanguage());

			if (lovMaintenanceRequest.getRequestObj().getFlag().equals("ADD")
					&& (lovMaintenanceRequest.getRequestObj().getLovId().equals("")
							|| lovMaintenanceRequest.getRequestObj().getLovId() == null)) {

				int currVal = 0;
				int id = 0;

				if (lovMasterDomain.isPresent()) {
					CommonUtils.generateHeaderForDataExists(respHeader);
					respBody.setResponseObj(Errors.DATAEXISTS.getErrorMessage());
				} else {

					TbAbmiLovMaster tbAbmiLovMaster = new TbAbmiLovMaster();
					TbAbmiLovMaster tbAbmiLovMasterDomain = lovRepository.findTopByOrderByLovIdDesc();
					if (tbAbmiLovMasterDomain != null) {
						id = tbAbmiLovMasterDomain.getLovId() + 1;
					} else {
						id = currVal + 1;
					}
					tbAbmiLovMaster.setLovId(id);
					tbAbmiLovMaster.setLanguage(lovMaintenanceRequest.getRequestObj().getLanguage());
					tbAbmiLovMaster.setLovName(lovMaintenanceRequest.getRequestObj().getLovName().toUpperCase());
					tbAbmiLovMaster.setAppId(lovMaintenanceRequest.getRequestObj().getAppId());

					try {
						tbAbmiLovMaster.setLovDtls(new ObjectMapper()
								.writeValueAsString(lovMaintenanceRequest.getRequestObj().getLovDtls()));
					} catch (JsonProcessingException e) {
						logger.debug("saving lov details error..");
						respBody.setResponseObj(Errors.DATAUPDATIONFAILURE.getErrorMessage());
					}
					lovRepository.save(tbAbmiLovMaster);
					CommonUtils.generateHeaderForSuccess(respHeader);
					respBody.setResponseObj(Errors.DATAINSERTIONSUCCESS.getErrorMessage());
				}
			} else if (lovMaintenanceRequest.getRequestObj().getFlag().equals("UPDATE")
					&& (lovMaintenanceRequest.getRequestObj().getLovId().equals("")
							|| lovMaintenanceRequest.getRequestObj().getLovId() == null)) {

				if (!(lovMasterDomain.isPresent())) {
					CommonUtils.generateHeaderForNoResult(respHeader);
					respBody.setResponseObj(Errors.NORECORD.getErrorMessage());
				}

				else {
					int lovId = lovMasterDomain.get().getLovId();
					lovMasterDomain.get().setLovId(lovId);
					lovMasterDomain.get().setLanguage(lovMaintenanceRequest.getRequestObj().getLanguage());
					lovMasterDomain.get().setLovName(lovMaintenanceRequest.getRequestObj().getLovName());
					lovMasterDomain.get().setAppId(lovMaintenanceRequest.getRequestObj().getAppId());

					try {
						lovMasterDomain.get().setLovDtls(new ObjectMapper()
								.writeValueAsString(lovMaintenanceRequest.getRequestObj().getLovDtls()));
					} catch (JsonProcessingException e) {
						logger.debug("updating lov details error ..");
						respBody.setResponseObj(Errors.DATAUPDATIONFAILURE.getErrorMessage());
					}
					lovRepository.save(lovMasterDomain.get());
					logger.debug("lov data updated in TbAbmiLovMaster table...");

					CommonUtils.generateHeaderForSuccess(respHeader);
					respBody.setResponseObj(Errors.DATAINSERTIONSUCCESS.getErrorMessage());
				}

			}

			break;

		case "upload":
			ByteArrayInputStream arrayInputStream = null;

			List<TbAbmiLovMaster> tbAbmiLovMasterList = new ArrayList<>();
			List<String> lovNameList = new ArrayList<>();

			TbAbmiLovMaster tbAbmiLovMasterDomain = lovRepository.findTopByOrderByLovIdDesc();
			List<TbAbmiLovMaster> tbAbmiLovList = (List<TbAbmiLovMaster>) lovRepository.findAll();
			int failureCount = 0;
			int nonEmptySheetCount = 0;
			String key = null;
			String value = null;
			ObjectMapper objectMapper = new ObjectMapper();
			String newJsonArray = new JSONArray().toString();
			ArrayNode existingJsonArrayNode = null;

			ObjectNode newKeyValuePair = objectMapper.createObjectNode();
			

			try {
				if (lovMaintenanceRequest.getRequestObj().getBase64Value() != null
						&& !lovMaintenanceRequest.getRequestObj().getBase64Value().isEmpty()) {
					byte[] decodedData = Base64.getDecoder()
							.decode(lovMaintenanceRequest.getRequestObj().getBase64Value());

					arrayInputStream = new ByteArrayInputStream(decodedData);
					Workbook workbook = WorkbookFactory.create(arrayInputStream);

					int sheetsNum = workbook.getNumberOfSheets();
					int count = 0;
					if (tbAbmiLovMasterDomain != null) {
						count = tbAbmiLovMasterDomain.getLovId() + 1;
					}
					for (int i = 0; i < sheetsNum; i++) {
						Sheet sheet = workbook.getSheetAt(i);
						// Process the Excel sheet and convert data to objects
						// check if rowsNum is 2 for all the sheets , then enter into the loop
						int rowsNum = sheet.getPhysicalNumberOfRows();
						if (!validateRowNum(sheet)) {
							logger.debug("number of rows in the sheet is {}", rowsNum);
							for (int j = 0; j < rowsNum; j++) {
								Row row = sheet.getRow(j);
								nonEmptySheetCount++;
								if (validateLOVFile(row)) {
									if (CommonService.filterLovName(tbAbmiLovList, respBody, row, respHeader)) {
										TbAbmiLovMaster tbAbmiLovMaster = new TbAbmiLovMaster();
										tbAbmiLovMaster.setAppId(lovMaintenanceRequest.getRequestObj().getAppId());
										tbAbmiLovMaster.setLanguage(sheet.getSheetName());
										tbAbmiLovMaster.setLovId(count++);
										tbAbmiLovMaster.setLovName(row.getCell(0).getStringCellValue());
										key = row.getCell(1).getStringCellValue();
										value = row.getCell(2).getStringCellValue();
										existingJsonArrayNode = (ArrayNode) objectMapper.readTree(newJsonArray);
										newKeyValuePair.put("key", key);
										newKeyValuePair.put("value", value);
										existingJsonArrayNode.add(newKeyValuePair);
										tbAbmiLovMaster.setLovDtls(objectMapper.writeValueAsString(existingJsonArrayNode));
										tbAbmiLovMasterList.add(tbAbmiLovMaster);
										lovNameList.add(row.getCell(0).getStringCellValue());
									}
								} else {
									if (getCellValueAsString(row.getCell(0)).isEmpty()
											&& getCellValueAsString(row.getCell(1)).isEmpty() && getCellValueAsString(row.getCell(2)).isEmpty() ) {
										logger.debug("empty cells present in the file");
									} 
									else if (getCellValueAsString(row.getCell(0)).isEmpty()
											|| getCellValueAsString(row.getCell(1)).isEmpty()
											|| getCellValueAsString(row.getCell(2)).isEmpty()) {
										failureCount++;
										logger.debug("empty key / value present in the file");
									}

									// failureCount++;
								}
							}
						} 
					}
					 if (nonEmptySheetCount == 0) {
						failureCount++;
					}
					if (failureCount > 0) {
						respBody.setResponseObj("file content can not be empty");
						CommonUtils.generateHeaderForFailure(respHeader, "Please update the file");
						break;
					} else if (!tbAbmiLovMasterList.isEmpty()) {
						if (lovMaintenanceRequest.getRequestObj().getFileContent().equalsIgnoreCase("replace")) {
							lovRepository.deleteByLovNameIn(lovNameList);
						}
						lovRepository.saveAll(tbAbmiLovMasterList);
						CommonUtils.generateHeaderForSuccess(respHeader);
						respBody.setResponseObj(Errors.DATAINSERTIONSUCCESS.getErrorMessage());
					}
					workbook.close();
					arrayInputStream.close();

				} else {
					respBody.setResponseObj("");
					CommonUtils.generateHeaderForFailure(respHeader, "Base64 content is empty");
				}
			} catch (Exception e) {
				logger.error("file upload error {}", e.getMessage());
				respBody.setResponseObj("");
				CommonUtils.generateHeaderForFailure(respHeader, "Please check the file content and update");
			}
			break;

		default:
			break;
		}
		response.setResponseHeader(respHeader);
		response.setResponseBody(respBody);
		logger.debug("savelovdetails service end :: {}", response);
		return response;
	}

	private String getCellValueAsString(Cell cell) {
		if (cell != null) {
			switch (cell.getCellType()) {
			case STRING:
				return cell.getStringCellValue();
			case NUMERIC:
				// Handle numeric values if needed
				return String.valueOf(cell.getNumericCellValue());
			// Add more cases as needed
			default:
				return "";
			}
		}
		return "";
	}

	private static boolean validateRowNum(Sheet sheet) {
		int rowCount = 0;
		for (Row row : sheet) {
			if (rowCount == 0) {
				// Skip the header row
				rowCount++;
				continue;
			}
			rowCount++;
		}
		return rowCount <= 1;
	}

	public static boolean filterLovName(List<TbAbmiLovMaster> tbAbmiLovList, ResponseBody respBody, Row row,
			ResponseHeader respHeader) {
		List<TbAbmiLovMaster> filteredList = tbAbmiLovList.stream()
				.filter(lovMaster -> lovMaster.getLovName().equals(row.getCell(0).getStringCellValue()))
				.collect(Collectors.toList());

		if (!filteredList.isEmpty()) {
			respBody.setResponseObj("data with same lov name is already present in the table");
			CommonUtils.generateHeaderForFailure(respHeader, "lov name is already present");
			return false;
		}
		return true;
	}

	public static boolean validateLOVFile(Row row) {
		return ((null != row.getCell(0)) && (null != row.getCell(1)) &&  (null != row.getCell(2)) && (row.getRowNum() != 0)
				&& !CommonUtils.isNullOrEmpty(row.getCell(0).getStringCellValue())
				&& !CommonUtils.isNullOrEmpty(row.getCell(1).getStringCellValue())
				&& !CommonUtils.isNullOrEmpty(row.getCell(2).getStringCellValue()));

	}

	public Response savelovdetailsFallback(LovMaintenanceRequest lovMaintenanceRequest, Exception e) {
		logger.error("savelovdetailsFallback fallback error : ", e);
		return FallbackUtils.genericFallback();
	}

	public Response fetchLovNameFallback(String appId, Exception e) {
		logger.error("fetchLovNameFallback fallback error : ", e);
		return FallbackUtils.genericFallback();
	}

	public Response fetchLovDetailsFallback(String lovName, String language, Exception e) {
		logger.error("fetchLovDetailsFallback fallback error : ", e);
		return FallbackUtils.genericFallback();
	}

}
