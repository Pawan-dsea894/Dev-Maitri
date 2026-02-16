package com.iexceed.appzillonbanking.cagl.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gk_cust_demo_data")
@Data
@IdClass(GkCustDemoDataPK.class)
@NoArgsConstructor
@AllArgsConstructor
public class GkCustDemoData {

	@Id
	@JsonProperty("customerId")
	private String customerId;

	@JsonProperty("customerName")
	@Column(name = "CUSTOMERNAME")
	private String customerName;

	@JsonProperty("kendraId")
	@Column(name = "KENDRAID")
	private int kendraId;

	@JsonProperty("kendraName")
	@Column(name = "KENDRANAME")
	private String kendraName;

	@JsonProperty("groupId")
	@Column(name = "GROUPID")
	private int groupId;

	@JsonProperty("branchId")
	@Column(name = "BRANCHID")
	private String branchId;

	@JsonProperty("branchName")
	@Column(name = "BRANCHNAME")
	private String branchName;

	@JsonProperty("primaryType")
	@Column(name = "PRIMARYTYPE")
	private String primaryType;

	@JsonProperty("primaryId")
	@Column(name = "PRIMARYID")
	private String primaryId;

	@JsonProperty("dob")
	@Column(name = "DOB")
	private String dob;

	@JsonProperty("maritalStatus")
	@Column(name = "MARITALSTATUS")
	private String maritalStatus;

	@JsonProperty("address")
	@Column(name = "ADDRESS")
	private String address;

	@JsonProperty("bankAccNo")
	@Column(name = "BANKACNO")
	private String bankAccNo;

	@JsonProperty("bankAccName")
	@Column(name = "BANKACCOUNTNAME")
	private String bankAccName;

	@JsonProperty("bankBranchName")
	@Column(name = "BANKBRANCHNAME")
	private String bankBranchName;

	@JsonProperty("bankName")
	@Column(name = "BANKNAME")
	private String bankName;

	@JsonProperty("bankIfscCode")
	@Column(name = "BANKIFSCCODE")
	private String bankIfscCode;

	@JsonProperty("depName")
	@Column(name = "DEPNAME")
	private String depName;

	@JsonProperty("depDob")
	@Column(name = "DEPDOB")
	private String depDob;

	@JsonProperty("depDocId")
	@Column(name = "DEPDOCID")
	private String depDocId;

	@JsonProperty("depDocType")
	@Column(name = "DEPDOCTYPE")
	private String depDocType;

	@JsonProperty("memRelation")
	@Column(name = "MEM_RELATION")
	private String memRelation;

	@JsonProperty("recordType")
	@Column(name = "RECORD_TYPE")
	private String recordType;

	@JsonProperty("custQualify")
	@Column(name = "CUST_QUALIFY")
	private String custQualify;

	@JsonProperty("religion")
	@Column(name = "RELIGION")
	private String religion;

	@JsonProperty("caste")
	@Column(name = "CASTE")
	private String caste;

	@JsonProperty("state")
	@Column(name = "STATE")
	private String state;

	@JsonProperty("city")
	@Column(name = "CITY")
	private String city;

	@JsonProperty("village")
	@Column(name = "VILLAGE")
	private String village;

	@JsonProperty("pincode")
	@Column(name = "PINCODE")
	private String pincode;

	@JsonProperty("mobileNum")
	@Column(name = "MOBILE_NUMBER")
	private String mobileNum;

	@JsonProperty("permAddLine1")
	@Column(name = "PERMANENT_ADDRESS_LINE1")
	private String permAddLine1;

	@JsonProperty("permAddLine2")
	@Column(name = "PERMANENT_ADDRESS_LINE2")
	private String permAddLine2;

	@JsonProperty("permanentState")
	@Column(name = "PERMANENT_STATE")
	private String permanentState;

	@JsonProperty("permanentDistrict")
	@Column(name = "PERMANENT_DISTRICT")
	private String permanentDistrict;

	@JsonProperty("permanentVillageLocality")
	@Column(name = "PERMANENT_VILLAGE_LOCALITY")
	private String permanentVillageLocality;

	@JsonProperty("permanentPincode")
	@Column(name = "PERMANENT_PINCODE")
	private String permanentPincode;

	@JsonProperty("permanentTaluk")
	@Column(name = "PERMANENT_TALUK")
	private String permanentTaluk;

	@JsonProperty("commAddLIne1")
	@Column(name = "COMMUNICATION_ADDRESS_LINE1")
	private String commAddLIne1;

	@JsonProperty("commAddLIne2")
	@Column(name = "COMMUNICATION_ADDRESS_LINE2")
	private String commAddLIne2;

	@JsonProperty("commState")
	@Column(name = "COMMUNICATION_STATE")
	private String commState;

	@JsonProperty("commDistrict")
	@Column(name = "COMMUNICATION_DISTRICT")
	private String commDistrict;

	@JsonProperty("commVillageLocality")
	@Column(name = "COMMUNICATION_VILLAGE_LOCALITY")
	private String commVillageLocality;

	@JsonProperty("commPincode")
	@Column(name = "COMMUNICATION_PINCODE")
	private String commPincode;

	@JsonProperty("commTaluk")
	@Column(name = "COMMUNICATION_TALUK")
	private String commTaluk;

}