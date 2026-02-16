package com.iexceed.appzillonbanking.kendra.domain.cus;

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
@Table(name = "TB_UASR_APPLICATION")
@IdClass(SRCreationId.class)
@NoArgsConstructor
@Data
@AllArgsConstructor
public class SRCreation {

	@Id
	@Column(name = "APPLICATION_ID")
	private String applicationId;
	
	@Id
	@JsonProperty("srType")
	@Column(name = "SR_TYPE")
	private String srType;

	@JsonProperty("payload")
	@Column(name = "PAYLOAD")
	private String payload;

	@JsonProperty("createdRole")
	@Column(name = "CREATED_ROLE")
	private String createdRole;

	@JsonProperty("currRole")
	@Column(name = "CURR_ROLE")
	private String currRole;

	@JsonProperty("nextRole")
	@Column(name = "NEXT_ROLE")
	private String nextRole;

	@JsonProperty("appStatus")
	@Column(name = "APP_STATUS")
	private String appStatus;

	@JsonProperty("createTs")
	@Column(name = "CREATE_TS")
	private String createTs;

	@JsonProperty("createBy")
	@Column(name = "CREATE_BY")
	private String createBy;

	@JsonProperty("updateTs")
	@Column(name = "UPDATE_TS")
	private String updateTs;

	@JsonProperty("updateBy")
	@Column(name = "UPDATE_BY")
	private String updateBy;

	@JsonProperty("remarks")
	@Column(name = "REMARK")
	private String remarks;

	@JsonProperty("addInfo1")
	@Column(name = "ADD_INFO1")
	private String addInfo1;

	@JsonProperty("addInfo2")
	@Column(name = "ADD_INFO2")
	private String addInfo2;
	
	@Column(name = "branch_id")
	@JsonProperty("branchId")
	private String branchId;
}
