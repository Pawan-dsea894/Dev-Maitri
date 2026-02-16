package com.iexceed.appzillonbanking.kendra.domain.cus;

import java.io.Serializable;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SRCreationId implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "APPLICATION_ID", nullable = false)
	private String applicationId;

	@Column(name = "SR_TYPE", nullable = false)
	private String srType;

}