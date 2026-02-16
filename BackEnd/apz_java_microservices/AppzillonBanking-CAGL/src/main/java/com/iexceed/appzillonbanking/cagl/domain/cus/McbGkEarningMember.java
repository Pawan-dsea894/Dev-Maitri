package com.iexceed.appzillonbanking.cagl.domain.cus;

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
@Table(name = "mcb_gk_l_earning_member")
@IdClass(McbGkEarningMemberPK.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class McbGkEarningMember {

	@Id
	@JsonProperty("recId")
	private String recId;
	
	@JsonProperty("customerId")
	@Column(name = "CUSTOMERID")
	private String customerId;
	
	@JsonProperty("name")
	@Column(name = "NAME")
	private String name;
	
	@JsonProperty("dob")
	@Column(name = "DOB")
	private String dob;
	
	@JsonProperty("memRelation")
	@Column(name = "MEM_RELATION")
	private String memRelation;
	
	@JsonProperty("legalDocName")
	@Column(name = "LEGAL_DOC_NAME")
	private String legalDocName;
	
	@JsonProperty("legalId")
	@Column(name = "LEGAL_ID")
	private String legalId;
	
	@JsonProperty("incomeFlag")
	@Column(name = "INCOME_FLAG")
	private String incomeFlag;
	
	@JsonProperty("override")
	@Column(name = "OVERRIDE")
	private String override;
	
	@JsonProperty("recordStatus")
	@Column(name = "RECORD_STATUS")
	private String recordStatus;
	
	@JsonProperty("currNo")
	@Column(name = "CURR_NO")
	private String currNo;
	
	@JsonProperty("inputter")
	@Column(name = "INPUTTER")
	private String inputter;
	
	@JsonProperty("dateTime")
	@Column(name = "DATE_TIME")
	private String dateTime;
	
	@JsonProperty("authoriser")
	@Column(name = "AUTHORISER")
	private String authoriser;
	
	@JsonProperty("coCode")
	@Column(name = "CO_CODE")
	private String coCode;
	
	@JsonProperty("deptCode")
	@Column(name = "DEPT_CODE")
	private String deptCode;
	
	@JsonProperty("auditorCode")
	@Column(name = "AUDITOR_CODE")
	private String auditorCode;
	
	@JsonProperty("auditDateTime")
	@Column(name = "AUDIT_DATE_TIME")
	private String auditDateTime;
	
	@JsonProperty("t24LoadDate")
	@Column(name = "T24_LOAD_DATE")
	private String t24LoadDate;
	
	@JsonProperty("efzLoadDate")
	@Column(name = "EFZ_LOAD_DATE")
	private String efzLoadDate;
	
}
