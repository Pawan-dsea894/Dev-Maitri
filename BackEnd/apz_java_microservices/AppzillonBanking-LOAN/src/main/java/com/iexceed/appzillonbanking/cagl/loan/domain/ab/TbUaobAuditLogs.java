package com.iexceed.appzillonbanking.cagl.loan.domain.ab;

import java.io.Serializable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.sql.Timestamp;


@Entity
@Table(name = "TB_UAOB_API_AUDIT_LOGS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TbUaobAuditLogs implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "SEQ_ID")
	private String seqId;
	
	@Column(name = "APP_ID")
	private String appId;
	
	@Column(name = "APPLICATION_ID")
	private String applicationId;

	@Column(name = "APPLICATION_VERSION_NO")
	private String versionNo;
	
	@Column(name = "API_NAME")
	private String apiName;
	
	@Column(name = "CUST_DTL_ID")
	private String custDtlId;
	
	@Column(name = "REQUEST_PAYLOAD")
	private String requestPayload;
	
	@Column(name = "RESPONSE_PAYLOAD")
	private String responsePayload;
	
	@Column(name = "API_REQ_TS")
	private Timestamp reqTs;
	
	@Column(name = "API_RES_TS")
	private Timestamp resTs;
	
	@Column(name = "SCHEDULER_STATUS")
	private String schedulerStatus;
	
	@Column(name = "API_STATUS")
	private String apiStatus;
	
	@Column(name = "CREATE_TS")
	private Timestamp createTs;
	
	@Column(name = "UPDATE_TS")
	private Timestamp updateTs;

}
