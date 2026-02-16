package com.iexceed.appzillonbanking.cagl.incomeassesment.domain.ab;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "t24_office")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfficeData {
	
	@Id
	@Column(name = "BRANCHID")
	private String branchId;
	
	@Column(name = "AreaName")
	private String areaName;
	
	@Column(name = "Branchname")
	private String branchName;
	
	@Column(name = "branch_opening_date")
	private String branchOpeningDate;
	
	@Column(name = "district")
	private String district;
	
	@Column(name = "mifos_office_id")
	private String mifosOfficeId;
	
	@Column(name = "Region_Name")
	private String regionName;
	
	@Column(name = "state")
	private String state;
	
	@Column(name = "sub_district")
	private String subDistrict;
	
	@Column(name = "type")
	private String type;
	
	@Column(name = "zone")
	private String zone;
	
	@Column(name = "branch_lan")
	private String branchLan;
	
	@Column(name = "area_id")
	private String areaId;
	
	@Column(name = "region_id")
	private String regionId;
	
	@Column(name = "state_id")
	private String state_Id;
	
	@Column(name = "consent_type")
	private String consentType;

}
