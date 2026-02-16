package com.iexceed.appzillonbanking.cagl.loan.domain.ab;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_SANCTION_REPAY")
@NoArgsConstructor
@Data
@AllArgsConstructor
public class SanctionRepaymentSchedule {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "report_id")
	private long report_id;
	
	@JsonProperty("parameter")
	@Column(name = "parameter")
	private String parameter;
	
	@JsonProperty("outstanding principal")
	@Column(name = "outstanding principal")
	private String outstandingPrincipal;
	
	@JsonProperty("principal")
	@Column(name = "principal")
	private String principal;
	
	@JsonProperty("interest")
	@Column(name = "interest")
	private String interest;
	
	@JsonProperty("installment")
	@Column(name = "installment")
	private String installment;
	
	@JsonProperty("sanctRepoId")
	@Column(name = "sanctRepoId")
	private String sanctRepoId;
	
	@JsonProperty("typeOfStage")
	@Column(name = "type_Of_Stage")
	private String typeOfStage;
	
	@JsonProperty("applicationId")
	@Column(name = "applicationId")
	private String applicationId;
	
	@JsonProperty("createTs")
	@Column(name = "createTs")
	private String createTs;
	
}
