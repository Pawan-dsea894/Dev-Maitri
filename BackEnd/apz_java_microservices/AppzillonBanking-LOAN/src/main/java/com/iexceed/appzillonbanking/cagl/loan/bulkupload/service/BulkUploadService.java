package com.iexceed.appzillonbanking.cagl.loan.bulkupload.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.iexceed.appzillonbanking.cagl.loan.bulkupload.domain.ab.BulkRecordsEntity;
import com.iexceed.appzillonbanking.cagl.loan.bulkupload.domain.ab.BulkUploadEntity;
import com.iexceed.appzillonbanking.cagl.loan.bulkupload.payload.BulkUploadRequest;
import com.iexceed.appzillonbanking.cagl.loan.bulkupload.payload.BulkUploadRequestFields;
import com.iexceed.appzillonbanking.cagl.loan.bulkupload.payload.FetchBulkUploadRequest;
import com.iexceed.appzillonbanking.cagl.loan.bulkupload.payload.FetchExcelDataRequest;
import com.iexceed.appzillonbanking.cagl.loan.bulkupload.repository.ab.BulkRecordsRepo;
import com.iexceed.appzillonbanking.cagl.loan.bulkupload.repository.ab.BulkUploadRepo;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.ApplicationMaster;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbUalnLoanDtls;
import com.iexceed.appzillonbanking.cagl.loan.payload.PopulateapplnWFRequest;
import com.iexceed.appzillonbanking.cagl.loan.payload.PopulateapplnWFRequestFields;
import com.iexceed.appzillonbanking.cagl.loan.payload.WorkFlowDetails;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.ApplicationMasterRepository;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.ApplicationWorkflowRepository;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.TbAbobCommonCodeRepository;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.TbUalLoanDtlsRepository;
import com.iexceed.appzillonbanking.cagl.loan.service.LoanService;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;


@Service
public class BulkUploadService {
	
	private static final Logger logger = LogManager.getLogger(BulkUploadService.class);
	
	public static final String INVALID_BASE64 = "Incoming BASE64 is Invalid";
	private static final Pattern INVALID_CHAR_PATTERN = Pattern.compile("[^0-9.]");
	private static final Pattern NEGATIVE_AMOUNT_PATTERN = Pattern.compile("^-\\d+\\.?\\d*$");
	
	@Autowired
	private BulkUploadRepo bulkUploadRepo;
	
	@Autowired
	private BulkRecordsRepo bulkRecordsRepo;
	
	@Autowired
	private LoanService loanService;
	
	@Autowired
	private ApplicationMasterRepository applicationMasterRepository;
	
	@Autowired
	private TbUalLoanDtlsRepository loanDtlRepository;
	
	@Autowired
	private ApplicationWorkflowRepository applnWfRepository;
	
	@Autowired
	private TbAbobCommonCodeRepository tbAbobCommonCodeRepository;
	
	@Autowired
	private ObjectMapper objectMapper; 
	
	@PersistenceContext
	private final EntityManager entityManager;

	public BulkUploadService(EntityManager entityManager) {
		this.entityManager = entityManager;
	}

