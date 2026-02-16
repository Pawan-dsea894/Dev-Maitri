package com.iexceed.appzillonbanking.cagl.document.domain.ab;


//Created by Prem  ;this entity class  

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "T24_OFFICE")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TbOfficeData {
	
	@Id
	private String branchid;
	
	@Column(name = "BRANCH_LAN")
	private String branchLan;
	
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



}
