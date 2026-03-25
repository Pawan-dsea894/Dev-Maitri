package com.iexceed.appzillonbanking.kendra.domain.cus;

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
	
	@Column(name = "Branchname")
	private String branchName;
	
	@Column(name = "AreaName")
	private String areaName;
	
	@Column(name = "Region_Name")
	private String regionName;
	
	@Column(name = "zone")
	private String zone;
	
	@Column(name = "mifos_office_id")
	private String mifosOfficeId;
	
	@Column(name = "type")
	private String type;
	
	@Column(name = "state")
	private String state;
	
	@Column(name = "district")
	private String district;
	
	@Column(name = "sub_district")
	private String subDistrict;
	
	@Column(name = "branch_opening_date")
	private String branchOpeningDate;

	@Column(name = "zone_id")
	private String zoneId;

	@Column(name = "state_id")
	private String stateId;

	@Column(name = "area_id")
	private String areaId;

	@Column(name = "region_id")
	private String regionId;
	
	@Column(name = "disabled_products")
	private String disabled_products;
	
	@Column(name = "disabled_modules")
	private String disabled_modules;
	
}