	public Response saveExcelData(BulkUploadRequest apiRequest) {
		logger.debug("Printing BulkUploadRequest " + apiRequest);
		
		long start = System.currentTimeMillis();
		
	    logger.debug("====Inside saveExcelData service====");
	    //logger.debug("Incoming Request: {}", apiRequest);

	    Response response = new Response();
	    ResponseHeader respHeader = new ResponseHeader();
	    ResponseBody respBody = new ResponseBody();
	    JSONObject responseObject = new JSONObject();

	    BulkUploadRequestFields requestObj = apiRequest.getRequestObj();
	    String base64Data = requestObj.getBase64Data();

	    if (!isValidBase64(base64Data)) {
	        return generateFailureResponse(respHeader, respBody, response, INVALID_BASE64);
	    }

	    String docId = CommonUtils.generateRandomNum().toString();
	    String createdAt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(new Date());

	    JSONObject validationResponse = convertBase64ToExelAndStoreInDB(base64Data, docId, apiRequest.getUserId(),apiRequest.getRequestObj().getUserId(),apiRequest.getRequestObj().getUserRole(),apiRequest.getRequestObj().getAppVersion(),apiRequest.getRequestObj().getRemarks(),apiRequest.getRequestObj().getUserName());
	    if ("false".equalsIgnoreCase(validationResponse.optString("status"))) {
	        return generateFailureResponse(respHeader, respBody, response, validationResponse.optString("message"));
	    }

	    // Insert Bulk Upload Entity if validation passes
	    BulkUploadEntity bulkUploadEntity = new BulkUploadEntity();
	    bulkUploadEntity.setDocId(docId);
	    bulkUploadEntity.setDocName(requestObj.getDocName());
	    bulkUploadEntity.setCreatedAt(createdAt);
	    bulkUploadEntity.setUserId(requestObj.getUserId());
	    bulkUploadEntity.setUploadedBy(requestObj.getUploadedBy());
	    bulkUploadEntity.setTypeOfUpload(requestObj.getTypeOfUpload());
	    bulkUploadEntity.setStatus("SUCCESS");

	    bulkUploadRepo.save(bulkUploadEntity);

	    responseObject.put("docId", docId);
	    responseObject.put("message", "Record inserted successfully!!!");
	    responseObject.put("status", "Success");

	    respBody.setResponseObj(responseObject.toString());
	    CommonUtils.generateHeaderForSuccess(respHeader);
	    response.setResponseBody(respBody);
	    response.setResponseHeader(respHeader);

	    logger.debug("Execution time for methodCall(): {} ms", (System.currentTimeMillis() - start));
	    
	    return response;
	}

	private Response generateFailureResponse(ResponseHeader respHeader, ResponseBody respBody, Response response, String message) {
	    logger.error("Validation failed: {}", message);
	    respBody.setResponseObj(message);
	    CommonUtils.generateHeaderForFailure(respHeader, message);
	    response.setResponseBody(respBody);
	    response.setResponseHeader(respHeader);
	    return response;
	}
	
