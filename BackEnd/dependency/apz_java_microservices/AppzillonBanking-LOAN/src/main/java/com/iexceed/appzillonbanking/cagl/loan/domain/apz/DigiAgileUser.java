package com.iexceed.appzillonbanking.cagl.loan.domain.apz;

import java.time.LocalDateTime;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Component
@Table(name = "TB_UACO_DIGIAGILE_APPLICATION")
public class DigiAgileUser {

	@Id
	@JsonProperty("digiagile_applicationId")
	@Column(name = "DIGIAGILE_APPLICATION_ID")
	private String digiagile_applicationId;

	@JsonProperty("applicationId")
	@Column(name = "APPLICATION_ID")
	private String applicationId;

	@JsonProperty("customerId")
	@Column(name = "CUSTOMER_ID")
	private String customerId;

	@JsonProperty("payload")
	@Column(name = "PAYLOAD")
	private String payload;

	@JsonProperty("createdDate")
	@Column(name = "CREATED_DATE")
	private LocalDateTime createdDate;

	@JsonProperty("status")
	@Column(name = "STATUS")
	private String status;

	@JsonProperty("remarks")
	@Column(name = "REMARKS")
	private String remarks;

	@JsonProperty("response")
	@Column(name = "RESPONSE")
	private String response;

}
