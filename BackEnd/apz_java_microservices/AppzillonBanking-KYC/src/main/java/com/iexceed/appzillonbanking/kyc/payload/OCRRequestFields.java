package com.iexceed.appzillonbanking.kyc.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OCRRequestFields {

	@JsonProperty("imageUrl")
	private String imageUrl; 
    
	@JsonProperty("requestID")
	private String requestID;
	
	@JsonProperty("isBlackWhiteCheck")
	private String isBlackWhiteCheck;
	
	@JsonProperty("isCompleteImageCheck")
	private String isCompleteImageCheck;
    	
	@JsonProperty("confidence")
	private String confidence;
	
	@JsonProperty("fraudCheck")
	private String fraudCheck;
	
	@JsonProperty("doctype")
	private String doctype;
	
	@JsonProperty("isFront")
	private String isFront;

	@JsonProperty("base64String")
	private String base64String;

	@JsonProperty("mimetype")
	private String mimetype;

	@JsonProperty("ttl")
	private String ttl;

//	@JsonProperty("files")
//	private List<String> files;
}