	private JSONObject convertBase64ToExelAndStoreInDB(String base64Data, String docId, String userid,String userIdCRT,String userRole, String appVersion,String remarks,String userName) {
	    logger.debug("===== Inside convertBase64ToExelAndStoreInDB service =====");
	    
	    long start = System.currentTimeMillis();

	    JSONObject statusWithMessageVal = new JSONObject();
	    List<String> expectedTitles = Arrays.asList("CUSTOMER ID", "LOAN ID", "APPROVED/REJECTED", "APPROVED AMOUNT", "REMARKS", "FOIR", "APR");

	    // Decode Base64
	    byte[] decodedBytes;
	    try {
	        decodedBytes = Base64.getDecoder().decode(base64Data);
	    } catch (IllegalArgumentException e) {
	        logger.error("Invalid Base64 data", e);
	        statusWithMessageVal.put("status", "false");
	        statusWithMessageVal.put("message", "Invalid Base64 data");
	        return statusWithMessageVal;
	    }

	    try (InputStream inputStream = new ByteArrayInputStream(decodedBytes);
	         Workbook workbook = new XSSFWorkbook(inputStream)) {

	        Sheet sheet = workbook.getSheetAt(0);
	        Iterator<Row> rowIterator = sheet.iterator();

	        // Check if file has any rows
	        if (!rowIterator.hasNext()) {
	            logger.error("No rows found in the sheet.");
	            statusWithMessageVal.put("status", "false");
	            statusWithMessageVal.put("message", "No rows found in the sheet..Excel is empty");
	            return statusWithMessageVal;
	        }

	        // Validate headers
	        Row titleRow = rowIterator.next();
	        for (int i = 0; i < expectedTitles.size(); i++) {
	            Cell cell = titleRow.getCell(i);
	            if (cell == null || !expectedTitles.get(i).equals(cell.toString().trim())) {
	                logger.error("Error: Column titles do not match expected titles.");
	                statusWithMessageVal.put("status", "false");
	                statusWithMessageVal.put("message", "Column titles do not match expected titles");
	                return statusWithMessageVal;
	            }
	        }

	        // Processing rows
	        List<Row> validRows = new ArrayList<>();
	        List<String> applicationIds = new ArrayList<String>();

	        while (rowIterator.hasNext()) {
	            Row row = rowIterator.next();
	            String loanId = getCellValue(row.getCell(1));
	            if (loanId != null && !loanId.isEmpty()) {
	                applicationIds.add(loanId);
	                validRows.add(row);
	            }
	        }

	        
	         logger.debug("Time for validRow method {} ms", (System.currentTimeMillis() - start));
	        
	        logger.debug("applicationIds size is " + applicationIds.size());
	        logger.debug("applicationIds" + applicationIds);
	        // Fetch application data in bulk
	        Map<String, ApplicationMaster> applicationMap = applicationMasterRepository
	                .findByApplicationIdIn(applicationIds)
	                .stream()
	                .collect(Collectors.toMap(ApplicationMaster::getApplicationId, app -> app));

	        
	        Map<String, TbUalnLoanDtls> LoanDtlsMap =  loanDtlRepository.findByApplicationIdIn(applicationIds)
	        .stream()
	        .collect(Collectors.toMap(TbUalnLoanDtls::getApplicationId, app -> app));
	        
	        logger.debug("applicationMap" + applicationMap);
	        logger.debug("applicationMap size" + applicationMap.size());
	        
	        List<BulkRecordsEntity> bulkRecordsList = new ArrayList<>();
	        List<PopulateapplnWFRequest> workflowRequests = new ArrayList<>();
	        List<ApplicationMaster> applicationUpdates = new ArrayList<>();
	        List<TbUalnLoanDtls> loanDetailsUpdates = new ArrayList<>();
	        String codeDescJson = tbAbobCommonCodeRepository.fetchCodeDesc();
			JsonNode rootNode = objectMapper.readTree(codeDescJson);
			JsonNode approvalMatrixArray = rootNode.get("APPROVALMATRIX");
			
	        //logger.debug("Time for applicationMap method {} ms", (System.currentTimeMillis() - start));
	        
	        int i =1;
	        for (Row row : validRows) {
	        	
	            String loanId = getCellValue(row.getCell(1));
	            ApplicationMaster applicationMaster = applicationMap.get(loanId);

	            TbUalnLoanDtls tbUalnLoanDtls = LoanDtlsMap.get(loanId);
	            logger.debug("applicationMaster" + applicationMaster);
	            
	            // Validate application status   
	            if (applicationMaster != null) {
	                String status = applicationMaster.getApplicationStatus();
	                List<String> invalidStatuses = Arrays.asList("PENDING", "REJECTED", "CANCELLED", "DISBURSED");

	                if (status != null && invalidStatuses.stream().anyMatch(s -> s.equalsIgnoreCase(status))) {
	                    logger.error("Error: Record Not Present in CRT list for row {}", i);
	                    statusWithMessageVal.put("status", "false");
	                    statusWithMessageVal.put("message", "Record Not Present in CRT list for row " + i);
	                    return statusWithMessageVal;
	                }
	            }
	            
	            // Validate amount, FOIR, APR
	            String amount = getCellValue(row.getCell(3));
	            String foir = getCellValue(row.getCell(5));
	            String apr = getCellValue(row.getCell(6));

	            if (!isValidAmount(amount) || !isValidFoir(foir) || !isValidArp(apr)) {
	                logger.error("Error: Invalid values for Amount, FOIR, or APR. for line "+i);
	                statusWithMessageVal.put("status", "false");
	                statusWithMessageVal.put("message", "Invalid Amount, FOIR, or APR. for row "+i);
	                return statusWithMessageVal;
	            }

	            // Create Bulk Record Entity
	            BulkRecordsEntity record = new BulkRecordsEntity();
	            record.setDocId(docId);
	            record.setCustomerId(getCellValue(row.getCell(0)));
	            record.setLoanId(loanId);
	            record.setStatus(getCellValue(row.getCell(2)));
	            record.setAmount(amount);
	            record.setRemark(getCellValue(row.getCell(4)));
	            record.setUploadStatus("Success");

	            bulkRecordsList.add(record);
	            
	            // Update ApplicationMaster (Collect updates in a list)
	            if (applicationMaster != null) {
	            	logger.debug("applicationMaster for row " + i + " is applicationMaster" );
	                updateApplicationMasterData(applicationMaster, amount, foir, apr,approvalMatrixArray);
	                
	                logger.debug("updated applicationMaster" + applicationMaster);
	                
	                applicationUpdates.add(applicationMaster);
	                // Collect workflow updates
	                workflowRequests.add(createWorkflowRequest(applicationMaster, row,userIdCRT,userRole,appVersion,remarks,userName));
	            }
	            
	            
	            // Update loandetailList ( Collect update in a list)
	            
	            
	            if( tbUalnLoanDtls  != null )
	            {
	            	tbUalnLoanDtls.setLoanAmount(amount);
	            	loanDetailsUpdates.add(tbUalnLoanDtls);
	            }
	            

	            logger.debug("updateApplicationMasterData time {} ms", (System.currentTimeMillis() - start));
	            
	            // Batch Insert Every 1000 Records
	            if (bulkRecordsList.size() >= 1000) {
	                bulkRecordsRepo.saveAll(bulkRecordsList);
	                bulkRecordsList.clear();
	            }
	            i++;
	        }
	        logger.debug("BulkRecord insert time {} ms", (System.currentTimeMillis() - start));

	        // Insert Remaining Records
	        if (!bulkRecordsList.isEmpty()) {
	            bulkRecordsRepo.saveAll(bulkRecordsList);
	        }
	        logger.debug("workflow master is " + applicationUpdates);
	     // Batch update ApplicationMaster
	        if (!applicationUpdates.isEmpty()) {
	            applicationMasterRepository.saveAll(applicationUpdates);
	        }
	        logger.debug("Execution time for applicationUpdates: {} ms", (System.currentTimeMillis() - start));
	        
	        
	        if (!loanDetailsUpdates.isEmpty()) {
	        	loanDtlRepository.saveAll(loanDetailsUpdates);
	        }
	        logger.debug("Execution time for applicationUpdates: {} ms", (System.currentTimeMillis() - start));
	        
	        
	        
	        // Process Workflow Updates
	        /*
	        for (PopulateapplnWFRequest request : workflowRequests) {
	            loanService.updateStatusInMaster(request);
	            //logger.debug("Execution time updateStatusInMaster: {} ms", (System.currentTimeMillis() - start));
	            loanService.populateApplnWorkFlow(request);
	            //logger.debug("Execution time populateApplnWorkFlow: {} ms", (System.currentTimeMillis() - start));
	        }
	        */

	        logger.debug("workflow master is " + workflowRequests);
	        logger.debug("Workflow and update execution started ");
	        workflowRequests.forEach(request -> {
	        	logger.debug("Execution time for populateApplnWorkFlow: {} ms" ,(System.currentTimeMillis() - start));
	        	 logger.debug("workflow master is " + request);
	        	loanService.populateApplnWorkFlow(request);
	            loanService.updateStatusInMaster(request);
	            logger.debug("Execution time for updateStatusInMaster: {} ms" ,(System.currentTimeMillis() - start));
	        });

	        logger.debug("Workflow and update execution done ");
	        statusWithMessageVal.put("status", "true");
	        statusWithMessageVal.put("message", "File Data inserted in DB");
	        logger.debug("Execution time for before return (): {} ms", (System.currentTimeMillis() - start));
	        return statusWithMessageVal;
	    } catch (IOException e) {
	        logger.error("Error processing Excel file", e);
	        statusWithMessageVal.put("status", "false");
	        statusWithMessageVal.put("message", "Error processing Excel file");
	        return statusWithMessageVal;
	    }
	}

