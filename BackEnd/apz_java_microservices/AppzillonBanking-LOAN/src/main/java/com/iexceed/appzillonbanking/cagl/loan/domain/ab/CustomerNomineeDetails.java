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
@IdClass(CustomerNomineeDetailsId.class)
@Table(name = "tb_ucno_customer_nominee_details", schema = "public")
public class CustomerNomineeDetails {
	
	@Id
	@Column(name = "application_id")
	private String application_id;

	@Id
	@Column(name = "latest_version_no")
	private String latest_version_no;

	@JsonProperty("customer_id")
	@Column(name = "customer_id")
	private String customer_id;

	@CreationTimestamp
	@JsonProperty("create_ts")
	@Column(name = "create_ts")
	private Timestamp create_ts;
	
	@CreationTimestamp
	@JsonProperty("updated_ts")
	@Column(name = "updated_ts")
	private String updated_ts;
	
	@JsonProperty("relationType")
	@Column(name = "relationType")
	private String relationType;
	
	@JsonProperty("memRelation")
	@Column(name = "memRelation")
	private String memRelation;
	
	@JsonProperty("legaldocName")
	@Column(name = "legaldocName")
	private String legaldocName;
	
	@JsonProperty("legaldocId")
	@Column(name = "legaldocId")
	private String legaldocId;
	
	@JsonProperty("inputData")
	@Column(name = "inputData")
	private String inputData;
	
	@JsonProperty("docuNoF")
	@Column(name = "docuNoF")
	private String docuNoF;
		
	@JsonProperty("docuNoB")
	@Column(name = "docuNoB")
	private String docuNoB;
	
	@JsonProperty("reason")
	@Column(name = "reason")
	private String reason;
	
	@JsonProperty("ocrresponsepayload")
	@Column(name = "ocrresponsepayload")
	private String ocrresponsepayload;
	
	@JsonProperty("nomineeDtls")
	@Column(name = "nomineeDtls")
	private String nomineeDtls;
	
	@JsonProperty("ocrdetails")
	@Column(name = "ocrdetails")
	private String ocrdetails;
	
	@JsonProperty("customer_name")
	@Column(name = "customer_name")
	private String customer_name;
	
	@JsonProperty("add_info")
	@Column(name = "add_info")
	private String add_info;
	
}
