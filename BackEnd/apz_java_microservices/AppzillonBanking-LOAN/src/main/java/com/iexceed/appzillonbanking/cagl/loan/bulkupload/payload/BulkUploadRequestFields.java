package com.iexceed.appzillonbanking.cagl.loan.bulkupload.payload;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BulkUploadRequestFields {

	@JsonProperty("base64Data")
	private String base64Data;

	@JsonProperty("docId")
	private String docId;

	@JsonProperty("docName")
	private String docName;

	@JsonProperty("userId")
	private String userId;

	@JsonProperty("uploadedBy")
	private String uploadedBy;

	@JsonProperty("createdAt")
	private String createdAt;

	@JsonProperty("typeOfUpload")
	private String typeOfUpload;

	@JsonProperty("status")
	private String status;
	
	// For Audit Trail 	
	@JsonProperty("userRole")
	private String userRole;

	@JsonProperty("appVersion")
	private String appVersion;
	
	@JsonProperty("remarks")
	private String remarks;
	
	@JsonProperty("userName")
	private String userName;
	
}