	private void updateApplicationMasterData(ApplicationMaster applicationMaster, String amount, String foir,
			String apr, JsonNode approvalMatrixArray) {
		applicationMaster.setAmount(new BigDecimal(amount));
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode addInfoJson = objectMapper.readTree(applicationMaster.getAddInfo());

			((ObjectNode) addInfoJson).put("FOIR", foir);
			((ObjectNode) addInfoJson).put("APR", apr);
			((ObjectNode) addInfoJson).put("isApprovedCRT", true);

			if (addInfoJson.has("lnOutstandingAmt") && !addInfoJson.get("lnOutstandingAmt").asText().isEmpty()) {
				String lnOutstandingAmtStr = addInfoJson.get("lnOutstandingAmt").asText();
				BigDecimal lnOutstandingAmt = new BigDecimal(lnOutstandingAmtStr);
				BigDecimal inputAmount = new BigDecimal(amount);
				BigDecimal finalAmount = lnOutstandingAmt.add(inputAmount);
				((ObjectNode) addInfoJson).put("finallnOutstandingAmt", finalAmount.toString());
			}
			applicationMaster.setAddInfo(objectMapper.writeValueAsString(addInfoJson));

			if (addInfoJson.has("finallnOutstandingAmt")
					&& !addInfoJson.get("finallnOutstandingAmt").asText().isEmpty()) {
				String finalOutstandingStr = addInfoJson.get("finallnOutstandingAmt").asText();
				try {
					BigDecimal finalOutstandingAmt = new BigDecimal(finalOutstandingStr);
					for (JsonNode approvalNode : approvalMatrixArray) {
						String minAmtStr = approvalNode.get("MinAmt").asText();
						String maxAmtStr = approvalNode.get("MaxAmt").asText();
						if (minAmtStr != null && maxAmtStr != null && !minAmtStr.isEmpty() && !maxAmtStr.isEmpty()) {
							try {
								BigDecimal minAmt = new BigDecimal(minAmtStr);
								BigDecimal maxAmt = new BigDecimal(maxAmtStr);

								if (finalOutstandingAmt.compareTo(minAmt) >= 0
										&& finalOutstandingAmt.compareTo(maxAmt) <= 0) {
									String leadRole = approvalNode.get("LeadRole").asText();
									applicationMaster.setLeader(leadRole);
									break;
								}
							} catch (NumberFormatException e) {
								logger.warn("Invalid MinAmt or MaxAmt in approval matrix: MinAmt={}, MaxAmt={}",
										minAmtStr, maxAmtStr);
							}
						}
					}
				} catch (NumberFormatException e) {
					logger.warn("Invalid finalOutstandingAmt in addInfo: {}", finalOutstandingStr);
				}
			}

		} catch (JsonProcessingException e) {
			logger.error("Error updating ApplicationMaster JSON", e);
		}
	}

	private PopulateapplnWFRequest createWorkflowRequest(ApplicationMaster applicationMaster, Row row,String userIdCRT,String userRole,String appVersion,String remarks,String userName) {
	    PopulateapplnWFRequestFields populateapplnWFRequestFields = new PopulateapplnWFRequestFields();
	    populateapplnWFRequestFields.setApplicationId(applicationMaster.getApplicationId());
	   // populateapplnWFRequestFields.setCreatedBy("System"); // Change as per requirement
	    populateapplnWFRequestFields.setCreatedBy(userIdCRT); 
	    populateapplnWFRequestFields.setApplicationStatus("Move to BM");
	    populateapplnWFRequestFields.setVersionNum(applicationMaster.getVersionNum());
	    populateapplnWFRequestFields.setAppId("APZCBO");
	   

	    String statusval = getCellValue(row.getCell(2));
	    WorkFlowDetails workFlowDetails = new WorkFlowDetails();
	    String productGroupCode = applicationMaster.getProductGroupCode();
	    
	    if ("APPROVED".equalsIgnoreCase(statusval) && "BM".equalsIgnoreCase(productGroupCode)) {
	        populateapplnWFRequestFields.setCbApproveManual("Y");
	        populateapplnWFRequestFields.setCreatedBy(userIdCRT);
	        populateapplnWFRequestFields.setUserRole(userRole);
	        populateapplnWFRequestFields.setAppVersion(appVersion);
	        populateapplnWFRequestFields.setRemarks(remarks);
	        populateapplnWFRequestFields.setUserName(userName);
	        workFlowDetails.setNextRole("AM");
	        workFlowDetails.setNextStageId("AMQUEUE");
	        workFlowDetails.setNextWorkflowStatus("SANCTIONINPROGRESS");
	    } else if ("APPROVED".equalsIgnoreCase(statusval)) {
	        populateapplnWFRequestFields.setCbApproveManual("Y");
	        populateapplnWFRequestFields.setCreatedBy(userIdCRT);
	        populateapplnWFRequestFields.setUserRole(userRole);
	        populateapplnWFRequestFields.setAppVersion(appVersion);
	        populateapplnWFRequestFields.setRemarks(remarks);
	        populateapplnWFRequestFields.setUserName(userName);
	        workFlowDetails.setNextRole("BM");
	        workFlowDetails.setNextStageId("BMQUEUE");
	        workFlowDetails.setNextWorkflowStatus("SANCTIONINPROGRESS");
	    } else {
	        populateapplnWFRequestFields.setCbApproveManual("N");
	        populateapplnWFRequestFields.setCreatedBy(userIdCRT);
	        populateapplnWFRequestFields.setUserRole(userRole);
	        populateapplnWFRequestFields.setAppVersion(appVersion);
	        populateapplnWFRequestFields.setRemarks(remarks);
	        populateapplnWFRequestFields.setUserName(userName);
	        workFlowDetails.setNextStageId("CRTREJECT");
	        workFlowDetails.setCurrentRole("CRT");
	        workFlowDetails.setAction("REJECT");
	        workFlowDetails.setWorkflowId("LOANINPUT");
	        workFlowDetails.setNextWorkflowStatus("REJECTED");
	    }
	    populateapplnWFRequestFields.setWorkflow(workFlowDetails);
	    PopulateapplnWFRequest populateapplnWFRequest = new PopulateapplnWFRequest();
	    populateapplnWFRequest.setRequestObj(populateapplnWFRequestFields);
	    return populateapplnWFRequest;
	}
	
	private static final double THOUSAND = 1000.0;

	private boolean isValidAmount(String amount) {
	    try {
	        double value = Double.parseDouble(amount);
	        return value >= 0 && value % THOUSAND == 0;
	    } catch (NumberFormatException e) {
	        //logger.debug("Invalid amount format: {}", amount, e);
	        return false;
	    }
	}

	private boolean isValidFoir(String foir) {
	    return isValidNonNegativeNumber(foir);
	}

	private boolean isValidArp(String arp) {
	    return isValidNonNegativeNumber(arp);
	}

	private boolean isValidNonNegativeNumber(String input) {
	    try {
	        double value = Double.parseDouble(input);
	        return value >= 0;
	    } catch (NumberFormatException e) {
	        //logger.debug("Invalid number format: {}", input, e);
	        return false;
	    }
	}

	private boolean isValidBase64(String str) {
	    if (str == null || str.length() % 4 != 0) {
	        return false;
	    }
	    try {
	        Base64.getDecoder().decode(str);
	        return true;
	    } catch (IllegalArgumentException e) {
	        //logger.debug("Invalid Base64 encoding", e);
	        return false;
	    }
	}
