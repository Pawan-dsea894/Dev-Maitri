package com.iexceed.appzillonbanking.scheduler.domain.ab;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tb_uaob_mis_report")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MisReport {

	@JsonProperty("applicationId")
	@Column(name = "applicationId")
	@Id
	private String applicationId;

	@JsonProperty("applicationStatus")
	@Column(name = "applicationStatus")
	private String applicationStatus;

	@JsonProperty("appVersion ")
	@Column(name = "appVersion ")
	private String appVersion;

	@JsonProperty("stageID")
	@Column(name = "stageID")
	private String stageID;

	@JsonProperty("createDate")
	@Column(name = "createDate")
	private String createDate;

	@JsonProperty("updateDate")
	@Column(name = "updateDate")
	private String updateDate;

	@JsonProperty("createdBy")
	@Column(name = "createdBy")
	private String createdBy;

	@JsonProperty("modifyBy")
	@Column(name = "modifyBy")
	private String modifyBy;

	@JsonProperty("kendraFrequency")
	@Column(name = "kendraFrequency")
	private String kendraFrequency;

	@JsonProperty("meetingDay")
	@Column(name = "meetingDay")
	private String meetingDay;

	@JsonProperty("kendraId")
	@Column(name = "kendraId")
	private String kendraId;

	@JsonProperty("groupId")
	@Column(name = "groupId")
	private String groupId;

	@JsonProperty("branchId")
	@Column(name = "branchId")
	private String branchId;

	@JsonProperty("activeLoanCount")
	@Column(name = "activeLoanCount")
	private String activeLoanCount;

	@JsonProperty("outstandingPrincipal")
	@Column(name = "outstandingPrincipal")
	private String outstandingPrincipal;

	@JsonProperty("outstandingInterest")
	@Column(name = "outstandingInterest")
	private String outstandingInt;

	@JsonProperty("remarks")
	@Column(name = "remarks")
	private String remarks;

	@JsonProperty("userRole")
	@Column(name = "userRole")
	private String userrole;

	@JsonProperty("addInfo")
	@Column(name = "addInfo")
	private String addInfo;

	@JsonProperty("addInfo2")
	@Column(name = "addInfo2")
	private String addInfo2;

	@JsonProperty("loanFrequency")
	@Column(name = "loanFrequency")
	private String loanFrequency;

}
