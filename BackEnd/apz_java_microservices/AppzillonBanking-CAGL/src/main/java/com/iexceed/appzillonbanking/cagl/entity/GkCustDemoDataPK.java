package com.iexceed.appzillonbanking.cagl.entity;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class GkCustDemoDataPK implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@JsonProperty("customerId")
	@Column(name = "CUSTOMERID")
	private String customerId;
}
