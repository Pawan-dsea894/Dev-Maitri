package com.iexceed.appzillonbanking.cagl.loan.domain.ab;

import java.sql.Timestamp;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@IdClass(CustomerApplicationMasterId.class)
@Table(name = "tb_ucao_customer_application_master", schema = "public")
public class CustomerApplicationMaster {

	@Id
	private String app_id;

	@Id
	private String application_id;

	@Id
	private String latest_version_no;

	@JsonProperty("kendra_id")
	@Column(name = "kendra_id")
	private String kendra_id;

	@JsonProperty("customer_id")
	@Column(name = "customer_id")
	private String customer_id;
	
	@CreationTimestamp
	@JsonProperty("create_ts")
	@Column(name = "create_ts")
	private Timestamp  create_ts;

	@JsonProperty("created_by")
	@Column(name = "created_by")
	private String created_by;
	
	@JsonProperty("updated_by")
	@Column(name = "updated_by")
	private String updated_by;

	@CreationTimestamp
	@JsonProperty("updated_ts")
	@Column(name = "updated_ts")
	private Timestamp  updated_ts;
			
	@JsonProperty("application_type")
	@Column(name = "application_type")
	private String application_type;

	@JsonProperty("kyc_type")
	@Column(name = "kyc_type")
	private String kyc_type;

	@JsonProperty("application_status")
	@Column(name = "application_status")
	private String application_status;

	@JsonProperty("branch_code")
	@Column(name = "branch_code")
	private String branch_code;
	
	@JsonProperty("branch_name")
	@Column(name = "branch_name")
	private String branch_name;

	@JsonProperty("current_screen_id")
	@Column(name = "current_screen_id")
	private String current_screen_id;

	@JsonProperty("remarks")
	@Column(name = "remarks")
	private String remarks;

	@JsonProperty("current_stage")
	@Column(name = "current_stage")
	private String current_stage;

	@JsonProperty("kmid")
	@Column(name = "kmid")
	private String kmid;

	@JsonProperty("kendraname")
	@Column(name = "kendraname")
	private String kendraname;

	@JsonProperty("customer_name")
	@Column(name = "customer_name")
	private String customer_name;

	@JsonProperty("add_info1")
	@Column(name = "add_info1")
	private String add_info1;

	@JsonProperty("add_info2")
	@Column(name = "add_info2")
	private String add_info2;
	
	@JsonProperty("loan_application_no")
	@Column(name = "loan_application_no")
	private String loan_application_no;
	
	@JsonProperty("request_type")
	@Column(name = "request_type")
	private String request_type;

}
