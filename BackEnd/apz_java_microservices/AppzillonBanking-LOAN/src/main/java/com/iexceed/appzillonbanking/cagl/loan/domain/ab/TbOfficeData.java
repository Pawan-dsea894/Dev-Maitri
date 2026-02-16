package com.iexceed.appzillonbanking.cagl.loan.domain.ab;

import com.fasterxml.jackson.annotation.JsonProperty;


//Created by Prem  ;this entity class  
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "T24_OFFICE")
@IdClass(TbOfficeDataId.class)
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
