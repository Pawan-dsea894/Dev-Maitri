package com.iexceed.appzillonbanking.cagl.collection.domain.ab;

import java.sql.Timestamp;

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

@Entity
@Table(name = "TB_UACO_QR_DETAILS")
@IdClass(TbUacoQRDtlsId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TbUacoQRDtls {

	@Id
	private String appId;

	@Id
	private String customerId;

	@Id
	private String billNumber;

	@JsonProperty("intentLink")
	@Column(name = "intent_link")
	private String intentLink;

	@JsonProperty("remarks")
	@Column(name = "remarks")
	private String remarks;

	@JsonProperty("customerName")
	@Column(name = "customer_name")
	private String customerName;

	@JsonProperty("customerTxnId")
	@Column(name = "customer_txn_id")
	private String customerTxnId;

	@JsonProperty("createTs")
	@Column(name = "create_ts")
	private Timestamp createTs;

	@JsonProperty("apiStatusCode")
	@Column(name = "api_status_code")
	private String apiStatusCode;

	@JsonProperty("status")
	@Column(name = "status")
	private String status;

	@JsonProperty("payload")
	@Column(name = "payload")
	private String payload;

}