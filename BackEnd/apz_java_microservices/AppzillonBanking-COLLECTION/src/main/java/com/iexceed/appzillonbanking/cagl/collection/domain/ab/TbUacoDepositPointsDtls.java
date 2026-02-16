package com.iexceed.appzillonbanking.cagl.collection.domain.ab;

import java.sql.Timestamp;
import java.util.Date;

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
@Table(name = "TB_UACO_DEPOSITPOINT_DETAILS")
@IdClass(TbUacoDepositPointsDtlsId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TbUacoDepositPointsDtls {

	@Id
	private String appId;

	@Id
	private String applicationId;

	@Id
	private String versionNo;

	@Id
	private String refNo;

	@Id
	private String kmId;

	@Column(name = "APPLICATION_DATE")
	private Date applicationDate;

	@Column(name = "DEPOSITPOINT_NAME")
	private String depositPointName;

	@Column(name = "AMOUNT")
	private int amount;

	@Column(name = "FILE_PATH")
	private String filePath;

	@Column(name = "CREATE_TS")
	private Timestamp createTs;

	@Column(name = "PAYLOAD")
	private String payload;
}
