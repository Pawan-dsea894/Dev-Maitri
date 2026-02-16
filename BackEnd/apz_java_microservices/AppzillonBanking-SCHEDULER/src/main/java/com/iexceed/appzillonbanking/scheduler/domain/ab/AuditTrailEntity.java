package com.iexceed.appzillonbanking.scheduler.domain.ab;

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

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tb_uaco_audit_trail")
public class AuditTrailEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonProperty("Id")
	@Column(name = "id")
	private Integer id;

	@JsonProperty("appId")
	@Column(name = "app_id")
	private String appId;

	@JsonProperty("applicationId")
	@Column(name = "application_id")
	private String applicationId;

	@JsonProperty("userId")
	@Column(name = "user_id")
	private String userId;

	@JsonProperty("userRole")
	@Column(name = "user_role")
	private String userRole;

	@JsonProperty("stageId")
	@Column(name = "stage_id")
	private String stageid;

	@JsonProperty("createTs")
	@Column(name = "create_ts")
	private String createTs;

	@JsonProperty("createDate")
	@Column(name = "create_date")
	private String createDate;

	@JsonProperty("addInfo1")
	@Column(name = "add_info1")
	private String addInfo1;

	@JsonProperty("addInfo2")
	@Column(name = "add_info2")
	private String addInfo2;

	@JsonProperty("addInfo3")
	@Column(name = "add_info3")
	private String addInfo3;

	@JsonProperty("addInfo4")
	@Column(name = "add_info4")
	private String addInfo4;

	@JsonProperty("loanAmount")
	@Column(name = "loan_amt")
	private String loanAmount;

	@JsonProperty("mobileNumber")
	@Column(name = "mobile_number")
	private String mobileNumber;

	@JsonProperty("repaymentFrequency")
	@Column(name = "repayment_frequency")
	private String repaymentFrequency;

	@JsonProperty("purpose")
	@Column(name = "purpose")
	private String purpose;

	@JsonProperty("branchId")
	@Column(name = "branch_id")
	private String branchId;

	@JsonProperty("payload")
	@Column(name = "payload")
	private String payload;

	@JsonProperty("productId")
	@Column(name = "product_id")
	private String productId;

	@JsonProperty("customerId")
	@Column(name = "customer_id")
	private String customerId;

	@JsonProperty("kendraName")
	@Column(name = "kendra_name")
	private String kendraName;

	@JsonProperty("customerName")
	@Column(name = "customer_name")
	private String customerName;

	@JsonProperty("spouse")
	@Column(name = "spouse")
	private String spouse;

	@JsonProperty("userName")
	@Column(name = "user_name")
	private String userName;

	@JsonProperty("kendraId")
	@Column(name = "kendraId")
	private String kendraId;

	@JsonProperty("appVersion ")
	@Column(name = "appVersion ")
	private String appVersion;
}
