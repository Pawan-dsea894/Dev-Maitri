package com.iexceed.appzillonbanking.kyc.entity;

import java.sql.Timestamp;
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
@Table(name = "tb_uaex_external_system_audit_logs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExternalSystemAuditLogs {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "seq_id")
	private Long  seq_id;
	
	@Column(name = "user_id")
	private String user_id;
	
	@Column(name = "user_role")
	private String user_role;
	
	@Column(name = "api_name")
	private String api_name;
	
	@Column(name = "id_type")
	private String id_type;
	
	@Column(name = "uid")
	private String uid;
	
	@Column(name = "api_req_ts")
	private Timestamp api_req_ts;
	
	@Column(name = "request_payload")
	private String request_payload;
	
	@Column(name = "api_res_ts")
	private Timestamp api_res_ts;
	
	@Column(name = "response_payload")
	private String response_payload;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "api_status")
	private String api_status;
	
	@Column(name = "scheduler_status")
	private String scheduler_status;
	
	@Column(name = "app_ver")
	private String app_ver;
	
	@Column(name = "add_info1")
	private String add_info1;
	
	@Column(name = "add_info2")
	private String add_info2;
	
	@Column(name = "create_ts", insertable = false, updatable = false)
	private Timestamp create_ts;
	
	
}
