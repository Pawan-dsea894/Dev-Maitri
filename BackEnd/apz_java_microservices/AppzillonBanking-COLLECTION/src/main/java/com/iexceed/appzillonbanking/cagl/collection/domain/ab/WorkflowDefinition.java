package com.iexceed.appzillonbanking.cagl.collection.domain.ab;

import java.time.LocalDateTime;

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
@Table(name = "TB_UAWF_WORKFLOW_DEFINITION")
@IdClass(WorkflowDefinitionId.class)
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowDefinition {

	@Id
	private String appId;
	
	@Id
	private String workFlowId;
	
	@Id
	private int stageSeqNum;
	      
	@JsonProperty("fromStageId")
	@Column(name = "FROM_STAGE_ID")
	private String fromStageId;
	
	@JsonProperty("action")
	@Column(name = "ACTION")
	private String action;
	
	@JsonProperty("nextStageId")
	@Column(name = "NEXT_STAGE_ID")
	private String nextStageId;
	
	@JsonProperty("nextWFStatus")
	@Column(name = "NEXT_WORKFLOW_STATUS")
	private String nextWFStatus;
	
	@JsonProperty("createTs")
	@Column(name = "CREATE_TS")
	private LocalDateTime createTs;
	   
	@JsonProperty("ruleId")
	@Column(name = "RULE_ID")
	private String ruleId;
	
	@JsonProperty("currentRole")
	@Column(name = "CURRENT_ROLE")
	private String currentRole;
	
	@JsonProperty("nextRole")
	@Column(name = "NEXT_ROLE")
	private String nextRole;	
}