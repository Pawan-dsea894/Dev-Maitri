package com.iexceed.appzillonbanking.cagl.domain.cus;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GkLoanDataPK implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@JsonProperty("loanId")
	@Column(name = "LOAN_ID")
	private String loanId;
}
