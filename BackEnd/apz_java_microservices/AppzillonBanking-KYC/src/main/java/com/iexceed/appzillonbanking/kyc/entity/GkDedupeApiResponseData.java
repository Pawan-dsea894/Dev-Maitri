package com.iexceed.appzillonbanking.kyc.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "gk_dedupe_api_response")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GkDedupeApiResponseData {

	@Id
	@JsonProperty("dedupe_id")
	private String dedupeId;
	
	//@Id
	@JsonProperty("dedupe_type")
	@Column(name = "DEDUPE_TYPE")
	private String dedupeType;
	
	@JsonProperty("customer_id")
	@Column(name = "CUSTOMER_ID")
	private String customerId;
	
	@JsonProperty("start_time")
	@Column(name = "START_TIME")
	private LocalDateTime startTime;
	
	@JsonProperty("recieved_time")
	@Column(name = "RECEIVED_TIME")
	private LocalDateTime recievedTime;
	
	@JsonProperty("request")
	@Column(name = "REQUEST", length = 4000)
	private String request;
	
	@JsonProperty("response")
	@Column(name = "RESPONSE", length = 20000)
	private String response;

}
