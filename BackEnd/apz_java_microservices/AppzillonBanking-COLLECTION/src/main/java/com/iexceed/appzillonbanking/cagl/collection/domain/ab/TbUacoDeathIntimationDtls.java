package com.iexceed.appzillonbanking.cagl.collection.domain.ab;

import java.sql.Timestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_UACO_DEATHINTIMATION_DETAILS")
@IdClass(TbUacoDeathIntimationDtlsId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TbUacoDeathIntimationDtls {

	@Id
	private String appId;

	@Id
	private String applicationId;

	@Id
	private String versionNo;

	@Id
	private String kendraId;

	@Id
	private String customerId;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "CUSTOMER_NAME")
	private String customerName;

	@Column(name = "INTIMATION_TYPE")
	private String intimationType;

	@Column(name = "CREATE_TS")
	private Timestamp createTs;

	@Column(name = "PAYLOAD")
	private String payload;
}
