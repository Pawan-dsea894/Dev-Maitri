package com.iexceed.appzillonbanking.cagl.domain.cus;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Table(name = "gk_income_assesment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GkIncomeAssesment {	
	@Id
	@JsonProperty("customerId")
	private String customerId;
	
	@JsonProperty("totIncome")
	@Column(name = "TOT_INCOME")
	private String totIncome;
	
	@JsonProperty("totExpense")
	@Column(name = "TOT_EXPENSES")
	private String totExpense;
	
	@JsonProperty("assesmentDt")
	@Column(name = "ASSESMENT_DATE")
	private String assesmentDt;
	
	@JsonProperty("statusFlag")
	@Column(name = "STATUS_FLAG")
	private String statusFlag;
	
	@JsonProperty("qaFlag")
	@Column(name = "QA_FLAG")
	private String qaFlag;
	
	@JsonProperty("elgEmi")
	@Column(name = "ELG_EMI")
	private String elgEmi;
	
	@JsonProperty("cbEmi")
	@Column(name = "CB_EMI")
	private String cbEmi;
	
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
