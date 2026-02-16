package com.iexceed.appzillonbanking.kendra.domain.cus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_uaum_location_details")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LocationMappingDetails {
	
	@Id
	@Column(name = "USER_ID")
	private String userId;
	
	@Column(name = "LOCATION_TYPE")
	private String locationType;
	
	@Column(name = "LOCATION_DETAILS")
	private String locationDetails;
	
	@Column(name = "HRIS_LOCATION")
	private String hrisLocation;
	
	@Column(name = "REPORT_MANAGER_NAME")
	private String reportManagerName;

	@Column(name = "REPORT_MANAGER_ID")
	private String reportManagerId;
	
	@Column(name = "SUB_FUNCTION")
	private String subFunction;
	
	@Column(name = "DEPARTMENT")
	private String department;
	
	@Column(name = "ADD_INFO1")
	private String addInfo1;
	
	@Column(name = "ADD_INFO2")
	private String addInfo2;
	
	@Column(name = "ADD_INFO3")
	private String addInfo3;
	
	@Column(name = "ADD_INFO4")
	private String addInfo4;
	
	@Column(name = "ADD_INFO5")
	private String addInfo5;
}