/*
	private String getCellValue(Cell cell) {
	    if (cell == null) {
	        return "";
	    }
	    return switch (cell.getCellType()) {
		    case STRING -> cell.getStringCellValue().trim();
		   // case NUMERIC -> {     
	        //        yield BigDecimal.valueOf(cell.getNumericCellValue())
	        //                        .setScale(2, RoundingMode.HALF_UP)
	        //                        .toPlainString()
	        //                        .trim();
	       // }
		    case NUMERIC -> new BigDecimal(cell.getNumericCellValue()).toPlainString().trim(); // Avoids scientific notation
	        case BOOLEAN -> String.valueOf(cell.getBooleanCellValue()).trim();
	        case FORMULA -> cell.getCellFormula().trim();
	        default -> "";
	    };
	}
*/

	
	private String getCellValue(Cell cell) {
	    if (cell == null) {
	        return "";
	    }

	    return switch (cell.getCellType()) {
	        case STRING -> cell.getStringCellValue().trim();

	        case NUMERIC -> {
	            if (DateUtil.isCellDateFormatted(cell)) {
	                yield new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
	            }
	            BigDecimal value = BigDecimal.valueOf(cell.getNumericCellValue()).stripTrailingZeros();
	            yield value.scale() > 0 ? value.setScale(2, RoundingMode.HALF_UP).toPlainString() : value.toPlainString();
	        }

	        case BOOLEAN -> String.valueOf(cell.getBooleanCellValue()).trim();

	        case FORMULA -> {
	            FormulaEvaluator evaluator = cell.getSheet().getWorkbook().getCreationHelper().createFormulaEvaluator();
	            CellValue evaluatedValue = evaluator.evaluate(cell);
	            yield switch (evaluatedValue.getCellType()) {
	                case STRING -> evaluatedValue.getStringValue().trim();
	                case NUMERIC -> {
	                    BigDecimal val = BigDecimal.valueOf(evaluatedValue.getNumberValue()).stripTrailingZeros();
	                    yield val.scale() > 0 ? val.setScale(2, RoundingMode.HALF_UP).toPlainString() : val.toPlainString();
	                }
	                case BOOLEAN -> String.valueOf(evaluatedValue.getBooleanValue()).trim();
	                default -> "";
	            };
	        }

	        default -> "";
	    };
	}

	
	

	public Response fetchExcelData(FetchBulkUploadRequest apiRequest) {
	    //logger.debug("==== Inside fetchExcelData service ====");
	    //logger.debug("Incoming Request: {}", apiRequest);

	    Response response = new Response();
	    ResponseHeader respHeader = new ResponseHeader();
	    ResponseBody respBody = new ResponseBody();
	    JSONObject responseObject = new JSONObject();

	    String userId = apiRequest.getRequestObj().getUserId();
	    String typeOfUpload = apiRequest.getRequestObj().getTypeOfUpload();

	    if (isValidString(userId) && isValidString(typeOfUpload)) {
	        List<BulkUploadEntity> bulkRecords = bulkUploadRepo.findAllTodaysRecords();

	        if (!bulkRecords.isEmpty()) {
	            //logger.debug("Fetched records: {}", bulkRecords);
	            responseObject.put("apiResponse", bulkRecords);
	            respBody.setResponseObj(responseObject.toString());
	            CommonUtils.generateHeaderForSuccess(respHeader);
	        } else {
	            setFailureResponse(respBody, respHeader, "Records Not Found", "Failure in retrieval");
	        }
	    } else {
	        setFailureResponse(respBody, respHeader, "Invalid Request Parameters", "Failure in retrieval");
	    }

	    response.setResponseBody(respBody);
	    response.setResponseHeader(respHeader);
	    return response;
	}

	public Response fetchExcelInsertDataService(FetchExcelDataRequest apiRequest) {
	    //logger.debug("==== Inside fetchExcelInsertDataService ====");
	    //logger.debug("Incoming Request: {}", apiRequest);

	    Response response = new Response();
	    ResponseHeader respHeader = new ResponseHeader();
	    ResponseBody respBody = new ResponseBody();
	    JSONObject responseObject = new JSONObject();

	    String docId = apiRequest.getRequestObj().getDocId();

	    List<BulkRecordsEntity> bulkRecords = bulkRecordsRepo.findByDocId(docId).orElse(Collections.emptyList());

	    if (!bulkRecords.isEmpty()) {
	        responseObject.put("apiResponse", bulkRecords);
	        respBody.setResponseObj(responseObject.toString());
	        CommonUtils.generateHeaderForSuccess(respHeader);
	    } else {
	        setFailureResponse(respBody, respHeader, "No Data available for provided docId", "No Data");
	    }
	    response.setResponseBody(respBody);
	    response.setResponseHeader(respHeader);
	    return response;
	}

	// Utility method to validate strings
	private boolean isValidString(String str) {
	    return str != null && !str.trim().isEmpty();
	}

	// Utility method to set failure response
	private void setFailureResponse(ResponseBody respBody, ResponseHeader respHeader, String responseObj, String headerMessage) {
	    respBody.setResponseObj(responseObj);
	    CommonUtils.generateHeaderForFailure(respHeader, headerMessage);
	}	
}
