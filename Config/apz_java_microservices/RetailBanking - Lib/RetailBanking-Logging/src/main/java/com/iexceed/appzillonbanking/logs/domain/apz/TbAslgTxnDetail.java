package com.iexceed.appzillonbanking.logs.domain.apz;

import java.io.Serializable;
import java.time.LocalDateTime;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "TB_ASLG_TXN_DETAIL")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TbAslgTxnDetail implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Basic
	@Column(name = "TXN_REF")
	private String txnRef;

	@Column(name = "INFO1")
	private String masterTxnRef;

	@Column(name = "INTERFACE_ID")
	private String interfaceId;

	@Column(name = "ENDPOINT_TYPE")
	private String endpointType;

	@Column(name = "ST_TM")
	private LocalDateTime stTm;

	@Column(name = "END_TM")
	private LocalDateTime endTm;

	@Column(name = "STATUS")
	private String status;

	@Column(name = "CREATE_TS")
	private LocalDateTime createTs;

	@Column(name = "REQ_LD_REFNO")
	private String reqLdRefNo;

	@Column(name = "RES_LD_REFNO")
	private String resLdRefNo;

	@Column(name = "REQ_NO_RECS")
	private int reqNoRecs;

	@Column(name = "RES_NO_RECS")
	private int resNoRecs;

	@Column(name = "APP_ID")
	private String appId;

	@Column(name = "EXT_ST_TM")
	private LocalDateTime extStTm;

	@Column(name = "EXT_END_TM")
	private LocalDateTime extEndTm;

	@Column(name = "SOURCE")
	private String source;

	@Column(name = "TXN_STAT")
	private String txnStatus;

	@Column(name = "DEVICE_ID")
	private String deviceId;

	@Column(name = "USER_ID")
	private String userId;

}