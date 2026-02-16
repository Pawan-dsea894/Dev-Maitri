package com.iexceed.appzillonbanking.cagl.incomeassesment.domain.ab;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "tb_uaco_income_questionary")
@IdClass(TbUacoIncomeQuestionaryId.class)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class TbUacoIncomeQuestionary {

	@Id
	private String appId;

	@Id
	private String applicationId;

	@JsonProperty("userId")
	@Column(name = "USER_ID")
	private String userId;

	@JsonProperty("addInfo")
	@Column(name = "ADD_INFO")
	private String addInfo;

	@JsonProperty("loanId")
	@Column(name = "LOAN_ID")
	private String loanId;

	@JsonProperty("customerId")
	@Column(name = "CUSTOMER_ID")
	private String customerId;

	@JsonProperty("createdBy")
	@Column(name = "CREATED_BY")
	private String createdBy;

	@JsonProperty("createdTs")
	@Column(name = "CREATED_TS")
	private Timestamp createdTs;

	@JsonProperty("updatedBy")
	@Column(name = "UPDATED_BY")
	private String updatedBy;

	@JsonProperty("updatedTs")
	@Column(name = "UPDATED_TS")
	private Timestamp updatedTs;

}
