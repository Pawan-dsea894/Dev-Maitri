package com.iexceed.appzillonbanking.cagl.loan.service;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.iexceed.appzillonbanking.cagl.loan.core.utils.CobFlagsProperties;
import com.iexceed.appzillonbanking.cagl.loan.core.utils.Errors;
import com.iexceed.appzillonbanking.cagl.loan.core.utils.ResponseCodes;
import com.iexceed.appzillonbanking.cagl.loan.core.utils.SpringCloudProperties;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.LovMaster;
import com.iexceed.appzillonbanking.cagl.loan.domain.ab.TbAbobCommonCodeDomain;
import com.iexceed.appzillonbanking.cagl.loan.payload.CommonParamRequest;
import com.iexceed.appzillonbanking.cagl.loan.payload.CommonParamResponse;
import com.iexceed.appzillonbanking.cagl.loan.payload.FetchLitByLanguageRequest;
import com.iexceed.appzillonbanking.cagl.loan.payload.FetchLovRequest;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.LovMasterRepository;
import com.iexceed.appzillonbanking.cagl.loan.repository.ab.TbAbobCommonCodeRepository;
import com.iexceed.appzillonbanking.core.payload.Request;
import com.iexceed.appzillonbanking.core.payload.Response;
import com.iexceed.appzillonbanking.core.payload.ResponseBody;
import com.iexceed.appzillonbanking.core.payload.ResponseHeader;
import com.iexceed.appzillonbanking.core.utils.CommonUtils;

@Service
public class CommonParamService {

	private static final Logger logger = LogManager.getLogger(CommonParamService.class);
	
	@Autowired
	private TbAbobCommonCodeRepository tbAbobCommonCodeRepository;
	
	@Autowired
	private LovMasterRepository lovMasterRepository;
	   
	@Value("${litFilePath}")
	private String litFilePath;
	
	@Value("${litFileFormat}")
	private String litFileFormat;
	
	@Value("${cobFlags}")
	private String cobFlags;
	
	@Value("${litCodeFilePath}")
	private String litCodeFilePath;	
	
