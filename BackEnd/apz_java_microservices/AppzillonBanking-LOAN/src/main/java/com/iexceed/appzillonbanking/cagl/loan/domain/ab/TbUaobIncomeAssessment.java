package com.iexceed.appzillonbanking.cagl.loan.domain.ab;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_UAOB_INCOME_ASSESSMENT")
@IdClass(TbUaobIncomeAssessmentId.class)
@NoArgsConstructor
@Data
@AllArgsConstructor
public class TbUaobIncomeAssessment {

	@Id
	private String incAssessmentId;

	@Column(name = "APP_ID")
	private String appId;

	@Column(name = "APPLICATION_ID")
	private String applicationId;

	@Column(name = "VERSION_NO")
	private String versionNum;

	@Column(name = "PAYLOAD")
	private String payload;
	
	@Column(name = "CREATEDBY")
	private String createdBy;
	
	@Column(name = "CREATETS")
	private Timestamp createTs;
	
	@Column(name = "UPDATEDBY")
	private String updatedBy;
	
	@Column(name = "UPDATETS")
	private Timestamp updateTs;
}
