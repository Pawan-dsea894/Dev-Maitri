package com.iexceed.appzillonbanking.cagl.incomeassesment.domain.ab;

import java.io.Serializable;

import jakarta.persistence.Column;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LockCustomerDtlsPK implements Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "APPLICATION_ID", nullable = false)
	private String applicationId;
	
	@Column(name = "CREATED_BY", nullable = false)
	private String createdBy;
}