	public Response fetchAllData(CommonParamRequest commonRequestParam) throws IOException  {
		Gson gson = new Gson();
		CommonParamResponse commonParamResponseOBJ;
		Response commonParamResponse = new Response();
		List<CommonParamResponse> commonParamResponseList = null;

		String accessType = commonRequestParam.getRequestObj().getAccessType();
		logger.debug("Access Type :" + accessType);

		String code = commonRequestParam.getRequestObj().getCode();
		logger.debug("Code:" + code);

		if (accessType.isEmpty() && code.isEmpty()) {
			logger.debug("COB Fetching all the common codes from DB");
			Iterable<TbAbobCommonCodeDomain> commonParam = tbAbobCommonCodeRepository.findAll();
			commonParamResponseList = generateResponseWrapper(commonParam);
		}

		else if (!accessType.isEmpty() && !code.isEmpty()) {
			logger.debug("COB Fetching based on accessType and code from DB");
			Iterable<TbAbobCommonCodeDomain> commonParam = tbAbobCommonCodeRepository.findAllByCodeAndAccessType(code, accessType);
			commonParamResponseList = generateResponseWrapper(commonParam);
		}

		else {
			if (!accessType.isEmpty()) {
				logger.debug("COB Fetching based on accessType only from DB.");
				Iterable<TbAbobCommonCodeDomain> commonParam = tbAbobCommonCodeRepository.findAllByAccessType(accessType);
				commonParamResponseList = generateResponseWrapper(commonParam);
			}

			else if (!code.isEmpty()) {
				logger.debug("COB Fetching based on code only from DB.");
				Iterable<TbAbobCommonCodeDomain> commonParam = tbAbobCommonCodeRepository.findAllByCode(code);
				commonParamResponseList = generateResponseWrapper(commonParam);
			}
		}
		Properties prop=new Properties(); 
		try(FileReader fileReader=new FileReader(cobFlags)) {
			prop.load(fileReader);
		} catch (IOException ex) {
			logger.error("Exception in while reading property file: {}",ex);
			throw ex;
		}
		if(commonParamResponseList!=null) {
			LocalDate today=LocalDate.now();
			commonParamResponseOBJ= new CommonParamResponse();
			commonParamResponseOBJ.setCodeType("COMM");
			commonParamResponseOBJ.setParamName("allowPartiallyFilledApplication");
			commonParamResponseOBJ.setParamValue(prop.getProperty(CobFlagsProperties.ALLOW_PARTIAL_APPLICATION.getKey()));
			commonParamResponseList.add(commonParamResponseOBJ);
			
			commonParamResponseOBJ= new CommonParamResponse();
			commonParamResponseOBJ.setCodeType("COMM");			
			commonParamResponseOBJ.setParamName("demoMode");
			commonParamResponseOBJ.setParamValue(prop.getProperty(CobFlagsProperties.DEMO_MODE.getKey()));
			commonParamResponseList.add(commonParamResponseOBJ);		
			
			commonParamResponseOBJ= new CommonParamResponse();
			commonParamResponseOBJ.setCodeType("COMM");
			commonParamResponseOBJ.setParamName("accountSTP");
			commonParamResponseOBJ.setParamValue(prop.getProperty(CobFlagsProperties.ACCOUNT_STP.getKey()));
			commonParamResponseList.add(commonParamResponseOBJ);
			
			commonParamResponseOBJ= new CommonParamResponse();
			commonParamResponseOBJ.setCodeType("COMM");
			commonParamResponseOBJ.setParamName("depositSTP");
			commonParamResponseOBJ.setParamValue(prop.getProperty(CobFlagsProperties.DEPOSIT_STP.getKey()));
			commonParamResponseList.add(commonParamResponseOBJ);
			
			commonParamResponseOBJ= new CommonParamResponse();
			commonParamResponseOBJ.setCodeType("COMM");
			commonParamResponseOBJ.setParamName("cardSTP");
			commonParamResponseOBJ.setParamValue(prop.getProperty(CobFlagsProperties.CARD_STP.getKey()));
			commonParamResponseList.add(commonParamResponseOBJ);
			
			commonParamResponseOBJ= new CommonParamResponse();
			commonParamResponseOBJ.setCodeType("COMM");
			commonParamResponseOBJ.setParamName("loanSTP");
			commonParamResponseOBJ.setParamValue(prop.getProperty(CobFlagsProperties.LOAN_STP.getKey()));
			commonParamResponseList.add(commonParamResponseOBJ);
					
			commonParamResponseOBJ= new CommonParamResponse();
			commonParamResponseOBJ.setCodeType("COMM");
			commonParamResponseOBJ.setParamName("defaultCasaProductGrpCode");
			commonParamResponseOBJ.setParamValue(prop.getProperty(CobFlagsProperties.DEFAULT_CASA_GRP.getKey()));
			commonParamResponseList.add(commonParamResponseOBJ);
			
			commonParamResponseOBJ= new CommonParamResponse();
			commonParamResponseOBJ.setCodeType("COMM");
			commonParamResponseOBJ.setParamName("defaultCasaProductCode");
			String defaultCasaProductCode=prop.getProperty(CobFlagsProperties.DEFAULT_CASA_PRODUCT.getKey());
			commonParamResponseOBJ.setParamValue(defaultCasaProductCode);
			commonParamResponseList.add(commonParamResponseOBJ);
			
			
			commonParamResponseOBJ= new CommonParamResponse();
			commonParamResponseOBJ.setCodeType("COMM");
			commonParamResponseOBJ.setParamName("defaultCasaProductCodeLN");
			String defaultCasaProductCodeLN=prop.getProperty(CobFlagsProperties.DEFAULT_CASA_PRODUCTLN.getKey());
			commonParamResponseOBJ.setParamValue(defaultCasaProductCodeLN);
			commonParamResponseList.add(commonParamResponseOBJ);
		
			commonParamResponseOBJ= new CommonParamResponse();
			commonParamResponseOBJ.setCodeType("COMM");
			commonParamResponseOBJ.setParamName("defaultCardlocation");
			commonParamResponseOBJ.setParamValue(prop.getProperty(CobFlagsProperties.DEFAULT_CARD_LOCATION.getKey()));
			commonParamResponseList.add(commonParamResponseOBJ);
			
		}
		ResponseHeader commonParamRespHeader = new ResponseHeader();
		ResponseBody commonParamRespBody = new ResponseBody();

		if (commonParamResponseList==null || commonParamResponseList.isEmpty()){
			logger.debug("COB Setting failure response for list common params");
			CommonUtils.generateHeaderForNoResult(commonParamRespHeader);
			commonParamRespBody.setResponseObj("");
		}

		else {
			logger.debug("COB Setting success response for list common params");
			CommonUtils.generateHeaderForSuccess(commonParamRespHeader);
			String commonParamsResponseJson = gson.toJson(commonParamResponseList);
			commonParamRespBody.setResponseObj(commonParamsResponseJson);
		}

		commonParamResponse.setResponseBody(commonParamRespBody);
		commonParamResponse.setResponseHeader(commonParamRespHeader);

		logger.debug("COB Common Param Response:" + commonParamResponse.toString());
		return commonParamResponse;
	}

	private List<CommonParamResponse> generateResponseWrapper(Iterable<TbAbobCommonCodeDomain> commonParam) {
		logger.debug("Inside generate Response format function.");
		List<CommonParamResponse> commonParamResponse = new ArrayList<>();
		for (TbAbobCommonCodeDomain tbCodeDomain : commonParam) {
			CommonParamResponse commonParamResponseOBJ = new CommonParamResponse();
			commonParamResponseOBJ.setParamName(tbCodeDomain.getCode());
			commonParamResponseOBJ.setParamValue(tbCodeDomain.getCodeDesc());
			commonParamResponseOBJ.setAccessType(tbCodeDomain.getAccessType());
			commonParamResponseOBJ.setLanguage(tbCodeDomain.getLanguage());
			commonParamResponseOBJ.setCodeType(tbCodeDomain.getCodeType());
			commonParamResponse.add(commonParamResponseOBJ);
		}
		return commonParamResponse;
	}	
	
	 public Response fetchLovMaster(Request request) {
	    	Gson gson = new Gson();
	        Response response = new Response();
	        ResponseHeader responseHeader = new ResponseHeader();
	        ResponseBody responseBody = new ResponseBody();
	        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
	        response.setResponseHeader(responseHeader);
	    	List<LovMaster> lovMasterList= lovMasterRepository.findDistinctLovs(request.getAppId());
	        responseBody.setResponseObj(gson.toJson(lovMasterList));
	        response.setResponseBody(responseBody);
	        return response;
		}
	
	 public Response fetchLitByLanguage(FetchLitByLanguageRequest request) {
	        Response response = new Response();
	        ResponseHeader responseHeader = new ResponseHeader();
	        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
	        ResponseBody responseBody = new ResponseBody();
	        Gson gson = new Gson();
	        List<CommonParamResponse> commonParamResponseList = new ArrayList<>();
	        String languageCode = request.getRequestObj().getLanguageCode();
	        CommonParamResponse commonParamResponseObj;
	        String file = CommonUtils.getCommonProperties(SpringCloudProperties.LIT_FILE_PATH.getKey()) + languageCode + "." + CommonUtils.getCommonProperties(SpringCloudProperties.LIT_FILE_FORMAT.getKey());
	        try (FileReader fileReader = new FileReader(file);
	             BufferedReader stream = new BufferedReader(fileReader)) {
	            String line;
	            List<String> list = new ArrayList<>();
	            commonParamResponseObj = new CommonParamResponse();
	            commonParamResponseObj.setParamName("LITCODES");
	            //line = stream.readLine(); // Read the header and ignore
	            while ((line = stream.readLine()) != null) {
	                list.add(line);
	            }
	            commonParamResponseObj.setParamValue(gson.toJson(list));
	            commonParamResponseList.add(commonParamResponseObj);
	        } catch (Exception e) {
	            logger.error("Exception in fetchLitByLanguage ", e);
				return formFailResponse(ResponseCodes.FAILURE.getValue(), ResponseCodes.FAILURE.getKey());
	        }
	        responseBody.setResponseObj(gson.toJson(commonParamResponseList));
	        response.setResponseBody(responseBody);
	        response.setResponseHeader(responseHeader);
	        return response;
	    }
	 
	  public Response fetchLov(FetchLovRequest request) {
	        Gson gson = new Gson();
	        String response="";
	        Response fetchLovMasterResponse = new Response();
	        ResponseHeader responseHeader = new ResponseHeader();
	        ResponseBody responseBody = new ResponseBody();
	        responseHeader.setResponseCode(ResponseCodes.SUCCESS.getKey());
	        fetchLovMasterResponse.setResponseHeader(responseHeader);
	        Optional<LovMaster> lovDtls = lovMasterRepository.findByAppIdAndLanguageAndLovName(request.getAppId(), request.getRequestObj().getLanguage(), request.getRequestObj().getLovName());
	        if(lovDtls.isPresent()) {
	        	response = gson.toJson(lovDtls.get());
	        }
	        responseBody.setResponseObj(response);
	        fetchLovMasterResponse.setResponseBody(responseBody);
	        return fetchLovMasterResponse;
	    }
	 
		public static Response formFailResponse(String responseObj, String resCode) {
			Response response = new Response();
			ResponseHeader responseHeader = new ResponseHeader();
			ResponseBody responseBody = new ResponseBody();
			responseHeader.setHttpStatus(HttpStatus.OK);
			responseHeader.setResponseMessage(Errors.PROCESSINGREQUESTERROR.getErrorMessage());
			responseHeader.setResponseCode(resCode);
			responseBody.setResponseObj(responseObj);
			response.setResponseBody(responseBody);
			response.setResponseHeader(responseHeader);
			return response;
		}	
	
}